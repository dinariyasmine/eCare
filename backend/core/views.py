from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import Appointment, Doctor, Patient, Notification, Feedback, Prescription
from .serializers import AppointmentSerializer, DoctorSerializer, PatientSerializer, NotificationSerializer, FeedbackSerializer, PrescriptionSerializer
from django.http import JsonResponse
from supabase import create_client
import os
from django.conf import settings
from supabase import create_client

supabase = create_client(settings.SUPABASE_URL, settings.SUPABASE_KEY)
def get_users(request):
    # Query the "user" table
    response = supabase.table('core_user').select('*').execute()

    # Return the response data as JSON
    return JsonResponse(response.data, safe=False)


