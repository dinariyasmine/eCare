# authentication/views.py
from rest_framework import status, generics, permissions
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework_simplejwt.tokens import RefreshToken
from django.contrib.auth import authenticate
from core.models import User
from .serializers import (
    LoginSerializer, 
    RegisterUserSerializer, 
    DoctorRegisterSerializer,
    UserSerializer
)
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi

class LoginView(APIView):
    permission_classes = [permissions.AllowAny]
    
    @swagger_auto_schema(
        request_body=LoginSerializer,
        responses={
            200: openapi.Response(
                description="Login successful",
                schema=openapi.Schema(
                    type=openapi.TYPE_OBJECT,
                    properties={
                        'access': openapi.Schema(type=openapi.TYPE_STRING),
                        'refresh': openapi.Schema(type=openapi.TYPE_STRING),
                        'user': openapi.Schema(type=openapi.TYPE_OBJECT),
                    }
                )
            ),
            401: openapi.Response(description="Invalid credentials")
        },
        operation_summary="Login user",
        operation_description="Login with username and password to receive JWT tokens"
    )
    def post(self, request):
        serializer = LoginSerializer(data=request.data)
        if serializer.is_valid():
            username = serializer.validated_data['username']
            password = serializer.validated_data['password']
            
            user = authenticate(username=username, password=password)
            
            if user:
                refresh = RefreshToken.for_user(user)
                user_serializer = UserSerializer(user)
                
                return Response({
                    'refresh': str(refresh),
                    'access': str(refresh.access_token),
                    'user': user_serializer.data
                }, status=status.HTTP_200_OK)
            return Response({'error': 'Invalid Credentials'}, status=status.HTTP_401_UNAUTHORIZED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class RegisterPatientView(generics.CreateAPIView):
    queryset = User.objects.all()
    serializer_class = RegisterUserSerializer
    permission_classes = [permissions.AllowAny]
    
    @swagger_auto_schema(
        responses={
            201: openapi.Response(
                description="Registration successful",
                schema=openapi.Schema(
                    type=openapi.TYPE_OBJECT,
                    properties={
                        'user': openapi.Schema(type=openapi.TYPE_OBJECT),
                        'tokens': openapi.Schema(
                            type=openapi.TYPE_OBJECT,
                            properties={
                                'refresh': openapi.Schema(type=openapi.TYPE_STRING),
                                'access': openapi.Schema(type=openapi.TYPE_STRING),
                            }
                        )
                    }
                )
            ),
        },
        operation_summary="Register a new patient",
        operation_description="Create a new patient user account with associated patient profile"
    )
    def post(self, request, *args, **kwargs):
        # Make sure role is set to patient
        request.data['role'] = 'patient'
        
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.save()
        
        # Generate tokens
        refresh = RefreshToken.for_user(user)
        
        return Response({
            'user': UserSerializer(user).data,
            'tokens': {
                'refresh': str(refresh),
                'access': str(refresh.access_token),
            }
        }, status=status.HTTP_201_CREATED)

class RegisterDoctorView(generics.CreateAPIView):
    queryset = User.objects.all()
    serializer_class = DoctorRegisterSerializer
    permission_classes = [permissions.AllowAny]
    
    @swagger_auto_schema(
        responses={
            201: openapi.Response(
                description="Registration successful",
                schema=openapi.Schema(
                    type=openapi.TYPE_OBJECT,
                    properties={
                        'user': openapi.Schema(type=openapi.TYPE_OBJECT),
                        'tokens': openapi.Schema(
                            type=openapi.TYPE_OBJECT,
                            properties={
                                'refresh': openapi.Schema(type=openapi.TYPE_STRING),
                                'access': openapi.Schema(type=openapi.TYPE_STRING),
                            }
                        )
                    }
                )
            ),
        },
        operation_summary="Register a new doctor",
        operation_description="Create a new doctor user account with associated doctor profile"
    )
    def post(self, request, *args, **kwargs):
        # Make sure role is set to doctor
        request.data['role'] = 'doctor'
        
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.save()
        
        # Generate tokens
        refresh = RefreshToken.for_user(user)
        
        return Response({
            'user': UserSerializer(user).data,
            'tokens': {
                'refresh': str(refresh),
                'access': str(refresh.access_token),
            }
        }, status=status.HTTP_201_CREATED)