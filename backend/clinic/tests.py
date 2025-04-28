from django.test import TestCase

from rest_framework.test import APITestCase
from rest_framework import status
from core.models import Clinic
from django.urls import reverse

class ClinicAPITestCase(APITestCase):

    def setUp(self):
        self.clinic1 = Clinic.objects.create(
            name="Clinic One",
            address="123 Main St",
            map_location="Location 1"
        )
        self.clinic2 = Clinic.objects.create(
            name="Clinic Two",
            address="456 Elm St",
            map_location="Location 2"
        )
        self.list_url = reverse('list_clinics')
        self.create_url = reverse('create_clinic')
        self.retrieve_url = lambda pk: reverse('retrieve_clinic', kwargs={'pk': pk})
        self.update_url = lambda pk: reverse('update_clinic', kwargs={'pk': pk})
        self.delete_url = lambda pk: reverse('delete_clinic', kwargs={'pk': pk})

    def test_list_clinics(self):
        response = self.client.get(self.list_url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 2)

    def test_create_clinic(self):
        data = {
            "name": "Clinic Three",
            "address": "789 Pine St",
            "map_location": "Location 3"
        }
        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(Clinic.objects.count(), 3)
        self.assertEqual(response.data['name'], data['name'])

    def test_retrieve_clinic(self):
        response = self.client.get(self.retrieve_url(self.clinic1.id))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['name'], self.clinic1.name)

    def test_retrieve_nonexistent_clinic(self):
        response = self.client.get(self.retrieve_url(999))
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

    def test_update_clinic(self):
        data = {"name": "Updated Clinic One"}
        response = self.client.patch(self.update_url(self.clinic1.id), data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.clinic1.refresh_from_db()
        self.assertEqual(self.clinic1.name, "Updated Clinic One")

    def test_update_nonexistent_clinic(self):
        data = {"name": "Nonexistent Clinic"}
        response = self.client.patch(self.update_url(999), data, format='json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

    def test_delete_clinic(self):
        response = self.client.delete(self.delete_url(self.clinic2.id))
        self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)
        self.assertEqual(Clinic.objects.count(), 1)

    def test_delete_nonexistent_clinic(self):
        response = self.client.delete(self.delete_url(999))
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
