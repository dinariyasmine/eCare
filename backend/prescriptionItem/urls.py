from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import PrescriptionItemViewSet

router = DefaultRouter()
router.register(r'prescriptionsItem', PrescriptionItemViewSet)
urlpatterns = [
    path('', include(router.urls)),  
]
