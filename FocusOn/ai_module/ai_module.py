import json
import numpy as np
from sklearn.ensemble import RandomForestRegressor

class AIModule:
    def __init__(self):
        self._emotions = ["anger", "contempt", "disgust", "fear", "happiness", "neutral", "sadness", "surprise"]
        self._trackFeatures = ["danceability", "energy",  "mode", "time_signature", "acousticness", "instrumentalness",
                     "liveness", "loudness", "speechiness", "valence", "tempo"]

    def normalize(self, v):
        return (v / np.linalg.norm(v, ord = 1)).tolist()

    def convertTargetInformationToJSON(self, txt_path):
        raw_data = open(txt_path, "r").read()
        j = {}
        trucks = raw_data.split("\n")
        for t in trucks:
            truck_info = t.split(" -> ")
            id = truck_info[0]
            j[id] = {}
            emotions_list = truck_info[1].split(" ")
            emotions_values = self.normalize([float(x) for x in emotions_list])
            for i in range(len(emotions)):
                j[id][emotions[i]] = emotions_values[i]
        return j


    # Both JSON have the same tracks and the first one
    # has the information of the explicative variables
    # and the second onw the target variables.
    def mergeTrackJSONFields(self, j1 , j2):
        j = {}
        for key in j1:
            fields1 = j1[key]
            if key in j2:
                j[key] = {}
                fields2 = j2[key]
                for field in fields1:
                    j[key][field] = fields1[field]
                for field in fields2:
                    j[key][field] = fields2[field]
        return j

    def saveJSONToPath(self, json_path, fileName, data):
        filePathName = json_path + fileName
        with open(filePathName, 'w') as fp:
            json.dump(data, fp)


    def getTrackJSONFromPath(self, json_path, fileName):
        filePathName = json_path + fileName
        with open(filePathName) as data_file:
            data = json.load(data_file)
        return data


    def matrixFromTracksJSON(self, json, attrElements):
        songs = []
        for _, item in json.items():
            attributes = []
            for a in attrElements:
                attributes.append(item[a])
            songs.append(attributes)
        return songs

    def getJSONFromMatrix(self, Y, emotions):
        d = {}
        for i in range(len(Y)):
           d[emotions[i]] = Y[i]
        return d

    def generateTracksRegresionModel(self, JSON, featureNames, outputNames):
        X = self.matrixFromTracksJSON(JSON, featureNames)
        Y = self.matrixFromTracksJSON(JSON, outputNames)
        return self.generateRegresionModel(X, Y)

    def generateRegresionModel(self, X, Y):
        model = RandomForestRegressor(n_estimators=10)
        model = model.fit(X, Y)
        return model

    def getModelPredict(self, model, X):
        return model.predict(X)

    def predictTrackEmotion(self, model, trackJSON):
        X = self.arrayFromTracksJSON(trackJSON, self._trackFeatures)
        Y = self.normalize(self.getModelPredict(model, [X])[0].tolist())
        return self.getJSONFromMatrix(Y, emotions)

    def arrayFromTracksJSON(self, trackJSON, attrElements):
        attributes = []
        for a in attrElements:
            attributes.append(trackJSON[a])
        return attributes

if __name__ == '__main__':
    emotions = ["anger", "contempt", "disgust", "fear", "happiness", "neutral", "sadness", "surprise"]
    trackFeatures = ["danceability", "energy",  "mode", "time_signature", "acousticness", "instrumentalness",
                     "liveness", "loudness", "speechiness", "valence", "tempo"]
    ai = AIModule()
    j = ai.convertTargetInformationToJSON("./TargetTrainingDataSet.txt")
    ai.saveJSONToPath(".", "TargetTrucksInfo", j)
    featuresJSON = ai.getTrackJSONFromPath(".", "songs_info")
    targetJSON = ai.getTrackJSONFromPath(".", "TargetTrucksInfo")
    JSON = ai.mergeTrackJSONFields(featuresJSON, targetJSON)
    model = ai.generateTracksRegresionModel(JSON, trackFeatures, emotions)
