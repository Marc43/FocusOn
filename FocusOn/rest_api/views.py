from django.http import JsonResponse
from django.http import HttpResponse
from threading import Timer
import base64


from spotify.spotifyAPI import *
from ai_module.ai_module import AIModule
from emotion_api.emotion_api import get_image_emotion
from RankingAI.rankingAI import *

import matplotlib.pylab as plt

spotifyAPI = SpotifyAPI()
aiModule = AIModule()
rankingAI = RankingAI()


def timeout():
    t = Timer(rankingAI.getNextSongDuration(), timeout)
    t.start()
    spotifyAPI.setNextSong(rankingAI.getNextSongID())

# Create your views here.
def upload_image(request):
    imgdata = base64.b64decode(request.POST['image'])
    filename = 'some_image.jpg'
    with open(filename, 'wb') as f:
        f.write(imgdata)
    res = get_image_emotion(filename)
    aiModule.reorder_songs(res)
    return JsonResponse({"result": True})

def get_updated_info(request):
    return JsonResponse({"Updated Info": "Pollon"})

def get_alerts(request):
    return JsonResponse({"Alerts": "Pollon"})

def get_token(request):
    return spotifyAPI.generateToken()

def callback(request):
    spotifyAPI.setAuthToken(request.GET['code'])
    res1 = spotifyAPI.authorize()
    res2 = spotifyAPI.get_tracks_from_playlist_call("4VpgpY0zaW5OKb4P7K0QNr")
    global aiModule
    aiModule.init(res2['items'], spotifyAPI)
    return HttpResponse(res1)

def testing(request):

    #response = spotifyAPI.connect_to_device("edbbf6dfa3678059cbf7252b056cc9c314126be6")
    #return JsonResponse(response)
    #return JsonResponse(spotifyAPI.get_list_playlists_call())
    """
    res = spotifyAPI.get_tracks_from_playlist_call("4VpgpY0zaW5OKb4P7K0QNr")
    output = []
    features = ["danceability", "energy",  "mode", "time_signature", "acousticness", "instrumentalness",
                     "liveness", "loudness", "speechiness", "valence", "tempo"]
    for track in res['items']:
        aux = []
        info = json.loads(spotifyAPI.get_track_info_call(track['track']['id']))['audio_features'][0]
        for feature in features:
            aux.append(info[feature])
        aux.append(track['track']['id'])
        aux.append(track['track']['name'])
        output.append(aux)
    """
    # res = get_image_emotion('/home/fmartinez/Desktop/test2.jpeg')
    # return JsonResponse({'data': []})


    #t = Timer(10.0, timeout)
    #t.start()

    #t.cancel()

    #response = spotifyAPI.get_available_devices()
    spotifyAPI.reset_song_call()
    return HttpResponse('OK')

    #return JsonResponse(spotifyAPI.get_track_info_call("7CEwFvLHy7KAr1g6ql3QdV"))


def playMusic(request):
    spotifyAPI.play_song_call()
    return HttpResponse('OK')

def pauseMusic(request):
    spotifyAPI.pause_song_call()
    return HttpResponse('OK')

def playNextSong(request):
    spotifyAPI.next_song_call()
    return HttpResponse('OK')

def playPreviousSong(request):
    timeSec = spotifyAPI.getSongProgress()
    if timeSec > 5.0:
        spotifyAPI.reset_song_call()
        t.cancel()
        t = Timer(20, timeout)
    else:
        spotifyAPI.previous_song_call()
    return HttpResponse('OK')

def getUpcomingSongs(request):
    return JsonResponse(spotifyAPI.getUpcomingSongsInfo())

def playNewSong(request):
    t = Timer(8, timeout)
    t.start()
    spotifyAPI.setNextSong(rankingAI.getNextSongID())
    return HttpResponse('OK')

def init(request):
    return spotifyAPI.generateToken()
