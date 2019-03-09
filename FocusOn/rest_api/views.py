from django.shortcuts import render
from django.http import JsonResponse
from django.http import HttpResponse

from spotify.spotifyAPI import *
from RankingAI.rankingAI import *
import requests
from django.shortcuts import redirect
from threading import Timer


def timeout():
    t = Timer(rankingAI.getNextSongDuration(), timeout)
    t.start()
    spotifyAPI.setNextSong(rankingAI.getNextSongID())



spotifyAPI = SpotifyAPI()
rankingAI = RankingAI()

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
        t = Timer(, timeout)
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
