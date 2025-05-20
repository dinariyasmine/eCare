from django.db import models
from django.contrib.auth.models import AbstractUser
from django.utils import timezone

class User(AbstractUser):
    """Extended user model for the medical system"""
    name = models.CharField(max_length=255, blank=True, null=True)
    email = models.EmailField(unique=True, blank=True, null=True)
    password = models.CharField(max_length=255, blank=True, null=True)
    phone = models.CharField(max_length=20, blank=True, null=True)
    address = models.CharField(max_length=255, blank=True, null=True)
    role = models.CharField(
        max_length=20,
        choices=[
            ('patient', 'Patient'),
            ('doctor', 'Doctor'),
            ('admin', 'Administrator'),
        ]
    )
    birth_date = models.DateField(null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    groups = models.ManyToManyField(
        'auth.Group', related_name='custom_user_set', blank=True
    )
    user_permissions = models.ManyToManyField(
        'auth.Permission', related_name='custom_user_set', blank=True
    )

class Clinic(models.Model):
    """Medical clinics where doctors practice"""
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=255)
    address = models.CharField(max_length=255)
    map_location = models.CharField(max_length=255, blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.name


class Doctor(models.Model):
    """Doctor profile linked to a user account"""
    id = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='doctor_profile')
    photo = models.CharField(max_length=255, blank=True, null=True)
    specialty = models.CharField(max_length=100)
    clinic = models.ForeignKey(Clinic, on_delete=models.SET_NULL, null=True, related_name='doctors')
    grade = models.FloatField(default=0.0)
    description = models.TextField(blank=True, null=True)
    nbr_patients = models.IntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"Dr. {self.user.get_full_name()}"


class Patient(models.Model):
    """Patient profile linked to a user account"""
    id = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='patient_profile')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.user.get_full_name()


class Appointment(models.Model):
    """Medical appointments between doctors and patients"""
    id = models.AutoField(primary_key=True)
    doctor = models.ForeignKey(Doctor, on_delete=models.CASCADE, related_name='appointments')
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, related_name='appointments')
    start_time = models.DateTimeField()
    end_time = models.DateTimeField()
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    status = models.CharField(max_length=20, choices=[
        ('confirmed', 'confirmed'),
        ('completed', 'Completed'),
        ('in_progress', 'In Progress'),
    ], default='scheduled')
    qr_Code = models.CharField(max_length=255, blank=True, null=True)

    def __str__(self):
        return f"Appointment with {self.doctor} on {self.start_time.strftime('%Y-%m-%d %H:%M')}"




class Feedback(models.Model):
    """Patient feedback for doctors or appointments"""
    id = models.AutoField(primary_key=True)
    title = models.CharField(max_length=255)
    description = models.TextField()
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, related_name='feedback')
    doctor = models.ForeignKey(Doctor, on_delete=models.CASCADE, related_name='feedback')
    date_creation = models.DateField()
    time_creation = models.TimeField()
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"Feedback from {self.patient} for {self.doctor}"

class Prescription(models.Model):
    """Medical prescriptions given to patients"""
    id = models.AutoField(primary_key=True)
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, related_name='prescriptions')
    doctor = models.ForeignKey(Doctor, on_delete=models.CASCADE, related_name='prescriptions')
    date = models.DateField(default=timezone.now)  # Use current date by default
    notes = models.TextField(blank=True, null=True)  # For additional prescription notes
    pdf_file = models.FileField(upload_to='prescriptions/', null=True, blank=True)  # For storing generated PDFs
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"Prescription for {self.patient} by {self.doctor} on {self.date}"

class Medication(models.Model):
    """Medical drugs that can be prescribed"""
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=255)
    description = models.TextField(blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    

    def __str__(self):
        return self.name

class PrescriptionItem(models.Model):
    """Individual medication items within a prescription"""
    FREQUENCY_CHOICES = [
        ('once_daily', 'Once Daily'),
        ('twice_daily', 'Twice Daily'),
        ('three_daily', 'Three Times Daily'),
        ('four_daily', 'Four Times Daily'),
        ('as_needed', 'As Needed'),
    ]
    
    prescription = models.ForeignKey(Prescription, on_delete=models.CASCADE, related_name='items')
    medication = models.ForeignKey(Medication, on_delete=models.CASCADE)
    dosage = models.CharField(max_length=100)  # e.g., "1 capsule (250mg)"
    duration = models.CharField(max_length=100)  # e.g., "14 days" or "30 days"
    frequency = models.CharField(max_length=50, choices=FREQUENCY_CHOICES)
    prescribed_by = models.ForeignKey(Doctor, on_delete=models.CASCADE, related_name='prescribed_items')
    prescribed_to = models.ForeignKey(Patient, on_delete=models.CASCADE, related_name='medication_items')
    instructions = models.TextField(blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.medication.name} for {self.prescription}"


class Availability(models.Model):
    """Doctor availability slots for appointments"""
    id = models.AutoField(primary_key=True)
    doctor_id = models.ForeignKey('Doctor', on_delete=models.CASCADE, related_name='availabilities')
    start_time = models.DateTimeField()
    end_time = models.DateTimeField()
    booked = models.BooleanField(default=False)

    def __str__(self):
        return f"Dr. {self.doctor_id} available from {self.start_time} to {self.end_time}"

class SocialMedia(models.Model):
    """Social media profiles for doctors"""
    id = models.AutoField(primary_key=True)
    doctor_id = models.ForeignKey('Doctor', on_delete=models.CASCADE, related_name='social_media')
    name = models.CharField(max_length=100)  # Platform name (e.g., Facebook, Twitter)
    link = models.CharField(max_length=255)  # Profile URL
    
    def __str__(self):
        return f"{self.name} profile for Dr. {self.doctor_id}"
    


class DoctorRating(models.Model):
    """Rating given to a doctor by a user"""
    doctor = models.ForeignKey(Doctor, on_delete=models.CASCADE, related_name='ratings')
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE, related_name='ratings')
    grade = models.FloatField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Rating {self.grade} for Dr. {self.doctor} by {self.patient}"



class Notification(models.Model):
    """System notifications for users"""
    NOTIFICATION_TYPES = [
        # Appointment related
        ('appointment_scheduled', 'Appointment Scheduled'),
        ('appointment_reminder', 'Appointment Reminder'),
        ('appointment_canceled', 'Appointment Canceled'),
        ('appointment_rescheduled', 'Appointment Rescheduled'),
        ('appointment_completed', 'Appointment Completed'),
        
        # Prescription related
        ('prescription_created', 'Prescription Created'),
        ('prescription_updated', 'Prescription Updated'),
        ('medication_reminder', 'Medication Reminder'),
    ]
    
    id = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='notifications')
    title = models.CharField(max_length=255)
    description = models.TextField()
    date_creation = models.DateField()
    time_creation = models.TimeField()
    type = models.CharField(max_length=50, choices=NOTIFICATION_TYPES, default='appointment_reminder')
    read = models.BooleanField(default=False)  # Add this field to track read status
    
    # References to related objects
    appointment = models.ForeignKey(Appointment, on_delete=models.CASCADE, null=True, blank=True, related_name='notifications')
    prescription = models.ForeignKey(Prescription, on_delete=models.CASCADE, null=True, blank=True, related_name='notifications')
    
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.title
