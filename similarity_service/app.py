from flask import Flask, config, render_template, request, jsonify
from flask_cors import CORS, cross_origin
from flask_restful import reqparse
#from flask_cache import Cache

from model.precedent_dao import *
from service.similarity_service import *

import time

app = Flask(__name__)
cors = CORS(app, resources={r"/pin": {"origins": "*"}})
app.config['CORS_HEADERS'] = 'Content-Type'

@app.route('/')
@cross_origin(origin='*',headers=['Content-Type','Authorization'])
def index():
  return "ok"

@app.route('/pin', methods = ['POST','OPTIONS'])
@cross_origin()
def pin():
  start = time.time()
  print("start")
  parser = reqparse.RequestParser()
  parser.add_argument('news', type=str)
  args = parser.parse_args()
  news = args['news']

  similar_precedent = make_simtext(news)

  data = {
    "news" : news,
    "similar_precedent1":similar_precedent[0],
    "similar_precedent2":similar_precedent[1],
    "similar_precedent3":similar_precedent[2]
  }
  print(start-time.time())

  return jsonify(data)

if __name__=="__main__":
    app.run(host = 'localhost', port=5000, debug=True)
