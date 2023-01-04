# -*- coding: utf-8 -*-
from frameextractor import frameExtractor
import cv2
import os
import numpy as np
import tensorflow as tf
import pandas as pd
from glob import glob
from tqdm import tqdm
from handshape_feature_extractor import HandShapeFeatureExtractor
from PIL import Image

def frametoclass(file):
    numbers = {
        "Num0" : 0,
        "Num1" : 1,
        "Num2" : 2,
        "Num3" : 3,
        "Num4" : 4,
        "Num5" : 5,
        "Num6" : 6,
        "Num7" : 7,
        "Num8" : 8,
        "Num9": 9,
        "FanDown": 10,
        "FanOn": 11,
        "FanOff": 12,
        "FanUp": 13,
        "LightOff": 14,
        "LightOn": 15,
        "SetThemo": 16
    }

    return numbers.get(file, None)

def frametoclasstest(file):
    numbers = {
        "0" : 0,
        "1" : 1,
        "2" : 2,
        "3" : 3,
        "4" : 4,
        "5" : 5,
        "6" : 6,
        "7" : 7,
        "8" : 8,
        "9": 9,
        "DecreaseFanSpeed": 10,
        "DecereaseFanSpeed":10,
        "FanOn": 11,
        "FanOff": 12,
        "IncreaseFanSpeed": 13,
        "LightOff": 14,
        "LightOn": 15,
        "SetThermo": 16
    }

    return numbers.get(file, None)





#frameExtractor('./video/SetThemo_PRACTICE_3_LIAO.mp4','./frame',0)


encoding = 'utf-8'
directory = os.fsencode('./video')

n = 0
train_gesture_name = []
train_class = []
for root, dirs, files in os.walk(os.path.abspath(directory)):
    for file in files:
        frameExtractor(str(os.path.join(root, file),encoding),'./frame',n)
        train_gesture_name.append(str(file, encoding).split('_')[0])
        train_class.append(frametoclass(str(file, encoding).split('_')[0]))
        n = n + 1
        #print(str(os.path.join(root, file),encoding))
train_images = glob('./frame/*.png')
for i in train_images:
    im = Image.open(i)
    out = im.transpose(Image.FLIP_LEFT_RIGHT)
    out.save(i)

train_data = pd.DataFrame()
train_image = []
for i in tqdm(range(len(train_images))):
    # creating the image name
    train_image.append(train_images[i].split('/')[2])

train_data['images'] = train_image
train_data['gesture_name'] = train_gesture_name
train_data['class'] = train_class

train_feature = []
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
train_feature_list = []
for i in train_images:
    train_feature_list.append(i)
    image = cv2.imread(i,cv2.IMREAD_UNCHANGED)
    train_feature.append(HandShapeFeatureExtractor.get_instance().extract_feature(image))


#print(train_feature_list)
#train_data['feature'] = train_feature
#print(train_data)

directory_test = os.fsencode('./testdata')

n = 0
test_gesture_name = []
test_class = []
test_video = []
for root, dirs, files in os.walk(os.path.abspath(directory_test)):
    for file in files:
        frameExtractor(str(os.path.join(root, file),encoding),'./frame_test',n)
        test_gesture_name.append(str(file, encoding).split('-')[2].split('.')[0])
        test_video.append(str(file, encoding))
        test_class.append(frametoclasstest(str(file, encoding).split('-')[2].split('.')[0]))
        n = n + 1

test_data = pd.DataFrame()
test_images = glob('./frame_test/*.png')
test_image = []
for i in tqdm(range(len(test_images))):
    # creating the image name
    test_image.append(test_images[i].split('/')[2])

#test_data['video'] = test_video
#test_data['images'] = test_image
test_data['Gesture Name'] = test_gesture_name
#test_data['class'] = test_class
#print(test_data)

test_feature = []
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
test_feature_list = []
for i in test_images:
    test_feature_list.append(i)
    image = cv2.imread(i,cv2.IMREAD_UNCHANGED)
    test_feature.append(HandShapeFeatureExtractor.get_instance().extract_feature(image))


#print(test_feature_list)
#test_data['feature'] = test_feature
#print(test_data)

#train_data.to_csv('train_new.csv',header=True, index=False)
#test_data.to_csv('test_new.csv',header=True, index=False)

predict_list = []
for i in test_feature:
    s = tf.keras.losses.cosine_similarity(i,train_feature,axis=-1).numpy()
    predict_list.append(train_data['class'][np.argmin(s)])

test_data['Output Label'] = predict_list
n = 0
r = 0
for i in test_data['prediction']:
    if i == test_data['class'][n]:
        r = r + 1
    n = n + 1

print(test_data)
#print(r/51)



