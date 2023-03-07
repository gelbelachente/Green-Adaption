import datetime

import dateutil.parser
import requests as requests
import pandas as pd
import datetime as dt


def transform(df,name):
    d = pd.DataFrame()
    d["start_time"] = df["time"].map(lambda x: str(x)[0:13])
    d[name + "_temp"] = df["temperature_2m (°C)"]
    d[name + "_humidity"] = df["relativehumidity_2m (%)"]
    d[name + "_precipitation"] = df["precipitation (mm)"]
    d[name + "_cloud_cover_total"] = df["cloudcover (%)"]
    d[name + "_direct_radiation"] = df["direct_radiation (W/m²)"]
    d[name + "_direct_normal_irradiance"] = df["direct_normal_irradiance (W/m²)"]
    d[name + "_wind_speed_100m"] = df["windspeed_100m (km/h)"]
    d[name + "_wind_direction_100m"] = df["winddirection_100m (°)"]
    d = d.set_index("start_time")
    return d

places = ["hamburg","köln","kassel","leipzig","augsburg","rügen_island","sylt"]
df = pd.DataFrame()

for place in places:
    df1 = pd.read_csv("weather_{}.csv".format(place))
    df1 = transform(df1,place)
    df = pd.merge(df,df1,how="outer",left_index=True,right_index=True)


def pad(a):
    if len(a) == 1:
        return "0" + a
    else:
        return a
def adjust_time(x):
    year = int(x[0:4])
    month = int(x[5:7])
    day = int(x[8:10])
    hour = int(x[11:13])
    time = datetime.datetime(year=year,month=month,day=day,hour=hour)-datetime.timedelta(hours=2)
    return "{}-{}-{}T{}".format(time.year, pad(str(time.month)), pad(str(time.day)), pad(str(time.hour)))


df.index = df.index.map(lambda x: adjust_time(x))
print(len(df.columns))
print(df.index)
df.to_csv("weather_data.csv")