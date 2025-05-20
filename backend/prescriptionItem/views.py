# prescriptionItem/views.py

from rest_framework import viewsets, status
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from core.models import PrescriptionItem, Doctor, Patient
from core.serializers import PrescriptionItemSerializer

class PrescriptionItemViewSet(viewsets.ModelViewSet):
    """
    ViewSet for managing prescription items.

    Provides standard CRUD operations on PrescriptionItem instances.
    Access is restricted to authenticated users. When a prescription item is
    created, it automatically assigns the current user as the prescribing doctor,
    if a corresponding doctor profile exists.
    """
    queryset = PrescriptionItem.objects.all()
    serializer_class = PrescriptionItemSerializer
    permission_classes = [IsAuthenticated]
    
    def perform_create(self, serializer):
        """
        Assigns the logged-in doctor to the 'prescribed_by' field during creation.

        Args:
            serializer (PrescriptionItemSerializer): Serializer instance to be saved.
        """
        doctor = Doctor.objects.filter(user=self.request.user).first()
        if doctor:
            serializer.save(prescribed_by=doctor)
        else:
            serializer.save()
