from django.shortcuts import render

# Create your views here.
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from core.models import Doctor
from doctor.views import get_doctor_by_id

@api_view(['POST'])
def rate_doctor(request, doctor_id):
    try:
        doctor = Doctor.objects.get(id=doctor_id)
    except Doctor.DoesNotExist:
        return Response({"error": "Doctor not found."}, status=status.HTTP_404_NOT_FOUND)

    grade = request.data.get('grade')
    if grade is None:
        return Response({"error": "Grade is required."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        grade = float(grade)
        if grade < 0 or grade > 5:
            return Response({"error": "Grade must be between 0 and 5."}, status=status.HTTP_400_BAD_REQUEST)
    except ValueError:
        return Response({"error": "Invalid grade value."}, status=status.HTTP_400_BAD_REQUEST)

    doctor.grade = grade
    doctor.save()

    return Response({"message": "Doctor rated successfully.", "grade": doctor.grade}, status=status.HTTP_200_OK)





from core.models import Patient, Feedback, Doctor
from datetime import date, datetime

@api_view(['POST'])
def submit_feedback(request, doctor_id):
    try:
        doctor = Doctor.objects.get(id=doctor_id)
    except Doctor.DoesNotExist:
        return Response({"error": "Doctor not found."}, status=status.HTTP_404_NOT_FOUND)

    patient_id = request.data.get('patient_id')  # You could also use request.user if authenticated
    title = request.data.get('title')
    description = request.data.get('description')

    if not (patient_id and title and description):
        return Response({"error": "All fields are required."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        patient = Patient.objects.get(id=patient_id)
    except Patient.DoesNotExist:
        return Response({"error": "Patient not found."}, status=status.HTTP_404_NOT_FOUND)

    feedback = Feedback.objects.create(
        title=title,
        description=description,
        patient=patient,
        doctor=doctor,
        date_creation=date.today(),
        time_creation=datetime.now().time()
    )

    return Response({"message": "Feedback submitted successfully."}, status=status.HTTP_201_CREATED)




from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from core.models import Doctor, Feedback

@api_view(['GET'])
def get_doctor_feedback(request, doctor_id):
    try:
        doctor = Doctor.objects.get(id=doctor_id)
    except Doctor.DoesNotExist:
        return Response({"error": "Doctor not found."}, status=status.HTTP_404_NOT_FOUND)

    feedbacks = Feedback.objects.filter(doctor=doctor).values(
        'id', 'title', 'description', 'date_creation', 'time_creation', 'patient__user__name'
    )

    return Response(list(feedbacks), status=status.HTTP_200_OK)
