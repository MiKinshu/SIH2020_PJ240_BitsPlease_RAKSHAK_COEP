#!/usr/bin/env python

import pandas as pd
import model
import constants


# Create a new model.save model file from the csv file that is our input
def create(csv):
    df = pd.read_csv(csv)
    model.train(df)


if __name__  = "__main__":
    create(constants.FINAL_DATASET)
