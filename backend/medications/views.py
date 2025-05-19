from django.shortcuts import render
from rest_framework import viewsets
from rest_framework.permissions import AllowAny
from core.models import Medication
from core.serializers import MedicationSerializer  # Import the existing MedicationSerializer from core
from rest_framework.permissions import IsAuthenticated

class MedicationViewSet(viewsets.ModelViewSet):
    queryset = Medication.objects.all()
    serializer_class = MedicationSerializer 
    permission_classes = [AllowAny] 
