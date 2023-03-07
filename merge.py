import datetime

import pandas as pd

"""Hier wird der Trainingsdatensatz erstellt und zusammengefügt. Unter anderem werden hier auch neue Features erstellt."""

def read_file(path):
    a = []
    with open(path) as file:
        for line in file.readlines():
            a.append(list(map(lambda x: int(x),line.split(" ")[0:24])))
    return a

elevation_angle = read_file("sun_elevation_angles")
azimuth_angle = read_file("sun_azimuth_angles")

def get_azimuth_angle(x):
    month = int(str(x)[5:7])
    day = int(str(x)[8:10])
    hour = int(str(x)[11:13])
    yday = ((month-1) * 30) + day
    return azimuth_angle[(yday - 1) % 7][hour]


def get_elevation_angle(x):
    month = int(str(x)[5:7])
    day = int(str(x)[8:10])
    hour = int(str(x)[11:13])
    yday = ((month-1) * 30) + day
    return elevation_angle[(yday - 1) % 7][hour]


df = pd.read_csv("weather_data.csv",index_col="start_time").dropna()
print(len(df))
prod = pd.read_csv("germany_production.csv", index_col="start_time").dropna()
print(len(df))
df = df.join(prod, how='right')
print(len(df))
df = df.dropna()
df["elevation_angle"] = df.index.map(lambda x: get_elevation_angle(x))
df["azimuth_angle"] = df.index.map(lambda x: get_azimuth_angle(x))

print(df["elevation_angle"].describe())

print(len(df))
df.to_csv("data.csv")