#!/usr/bin/env python

# hardcoded relative path for file
FILENAME = "model.save"

# This is the final and cleaned dataset created from the original
# Here's the result of `head -n 5 datasets/dataset_v4_with_intensity.csv`
"""
age,gender,sin_time,cos_time,intensity
33,0,0.6050900843265542,0.796157013314386,0.14386198510394643
42,1,0.656389925593835,0.7544218087906259,0.8966936903553037
19,1,0.7439016592674368,0.6682891001199663,0.1497159095209778
22,0,0.6820776173323867,0.7312797849894213,0.09772539223571874
18,0,0.9785444696303552,0.20603572737718767,0.23113093873266732
21,0,0.9011577642009134,0.4334912732921055,0.053067334354856
21,1,0.805522920260268,0.5925646166751521,0.971474714753385
25,1,0.6507052118807238,0.7593304466642058,0.14408882882601903
19,0,0.6080663930444475,0.7938861767595626,0.8547215083120775
"""
FINAL_DATASET = "datasets/dataset_v4_with_intensity.csv"

# This file contains a list of all locations where an emergency occurred
# Since it only contains locations, we can assume that this doesn't cause any privacy issues
# We will use this to calculate the proximity of a user to danger locations
LOCATIONS_CSV = "datasets/lat_long_only.csv"
