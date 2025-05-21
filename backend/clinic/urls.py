from django.urls import path
from . import views

urlpatterns = [
    path('clinics/', views.list_clinics, name='list_clinics'),
    path('clinics/create/', views.create_clinic, name='create_clinic'),
    path('clinics/<int:pk>/', views.retrieve_clinic, name='retrieve_clinic'),
    path('clinics/<int:pk>/update/', views.update_clinic, name='update_clinic'),
    path('clinics/<int:pk>/delete/', views.delete_clinic, name='delete_clinic'),
]
