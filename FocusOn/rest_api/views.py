from django.shortcuts import render
from django.http import JsonResponse
from django.http import HttpResponse

from spotify.spotifyAPI import *
import requests
from django.shortcuts import redirect

spotifyAPI = SpotifyAPI()

# Create your views here.
def upload_image(request):
    return JsonResponse({"Image": "Pollon"})

def get_updated_info(request):
    return JsonResponse({"Updated Info": "Pollon"})

def get_alerts(request):
    return JsonResponse({"Alerts": "Pollon"})

def get_token(request):
    return spotifyAPI.generateToken()

def callback(request):
    spotifyAPI.setAuthToken(request.GET['code'])
    return HttpResponse(spotifyAPI.authorize())

def testing(request):
    #response = spotifyAPI.connect_to_device("edbbf6dfa3678059cbf7252b056cc9c314126be6")
    #return JsonResponse(response)
    #return JsonResponse(spotifyAPI.get_list_playlists_call())
    return JsonResponse(spotifyAPI.get_tracks_from_playlist_call("0zirc7mJkjQ2gboYTBL4XZ"))

def init(request):
    return spotifyAPI.generateToken()
