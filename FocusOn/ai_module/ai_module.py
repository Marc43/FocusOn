import json
import numpy as np
from sklearn.ensemble import RandomForestRegressor

class AIModule:
    def __init__(self):
        self._emotions = ["anger", "contempt", "disgust", "fear", "happiness", "neutral", "sadness", "surprise"]
        self._trackFeatures = ["danceability", "energy",  "mode", "time_signature", "acousticness", "instrumentalness",
                     "liveness", "loudness", "speechiness", "valence", "tempo"]
        self._model_loaded = False

    def _normalize(self, v):
        return (v / np.linalg.norm(v, ord=1)).tolist()

    def init(self, tracks, spotify):
        self._tracks = tracks
        X, y = self._getTrainDataFromFilename('/home/fmartinez/FocusOn/FocusOn/ai_module/train.csv')
        y = np.array([self._normalize(yi) for yi in y])
        self.trainModel(X, y)
        self.generateTracksScore(spotify)
        self._next_tracks = self._tracks
        self._previous_tracks = []
        self._current_track = {}

    def _getTrainDataFromFilename(self, filename):
        data = np.genfromtxt(filename, delimiter=',')
        return (data[:, :11], data[:, 13:])

    def trainModel(self, X, y):
        self._model = RandomForestRegressor(n_estimators=100)
        self._model.fit(X, y)
        self._model_loaded = True

    def generateTracksScore(self, spotify):
        for i in range(len(self._tracks)):
            info = json.loads(spotify.get_track_info_call(self._tracks[i]['track']['id']))['audio_features'][0]
            aux = []
            for feature in self._trackFeatures:
                aux.append(info[feature])
            self._tracks[i]['score'] = self.getPrediction(np.array(aux).reshape(1, -1))


    def getPrediction(self, X):
        if not self._model_loaded:
            raise Exception('Quiza primero deberias entrenar un modelo, anormal')
        return self._normalize(self._model.predict(X))

    def get_next_song(self):
        return self._next_tracks[0]

    def get_next_n_songs(self, n):
        return None


    def get_previous_song(self):
        if len(self._previous_tracks) == 0:
            return self._current_track
        else:
            prev_song = self._previous_tracks[-1]
            self._previous_tracks.pop()
            self._next_tracks.append(self._current_track)
            self._current_track = prev_song
            return prev_song

    def reorder_songs(self, face_coeff):
        self._face_coeff = face_coeff
        diff_vector = []
        for track in self._tracks:
            None

