from rest_framework import viewsets

from core.models import Availability
from core.serializers import AvailabilitySerializer


class AvailabilityViewSet(viewsets.ModelViewSet):
    queryset = Availability.objects.all()
    serializer_class = AvailabilitySerializer
