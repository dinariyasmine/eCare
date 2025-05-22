from django.shortcuts import render

# Create your views here.
# Create your models here.
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from rest_framework.permissions import AllowAny
from rest_framework import status
from core.models import Clinic
from core.serializers import ClinicSerializer

@api_view(['GET'])
@permission_classes([AllowAny])
def list_clinics(request):
    clinics = Clinic.objects.all()
    serializer = ClinicSerializer(clinics, many=True)
    return Response(serializer.data)

@api_view(['POST'])
def create_clinic(request):
    serializer = ClinicSerializer(data=request.data)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
def retrieve_clinic(request, pk):
    try:
        clinic = Clinic.objects.get(pk=pk)
    except Clinic.DoesNotExist:
        return Response({'error': 'Clinic not found'}, status=status.HTTP_404_NOT_FOUND)
    serializer = ClinicSerializer(clinic)
    return Response(serializer.data)

@api_view(['PUT', 'PATCH'])
def update_clinic(request, pk):
    try:
        clinic = Clinic.objects.get(pk=pk)
    except Clinic.DoesNotExist:
        return Response({'error': 'Clinic not found'}, status=status.HTTP_404_NOT_FOUND)
    serializer = ClinicSerializer(clinic, data=request.data, partial=True)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['DELETE'])
def delete_clinic(request, pk):
    try:
        clinic = Clinic.objects.get(pk=pk)
    except Clinic.DoesNotExist:
        return Response({'error': 'Clinic not found'}, status=status.HTTP_404_NOT_FOUND)
    clinic.delete()
    return Response(status=status.HTTP_204_NO_CONTENT)
