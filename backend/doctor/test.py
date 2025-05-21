from django.urls import reverse
from rest_framework.test import APITestCase, APIClient
from rest_framework import status
from core.models import User, Doctor, Availability
from datetime import datetime, timedelta
from django.utils import timezone

class UserProfileTests(APITestCase):
    def setUp(self):
        self.user = User.objects.create_user(
            username='testuser',
            password='testpassword123',
            email='testuser@example.com',
            role='patient'
        )
        self.client = APIClient()
        self.client.force_authenticate(user=self.user)

    def test_get_profile(self):
        url = reverse('get_profile')
        response = self.client.get(url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['username'], self.user.username)

    def test_update_profile(self):
        url = reverse('update_profile')
        data = {
            'name': 'Updated Name',
            'phone': '1234567890',
            'address': 'New Address'
        }
        response = self.client.put(url, data, format='json')
      

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.user.refresh_from_db()
        self.assertEqual(self.user.name, 'Updated Name')
        self.assertEqual(self.user.phone, '1234567890')

class DoctorTests(APITestCase):
    def test_create_doctor(self):
        user = User.objects.create_user(
            username='doctoruser',
            password='docpassword123',
            email='doctor@example.com',
            role='doctor'
        )
        url = reverse('create_doctor')
        data = {
            "user": user.id,
            "specialty": "Cardiology"
        }
        response = self.client.post(url, data, format='json')
         
        print("here we areooooo ",response)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertTrue(Doctor.objects.filter(user=user).exists())

class AvailabilityTests(APITestCase):
    def setUp(self):
        self.user = User.objects.create_user(
            username='doctoruser',
            password='password123',
            role='doctor',
            email='doc@example.com'
        )
        self.doctor = Doctor.objects.create(user=self.user, specialty='Dermatology')
        self.client = APIClient()
        self.client.force_authenticate(user=self.user)

    def test_get_availability_no_doctor_id(self):
        url = reverse('get_availability')
        response = self.client.get(url)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_get_availability_valid_doctor(self):
        Availability.objects.create(
            doctor_id=self.doctor,
            start_time=timezone.now(),
            end_time=timezone.now() + timedelta(hours=1),
            booked=False
        )
        url = reverse('get_availability')
        response = self.client.get(url, {'doctor_id': self.doctor.id})
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 1)

    def test_create_availability(self):
        url = reverse('create_availability')
        start_time = timezone.now()
        end_time = start_time + timedelta(hours=1)
        data = {
            'doctor_id': self.doctor.id,
            'start_time': start_time.isoformat(),
            'end_time': end_time.isoformat(),
        }
        response = self.client.post(url, data, format='json')
        print("here we are ",response.status_code)
       
         
        self.assertEqual(response.status_code, 201)
        self.assertTrue(Availability.objects.filter(doctor_id=self.doctor).exists())

    def test_update_availability(self):
        availability = Availability.objects.create(
            doctor_id=self.doctor,
            start_time=timezone.now(),
            end_time=timezone.now() + timedelta(hours=1),
            booked=False
        )
        url = reverse('update_availability', args=[availability.id])
        data = {
            'booked': True
        }
        response = self.client.put(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        availability.refresh_from_db()
        self.assertTrue(availability.booked)
