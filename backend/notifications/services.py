from django.utils import timezone
from core.models import Notification, User, Appointment, Prescription

class NotificationService:
    @staticmethod
    def create_appointment_notification(appointment, notification_type, title=None, description=None):
        """Create a notification for an appointment"""
        if not title:
            if notification_type == 'appointment_scheduled':
                title = "Appointment Scheduled"
            elif notification_type == 'appointment_reminder':
                title = "Appointment Reminder"
            elif notification_type == 'appointment_canceled':
                title = "Appointment Canceled"
            else:
                title = "Appointment Update"
        
        if not description:
            doctor_name = appointment.doctor.user.name or appointment.doctor.user.get_full_name()
            appointment_time = appointment.start_time.strftime('%Y-%m-%d at %H:%M')
            
            if notification_type == 'appointment_scheduled':
                description = f"Your appointment with Dr. {doctor_name} has been scheduled for {appointment_time}"
            elif notification_type == 'appointment_reminder':
                description = f"Reminder: You have an appointment with Dr. {doctor_name} on {appointment_time}"
            elif notification_type == 'appointment_canceled':
                description = f"Your appointment with Dr. {doctor_name} on {appointment_time} has been canceled"
            else:
                description = f"Your appointment with Dr. {doctor_name} has been updated"
        
        return Notification.objects.create(
            user=appointment.patient.user,
            title=title,
            description=description,
            date_creation=timezone.now().date(),
            time_creation=timezone.now().time(),
            type=notification_type,
            appointment=appointment
        )
    
    @staticmethod
    def create_prescription_notification(prescription, notification_type, title=None, description=None):
        """Create a notification for a prescription"""
        if not title:
            if notification_type == 'prescription_created':
                title = "New Prescription"
            elif notification_type == 'prescription_updated':
                title = "Prescription Updated"
            elif notification_type == 'medication_reminder':
                title = "Medication Reminder"
            else:
                title = "Prescription Update"
        
        if not description:
            doctor_name = prescription.doctor.user.name or prescription.doctor.user.get_full_name()
            
            if notification_type == 'prescription_created':
                description = f"Dr. {doctor_name} has created a new prescription for you dated {prescription.date}"
            elif notification_type == 'prescription_updated':
                description = f"Your prescription from Dr. {doctor_name} dated {prescription.date} has been updated"
            else:
                description = f"Reminder about your prescription from Dr. {doctor_name}"
        
        return Notification.objects.create(
            user=prescription.patient.user,
            title=title,
            description=description,
            date_creation=timezone.now().date(),
            time_creation=timezone.now().time(),
            type=notification_type,
            prescription=prescription
        )
