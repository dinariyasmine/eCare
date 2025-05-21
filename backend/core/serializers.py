from rest_framework import serializers
from .models import User, Clinic, Doctor, Patient, Appointment, Notification, Feedback, Prescription, Medication, PrescriptionItem, Availability, SocialMedia

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'username', 'name', 'email', 'phone', 'address', 'role', 'birth_date', 'created_at', 'updated_at']


class ClinicSerializer(serializers.ModelSerializer):
    class Meta:
        model = Clinic
        fields = ['id', 'name', 'address', 'map_location', 'created_at', 'updated_at']


class DoctorSerializer(serializers.ModelSerializer):
    user = UserSerializer()
    clinic = ClinicSerializer()

    class Meta:
        model = Doctor
        fields = ['id', 'user', 'photo', 'specialty', 'clinic', 'grade', 'description', 'nbr_patients', 'created_at', 'updated_at']


class PatientSerializer(serializers.ModelSerializer):
    user = UserSerializer()

    class Meta:
        model = Patient
        fields = ['id', 'user', 'created_at', 'updated_at']


class AppointmentSerializer(serializers.ModelSerializer):

    class Meta:
        model = Appointment
        fields = ['id', 'doctor', 'patient', 'start_time', 'end_time', 'status','name', 'gender',
            'age', 'problem_description', 'qr_Code']

    def validate(self, data):
        if data['start_time'] >= data['end_time']:
            raise serializers.ValidationError("End time must be after start time")
        return data


class NotificationSerializer(serializers.ModelSerializer):
    user = UserSerializer()

    class Meta:
        model = Notification
        fields = ['id', 'user', 'title', 'description', 'date_creation', 'time_creation', 'type', 'created_at', 'updated_at']


class FeedbackSerializer(serializers.ModelSerializer):
    patient = PatientSerializer()
    doctor = DoctorSerializer()

    class Meta:
        model = Feedback
        fields = ['id', 'title', 'description', 'patient', 'doctor', 'date_creation', 'time_creation', 'created_at', 'updated_at']


class PrescriptionSerializer(serializers.ModelSerializer):
    patient = PatientSerializer()
    doctor = DoctorSerializer()

    class Meta:
        model = Prescription
        fields = ['id', 'patient', 'doctor', 'date', 'created_at', 'updated_at']


class MedicationSerializer(serializers.ModelSerializer):
    prescription = PrescriptionSerializer()

    class Meta:
        model = Medication
        fields = ['id', 'name', 'dosage', 'frequency', 'prescription']


class PrescriptionItemSerializer(serializers.ModelSerializer):
    prescription = PrescriptionSerializer()
    medication = MedicationSerializer()

    class Meta:
        model = PrescriptionItem
        fields = ['prescription', 'medication']


class AvailabilitySerializer(serializers.ModelSerializer):

    class Meta:
        model = Availability
        fields = ['id', 'doctor_id', 'start_time', 'end_time', 'booked']

    def validate(self, data):
        if data['start_time'] >= data['end_time']:
            raise serializers.ValidationError("End time must be after start time")
        return data


class SocialMediaSerializer(serializers.ModelSerializer):
    doctor_id = DoctorSerializer()

    class Meta:
        model = SocialMedia
        fields = ['id', 'doctor_id', 'name', 'link']
