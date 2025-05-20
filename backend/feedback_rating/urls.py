from django.urls import path
from . import views  # or wherever your views are

urlpatterns = [
    path('rate-doctor/<int:doctor_id>/', views.rate_doctor, name='rate_doctor'),
    path('submit-feedback/<int:doctor_id>/', views.submit_feedback, name='submit_feedback'),
    path('get-doctor-feedback/<int:doctor_id>/', views.get_doctor_feedback, name='get_doctor_feedback'),
]
