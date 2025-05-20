# medications/urls.py
from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import MedicationViewSet

router = DefaultRouter()
router.register(r'', MedicationViewSet)

urlpatterns = [
    path('', include(router.urls)),
]
