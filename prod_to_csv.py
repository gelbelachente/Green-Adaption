import pandas as pd

"""Hier wird die beschädigte CSV-File der Bundesnetzagentur ausgelesen und die wichtigen Werte gespeichert"""

df = {"start_time": [], "solar": [], "onshore":[], "offshore": []}

with open("/realisierte_erzeugung.csv") as file:
    lines = file.readlines()
    lines = lines[1:len(lines)]
    for line in lines:
        data = line.split(";")
        (day,month,year) = data[0].split(".")
        time = "{}-{}-{}T{}".format(year,month,day,data[1][0:2])
        df["offshore"] =  df["offshore"] + [float(data[5].split(",")[0])]
        df["onshore"] = df["onshore"] + [float(data[6].split(",")[0])]
        df["solar"] = df["solar"] + [float(data[7].split(",")[0])]
        df["start_time"] = df["start_time"] + [time]

p = pd.DataFrame(df)
p.to_csv("D:\SYSTEM\Development\_Python\pycharm-workspace\SOLII\germany_production.csv")