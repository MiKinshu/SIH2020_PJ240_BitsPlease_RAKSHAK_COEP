#!/usr/bin/env python3

import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
import constants
import joblib
from pathlib import Path

# Model to predict emergency occurrence to user Possible features used:
# current_military_time, day_of_week, date, location, age, gender More features
# could be added to the model as the system collects more data, increasing the
# possible accuracy and bias of the model Output: "Probability" of emergency
# occuring to the user

# Convert location data into something more useful? Replace location with a
# feature that calculates cumulative proximity? Location danger rating: a custom
# algorithm that simply adds a danger point for every emergency based location
# in dataset, weighted more towards the closer locations. final features:
# location_danger, cyclically encoded current_military_time (as sin and cos),
# one hot encoded date, encoded age, binarized gender


# Return if model.save exists
def check_for_saved_model() -> bool:
    return bool(Path(constants.FILENAME).is_file())


# Train model on existing mocked dataset
def train(clean_df):
    features = pd.DataFrame(clean_df, columns=clean_df.columns[:-1])
    labels = pd.DataFrame(clean_df, columns=[clean_df.columns[-1]])
    train_features, test_features, train_labels, test_labels = train_test_split(
        features, labels, test_size = 0.25, random_state = 42)

    # Instantiate model with 1000 decision trees
    rf = RandomForestRegressor(n_estimators=1000, random_state=42)
    # Train the model on training data
    rf.fit(train_features, np.ravel(train_labels));

    # Save model, overwriting any previous model
    joblib.dump(rf, constants.FILENAME)


# Predict
def predict(encoded_features: list) -> float:
    saved_model = joblib.load(constants.FILENAME)
    return float(saved_model.predict(encoded_features))
