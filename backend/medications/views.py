# medications/views.py
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from core.models import Medication
from core.serializers import MedicationSerializer

class MedicationViewSet(viewsets.ModelViewSet):
    queryset = Medication.objects.all()
    serializer_class = MedicationSerializer
    permission_classes = [IsAuthenticated]