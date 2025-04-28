from django.urls import path
from .views import (
    create_availability,
    delete_availability,
    get_availability,
    get_profile,
    update_availability,
    update_profile,
    create_doctor,  # <-- you forgot this import
)

urlpatterns = [
    path('profile/', get_profile, name='get_profile'),
    path('profile/update/', update_profile, name='update_profile'),
    path('doctor/create/', create_doctor, name='create_doctor'),  # <-- fixed path here
    path('availability/', get_availability, name='get_availability'),
    path('availability/create/', create_availability, name='create_availability'),
    path('availability/update/<int:availability_id>/', update_availability, name='update_availability'),
    path('availability/delete/<int:availability_id>/', delete_availability, name='delete_availability'),
]
