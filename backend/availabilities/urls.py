from django.urls import include, path
from rest_framework.routers import DefaultRouter

from availabilities.views import AvailabilityViewSet

router = DefaultRouter()
router.register(r'', AvailabilityViewSet)

urlpatterns = [
    path('', include(router.urls)),
]