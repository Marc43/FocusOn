from django.shortcuts import render
from django.http import JsonResponse

import FocusOn.spotify.spotifyAPI as spotify
import requests
from django.shortcuts import redirect


# Create your views here.
def upload_image(request):
    return JsonResponse({"Image": "Pollon"})

def get_updated_info(request):
    return JsonResponse({"Updated Info": "Pollon"})

def get_alerts(request):
    return JsonResponse({"Alerts": "Pollon"})

def get_token(request):
    return spotify.getAuthToken()
    #return redirect(API_URL + auth_query_parameters)

def callback(request):
    token = request.GET['code']
    return JsonResponse({"Token": token})
