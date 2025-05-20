# notifications/tasks.py
from celery import shared_task
from .services import NotificationService

@shared_task(name="send_appointment_reminders")
def send_appointment_reminders_task():
    """Celery task to send appointment reminders"""
    reminder_count = NotificationService.send_appointment_reminders()
    return f"Sent {reminder_count} appointment reminders"
