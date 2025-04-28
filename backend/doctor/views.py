from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework import status

from core.models import Availability, User ,Doctor
from django.shortcuts import get_object_or_404
from rest_framework.permissions import AllowAny

from core.serializers import AvailabilitySerializer, DoctorSerializer
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

    return Response({"message": "Profile updated successfully"}, status=status.HTTP_200_OK)
 
 
 
 
@api_view(['POST'])
@permission_classes([AllowAny])
def create_doctor(request):
    if request.method == 'POST':
        serializer = DoctorSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            print("seriiiiiiiiiiii",serializer.errors)
            return Response({"message": "Doctor created successfully.", "doctor": serializer.errors}, status=status.HTTP_201_CREATED)
        return Response({"error": "Invalid data provided. Please check your input.", "errors": serializer.errors}, status=status.HTTP_400_BAD_REQUEST)

 

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_availability(request):
    """
    Get all availability slots for a specific doctor.
    """
    doctor_id = request.query_params.get('doctor_id')
    if not doctor_id:
        return Response({"error": "Doctor ID is required"}, status=status.HTTP_400_BAD_REQUEST)

    try:
        doctor = Doctor.objects.get(id=doctor_id)
    except Doctor.DoesNotExist:
        return Response({"error": "Doctor not found"}, status=status.HTTP_404_NOT_FOUND)

    availabilities = Availability.objects.filter(doctor_id=doctor)
    serializer = AvailabilitySerializer(availabilities, many=True)
    return Response(serializer.data)


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def create_availability(request):
    """
    Create a new availability slot for a doctor.
    """
    doctor_id = request.data.get('doctor_id')
    start_time = request.data.get('start_time')
    end_time = request.data.get('end_time')
    
    if not doctor_id or not start_time or not end_time:
        return Response({"error": "Doctor ID, start time, and end time are required"}, status=status.HTTP_400_BAD_REQUEST)

    try:
        doctor = Doctor.objects.get(id=doctor_id)
    except Doctor.DoesNotExist:
        return Response({"error": "Doctor not found"}, status=status.HTTP_404_NOT_FOUND)

    # Create the availability slot
    availability = Availability.objects.create(
        doctor_id=doctor,
        start_time=start_time,
        end_time=end_time,
        booked=False  # Default value, assuming the slot is not booked initially
    )

    serializer = AvailabilitySerializer(availability)
    return Response({"message": "Availability created successfully", "availability": serializer.data}, status=status.HTTP_201_CREATED)


@api_view(['PUT'])
@permission_classes([IsAuthenticated])
def update_availability(request, availability_id):
    """
    Update an availability slot (e.g., change time or booking status).
    """
    try:
        availability = Availability.objects.get(id=availability_id)
    except Availability.DoesNotExist:
        return Response({"error": "Availability slot not found"}, status=status.HTTP_404_NOT_FOUND)

    # Update fields
    availability.start_time = request.data.get('start_time', availability.start_time)
    availability.end_time = request.data.get('end_time', availability.end_time)
    availability.booked = request.data.get('booked', availability.booked)
    availability.save()

    serializer = AvailabilitySerializer(availability)
    return Response({"message": "Availability updated successfully", "availability": serializer.data}, status=status.HTTP_200_OK)


@api_view(['DELETE'])
@permission_classes([IsAuthenticated])
def delete_availability(request, availability_id):
    """
    Delete an availability slot.
    """
    try:
        availability = Availability.objects.get(id=availability_id)
    except Availability.DoesNotExist:
        return Response({"error": "Availability slot not found"}, status=status.HTTP_404_NOT_FOUND)

    availability.delete()
    return Response({"message": "Availability deleted successfully"}, status=status.HTTP_204_NO_CONTENT)
 




