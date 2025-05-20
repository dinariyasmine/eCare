# authentication/views.py
from rest_framework import status, generics, permissions
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework_simplejwt.tokens import RefreshToken
from django.contrib.auth import authenticate
from core.models import User
from django.core.mail import send_mail
from django.conf import settings
from .models import PasswordResetOTP
import logging
from django.core.mail import EmailMultiAlternatives
from email.mime.image import MIMEImage
import os
from core.models import User 

from .serializers import (
    LoginSerializer,
    LogoutSerializer, 
    RegisterUserSerializer, 
    DoctorRegisterSerializer,
    UserSerializer,
    RequestPasswordResetSerializer,
    VerifyOTPSerializer,
    ResetPasswordSerializer
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
from django.template.loader import render_to_string
from django.utils.html import strip_tags

class RequestPasswordResetView(APIView):
    permission_classes = [permissions.AllowAny]
    
    @swagger_auto_schema(
        request_body=RequestPasswordResetSerializer,
        responses={
            200: openapi.Response(description="OTP sent successfully"),
            400: openapi.Response(description="Bad request")
        },
        operation_summary="Request password reset OTP",
        operation_description="Send a 5-digit OTP to the user's email for password reset"
    )
    def post(self, request):
        serializer = RequestPasswordResetSerializer(data=request.data)
        if serializer.is_valid():
            email = serializer.validated_data['email']
            user = User.objects.get(email=email)
            
            # Generate OTP
            otp_obj = PasswordResetOTP.create_otp_for_user(user)
            
            # Prepare email content
            subject = 'Your eCare Password Reset OTP'
            html_message = render_to_string('emails/password_reset_otp.html', {
                'otp_code': otp_obj.otp_code
            })
            plain_message = strip_tags(html_message)
            from_email = settings.DEFAULT_FROM_EMAIL
            recipient_list = [email]
            
            try:
                # Create email message with HTML alternative
                email = EmailMultiAlternatives(
                    subject,
                    plain_message,
                    from_email,
                    recipient_list
                )
                email.attach_alternative(html_message, "text/html")
                
                # Attach logo image
                logo_path = os.path.join(settings.BASE_DIR, 'static', 'logo.png')
                if os.path.exists(logo_path):
                    with open(logo_path, 'rb') as f:
                        logo = MIMEImage(f.read())
                        logo.add_header('Content-ID', '<logo.png>')
                        email.attach(logo)
                
                email.send()
                
                return Response({'message': 'OTP sent to your email address.'}, status=status.HTTP_200_OK)
            except Exception as e:
                logger.error(f"Error sending OTP email: {str(e)}")
                return Response({'error': 'Error sending email. Please try again later.'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
class VerifyOTPView(APIView):
    permission_classes = [permissions.AllowAny]
    
    @swagger_auto_schema(
        request_body=VerifyOTPSerializer,
        responses={
            200: openapi.Response(description="OTP verified successfully"),
            400: openapi.Response(description="Invalid OTP")
        },
        operation_summary="Verify OTP code",
        operation_description="Verify the OTP code sent to the user's email"
    )
    def post(self, request):
        serializer = VerifyOTPSerializer(data=request.data)
        if serializer.is_valid():
            # OTP is valid - don't mark as used yet, will be marked when reset is complete
            return Response({'message': 'OTP verified successfully.'}, status=status.HTTP_200_OK)
        
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class ResetPasswordView(APIView):
    permission_classes = [permissions.AllowAny]
    
    @swagger_auto_schema(
        request_body=ResetPasswordSerializer,
        responses={
            200: openapi.Response(description="Password reset successful"),
            400: openapi.Response(description="Invalid request")
        },
        operation_summary="Reset password",
        operation_description="Reset password using OTP verification"
    )
    def post(self, request):
        serializer = ResetPasswordSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.validated_data['user']
            otp = serializer.validated_data['otp']
            
            # Set new password
            user.set_password(serializer.validated_data['password'])
            user.save()
            
            # Mark OTP as used
            otp.is_used = True
            otp.save()
            
            return Response({'message': 'Password has been reset successfully.'}, status=status.HTTP_200_OK)
        
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

logger = logging.getLogger(__name__)

# In RequestPasswordResetView
logger.debug(f"Generated OTP {PasswordResetOTP.otp_code} for user {User.email}")

# In VerifyOTPView
logger.debug(f"Verifying OTP {PasswordResetOTP.otp_code} for user {User.email}")

# In ResetPasswordView
logger.debug(f"Resetting password for user {User.email}")


class LogoutView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    @swagger_auto_schema(
        request_body=LogoutSerializer,
        responses={
            205: openapi.Response(description="Logout successful"),
            400: openapi.Response(description="Bad request")
        },
        operation_summary="Logout user",
        operation_description="Blacklist the refresh token to prevent further use"
    )
    def post(self, request):
        serializer = LogoutSerializer(data=request.data)
        if serializer.is_valid():
            try:
                refresh_token = serializer.validated_data['refresh_token']
                token = RefreshToken(refresh_token)
                token.blacklist()

                logger.debug(f"User {request.user.username} logged out successfully")
                return Response({'message': 'Logout successful'}, status=status.HTTP_205_RESET_CONTENT)
            except Exception as e:
                logger.error(f"Error during logout: {str(e)}")
                return Response({'error': 'Invalid token'}, status=status.HTTP_400_BAD_REQUEST)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)