# -*- coding: utf-8 -*-
"""
Created on Thu Jan 28 00:44:25 2021

@author: chakati
"""
import cv2
import numpy as np
import os
import tensorflow as tf
import pandas as pd
from glob import glob
from tqdm import tqdm
from handshape_feature_extractor import HandShapeFeatureExtractor
from PIL import Image
from frameextractor import frameExtractor

## import the handfeature extractor class

# =============================================================================
# Get the penultimate layer for trainig data
# =============================================================================
# your code goes here
# Extract the middle frame of each gesture video
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
encoding = 'utf-8'
directory = os.fsencode('./traindata')

n = 0
train_gesture_name = []
train_class = []
for root, dirs, files in os.walk(os.path.abspath(directory)):
    for file in files:
        frameExtractor(str(os.path.join(root, file),encoding),'./frame',n)
        train_gesture_name.append(str(file, encoding).split('_')[0])
        train_class.append(frametoclass(str(file, encoding).split('_')[0]))
        n = n + 1
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
    image = cv2.imread(i,cv2.IMREAD_UNCHANGED)
    if len(image.shape) == 3:
        image = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
    train_feature.append(HandShapeFeatureExtractor.get_instance().extract_feature(image))

# =============================================================================
# Get the penultimate layer for test data
# =============================================================================
# your code goes here 
# Extract the middle frame of each gesture video
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
directory_test = os.fsencode('./TestData')
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

#test_data['Gesture Name'] = test_gesture_name

test_feature = []
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
test_feature_list = []
for i in test_images:
    image = cv2.imread(i,cv2.IMREAD_UNCHANGED)
    if len(image.shape) == 3:
        image = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
    test_feature.append(HandShapeFeatureExtractor.get_instance().extract_feature(image))


# =============================================================================
# Recognize the gesture (use cosine similarity for comparing the vectors)
# =============================================================================
predict_list = []
for i in test_feature:
    s = tf.keras.losses.cosine_similarity(i,train_feature,axis=-1).numpy()
    predict_list.append(train_data['class'][np.argmin(s)])

test_data['Output Label'] = predict_list


test_data.to_csv('Results.csv',header=False,index=False)