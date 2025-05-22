from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework import status

from core.models import Availability, SocialMedia, User ,Doctor
from django.shortcuts import get_object_or_404
from rest_framework.permissions import AllowAny

from core.serializers import AvailabilitySerializer, DoctorSerializer, SocialMediaSerializer
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_profile(request):
    user = request.user
    data = {
        "id": user.id,
        "username": user.username,
        "name": user.name,
        "email": user.email,
        "phone": user.phone,
        "address": user.address,
        "role": user.role,
        "birth_date": user.birth_date,
    }
    # If user is a doctor, add photo from doctor profile
    if user.role == 'doctor':
        try:
            doctor = Doctor.objects.get(user=user)
            data['photo'] = doctor.photo
        except Doctor.DoesNotExist:
            pass
    return Response(data)

@api_view(['PUT'])
@permission_classes([IsAuthenticated])
def update_profile(request):
    user = request.user
    data = request.data

    user.name = data.get('name', user.name)
    user.email = data.get('email', user.email)
    user.phone = data.get('phone', user.phone)
    user.address = data.get('address', user.address)
    user.birth_date = data.get('birth_date', user.birth_date)
    user.save()

    # If user is a doctor, update photo in doctor profile
    if user.role == 'doctor' and 'photo' in data:
        try:
            doctor = Doctor.objects.get(user=user)
            doctor.photo = data['photo']
            doctor.save()
        except Doctor.DoesNotExist:
            pass

    return Response({"message": "Profile updated successfully"}, status=status.HTTP_200_OK)
 
 
 
 
@api_view(['POST'])
@permission_classes([AllowAny])
def create_doctor(request):
    if request.method == 'POST':
        try:
            # First validate the user data
            user_data = request.data.get('user', {})
            if not user_data:
                return Response({"error": "User data is required"}, status=status.HTTP_400_BAD_REQUEST)

            # Check if user already exists
            try:
                existing_user = User.objects.get(email=user_data.get('email'))
                return Response({"error": "User with this email already exists"}, status=status.HTTP_400_BAD_REQUEST)
            except User.DoesNotExist:
                pass

            # Create the user first
            user = User.objects.create(
                username=user_data.get('username'),
                email=user_data.get('email'),
                name=user_data.get('name'),
                phone=user_data.get('phone'),
                address=user_data.get('address'),
                role='doctor',
                birth_date=user_data.get('birth_date')
            )

            # Now create the doctor with the user reference
            doctor_data = request.data.copy()
            doctor_data['user'] = user.id

            serializer = DoctorSerializer(data=doctor_data)
            if serializer.is_valid():
                doctor = serializer.save()
                return Response({
                    "message": "Doctor created successfully.",
                    "doctor": {
                        "id": doctor.id,
                        "user_id": user.id,
                        "specialty": doctor.specialty
                    }
                }, status=status.HTTP_201_CREATED)
            else:
                # If doctor creation fails, delete the user
                user.delete()
                return Response({
                    "error": "Invalid doctor data provided.",
                    "errors": serializer.errors
                }, status=status.HTTP_400_BAD_REQUEST)

        except Exception as e:
            print(f"Error creating doctor: {str(e)}")
            return Response({
                "error": f"Error creating doctor: {str(e)}"
            }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

 

# @api_view(['GET'])
# @permission_classes([IsAuthenticated])
# def get_availability(request):
#     """
#     Get all availability slots for a specific doctor.
#     """
#     doctor_id = request.query_params.get('doctor_id')
#     if not doctor_id:
#         return Response({"error": "Doctor ID is required"}, status=status.HTTP_400_BAD_REQUEST)

#     try:
#         doctor = Doctor.objects.get(id=doctor_id)
#     except Doctor.DoesNotExist:
#         return Response({"error": "Doctor not found"}, status=status.HTTP_404_NOT_FOUND)

#     availabilities = Availability.objects.filter(doctor_id=doctor)
#     serializer = AvailabilitySerializer(availabilities, many=True)
#     return Response(serializer.data)


# @api_view(['POST'])
# @permission_classes([IsAuthenticated])
# def create_availability(request):
#     """
#     Create a new availability slot for a doctor.
#     """
#     doctor_id = request.data.get('doctor_id')
#     start_time = request.data.get('start_time')
#     end_time = request.data.get('end_time')
    
#     if not doctor_id or not start_time or not end_time:
#         return Response({"error": "Doctor ID, start time, and end time are required"}, status=status.HTTP_400_BAD_REQUEST)

#     try:
#         doctor = Doctor.objects.get(id=doctor_id)
#     except Doctor.DoesNotExist:
#         return Response({"error": "Doctor not found"}, status=status.HTTP_404_NOT_FOUND)

#     # Create the availability slot
#     availability = Availability.objects.create(
#         doctor_id=doctor,
#         start_time=start_time,
#         end_time=end_time,
#         booked=False  # Default value, assuming the slot is not booked initially
#     )

#     serializer = AvailabilitySerializer(availability)
#     return Response({"message": "Availability created successfully", "availability": serializer.data}, status=status.HTTP_201_CREATED)


# @api_view(['PUT'])
# @permission_classes([IsAuthenticated])
# def update_availability(request, availability_id):
#     """
#     Update an availability slot (e.g., change time or booking status).
#     """
#     try:
#         availability = Availability.objects.get(id=availability_id)
#     except Availability.DoesNotExist:
#         return Response({"error": "Availability slot not found"}, status=status.HTTP_404_NOT_FOUND)

#     # Update fields
#     availability.start_time = request.data.get('start_time', availability.start_time)
#     availability.end_time = request.data.get('end_time', availability.end_time)
#     availability.booked = request.data.get('booked', availability.booked)
#     availability.save()

#     serializer = AvailabilitySerializer(availability)
#     return Response({"message": "Availability updated successfully", "availability": serializer.data}, status=status.HTTP_200_OK)


# @api_view(['DELETE'])
# @permission_classes([IsAuthenticated])
# def delete_availability(request, availability_id):
#     """
#     Delete an availability slot.
#     """
#     try:
#         availability = Availability.objects.get(id=availability_id)
#     except Availability.DoesNotExist:
#         return Response({"error": "Availability slot not found"}, status=status.HTTP_404_NOT_FOUND)

#     availability.delete()
#     return Response({"message": "Availability deleted successfully"}, status=status.HTTP_204_NO_CONTENT)
 



@api_view(['GET'])
@permission_classes([AllowAny])
def get_doctors(request):
    """
    Fetch a list of all doctors along with their user fields.
    """
    try:
        doctors = Doctor.objects.select_related('user', 'clinic').all()
        print(f"Total doctors found: {doctors.count()}")
        
        doctor_data = []
        invalid_doctors = []

        for doctor in doctors:
            print(f"Processing doctor {doctor.id} - Photo URL: {doctor.photo}")  # Debug log
            
            if not doctor.user:
                print(f"Doctor {doctor.id} has no user record")
                invalid_doctors.append(doctor.id)
                continue
                
            if not doctor.user.id:
                print(f"Doctor {doctor.id} has invalid user ID")
                invalid_doctors.append(doctor.id)
                continue

            try:
                user = User.objects.get(id=doctor.user.id)
                if not user:
                    print(f"User {doctor.user.id} not found in database")
                    invalid_doctors.append(doctor.id)
                    continue
            except User.DoesNotExist:
                print(f"User {doctor.user.id} does not exist in database")
                invalid_doctors.append(doctor.id)
                continue
                
            doctor_info = {
                "id": doctor.id,
                "name": doctor.user.name,
                "email": doctor.user.email,
                "phone": doctor.user.phone,
                "address": doctor.user.address,
                "role": doctor.user.role,
                "birth_date": doctor.user.birth_date,
                "photo": doctor.photo,
                "specialty": doctor.specialty,
                "clinic": doctor.clinic.name if doctor.clinic else None,
                "clinic_pos" : doctor.clinic.map_location if doctor.clinic else None,
                "grade": doctor.grade,
                "description": doctor.description,
                "nbr_patients": doctor.nbr_patients,
            }
            print(f"Doctor info being sent: {doctor_info}")  # Debug log
            doctor_data.append(doctor_info)

        if invalid_doctors:
            print(f"Found {len(invalid_doctors)} invalid doctors with IDs: {invalid_doctors}")
            
        return Response({
            "doctors": doctor_data,
            "total_doctors": len(doctor_data),
            "invalid_doctors_count": len(invalid_doctors)
        }, status=status.HTTP_200_OK)
    except Exception as e:
        print(f"Error in get_doctors: {str(e)}")
        return Response({"error": f"Error fetching doctors: {str(e)}"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


@api_view(['GET'])
@permission_classes([AllowAny])
def get_doctor_by_id(request, doctor_id):
    """
    Get details of a single doctor by their ID.
    """
    try:
        doctor = Doctor.objects.get(user_id=doctor_id)
        print(f"Getting doctor {doctor.id} - Photo URL: {doctor.photo}")  # Debug log
    except Doctor.DoesNotExist:
        return Response({"error": "Doctor not found"}, status=status.HTTP_404_NOT_FOUND)

    # Retrieve social media links for this doctor
    social_links = {
        "instagram": None,
        "facebook": None,
        "linkedin": None
    }

    for sm in doctor.social_media.all():
        platform = sm.name.lower()
        if "instagram" in platform:
            social_links["instagram"] = sm.link
        elif "facebook" in platform:
            social_links["facebook"] = sm.link
        elif "linkedin" in platform:
            social_links["linkedin"] = sm.link

    doctor_info = {
        "id": doctor.id,
        "name": doctor.user.name,
        "email": doctor.user.email,
        "phone": doctor.user.phone,
        "address": doctor.user.address,
        "role": doctor.user.role,
        "birth_date": doctor.user.birth_date,
        "photo": doctor.photo,
        "specialty": doctor.specialty,
        "clinic": doctor.clinic.name if doctor.clinic else None,
        "grade": doctor.grade,
        "description": doctor.description,
        "nbr_patients": doctor.nbr_patients,
        "social_links": social_links  
    }
    print(f"Doctor info being sent: {doctor_info}")  # Debug log
    return Response({"doctor": doctor_info}, status=status.HTTP_200_OK)

from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from core.models import Patient

@api_view(['GET'])
@permission_classes([AllowAny])
def get_patient_by_id(request, patient_id):
    try:
        patient = Patient.objects.select_related('user').get(user_id=patient_id)
        user = patient.user
        data = {
            "id": patient.id,
            "name": user.name,
            "email": user.email,
            "phone": user.phone,
            "address": user.address,
            "role": user.role,
            "birth_date": user.birth_date.strftime('%Y-%m-%d') if user.birth_date else None,
        }
        return Response(data, status=status.HTTP_200_OK)
    except Patient.DoesNotExist:
        return Response({"error": "Patient not found"}, status=status.HTTP_404_NOT_FOUND)

from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from rest_framework import status
from core.models import Doctor, User

@api_view(['PUT'])
@permission_classes([AllowAny])
def update_doctor_by_id(request, doctor_id):
    try:
        doctor = Doctor.objects.select_related('user').get(user_id=doctor_id)
        print(f"Updating doctor {doctor.id} - Current photo: {doctor.photo}")  # Debug log
        print(f"Request data: {request.data}")  # Debug log
    except Doctor.DoesNotExist:
        return Response({"error": "Doctor not found"}, status=status.HTTP_404_NOT_FOUND)

    user = doctor.user
    data = request.data

    # Update user fields
    for field in ['name', 'email', 'phone', 'address', 'role', 'birth_date']:
        if field in data:
            setattr(user, field, data[field])
    user.save()

    # Update doctor-specific fields
    if 'photo' in data:
        doctor.photo = data['photo']
        print(f"Setting photo to: {data['photo']}")  # Debug log
    
    for field in ['specialty', 'grade', 'description', 'nbr_patients']:
        if field in data:
            setattr(doctor, field, data[field])
    
    doctor.save()
    print(f"Updated doctor photo: {doctor.photo}")  # Debug log

    # Return the updated doctor data
    doctor_info = {
        "id": doctor.id,
        "name": doctor.user.name,
        "email": doctor.user.email,
        "phone": doctor.user.phone,
        "address": doctor.user.address,
        "role": doctor.user.role,
        "birth_date": doctor.user.birth_date,
        "photo": doctor.photo,
        "specialty": doctor.specialty,
        "clinic": doctor.clinic.name if doctor.clinic else None,
        "grade": doctor.grade,
        "description": doctor.description,
        "nbr_patients": doctor.nbr_patients,
    }
    
    return Response({
        "message": "Doctor updated successfully",
        "doctor": doctor_info
    }, status=status.HTTP_200_OK)


from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from rest_framework import status
from core.models import Patient, User

@api_view(['PUT'])
@permission_classes([AllowAny])
def update_patient_by_id(request, patient_id):
    try:
        patient = Patient.objects.select_related('user').get(user_id=patient_id)
    except Patient.DoesNotExist:
        return Response({"error": "Patient not found"}, status=status.HTTP_404_NOT_FOUND)

    user = patient.user
    data = request.data

    # Update user fields
    for field in ['name', 'email', 'phone', 'address', 'role', 'birth_date']:
        if field in data:
            setattr(user, field, data[field])
    user.save()

    return Response({"message": "Patient updated successfully"}, status=status.HTTP_200_OK)


@api_view(['POST'])
def create_social_media(request):
    """
    Create a new social media profile for a doctor
    Requires doctor_id, name, and link
    """
    try:
        doctor_id = request.data.get('doctor_id')
        name = request.data.get('name')
        link = request.data.get('link')
        
        # Check if all required fields are provided
        if not all([doctor_id, name, link]):
            return Response(
                {"error": "doctor_id, name, and link are required fields"}, 
                status=status.HTTP_400_BAD_REQUEST
            )
        
        # Check if the doctor exists
        try:
            doctor = Doctor.objects.get(id=doctor_id)
        except Doctor.DoesNotExist:
            return Response(
                {"error": f"Doctor with ID {doctor_id} does not exist"}, 
                status=status.HTTP_404_NOT_FOUND
            )
        
        # Create the social media profile
        social_media = SocialMedia.objects.create(
            doctor_id=doctor,
            name=name,
            link=link
        )
        
        # Serialize and return the created object
        serializer = SocialMediaSerializer(social_media)
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    
    except Exception as e:
        return Response(
            {"error": str(e)}, 
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )
