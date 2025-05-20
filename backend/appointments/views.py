import base64
from io import BytesIO
from django.utils import timezone
import datetime
from firebase_admin import messaging

from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response

import qrcode

from core.models import Appointment, Availability
from core.serializers import AppointmentSerializer
from notifications.services import NotificationService
from fcm_django.models import FCMDevice

def schedule_appointment_reminder(appointment):
    """Schedule a reminder notification for 1 hour before the appointment"""
    # Calculate when to send the reminder (1 hour before appointment)
    appointment_time = appointment.start_time
    reminder_time = appointment_time - datetime.timedelta(hours=1)
    
    # Get seconds until the reminder should be sent
    now = timezone.now()
    seconds_until_reminder = (reminder_time - now).total_seconds()
    
    if seconds_until_reminder > 0:
        # If the appointment is more than 1 hour in the future
        # Get the patient's FCM devices
        devices = FCMDevice.objects.filter(user=appointment.patient.user, active=True)
        
        if devices.exists():
            # Schedule the notification through Firebase
            for device in devices:
                message = messaging.Message(
                    notification=messaging.Notification(
                        title="Appointment Reminder",
                        body=f"Reminder: You have an appointment with Dr. {appointment.doctor.user.name} on {appointment_time.strftime('%Y-%m-%d at %H:%M')}"
                    ),
                    token=device.registration_id,
                    android=messaging.AndroidConfig(
                        ttl=int(seconds_until_reminder) * 1000,  # TTL in milliseconds
                        priority='high'
                    ),
                )
                
                try:
                    # Send the scheduled message
                    messaging.send(message)
                except Exception as e:
                    print(f"Error scheduling reminder: {e}")
        
        return True
    else:
        # If the appointment is less than 1 hour away, send reminder immediately
        NotificationService.create_appointment_notification(
            appointment=appointment,
            notification_type='appointment_reminder'
        )
        return True
    
    return False
def generate_qr_code(content):
    """Helper function to generate a QR code as base64."""
    qr = qrcode.make(content)
    buffer = BytesIO()
    qr.save(buffer, format="PNG")
    qr_base64 = base64.b64encode(buffer.getvalue()).decode()
    return qr_base64


class AppointmentViewSet(viewsets.ModelViewSet):
    queryset = Appointment.objects.all()
    serializer_class = AppointmentSerializer

    def create(self, request):
        data = request.data
        doctor_id = data.get('doctor')
        start_time = data.get('start_time')
        end_time = data.get('end_time')

        availability = Availability.objects.filter(
            doctor_id_id=doctor_id,
            start_time__lte=start_time,
            end_time__gte=end_time,
            booked=False
        ).first()

        if not availability:
            return Response({'error': 'No available slot for the given time.'}, status=status.HTTP_400_BAD_REQUEST)

        serializer = self.get_serializer(data=data)
        serializer.is_valid(raise_exception=True)
        appointment = serializer.save()

        # generating the QR code based on appointment info
        qr_content = f"Appointment ID: {appointment.id}, Patient ID: {appointment.id} Doctor ID: {appointment.doctor.id}, Start: {appointment.start_time}, End: {appointment.end_time}"
        qr_base64 = generate_qr_code(qr_content)

        appointment.qr_Code = qr_base64
        appointment.save()

        availability.booked = True
        availability.save()
        
        # Create notification for appointment scheduled
        NotificationService.create_appointment_notification(
            appointment=appointment,
            notification_type='appointment_scheduled'
        )

        return Response(serializer.data, status=status.HTTP_201_CREATED)

    @action(detail=False, methods=['get'], url_path='doctor/(?P<pk>[^/.]+)')
    def appoints_by_doctor(self, request, pk=None):
        appointments = Appointment.objects.filter(doctor_id=pk)
        serializer = self.get_serializer(instance=appointments, many=True)

        return Response(serializer.data, status=status.HTTP_200_OK)

    @action(detail=False, methods=['get'], url_path='patient/(?P<pk>[^/.]+)')
    def appoints_by_patient(self, request, pk=None):
        appointments = Appointment.objects.filter(patient_id=pk)
        serializer = self.get_serializer(instance=appointments, many=True)

        return Response(serializer.data, status=status.HTTP_200_OK)

    @action(detail=False, methods=['post'])
    def validate_appointment(self, request):
        data = request.data
        qr_code = data.get('qr_code')
        appointment = Appointment.objects.filter(qr_Code=qr_code).first()
        if not appointment:
            return Response({'error': 'Appointment not found'}, status=status.HTTP_404_NOT_FOUND)
        appointment.status = 'in_progress'
        appointment.save()
        # Create notification for appointment in progress
        NotificationService.create_appointment_notification(
            appointment=appointment,
            notification_type='appointment_in_progress'
        )
        return Response({'message': 'Appointment validated successfully.'}, status=status.HTTP_200_OK)

    @action(detail=True, methods=['post'])
    def reschedule(self, request, pk=None):
        """
        Reschedule an existing appointment to a new time slot.
        
        Request Data:
            start_time: New start time for the appointment
            end_time: New end time for the appointment
        """
        appointment = self.get_object()
        
        # Get new times from request
        new_start_time = request.data.get('start_time')
        new_end_time = request.data.get('end_time')
        
        if not new_start_time or not new_end_time:
            return Response(
                {'error': 'Both start_time and end_time are required'}, 
                status=status.HTTP_400_BAD_REQUEST
            )
        
        # Check if the new time slot is available
        availability = Availability.objects.filter(
            doctor_id=appointment.doctor.id,
            start_time__lte=new_start_time,
            end_time__gte=new_end_time,
            booked=False
        ).first()
        
        if not availability:
            return Response(
                {'error': 'No available slot for the given time.'}, 
                status=status.HTTP_400_BAD_REQUEST
            )
        
        # Free up the old availability slot
        old_availability = Availability.objects.filter(
            doctor_id=appointment.doctor.id,
            start_time__lte=appointment.start_time,
            end_time__gte=appointment.end_time,
            booked=True
        ).first()
        
        if old_availability:
            old_availability.booked = False
            old_availability.save()
        
        # Update appointment times
        appointment.start_time = new_start_time
        appointment.end_time = new_end_time
        appointment.save()
        
        # Mark new availability as booked
        availability.booked = True
        availability.save()
        
        # Create notification for rescheduled appointment
        NotificationService.create_appointment_notification(
            appointment=appointment,
            notification_type='appointment_rescheduled'
        )
        
        serializer = self.get_serializer(appointment)
        return Response(serializer.data, status=status.HTTP_200_OK)
    @action(detail=True, methods=['post'])
    def cancel(self, request, pk=None):
        """
        Cancel an existing appointment.
        """
        appointment = self.get_object()
        
        # Free up the availability slot
        availability = Availability.objects.filter(
            doctor_id=appointment.doctor.id,
            start_time__lte=appointment.start_time,
            end_time__gte=appointment.end_time,
            booked=True
        ).first()
        
        if availability:
            availability.booked = False
            availability.save()
        
        # Mark appointment as canceled
        appointment.status = 'canceled'
        appointment.save()
        
        # Create notification for canceled appointment
        NotificationService.create_appointment_notification(
            appointment=appointment,
            notification_type='appointment_canceled'
        )
        
        serializer = self.get_serializer(appointment)
        return Response(serializer.data, status=status.HTTP_200_OK)
    
    
