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
    res = spotifyAPI.get_tracks_from_playlist_call("4VpgpY0zaW5OKb4P7K0QNr")
    output = [(x['track']['name'], x['track']['id']) for x in res['items']]
    f = open('train.csv', 'w')
    for x in output:
        f.write(x[0] + ',' + x[1] + '\n')
    f.close()
    return JsonResponse({'data': []})

def init(request):
    return spotifyAPI.generateToken()
