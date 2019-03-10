from django.http import JsonResponse
from django.http import HttpResponse
from threading import Timer
from PIL import Image

from spotify.spotifyAPI import *
from ai_module.ai_module import AIModule
from emotion_api.emotion_api import get_image_emotion
from RankingAI.rankingAI import *
from django.views.decorators.csrf import csrf_exempt

i = 0

def playNewSong():
    cancelTimer()
    nextSong = aiModule.get_next_song()
    nextSongURI = nextSong['track']['uri']
    nextDurationTime = nextSong['track']['duration_ms']/1000
    spotifyAPI.setNextSong(nextSongURI)
    setTimer(nextDurationTime)

def resetTimer(duration):
    global t
    t.cancel()
    t = Timer(duration-0.5, playNewSong)
    t.start()

def setTimer(duration):
    global t
    t = Timer(duration-0.5, playNewSong)
    t.start()

def cancelTimer():
    global t
    t.cancel()

spotifyAPI = SpotifyAPI()
aiModule = AIModule()
rankingAI = RankingAI()
t = Timer(0.0, playNewSong)
initilizedAI = False

# Create your views here.
@csrf_exempt
def upload_image(request):
    global i
    imgdata = base64.b64decode(str(request.POST['image']))
    image = Image.open(io.BytesIO(imgdata))
    image = image.rotate(90)
    filename = 'some_image{}.jpg'.format(i)
    image.save(filename)
    res = get_image_emotion(filename)
    aiModule.reorder_songs(res)
    i += 1
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
    global aiModule, initilizedAI
    if not initilizedAI:
        res2 = spotifyAPI.get_tracks_from_playlist_call("4VpgpY0zaW5OKb4P7K0QNr")
        aiModule.init(res2['items'], spotifyAPI)
        initilizedAI = True
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
    timeSec = spotifyAPI.getSongProgress()
    durationTime = aiModule.get_current_song()['track']['duration_ms'] / 1000
    setTimer(durationTime-timeSec)
    spotifyAPI.play_song_call()
    return HttpResponse('OK')

def pauseMusic(request):
    cancelTimer()
    spotifyAPI.pause_song_call()
    return HttpResponse('OK')

def playNextSong(request):
    playNewSong()
    return HttpResponse('OK')

def playPreviousSong(request):
    timeSec = spotifyAPI.getSongProgress()
    if timeSec > 5.0:
        spotifyAPI.reset_song_call()
        durationTime = aiModule.get_current_song()['track']['duration_ms']/1000
        resetTimer(durationTime)
    else:
        prevSong = aiModule.get_previous_song()
        prevSongURI = prevSong['track']['uri']
        prevDurationTime = prevSong['track']['duration_ms']/1000
        resetTimer(prevDurationTime)
        spotifyAPI.setNextSong(prevSongURI)
    return HttpResponse('OK')

def getUpcomingSongs(request):
    return JsonResponse(spotifyAPI.getUpcomingSongsInfo())


def init(request):
    return spotifyAPI.generateToken()
