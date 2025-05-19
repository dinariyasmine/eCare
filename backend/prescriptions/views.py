from django.shortcuts import render

# Create your views here.
from rest_framework import viewsets
from rest_framework.permissions import AllowAny
from core.models import Prescription
from core.serializers import PrescriptionSerializer
from rest_framework.response import Response
from rest_framework.decorators import action
from rest_framework.permissions import IsAuthenticated
from core.models import Prescription, Patient

class PrescriptionViewSet(viewsets.ModelViewSet):
    queryset = Prescription.objects.all()
    serializer_class = PrescriptionSerializer
    permission_classes = [AllowAny]  
    @action(detail=False, methods=['get'], url_path='prescription-history/(?P<patient_id>\d+)')
    def prescription_history(self, request, patient_id=None):
        try:
            # Fetch the patient instance using the patient_id
            patient = Patient.objects.get(id=patient_id)
        except Patient.DoesNotExist:
            return Response({"detail": "Patient not found"}, status=404)

        # Fetch prescriptions for the patient
        prescriptions = Prescription.objects.filter(patient=patient).order_by('-date')

        # Serialize the prescription data, including prescription items and medications
        serializer = PrescriptionSerializer(prescriptions, many=True)
        return Response(serializer.data)
