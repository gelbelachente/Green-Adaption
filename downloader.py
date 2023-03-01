import dateutil.parser
import requests as requests
import pandas as pd
import datetime as dt

"""Hier werden die Wetter-daten stückchenweise heruntergeladen"""

df = {'start_time' : []}
p = True
with open("/places") as file:
    for line in file.readlines():
        (name,lat,lon) = line.split(" ")
        params = {"date": "2019-01-01T13:00+02:00", "last_date": "2020-02-27T12:00+02:00", "lat": lat, "lon":lon}
        resp = requests.get("https://api.brightsky.dev/weather",params)
        data = resp.json()['weather']
        for unit in data:
            #T03:00:00+00:00" => 0<->1
            time = (dateutil.parser.parse(unit["timestamp"]) - dt.timedelta(hours=3))
            t =  "{}-{}-{}T{}".format(time.year,time.month,time.day,time.hour)
            df[name + "_cloud_cover"] = df.get(name + "_cloud_cover",[]) +  [unit["cloud_cover"]]
            df[name + "_dew_point"] = df.get(name + "_dew_point",[]) + [unit["dew_point"]]
            df[name + "_pressure"] = df.get(name + "_pressure",[]) + [unit["pressure_msl"]]
            df[name + "_rel_humidity"] = df.get(name + "_rel_humidity",[]) + [unit["relative_humidity"]]
            df[name + "_sunshine"] = df.get(name + "_sunshine",[]) + [unit["sunshine"]]
            df[name + "_temperature"] = df.get(name + "_temperature",[]) + [unit["temperature"]]
            df[name + "_visibility"] = df.get(name + "_visibility",[]) + [unit["visibility"]]
            df[name + "_wind_direction"] = df.get(name + "_wind_direction",[]) + [unit["wind_direction"]]
            df[name + "_wind_speed"] = df.get(name + "_wind_speed",[]) + [unit["wind_speed"]]
            df[name + "_wind_gust_direction"] = df.get(name + "_wind_gust_direction",[]) + [unit["wind_gust_direction"]]
            df[name + "_wind_gust_speed"] = df.get(name + "_wind_gust_speed",[]) + [unit["wind_gust_speed"]]
            if p:
                df["start_time"].append(t)
        p = False
        print(name)

data = pd.DataFrame(df)
data = data.set_index("start_time")
data.to_csv("D:\SYSTEM\Development\_Python\pycharm-workspace\SOLII\weather_data_4.csv")