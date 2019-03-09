import base64
import json
import requests
import sys

from FocusOn.spotify.spotifyConstants import *
from FocusOn.spotify.tokensFile import *

try:
    import urllib.request, urllib.error
    import urllib.parse as urllibparse
except ImportError:
    import urllib as urllibparse


def getAuthToken():
    parameters = {
        "response_type": "code",
        "redirect_uri": REDIRECT_URI,
        "scope": API_SCOPE,
        # "state": STATE,
        # "show_dialog": SHOW_DIALOG_str,
        "client_id": CLIENT_ID
    }
    response = requests.get(SPOTIFY_AUTH_URL, params=parameters)
    return response.text

def authorize(auth_token):
    data = {
        "grant_type": "authorization_code",
        "code": str(),
        "redirect_uri": REDIRECT_URI
    }
    base64encoded = base64.b64encode(("{}:{}".format(CLIENT_ID, CLIENT_KEY)).encode())
    headers = {"Authorization": "Basic {}".format(base64encoded.decode())}
    post_request = requests.post(SPOTIFY_TOKEN_URL, data=data,
                                 headers=headers)

    # tokens are returned to the app
    response_data = json.loads(post_request.text)
    if "access_token" in response_data:
        ACCESS_TOKEN = response_data["access_token"]

        # use the access token to access Spotify API
        auth_header = {"Authorization": "Bearer {}".format(ACCESS_TOKEN)}
        return ACCESS_TOKEN
    else:
        return "There were a problem while obtaining Spotify token. Please, try again."


def get_tracks_from_playlist_call(id, token):
    print(str(id))
    url = 'https://api.spotify.com/v1/playlists/'+ str(id) + '/tracks'
    headers  = { 'Authorization': 'Bearer ' + token, 'Accept': 'application/json', 'Content-Type': 'application/json' }
    response = requests.get(url, headers=headers )
    return response.json()

def get_track_info_call(id,token):
    url = 'https://api.spotify.com/v1/audio-features?ids=' + str(id)
    headers  = { 'Authorization': 'Bearer ' + token, 'cache-control': "no-cache", 'Content-type': 'application/json', 'Accept': 'application/json' }
    response = requests.get(url,data='', headers=headers)
    return response.text


## FOR USER EXPERIENCE
def get_list_playlists_call(token):
    url = 'https://api.spotify.com/v1/me/playlists'
    headers  = { 'Authorization': 'Bearer ' + token }
    response = requests.post(url, headers=headers)
    return response.text

def get_available_devices(access_token):
    url = "https://api.spotify.com/v1/me/player/devices"
    headers  = { 'Authorization': 'Bearer ' + access_token }
    resp = requests.get(url, headers=headers)
    return resp.json()

def change_dispositive(access_token):
    url = "https://api.spotify.com/v1/me/player/devices"
    headers  = { 'Authorization': 'Bearer ' + access_token }
    resp = requests.get(url, headers=headers)
    return resp.json()

def get_current_playlist(access_token):
    url = 'https://api.spotify.com/v1/me/player'
    headers  = { 'Authorization': 'Bearer ' + access_token }
    resp = requests.get(url, headers=headers)
    return resp.json()

def get_current_track(access_token):
    url = 'https://api.spotify.com/v1/me/player/currently-playing'
    headers  = { 'Authorization': 'Bearer ' + access_token }
    resp = requests.get(url, headers=headers)
    return resp.json()

def connect_to_device(access_token, device):
    url = 'https://api.spotify.com/v1/me/player'
    headers  = { 'Authorization': 'Bearer ' + access_token, 'Content-Type': 'application/json' }
    device_data = {
    "device_ids": [
        device
        ]
    }
    resp = requests.put(url, headers=headers, data=device_data)
    return resp.json()


def pause_song_call(access_token):
    url = "https://api.spotify.com/v1/me/player/pause"

    headers = {'Authorization': 'Bearer ' + access_token,
               'cache-control': "no-cache",
    }
    response = requests.request("PUT", url, headers=headers)

def play_song_call(access_token, device_id):
    url = "https://api.spotify.com/v1/me/player/play"
    querystring = {"device_id": device_id}
    headers = {'Authorization': 'Bearer ' + access_token,
               'cache-control': "no-cache",
    }
    response = requests.request("PUT", url, headers=headers, params = querystring)

def next_song_call(access_token, device_id):
    url = "https://api.spotify.com/v1/me/player/next"
    querystring = {"device_id": device_id}
    headers = {'Authorization': 'Bearer ' + access_token,
               'cache-control': "no-cache",
    }
    response = requests.request("POST", url, headers=headers, params=querystring)

def reorder_playlist(playlist_id, tracks, token):
    current_position = get_tracks_from_playlist_call(playlist_id,token)
    list_tracks = tracks.split(',')
    item = list_tracks[0]
    pos = 0
    for original in current_position['items']:
        if item == original['track']['id']:
            print(original['track']['name'])
            break
        else: 
            pos += 1
    print(current_position['items'][pos]['track']['name'])
    body = {
        "range_start": pos,
        "range_length": 1,
        "insert_before": 1
    }
    url = 'https://api.spotify.com/v1/playlists/'+ playlist_id + '/tracks'
    headers  = { 'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json' }
    resp = requests.put(url, headers=headers, data=body)
    return current_position['items'][0]['track']
