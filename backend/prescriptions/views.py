# prescriptions/views.py

from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from django.http import HttpResponse
from core.models import Prescription, PrescriptionItem, Medication, Doctor, Patient
from core.serializers import PrescriptionSerializer, PrescriptionItemSerializer, MedicationSerializer
import io
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import letter
from django.utils import timezone

class PrescriptionViewSet(viewsets.ModelViewSet):
    """
    ViewSet for managing prescriptions.

    This class provides standard CRUD operations for prescriptions via the ModelViewSet,
    as well as custom actions for adding medications and generating a PDF version
    of the prescription. Access is restricted to authenticated users.

    Custom Actions:
    - add_medication: Adds a medication item to an existing prescription.
    - generate_pdf: Generates and returns a PDF document of the prescription.
    """
    queryset = Prescription.objects.all()
    serializer_class = PrescriptionSerializer
    permission_classes = [IsAuthenticated]
    
    def get_queryset(self):
        """
        Filters the prescription list based on whether the user is a doctor or a patient.
        
        Returns:
            QuerySet: Filtered prescriptions associated with the current user's profile.
        """
        user = self.request.user
        if hasattr(user, 'doctor_profile') and user.doctor_profile.exists():
            return Prescription.objects.filter(doctor__in=user.doctor_profile.all()).order_by('-date')
        elif hasattr(user, 'patient_profile') and user.patient_profile.exists():
            return Prescription.objects.filter(patient__in=user.patient_profile.all()).order_by('-date')
        return Prescription.objects.all().order_by('-date')
    
    def perform_create(self, serializer):
        """
        Automatically assigns the logged-in doctor to the prescription upon creation.
        
        Args:
            serializer (PrescriptionSerializer): Serializer instance to be saved.
        """
        doctor = Doctor.objects.filter(user=self.request.user).first()
        if doctor:
            serializer.save(doctor=doctor)
        else:
            serializer.save()
    
    @action(detail=True, methods=['post'])
    def add_medication(self, request, pk=None):
        """
        Add a medication to a specific prescription.

        This custom action allows a doctor to append a medication item to an existing prescription.

        Request Data:
            medication_id (int): ID of the medication.
            dosage (str): Dosage instructions (e.g., "1 capsule (250mg)").
            duration (str): Duration of the medication course (e.g., "14 days").
            frequency (str): Frequency of intake (e.g., "once_daily").
            instructions (str, optional): Any additional instructions for the patient.

        Returns:
            Response: Serialized PrescriptionItem if successful, otherwise error message.
        """
        prescription = self.get_object()
        
        # Get data from request
        medication_id = request.data.get('medication_id')
        dosage = request.data.get('dosage')
        duration = request.data.get('duration')
        frequency = request.data.get('frequency')
        instructions = request.data.get('instructions', '')
        
        if not all([medication_id, dosage, duration, frequency]):
            return Response(
                {'error': 'medication_id, dosage, duration, and frequency are required'}, 
                status=status.HTTP_400_BAD_REQUEST
            )
        
        try:
            medication = Medication.objects.get(id=medication_id)
            doctor = Doctor.objects.filter(user=self.request.user).first()
            
            # Create prescription item
            prescription_item = PrescriptionItem.objects.create(
                prescription=prescription,
                medication=medication,
                dosage=dosage,
                duration=duration,
                frequency=frequency,
                instructions=instructions,
                prescribed_by=doctor,
                prescribed_to=prescription.patient
            )
            
            return Response(
                PrescriptionItemSerializer(prescription_item).data,
                status=status.HTTP_201_CREATED
            )
        except Medication.DoesNotExist:
            return Response(
                {'error': 'Medication not found'}, 
                status=status.HTTP_404_NOT_FOUND
            )
    
    @action(detail=True, methods=['get'])
    def generate_pdf(self, request, pk=None):
        """
        Generate and return a PDF representation of the prescription.

        This action creates a formatted PDF containing prescription details,
        including patient and doctor names and all prescribed medications.
        The PDF is saved to the prescription model and returned in the response.

        Returns:
            HttpResponse: PDF file as a downloadable attachment.
        """
        prescription = self.get_object()
        
        # Create a file-like buffer to receive PDF data
        buffer = io.BytesIO()
        
        # Create the PDF object, using the buffer as its "file"
        p = canvas.Canvas(buffer, pagesize=letter)
        
        # Draw the prescription content
        p.drawString(100, 750, f"Prescription Date: {prescription.date.strftime('%B %d, %Y')}")
        p.drawString(100, 730, f"Patient: {prescription.patient.user.get_full_name()}")
        p.drawString(100, 710, f"Doctor: {prescription.doctor.user.get_full_name()}")
        
        y_position = 670
        for i, item in enumerate(prescription.items.all()):
            p.drawString(100, y_position, f"Medication {i+1}: {item.medication.name}")
            p.drawString(120, y_position-20, f"Dosage: {item.dosage}")
            p.drawString(120, y_position-40, f"Duration: {item.duration}")
            p.drawString(120, y_position-60, f"Frequency: {item.get_frequency_display()}")
            p.drawString(120, y_position-80, f"Instructions: {item.instructions}")
            y_position -= 120
        
        # Close the PDF object cleanly
        p.showPage()
        p.save()
        
        # Get the value of the BytesIO buffer and save it to the prescription
        pdf_file = buffer.getvalue()
        buffer.close()
        
        # Save the PDF to the prescription model
        prescription.pdf_file.save(f"prescription_{prescription.id}.pdf", io.BytesIO(pdf_file))
        
        # Create the HTTP response with PDF content
        response = HttpResponse(content_type='application/pdf')
        response['Content-Disposition'] = f'attachment; filename="prescription_{prescription.id}.pdf"'
        response.write(pdf_file)
        
        return response
