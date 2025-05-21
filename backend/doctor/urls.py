from django.urls import path
from .views import (
  
   
    create_social_media,
    get_doctor_by_id,
    get_doctors,
    get_patient_by_id,
    get_profile,
     
    update_doctor_by_id,
    update_patient_by_id,
    update_profile,
    create_doctor,  # <-- you forgot this import
)

urlpatterns = [
    path('profile/', get_profile, name='get_profile'),
 path('doctors/', get_doctors, name='get_doctors'), 
 path('doctor/<int:doctor_id>/', get_doctor_by_id, name='get_doctor_by_id'),
    path('profile/update/', update_profile, name='update_profile'),
    path('doctor/create/', create_doctor, name='create_doctor'),  # <-- fixed path here
    # path('availability/', get_availability, name='get_availability'),
    # path('availability/create/', create_availability, name='create_availability'),
    # path('availability/update/<int:availability_id>/', update_availability, name='update_availability'),
    # path('availability/delete/<int:availability_id>/', delete_availability, name='delete_availability'),
     path('patients/<int:patient_id>/', get_patient_by_id, name='get_patient_by_id'),
       path('doctors/<int:doctor_id>/update/', update_doctor_by_id),
    path('patients/<int:patient_id>/update/', update_patient_by_id), 
    path('social-media/', create_social_media, name='create_social_media'), 
]
