import requests
from emotion_api.emotion_api_keys import MICROSOFT_KEY, MICROSOFT_URL, EMOTIONS

headers  = {'Ocp-Apim-Subscription-Key': MICROSOFT_KEY, "Content-Type": "application/octet-stream" }


def get_image_emotion(image_path):
    image_data = open(image_path, "rb").read()
    url = MICROSOFT_URL + '?returnFaceId=true&returnFaceLandmarks=true&returnFaceAttributes=emotion'
    response = requests.post(url, headers=headers, data=image_data)
    response.raise_for_status()
    analysis = response.json()
    res = []
    if len(analysis) > 0:
        for em in EMOTIONS:
            res.append(analysis[0]['faceAttributes']['emotion'][em])
    return res


if __name__ == '__main__':
    get_image_emotion('test.jpg')
