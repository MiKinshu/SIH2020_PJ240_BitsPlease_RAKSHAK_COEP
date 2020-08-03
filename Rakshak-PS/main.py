#!/usr/bin/env python

from flask import Flask
import model
import encoding
import proximity
import numpy as np
import random
import os

app = Flask(__name__)


@app.route("/")
def status() -> str:
    status = str("Model trained: " + str(model.check_for_saved_model()))
    return status


@app.route("/predict/<military_time>/<lat>/<longitude>/<age>/<gender>")
def predict(military_time, lat, longitude, age, gender) -> float:
    # get proximity score
    location_node = [float(lat), float(longitude)]
    proximity_score = proximity.get_proximity(location_node)

    # get prediction of emergency
    sin_time = encoding.sin_time(encoding.military_time_in_minutes_fn(military_time))
    cos_time = encoding.cos_time(encoding.military_time_in_minutes_fn(military_time))
    prediction = model.predict(
        np.asarray([int(age), int(gender), sin_time, cos_time]).reshape(1, -1))
    # This returns a "safety score".
    # convert to a percentage if necessary by multiplying with 100
    extreme_danger_score = proximity_score * prediction
    extreme_safety_score = 1 - extreme_danger_score

    # mocking result for now
    # return(str(round(encoding.score_fencing(extreme_danger_score))))
    return str(round(random.uniform(5, 15), 2))


# run it directly via python3 main.py
if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    app.run(host='0.0.0.0', port=port)
