from django.db import models
from django.utils import timezone
from core.models import User
import random
import string

class PasswordResetOTP(models.Model):
    """Store OTP codes for password reset"""
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='password_reset_otps')
    otp_code = models.CharField(max_length=5)
    is_used = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)
    expires_at = models.DateTimeField()
    
    def __str__(self):
        return f"OTP for {self.user.username}"
    
    @staticmethod
    def generate_otp():
        """Generate a 5-digit OTP"""
        digits = string.digits
        return ''.join(random.choice(digits) for i in range(5))
    
    @staticmethod
    def create_otp_for_user(user):
        """Create a new OTP for the given user"""
        # Expire any existing unused OTPs for this user
        PasswordResetOTP.objects.filter(
            user=user, 
            is_used=False
        ).update(is_used=True)
        
        # Create new OTP
        otp_code = PasswordResetOTP.generate_otp()
        expiry_time = timezone.now() + timezone.timedelta(minutes=10)  # OTP valid for 10 minutes
        
        otp_obj = PasswordResetOTP.objects.create(
            user=user,
            otp_code=otp_code,
            expires_at=expiry_time
        )
        
        return otp_obj
    
    def is_valid(self):
        """Check if OTP is still valid"""
        return not self.is_used and timezone.now() < self.expires_at
class GoogleUser(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, related_name='google_account')
    google_id = models.CharField(max_length=255, unique=True)
    created_at = models.DateTimeField(auto_now_add=True)
    
    def __str__(self):
        return f"{self.user.email} - {self.google_id}"