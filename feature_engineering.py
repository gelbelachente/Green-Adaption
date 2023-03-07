
import pandas as pd
from sklearn.feature_selection import mutual_info_regression

"""Hier wurde des Mutual Information Score abgelesen"""

_features = ["_temp","_humidity","_precipitation","_cloud_cover_total","_direct_radiation","_direct_normal_irradiance","_wind_speed_100m",
             "_wind_direction_100m"]
places = ["hamburg","köln","kassel","leipzig","augsburg"]

df = pd.read_csv("data.csv", index_col="start_time")
df = df.dropna()

print(df["solar"].describe())
feature = [ p + _features[0] for p in places]
mi = mutual_info_regression(df[feature],df["solar"])
mis = pd.Series(mi,name="x",index=[feature])
print(mis)
print(mis.values.mean())
print(mis.median())