from django.utils import timezone
from core.models import Appointment
import datetime

# Current time
now = timezone.now()

# Create appointment 30 minutes from now
future_time = now + datetime.timedelta(minutes=30)

# Create test appointment
test_appointment = Appointment.objects.create(
    doctor_id=18,  # Use your doctor ID
    patient_id=28,  # Use your patient ID
    start_time=future_time,
    end_time=future_time + datetime.timedelta(minutes=30),
    status='confirmed',
    reason='Test appointment for reminder'
)
print(f"Created appointment ID: {test_appointment.id}")
