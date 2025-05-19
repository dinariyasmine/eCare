from django.urls import reverse
from rest_framework.test import APITestCase
from rest_framework import status
from core.models import Doctor, Patient, Feedback, User, Clinic

class DoctorFeedbackTests(APITestCase):
    def setUp(self):
        # Create a user for doctor
        self.doctor_user = User.objects.create_user(
            username="doctoruser",
            email="doctor@example.com",
            password="password123",
            role="doctor"
        )

        # Create a clinic for doctor (because doctor model needs a clinic ForeignKey)
        self.clinic = Clinic.objects.create(
            name="Test Clinic",
            address="123 Clinic Street"
        )

        # Create a doctor profile
        self.doctor = Doctor.objects.create(
            user=self.doctor_user,
            specialty="Cardiology",
            clinic=self.clinic
        )

        # Create a user for patient
        self.patient_user = User.objects.create_user(
            username="patientuser",
            email="patient@example.com",
            password="password123",
            role="patient"
        )

        # Create a patient profile
        self.patient = Patient.objects.create(user=self.patient_user)

    def test_rate_doctor_success(self):
        url = reverse('rate_doctor', args=[self.doctor.id])
        data = {"grade": 4.2}
        response = self.client.post(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['message'], "Doctor rated successfully.")
        self.assertEqual(float(response.data['grade']), 4.2)

    def test_rate_doctor_invalid_grade(self):
        url = reverse('rate_doctor', args=[self.doctor.id])
        data = {"grade": 7}  # Invalid (greater than 5)
        response = self.client.post(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn('error', response.data)

    def test_rate_doctor_not_found(self):
        url = reverse('rate_doctor', args=[999])  # Doctor id 999 doesn't exist
        data = {"grade": 3}
        response = self.client.post(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertIn('error', response.data)

    def test_submit_feedback_success(self):
        url = reverse('submit_feedback', args=[self.doctor.id])
        data = {
            "patient_id": self.patient.id,
            "title": "Excellent doctor",
            "description": "Very helpful and kind."
        }
        response = self.client.post(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(response.data['message'], "Feedback submitted successfully.")

    def test_submit_feedback_missing_fields(self):
        url = reverse('submit_feedback', args=[self.doctor.id])
        data = {
            "patient_id": self.patient.id,
            "title": "",  # Missing title
            "description": "No title provided."
        }
        response = self.client.post(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn('error', response.data)

    def test_submit_feedback_doctor_not_found(self):
        url = reverse('submit_feedback', args=[999])  # Invalid doctor ID
        data = {
            "patient_id": self.patient.id,
            "title": "Missing doctor",
            "description": "Doctor does not exist."
        }
        response = self.client.post(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertIn('error', response.data)

    def test_get_doctor_feedback_success(self):
        # Create a feedback first
        Feedback.objects.create(
            title="Nice experience",
            description="Very professional.",
            patient=self.patient,
            doctor=self.doctor,
            date_creation="2024-01-01",
            time_creation="10:00:00"
        )

        url = reverse('get_doctor_feedback', args=[self.doctor.id])
        response = self.client.get(url)

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        print(response.data)

        self.assertGreater(len(response.data), 0)

