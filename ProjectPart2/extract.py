import cv2
import os
import numpy as np
from scipy import spatial
import tensorflow as tf
from handshape_feature_extractor import HandShapeFeatureExtractor
from PIL import Image
import pandas as pd
from glob import glob
from tqdm import tqdm


train_images = glob('./frame/*.png')
for i in train_images:
    im = Image.open(i)
    out = im.transpose(Image.FLIP_LEFT_RIGHT)
    out.save(i)


#print(spatial.distance.cosine(test_feature[0],train_feature))













