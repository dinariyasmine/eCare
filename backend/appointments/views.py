import base64
from io import BytesIO

from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response

import qrcode

from core.models import Appointment, Availability
from core.serializers import AppointmentSerializer


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

    def retrieve(self, request, *args, **kwargs):
        appointment = self.get_object()
        serializer_data = self.get_serializer(appointment).data

        # Add doctor's name and specialty
        doctor = appointment.doctor
        serializer_data['doctor_name'] = doctor.name if hasattr(doctor, 'name') else "Charollette Baker"
        serializer_data['doctor_specialty'] = doctor.specialty if hasattr(doctor, 'specialty') else None

        return Response(serializer_data, status=status.HTTP_200_OK)

    def create(self, request):
        data = request.data
        doctor = data.get('doctor')
        start_time = data.get('start_time')
        end_time = data.get('end_time')

        availability = Availability.objects.filter(
            doctor_id=doctor,
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
        qr_content = f"Appointment ID: {appointment.id}, Patient ID: {appointment.patient.id} Doctor ID: {appointment.doctor.id}, Start: {appointment.start_time}, End: {appointment.end_time}"
        qr_base64 = generate_qr_code(qr_content)

        appointment.qr_Code = qr_base64
        appointment.save()

        availability.booked = True
        availability.save()

        return Response(serializer.data, status=status.HTTP_201_CREATED)

    def destroy(self, request, *args, **kwargs):
        appointment = self.get_object()

        # Find the associated availability and set booked to False
        availability = Availability.objects.filter(
            doctor_id=appointment.doctor.id,
            start_time__lte=appointment.start_time,
            end_time__gte=appointment.end_time,
            booked=True
        ).first()

        if availability:
            availability.booked = False
            availability.save()

        # Proceed with the standard deletion
        self.perform_destroy(appointment)

        return Response(status=status.HTTP_204_NO_CONTENT)

    @action(detail=False, methods=['get'], url_path='doctor/(?P<pk>[^/.]+)')
    def appoints_by_doctor(self, request, pk=None):
        appointments = Appointment.objects.filter(doctor_id=pk)
        serializer = self.get_serializer(instance=appointments, many=True)

        return Response(serializer.data, status=status.HTTP_200_OK)

    @action(detail=False, methods=['get'], url_path='patient/(?P<pk>[^/.]+)')
    def appoints_by_patient(self, request, pk=None):
        appointments = Appointment.objects.filter(patient_id=pk).select_related('doctor')
        serializer_data = []

        for appointment in appointments:
            # Serialize each appointment individually
            appointment_data = self.get_serializer(appointment).data
            # Add doctor name and specialty to the response
            doctor = appointment.doctor
            appointment_data['doctor_name'] = doctor.name if hasattr(doctor, 'name') else "Charollette Baker"
            appointment_data['doctor_specialty'] = doctor.specialty if hasattr(doctor, 'specialty') else None
            serializer_data.append(appointment_data)

        return Response(serializer_data, status=status.HTTP_200_OK)

    @action(detail=False, methods=['post'])
    def validate_appointment(self, request):
        data = request.data
        qr_code = data.get('qr_code')
        appointment = Appointment.objects.filter(qr_Code=qr_code).first()
        if not appointment:
            return Response({'error': 'Appointment not found'}, status=status.HTTP_404_NOT_FOUND)
        appointment.status = 'in_progress'
        appointment.save()
        return Response({'message': 'Appointment validated successfully.'}, status=status.HTTP_200_OK)

