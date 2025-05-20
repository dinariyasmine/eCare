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

from reportlab.lib import colors
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.lib.enums import TA_RIGHT, TA_CENTER
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, HRFlowable
from notifications.services import NotificationService

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
<<<<<<< HEAD
        Automatically assigns the logged-in doctor to the prescription upon creation.
        
        Args:
            serializer (PrescriptionSerializer): Serializer instance to be saved.
=======
        Automatically assigns the logged-in doctor to the prescription upon creation
        and marks the related appointment as done.
>>>>>>> notifications
        """
        doctor = Doctor.objects.filter(user=self.request.user).first()
        if doctor:
            prescription = serializer.save(doctor=doctor)
        else:
            prescription = serializer.save()
            
<<<<<<< HEAD
        # Create notification for the patient
=======
        # Mark related appointment as done if it exists
        patient = prescription.patient
        if patient:
            # Find the most recent in-progress appointment for this patient with this doctor
            appointment = Appointment.objects.filter(
                patient=patient,
                doctor=doctor,
                status='in_progress'
            ).order_by('-start_time').first()
            
            if appointment:
                appointment.status = 'done'
                appointment.save()
                
                # Create notification for appointment completed
                NotificationService.create_appointment_notification(
                    appointment=appointment,
                    notification_type='appointment_completed'
                )
        
        # Create notification for the patient about new prescription
>>>>>>> notifications
        if prescription.patient and prescription.patient.user:
            NotificationService.create_prescription_notification(
                prescription=prescription,
                notification_type='prescription_created'
            )
<<<<<<< HEAD
        
=======
   
>>>>>>> notifications
    def update(self, request, *args, **kwargs):
        """Override update to send notification when prescription is updated"""
        partial = kwargs.pop('partial', False)
        instance = self.get_object()
        serializer = self.get_serializer(instance, data=request.data, partial=partial)
        serializer.is_valid(raise_exception=True)
        prescription = serializer.save()
        
        # Create notification for prescription update
        NotificationService.create_prescription_notification(
            prescription=prescription,
            notification_type='prescription_updated'
        )
        
        return Response(serializer.data)
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
            
            NotificationService.create_prescription_notification(
                prescription=prescription,
                notification_type='prescription_updated'
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

        This action creates a professionally formatted PDF containing prescription details,
        including patient and doctor information and all prescribed medications.
        The PDF is saved to the prescription model and returned in the response.

        Returns:
            HttpResponse: PDF file as a downloadable attachment.
        """
        prescription = self.get_object()
        
        # Create a file-like buffer to receive PDF data
        buffer = io.BytesIO()
        
        # Create the PDF object, using the buffer as its "file"
        doc = SimpleDocTemplate(buffer, pagesize=letter, 
                            rightMargin=72, leftMargin=72,
                            topMargin=72, bottomMargin=72)
        
        # Container for the 'Flowable' objects
        elements = []
        
        # Define styles
        styles = getSampleStyleSheet()
        title_style = styles['Heading1']
        subtitle_style = styles['Heading2']
        normal_style = styles['Normal']
        
        # Create custom styles
        label_style = ParagraphStyle(
            'Label',
            parent=styles['Heading4'],
            textColor=colors.navy,
            spaceAfter=2
        )
        value_style = ParagraphStyle(
            'Value', 
            parent=styles['Normal'],
            leftIndent=20,
            spaceAfter=12
        )
        medication_style = ParagraphStyle(
            'Medication',
            parent=styles['Heading3'],
            textColor=colors.darkblue,
            spaceBefore=10
        )
        
        # Add clinic/hospital header
        clinic_name = "MEDICAL CENTER & HEALTHCARE CLINIC"
        clinic_address = "123 Healthcare Avenue, Medical District"
        clinic_contact = "Phone: (555) 123-4567 | Email: care@medicalcenter.com"
        
        elements.append(Paragraph(clinic_name, title_style))
        elements.append(Paragraph(clinic_address, normal_style))
        elements.append(Paragraph(clinic_contact, normal_style))
        elements.append(HRFlowable(width="100%", thickness=1, color=colors.black, spaceBefore=10, spaceAfter=10))
        
        # Add prescription title
        elements.append(Paragraph("MEDICAL PRESCRIPTION", subtitle_style))
        elements.append(Spacer(1, 12))
        
        # Format date nicely
        prescription_date = prescription.date.strftime('%B %d, %Y')
        
                # Create prescription info table
        data = [
            [Paragraph("<b>Prescription ID:</b>", label_style), 
            Paragraph(f"#{str(prescription.id)}", value_style)],
            [Paragraph("<b>Date:</b>", label_style), 
            Paragraph(prescription_date, value_style)],
            [Paragraph("<b>Patient Name:</b>", label_style), 
            Paragraph(prescription.patient.user.get_full_name(), value_style)],
            [Paragraph("<b>Patient ID:</b>", label_style), 
            Paragraph(f"#{str(prescription.patient.id)}", value_style)],
            [Paragraph("<b>Doctor:</b>", label_style), 
            Paragraph(f"Dr. {prescription.doctor.user.get_full_name()}", value_style)],
        ]
        
        table = Table(data, colWidths=[120, 350])
        table.setStyle(TableStyle([
            ('VALIGN', (0, 0), (-1, -1), 'TOP'),
            ('TOPPADDING', (0, 0), (-1, -1), 2),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
        ]))
        
        elements.append(table)
        elements.append(Spacer(1, 20))
        
        # Add medication header
        elements.append(Paragraph("PRESCRIBED MEDICATIONS", subtitle_style))
        elements.append(HRFlowable(width="100%", thickness=1, lineCap='round', color=colors.darkblue, 
                          spaceBefore=5, spaceAfter=10))

        
        # Add each medication with styled table
        for i, item in enumerate(prescription.items.all()):
            # Add medication name with number
            elements.append(Paragraph(f"{i+1}. {item.medication.name}", medication_style))
            
            # Create medication details table
            med_data = [
                [Paragraph("<b>Dosage:</b>", label_style), 
                Paragraph(str(item.dosage), value_style)],
                [Paragraph("<b>Frequency:</b>", label_style), 
                Paragraph(str(item.get_frequency_display()), value_style)],
                [Paragraph("<b>Duration:</b>", label_style), 
                Paragraph(str(item.duration), value_style)],
                [Paragraph("<b>Instructions:</b>", label_style), 
                Paragraph(str(item.instructions), value_style)],
            ]
            
            med_table = Table(med_data, colWidths=[120, 350])
            med_table.setStyle(TableStyle([
                ('VALIGN', (0, 0), (-1, -1), 'TOP'),
                ('TOPPADDING', (0, 0), (-1, -1), 2),
                ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
                ('LEFTPADDING', (0, 0), (0, -1), 20),
            ]))
            
            elements.append(med_table)
            
            # Add a separator between medications except for the last one
            if i < len(prescription.items.all()) - 1:
                elements.append(HRFlowable(
                    width="80%", 
                    thickness=0.5, 
                    color=colors.lightgrey, 
                    spaceBefore=10, 
                    spaceAfter=10
                ))
        
        # Add doctor signature section
        elements.append(Spacer(1, 30))
        elements.append(HRFlowable(width="40%", thickness=1, color=colors.black, 
                                spaceBefore=10, spaceAfter=2, hAlign='RIGHT'))
        elements.append(Paragraph(f"Dr. {prescription.doctor.user.get_full_name()}", 
                            ParagraphStyle('Signature', parent=normal_style, alignment=TA_RIGHT)))
        elements.append(Paragraph("Physician Signature", 
                            ParagraphStyle('SignatureLabel', parent=normal_style, 
                                        alignment=TA_RIGHT, textColor=colors.grey)))
        
        # Add footer with additional information
        elements.append(Spacer(1, 40))
        footer_text = """
        <para fontSize="8" alignment="center" textColor="grey">
        This prescription is valid for 30 days from the date of issue.
        Please present this document to your pharmacist. Contact your doctor for any clarifications.
        </para>
        """
        elements.append(Paragraph(footer_text, styles['Normal']))
        
        # Build the PDF document
        doc.build(elements)
        
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
        """
        Generate and return a PDF representation of the prescription.

        This action creates a professionally formatted PDF containing prescription details,
        including patient and doctor information and all prescribed medications.
        The PDF is saved to the prescription model and returned in the response.

        Returns:
            HttpResponse: PDF file as a downloadable attachment.
        """
        prescription = self.get_object()
        
        # Create a file-like buffer to receive PDF data
        buffer = io.BytesIO()
        
        # Create the PDF object, using the buffer as its "file"
        doc = SimpleDocTemplate(buffer, pagesize=letter, 
                            rightMargin=72, leftMargin=72,
                            topMargin=72, bottomMargin=72)
        
        # Container for the 'Flowable' objects
        elements = []
        
        # Define styles
        styles = getSampleStyleSheet()
        title_style = styles['Heading1']
        subtitle_style = styles['Heading2']
        normal_style = styles['Normal']
        
        # Create custom styles
        label_style = ParagraphStyle(
            'Label',
            parent=styles['Heading4'],
            textColor=colors.navy,
            spaceAfter=2
        )
        value_style = ParagraphStyle(
            'Value', 
            parent=styles['Normal'],
            leftIndent=20,
            spaceAfter=12
        )
        medication_style = ParagraphStyle(
            'Medication',
            parent=styles['Heading3'],
            textColor=colors.darkblue,
            spaceBefore=10
        )
        
        # Add clinic/hospital header
        clinic_name = "MEDICAL CENTER & HEALTHCARE CLINIC"
        clinic_address = "123 Healthcare Avenue, Medical District"
        clinic_contact = "Phone: (555) 123-4567 | Email: care@medicalcenter.com"
        
        elements.append(Paragraph(clinic_name, title_style))
        elements.append(Paragraph(clinic_address, normal_style))
        elements.append(Paragraph(clinic_contact, normal_style))
        elements.append(HRFlowable(width="100%", thickness=1, color=colors.black, spaceBefore=10, spaceAfter=10))
        
        # Add prescription title
        elements.append(Paragraph("MEDICAL PRESCRIPTION", subtitle_style))
        elements.append(Spacer(1, 12))
        
        # Format date nicely
        prescription_date = prescription.date.strftime('%B %d, %Y')
        
                # Create prescription info table
        data = [
            [Paragraph("<b>Prescription ID:</b>", label_style), 
            Paragraph(f"#{str(prescription.id)}", value_style)],
            [Paragraph("<b>Date:</b>", label_style), 
            Paragraph(prescription_date, value_style)],
            [Paragraph("<b>Patient Name:</b>", label_style), 
            Paragraph(prescription.patient.user.get_full_name(), value_style)],
            [Paragraph("<b>Patient ID:</b>", label_style), 
            Paragraph(f"#{str(prescription.patient.id)}", value_style)],
            [Paragraph("<b>Doctor:</b>", label_style), 
            Paragraph(f"Dr. {prescription.doctor.user.get_full_name()}", value_style)],
        ]
        
        table = Table(data, colWidths=[120, 350])
        table.setStyle(TableStyle([
            ('VALIGN', (0, 0), (-1, -1), 'TOP'),
            ('TOPPADDING', (0, 0), (-1, -1), 2),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
        ]))
        
        elements.append(table)
        elements.append(Spacer(1, 20))
        
        # Add medication header
        elements.append(Paragraph("PRESCRIBED MEDICATIONS", subtitle_style))
        elements.append(HRFlowable(width="100%", thickness=1, lineCap=1, color=colors.darkblue, 
                                spaceBefore=5, spaceAfter=10))
        
        # Add each medication with styled table
        for i, item in enumerate(prescription.items.all()):
            # Add medication name with number
            elements.append(Paragraph(f"{i+1}. {item.medication.name}", medication_style))
            
            # Create medication details table
            med_data = [
                [Paragraph("<b>Dosage:</b>", label_style), 
                Paragraph(str(item.dosage), value_style)],
                [Paragraph("<b>Frequency:</b>", label_style), 
                Paragraph(str(item.get_frequency_display()), value_style)],
                [Paragraph("<b>Duration:</b>", label_style), 
                Paragraph(str(item.duration), value_style)],
                [Paragraph("<b>Instructions:</b>", label_style), 
                Paragraph(str(item.instructions), value_style)],
            ]
            
            med_table = Table(med_data, colWidths=[120, 350])
            med_table.setStyle(TableStyle([
                ('VALIGN', (0, 0), (-1, -1), 'TOP'),
                ('TOPPADDING', (0, 0), (-1, -1), 2),
                ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
                ('LEFTPADDING', (0, 0), (0, -1), 20),
            ]))
            
            elements.append(med_table)
            
            # Add a separator between medications except for the last one
            if i < len(prescription.items.all()) - 1:
                elements.append(HRFlowable(
                    width="80%", 
                    thickness=0.5, 
                    color=colors.lightgrey, 
                    spaceBefore=10, 
                    spaceAfter=10
                ))
        
        # Add doctor signature section
        elements.append(Spacer(1, 30))
        elements.append(HRFlowable(width="40%", thickness=1, color=colors.black, 
                                spaceBefore=10, spaceAfter=2, hAlign='RIGHT'))
        elements.append(Paragraph(f"Dr. {prescription.doctor.user.get_full_name()}", 
                            ParagraphStyle('Signature', parent=normal_style, alignment=TA_RIGHT)))
        elements.append(Paragraph("Physician Signature", 
                            ParagraphStyle('SignatureLabel', parent=normal_style, 
                                        alignment=TA_RIGHT, textColor=colors.grey)))
        
        # Add footer with additional information
        elements.append(Spacer(1, 40))
        footer_text = """
        <para fontSize="8" alignment="center" textColor="grey">
        This prescription is valid for 30 days from the date of issue.
        Please present this document to your pharmacist. Contact your doctor for any clarifications.
        </para>
        """
        elements.append(Paragraph(footer_text, styles['Normal']))
        
        # Build the PDF document
        doc.build(elements)
        
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