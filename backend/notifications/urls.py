from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import NotificationViewSet, FCMDeviceViewSet

router = DefaultRouter()
router.register(r'notifications', NotificationViewSet, basename='notification')
router.register(r'devices', FCMDeviceViewSet, basename='device')

urlpatterns = [
    path('', include(router.urls)),
]
