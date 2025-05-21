from rest_framework import viewsets
from rest_framework.decorators import action
from rest_framework.response import Response

from core.models import Availability
from core.serializers import AvailabilitySerializer


class AvailabilityViewSet(viewsets.ModelViewSet):
    queryset = Availability.objects.all()
    serializer_class = AvailabilitySerializer

    @action(detail=False, methods=['get'], url_path='doctor/(?P<pk>[^/.]+)')
    def availaibilitiesPerDoctor(self, request, pk=None):
        queryset = Availability.objects.filter(doctor_id=pk)
        serializer = self.get_serializer(instance=queryset, many=True)
        return Response(serializer.data)