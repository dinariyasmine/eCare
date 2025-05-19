# authentication/urls.py
from django.urls import path
from rest_framework_simplejwt.views import TokenRefreshView
from .views import LoginView, RegisterPatientView, RegisterDoctorView ,RequestPasswordResetView, VerifyOTPView, ResetPasswordView

urlpatterns = [
    path('login/', LoginView.as_view(), name='login'),
    path('register/patient/', RegisterPatientView.as_view(), name='register-patient'),
    path('register/doctor/', RegisterDoctorView.as_view(), name='register-doctor'),
    path('token/refresh/', TokenRefreshView.as_view(), name='token-refresh'),
    path('password-reset/request/', RequestPasswordResetView.as_view(), name='password-reset-request'),
    path('password-reset/verify-otp/', VerifyOTPView.as_view(), name='verify-otp'),
    path('password-reset/reset/', ResetPasswordView.as_view(), name='password-reset'),

]