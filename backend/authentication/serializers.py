# authentication/serializers.py
from rest_framework import serializers
from core.models import User, Doctor, Patient, Clinic
from django.contrib.auth.password_validation import validate_password
from django.core.exceptions import ValidationError

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'name', 'phone', 'address', 'role', 'birth_date']
        read_only_fields = ['id']

class LoginSerializer(serializers.Serializer):
    username = serializers.CharField(max_length=255, required=True)
    password = serializers.CharField(max_length=255, required=True, write_only=True)

class RegisterUserSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True, required=True, validators=[validate_password])
    password2 = serializers.CharField(write_only=True, required=True)
    
    class Meta:
        model = User
        fields = ['username', 'password', 'password2', 'email', 'name', 'phone', 'address', 'role', 'birth_date']
    
    def validate(self, attrs):
        if attrs['password'] != attrs['password2']:
            raise serializers.ValidationError({"password": "Password fields didn't match."})
        
        if not attrs.get('role') in ['patient', 'doctor', 'admin']:
            raise serializers.ValidationError({"role": "Role must be either 'patient', 'doctor', or 'admin'."})
            
        return attrs
    
    def create(self, validated_data):
        validated_data.pop('password2')
        user = User.objects.create(
            username=validated_data['username'],
            email=validated_data['email'],
            name=validated_data.get('name', ''),
            phone=validated_data.get('phone', ''),
            address=validated_data.get('address', ''),
            role=validated_data['role'],
            birth_date=validated_data.get('birth_date')
        )
        
        user.set_password(validated_data['password'])
        user.save()
        
        # Create related profile based on role
        if validated_data['role'] == 'patient':
            Patient.objects.create(user=user)
        elif validated_data['role'] == 'doctor':
            # Get first clinic as default or None
            default_clinic = Clinic.objects.first()
            Doctor.objects.create(
                user=user,
                specialty="General",  # Default value, can be updated later
                clinic=default_clinic
            )
            
        return user

class DoctorRegisterSerializer(RegisterUserSerializer):
    specialty = serializers.CharField(required=True)
    clinic_id = serializers.IntegerField(required=False)
    
    class Meta(RegisterUserSerializer.Meta):
        fields = RegisterUserSerializer.Meta.fields + ['specialty', 'clinic_id']
    
    def validate(self, attrs):
        attrs = super().validate(attrs)
        
        if attrs['role'] != 'doctor':
            raise serializers.ValidationError({"role": "Role must be 'doctor' for doctor registration."})
            
        # Validate clinic exists if provided
        clinic_id = attrs.get('clinic_id')
        if clinic_id:
            try:
                Clinic.objects.get(id=clinic_id)
            except Clinic.DoesNotExist:
                raise serializers.ValidationError({"clinic_id": "Clinic with this ID does not exist."})
                
        return attrs
    
    def create(self, validated_data):
        specialty = validated_data.pop('specialty')
        clinic_id = validated_data.pop('clinic_id', None)
        
        # Call parent create to create the user
        user = super().create(validated_data)
        
        # Update the doctor profile with additional info
        doctor = Doctor.objects.get(user=user)
        doctor.specialty = specialty
        
        if clinic_id:
            clinic = Clinic.objects.get(id=clinic_id)
            doctor.clinic = clinic
            
        doctor.save()
        return user