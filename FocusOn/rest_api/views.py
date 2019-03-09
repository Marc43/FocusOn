from django.shortcuts import render
from django.http import JsonResponse
import requests
from django.shortcuts import redirect

API_URL = 'https://accounts.spotify.com/authorize'
CLIENT_ID = '05918c9e981747a3b85ec595dfeb2b71'
SCOPE = 'user-read-recently-played user-top-read user-follow-read user-follow-modify user-modify-playback-state user-read-playback-state user-read-currently-playing user-library-read user-library-modify user-read-private user-read-birthdate user-read-email playlist-modify-public playlist-read-collaborative playlist-modify-private playlist-read-private streaming app-remote-control'
REDIRECT_URI = 'http://127.0.0.1:8000/callback'
RESPONSE_TYPE = 'code'


auth_query_parameters = '?client_id={}&scope={}&redirect_uri={}&response_type={}'.format(CLIENT_ID, SCOPE, REDIRECT_URI, RESPONSE_TYPE)

# Create your views here.
def upload_image(request):
    return JsonResponse({"Image": "Pollon"})

def get_updated_info(request):
    return JsonResponse({"Updated Info": "Pollon"})

def get_alerts(request):
    return JsonResponse({"Alerts": "Pollon"})

def get_token(request):
    return redirect(API_URL + auth_query_parameters)

def callback(request):
    token = request.GET['code']
    return JsonResponse({"Token": token})
