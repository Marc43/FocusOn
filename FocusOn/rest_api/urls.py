from django.urls import path

from . import views

urlpatterns = [
    path('upload_image', views.upload_image, name='upload_image'),
    path('get_updated_info', views.get_updated_info, name='get_updated_info'),
    path('get_alerts', views.get_updated_info, name='get_alerts'),
    path('callback', views.callback, name='callback'),
    path('get_token', views.get_token, name='get_token'),
    path('playMusic', views.playMusic, name='playMusic'),
    path('pauseMusic', views.pauseMusic, name='pauseMusic'),
    path('playNextSong', views.playNextSong, name='playNextSong'),
    path('playPreviousSong', views.playPreviousSong, name='playPreviousSong'),
    path('getUpcomingSongs', views.getUpcomingSongs, name='getUpcomingSongs'),
    path('whichSong', views.whichSong, name='whichSong'),
    path('nextSongsOnTop', views.nextSongsOnTop, name='nextSongsOnTop'),
    path('testing', views.testing, name='testing'),
    path('', views.init, name='init')

]