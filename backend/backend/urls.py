from django.urls import path, include, re_path
from rest_framework import permissions
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from django.contrib import admin
from django.views.generic import TemplateView

schema_view = get_schema_view(
    openapi.Info(
        title="eCare API",
        default_version='v1',
        description="API documentation for eCare prescription-medication system",
        terms_of_service="https://www.google.com/policies/terms/",
        contact=openapi.Contact(email="contact@ecare.com"),
        license=openapi.License(name="BSD License"),
    ),
    public=True,
    permission_classes=(permissions.AllowAny,),
)

urlpatterns = [

    path('admin/', admin.site.urls),
    path('api/core/', include('core.urls')),
    path('api/medications/', include('medications.urls')),
    path('api/prescription-items/', include('prescriptionItem.urls')),
    path('api/prescriptions/', include('prescriptions.urls')),
    
    path('api/auth/', include('authentication.urls')),
    
    path('appointments/', include('appointments.urls')),
    path('availabilities/', include('availabilities.urls')),
    
    
    path('api/', include('doctor.urls')),
    path('api/', include('clinic.urls')),
    path('api/', include('feedback_rating.urls')),
    
    path('api/', include('notifications.urls')),
    
        path('firebase-messaging-sw.js',
        TemplateView.as_view(
            template_name='firebase-messaging-sw.js',
            content_type='application/javascript',
        ),
        name='firebase-messaging-sw.js'
    ),
    # Swagger documentation URLs
    re_path(r'^swagger(?P<format>\.json|\.yaml)$', schema_view.without_ui(cache_timeout=0), name='schema-json'),
    re_path(r'^swagger/$', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
    re_path(r'^redoc/$', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc'),
]

