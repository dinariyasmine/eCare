from django.urls import reverse
from rest_framework.test import APITestCase
from rest_framework import status
from core.models import User

class AuthenticationTests(APITestCase):
    def setUp(self):
        # Create a test user
        self.user = User.objects.create_user(
            username='testuser',
            password='testpassword123',
            email='test@example.com',
            name='Test User',
            role='doctor'
        )
        
    def test_login(self):
        # Attempt login
        url = reverse('login')  # Make sure this matches your URL name
        data = {
            'username': 'testuser',
            'password': 'testpassword123'
        }
        response = self.client.post(url, data, format='json')
        
        # Check response
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertIn('access', response.data)
        self.assertIn('refresh', response.data)
        
        # Save token for other tests
        self.token = response.data['access']
