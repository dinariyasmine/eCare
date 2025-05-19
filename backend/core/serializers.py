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
        fields = ['id', 'doctor', 'patient', 'start_time', 'end_time', 'status', 'qr_Code']


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


# Prescription System Serializers

class MedicationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Medication
        fields = ['id', 'name', 'description']

class PrescriptionItemSerializer(serializers.ModelSerializer):
    medication = MedicationSerializer(read_only=True)
    medication_id = serializers.PrimaryKeyRelatedField(
        queryset=Medication.objects.all(), 
        write_only=True,
        source='medication'
    )
    prescribed_by_name = serializers.ReadOnlyField(source='prescribed_by.user.name')
    prescribed_to_name = serializers.ReadOnlyField(source='prescribed_to.user.name')
    
    class Meta:
        model = PrescriptionItem
        fields = [
            'id', 'prescription', 'medication', 'medication_id', 
            'dosage', 'duration', 'frequency', 'instructions',
            'prescribed_by', 'prescribed_by_name', 
            'prescribed_to', 'prescribed_to_name'
        ]
        extra_kwargs = {
            'prescribed_by': {'write_only': True},
            'prescribed_to': {'write_only': True}
        }

class PrescriptionSerializer(serializers.ModelSerializer):
    patient_details = PatientSerializer(source='patient', read_only=True)
    doctor_details = DoctorSerializer(source='doctor', read_only=True)
    items = PrescriptionItemSerializer(many=True, read_only=True)
    pdf_file = serializers.FileField(read_only=True)
    
    class Meta:
        model = Prescription
        fields = [
            'id', 'patient', 'patient_details', 
            'doctor', 'doctor_details', 
            'date', 'notes', 'items', 'pdf_file',
            'created_at', 'updated_at'
        ]
    
    def create(self, validated_data):
        prescription = Prescription.objects.create(**validated_data)
        return prescription



class AddPrescriptionItemSerializer(serializers.Serializer):
    medication_id = serializers.PrimaryKeyRelatedField(queryset=Medication.objects.all())
    dosage = serializers.CharField(max_length=100)
    duration = serializers.CharField(max_length=100)
    frequency = serializers.ChoiceField(choices=PrescriptionItem.FREQUENCY_CHOICES)
    instructions = serializers.CharField(required=False, allow_blank=True)
    
    def create(self, validated_data):
        prescription_id = self.context.get('prescription_id')
        prescription = Prescription.objects.get(id=prescription_id)
        
        prescription_item = PrescriptionItem.objects.create(
            prescription=prescription,
            medication=validated_data['medication_id'],
            dosage=validated_data['dosage'],
            duration=validated_data['duration'],
            frequency=validated_data['frequency'],
            instructions=validated_data.get('instructions', ''),
            prescribed_by=prescription.doctor,
            prescribed_to=prescription.patient
        )
        
        return prescription_item
# End Prescription System Serializers

class AvailabilitySerializer(serializers.ModelSerializer):
    class Meta:
        model = Availability
        fields = ['id', 'doctor_id', 'start_time', 'end_time', 'booked']


class SocialMediaSerializer(serializers.ModelSerializer):
    doctor_id = DoctorSerializer()

    class Meta:
        model = SocialMedia
        fields = ['id', 'doctor_id', 'name', 'link']
