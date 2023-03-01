
import pandas as pd
from sklearn.feature_selection import mutual_info_regression

"""Hier wurde des Mutual Information Score abgelesen"""

_features = ["_cloud_cover","_dew_point","_pressure","_rel_humidity","_sunshine","_temperature","_visibility",
            "_wind_direction","_wind_speed","_wind_gust_direction","_wind_gust_speed"]
places = ["Rostock","Husum","Bremerhaven","Berlin","Hannover","Dortmund","Kassel","Leipzig","Frankfurt","Stuttgart","Nuernberg","Muenchen"]

df = pd.read_csv("data.csv", index_col="start_time")
df = df.dropna()

print(df["offshore"].describe())
feature = [ p + _features[4] for p in places]
mi = mutual_info_regression(df[feature],df["solar"])
mis = pd.Series(mi,name="x",index=[feature])
print(mis)
print(mis.values.mean())
print(mis.median())