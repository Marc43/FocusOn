from django.urls import path

from . import views

urlpatterns = [
    path('upload_image', views.upload_image, name='upload_image'),
    path('get_updated_info', views.get_updated_info, name='get_updated_info'),
    path('get_alerts', views.get_updated_info, name='get_alerts'),
    path('callback', views.callback, name='callback'),
    path('get_token', views.get_token, name='get_token')
]