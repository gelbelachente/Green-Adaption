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

pd.set_option('display.max_columns', 5000)
#Weather Merge
df1 = pd.read_csv("weather_data_1.csv", index_col="start_time")
df2 = pd.read_csv("weather_data_2.csv", index_col="start_time")
df3 = pd.read_csv("weather_data_3.csv", index_col="start_time")
df4 = pd.read_csv("weather_data_4.csv", index_col="start_time")

df = pd.concat([df1,df2])
df = pd.concat([df,df3])
df = pd.concat([df,df4])

prod = pd.read_csv("germany_production.csv", index_col="start_time")

df = df.join(prod, how='right')

df["elevation_angle"] = df.index.map(lambda x: get_elevation_angle(x))
df["azimuth_angle"] = df.index.map(lambda x: get_azimuth_angle(x))

df["month"] = df.index.map(lambda x: int(str(x)[5:7]))
df["season"] = df.month.map(lambda x: x == 12 or x <= 2)
df["hour"] = df.index.map(lambda x: int(str(x)[-2:]))


print(df["azimuth_angle"].describe())
print(df["elevation_angle"].describe())

def limit(val,limit):
    if val >= limit:
        return val / 1000
    else:
        return val



df["solar"] = df.solar.map(lambda x: limit(x,16.0))
df["onshore"] = df.onshore.map(lambda x: limit(x,41.0))
df["offshore"] = df.offshore.map(lambda x: limit(x,7.0))

df.to_csv("data.csv")