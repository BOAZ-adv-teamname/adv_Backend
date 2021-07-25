import pandas as pd
import numpy as np
import torch
import pickle
import re
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import TfidfVectorizer
from sentence_transformers import SentenceTransformer, util

from model.precedent_dao import *

def vectorization(DATABASE,NEWS):
    model_path = './model/-2021-07-14_21-48-16'
    embedder = SentenceTransformer(model_path)
    query = NEWS

    if torch.cuda.is_available():
        with open('./model/Sum_Database/embeddings.pkl', "rb") as fIn:
            stored_data = pickle.load(fIn)
            corpus_embeddings = stored_data['embeddings']
    else:
        with open('./model/Sum_Database/embeddings_cpu.pkl', "rb") as fIn:
            stored_data = pickle.load(fIn)
            corpus_embeddings = stored_data['embeddings']
        

    query_embedding = embedder.encode(query, convert_to_tensor=True)
    X = (corpus_embeddings,query_embedding)
    return X

def Cosine_similarity(X,DATABASE):
    top_k = 3 ; threshhold=0.3
    news = X[1]
    laws = X[0]
    cos_scores = util.pytorch_cos_sim(news, laws)[0]
    cos_scores = cos_scores.cpu()

    top_results = np.argpartition(-cos_scores, range(top_k))[0:top_k]

    final=[]
    for idx in top_results[0:top_k]:
        if cos_scores[idx] < threshhold:
            final.append('유사한 판례가 없습니다.')
        else:
            final.append(DATABASE[idx].strip())
    return final
    
def make_simtext(news):
    DATABASE = get_precedent_data(local=True)
    NEWS = news
    X=vectorization(DATABASE,NEWS)
    final = Cosine_similarity(X,DATABASE)

    output = []
    for i,f in enumerate(final):
        output.append(f)

        text_file = open(f"./service/Output/output_{i+1}.txt", "w",encoding='UTF-8')
        n = text_file.write(f)
        text_file.close()
    
    return output