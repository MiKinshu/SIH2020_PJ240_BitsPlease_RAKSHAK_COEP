#!/usr/bin/env python3

# Model to predict emergency occurrence to user
# Possible features used: current_military_time, day_of_week, date, location, age, gender
# More features could be added to the model as the system collects
# more data, increasing the possible accuracy and bias of the model
# Output: "Probability" of emergency occuring to the user

import constants

# Return if model.save exists
def check_for_saved_model() -> bool:
    pass


# Train model on existing mock dataset
def train(clean_df):
    # Format data
    # Instantiate model with 1000 decision trees
    # Train the model on training data
    # Save model, overwriting any previous model
    pass


# Predict
def predict(encoded_features: list) -> float:
    pass
