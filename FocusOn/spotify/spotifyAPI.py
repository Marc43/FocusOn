import base64
import json
import requests
import sys

from spotify.spotifyConstants import *
from spotify.tokensFile import *
from django.shortcuts import redirect

try:
    import urllib.request, urllib.error
    import urllib.parse as urllibparse
except ImportError:
    import urllib as urllibparse


class SpotifyAPI:
    AUTH_TOKEN = None
    ACCESS_TOKEN = None
    API_TOKEN_REFRESH = None
    TOKEN_TYPE = None
    AUTH_HEADER = None

    def generateToken(self):
        parameters = {
            "response_type": "code",
            "redirect_uri": REDIRECT_URI,
            "scope": API_SCOPE,
            "client_id": CLIENT_ID
        }
        RESPONSE_TYPE = 'code'
        auth_query_parameters = '?client_id={}&scope={}&redirect_uri={}&response_type={}'.format(CLIENT_ID, API_SCOPE,
                                                                                                 REDIRECT_URI,
                                                                                                 RESPONSE_TYPE)
        return redirect(SPOTIFY_AUTH_URL + auth_query_parameters)

    def setAuthToken(self, authToken):
        self.AUTH_TOKEN = str(authToken)

    def authorize(self):
        data = {
            "grant_type": "authorization_code",
            "code": self.AUTH_TOKEN,
            "redirect_uri": REDIRECT_URI
        }
        base64encoded = base64.b64encode(("{}:{}".format(CLIENT_ID, CLIENT_KEY)).encode())
        headers = {"Authorization": "Basic {}".format(base64encoded.decode())}
        post_request = requests.post(SPOTIFY_TOKEN_URL, data=data,
                                     headers=headers)
        response_data = json.loads(post_request.text)
        if "access_token" in response_data:
            self.ACCESS_TOKEN = response_data["access_token"]
            self.API_TOKEN_REFRESH = response_data["refresh_token"]
            self.TOKEN_TYPE = response_data["token_type"]
            self.AUTH_HEADER = {"Authorization":"{} {}".format(self.TOKEN_TYPE, self.ACCESS_TOKEN)}
            return "Token obtained correctly!"
        else:
            return "There were a problem while obtaining Spotify token. Please, try again."

    def updateAuthorize(self):
        data = {
            "grant_type": "refresh_token",
            "refresh_token": self.API_TOKEN_REFRESH
        }
        base64encoded = base64.b64encode(("{}:{}".format(CLIENT_ID, CLIENT_KEY)).encode())
        headers = {"Authorization": "Basic {}".format(base64encoded.decode())}
        post_request = requests.post(SPOTIFY_TOKEN_URL, data=data,
                                     headers=headers)
        response_data = json.loads(post_request.text)
        if "access_token" in response_data:
            self.ACCESS_TOKEN = response_data["access_token"]
            self.API_TOKEN_REFRESH = response_data["access_token"]
            self.TOKEN_TYPE = response_data["token_type"]
            self.AUTH_HEADER = {"Authorization": "{} {}".format(self.TOKEN_TYPE, self.ACCESS_TOKEN)}
            return True
        return False

    def get_tracks_from_playlist_call(self, id):
        url = SPOTIFY_API_URL + '/playlists/'+ str(id) + '/tracks'
        headers = self.AUTH_HEADER
        headers.update({'Accept': 'application/json', 'Content-Type': 'application/json'})
        response = requests.get(url, headers=headers)
        return response.json()

    def get_track_info_call(self, id):
        url = SPOTIFY_API_URL + '/audio-features?ids=' + str(id)
        headers = self.AUTH_HEADER
        headers.update({'cache-control': "no-cache", 'Content-type': 'application/json', 'Accept': 'application/json'})
        response = requests.get(url, data='', headers=headers)
        return response.json()

    def get_list_playlists_call(self):
        url = SPOTIFY_API_URL + '/me/playlists'
        headers = self.AUTH_HEADER
        response = requests.get(url, headers=headers)
        return response.json()

    def get_available_devices(self):
        url = SPOTIFY_API_URL + '/me/player/devices'
        headers = self.AUTH_HEADER
        resp = requests.get(url, headers=headers)
        return resp.json()

    def get_current_playback_info(self):
        url = SPOTIFY_API_URL + '/me/player'
        headers = self.AUTH_HEADER
        resp = requests.get(url, headers=headers)
        return resp.json()

    def get_current_track(self):
        url = SPOTIFY_API_URL + 'me/player/currently-playing'
        headers = self.AUTH_HEADER
        resp = requests.get(url, headers=headers)
        return resp.json()

    def connect_to_device(self, device):
        url = SPOTIFY_API_URL + '/me/player'
        headers = self.AUTH_HEADER
        device_data = {
        'device_ids': [
            device
            ]
        }
        requests.put(url, headers=headers, data=json.dumps(device_data))

    def pause_song_call(self):
        url = SPOTIFY_API_URL + '/me/player/pause'
        #parameters = {"device_id": device_id}
        headers = self.AUTH_HEADER
        headers.update({'cache-control': "no-cache"})
        response = requests.request("PUT", url, headers=headers)

    def play_song_call(self):
        url = "https://api.spotify.com/v1/me/player/play"
        #parameters = {"device_id": device_id}
        headers = self.AUTH_HEADER
        headers.update({'cache-control': "no-cache"})
        response = requests.request("PUT", url, headers=headers)

    def next_song_call(self):
        url = SPOTIFY_API_URL + '/me/player/next'
        #parameters = {"device_id": device_id}
        headers = self.AUTH_HEADER
        headers.update({'cache-control': "no-cache"})
        response = requests.request("POST", url, headers=headers)

    def previous_song_call(self):
        url = SPOTIFY_API_URL + '/me/player/previous'
        headers = self.AUTH_HEADER
        headers.update({'cache-control': "no-cache"})
        response = requests.request("POST", url, headers=headers)
        aux = 0

    def reorder_playlist(self, playlist_id, tracks):
        url = SPOTIFY_API_URL + '/playlists/' + playlist_id + '/tracks'

        '''current_position = get_tracks_from_playlist_call(playlist_id)
        list_tracks = tracks.split(',')
        item = list_tracks[0]
        pos = 0
        for original in current_position['items']:
            if item == original['track']['id']:
                print(original['track']['name'])
                break
            else:
                pos += 1
        print(current_position['items'][pos]['track']['name'])'''
        body = {
            "range_start": 1,
            "range_length": 1,
            "insert_before": 1
        }
        headers = self.AUTH_HEADER
        headers.update({'Content-Type': 'application/json'})
        response = requests.put(url, headers=headers, data=body)
        return None

    def getUpcomingSongsInfo(self):
        resp = self.get_current_playlist()
        id = None
        self.get_tracks_from_playlist_call(id)
        self. get_track_info_call(id)

    def setNextSong(self,song_uri):
        url = "https://api.spotify.com/v1/me/player/play"
        #parameters = {"device_id": device_id}
        headers = self.AUTH_HEADER
        body = {"uris": [song_uri]}
        headers.update({'cache-control': "no-cache"})
        response = requests.request("PUT", url, headers=headers, data=json.dumps(body))

    def getSongProgress(self):
        resp = self.get_current_playback_info()
        return float(resp["progress_ms"])/1000

    def reset_song_call(self):
        url = SPOTIFY_API_URL + '/me/player/seek'
        querystring = {"position_ms": "0"}

        headers = {
            'content-type': "multipart/form-data",
            'cache-control': "no-cache"
        }
        headers.update(self.AUTH_HEADER)

        response = requests.request("PUT", url,  headers=headers, params=querystring)

        '''url = SPOTIFY_API_URL + '/me/player/seek'
        headers = self.AUTH_HEADER
        headers.update({'Content-type': 'application/json'})
        headers.update({'Accept': 'application/json'})
        parameters = {'position_ms': 1000}
        response = requests.request("PUT", url, headers=headers, params=json.dumps(parameters))
        aux = 0
        '''

if __name__ == '__main__':
    s = SpotifyAPI()
    print(s.getAuthToken())