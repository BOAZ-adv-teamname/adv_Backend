import pymongo
from pymongo import MongoClient

import json
import pandas as pd
import numpy as np
import re

import os

def read_news_data(path):
    news_data = list(pd.read_csv(path))[0]
    string = re.sub("[^ㄱ-ㅎㅏ-ㅣ가-힣 ]"," ", news_data)
    string = re.sub(r'[·@%\\*=()/~#&\+á‘’“”?\xc3\xa1\-\|\:\;\!\-\,\_\~\$\'\"\[\]]', ' ', string) #remove punctuation
    string = re.sub(r'\n',' ', string)     # remove enter
    string = re.sub(r'[0-9]+', '', string) # remove number
    string = re.sub(r'\s+', ' ', string)   #remove extra space
    cleaned_news_data = re.sub(r'<[^>]+>',' ',string) #remove Html tags
    return cleaned_news_data

def get_precedent_data(local):
    # local csv 파일
    if local:
        sum_database = pd.read_csv('./model/Sum_Database/concat.csv')
    # mongo db
    else:
        try:
            client = MongoClient("localhost:27017")
            db = client.precedent
            collection = db.precedent
            sum_database = collection.find({})
        except:
            print('mongo db connect error')
            sum_database = pd.read_csv('./model/Sum_Database/concat.csv')

    sum_database=sum_database[sum_database.kobart_sum.notna()]
    return sum_database.kobart_sum.values.tolist()
