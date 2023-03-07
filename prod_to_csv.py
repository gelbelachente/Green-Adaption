import pandas as pd

df = pd.read_csv("production_2020.csv")
df = pd.concat([df,pd.read_csv("production_2021.csv")],axis=0)
df = pd.concat([df,pd.read_csv("production_2022.csv")],axis=0)

df["solar"] = df["Solar  - Actual Aggregated [MW]"].map(lambda x: float(str(x).replace(".","").replace(",","."))/1000)
df["onshore"] = df["Wind Onshore  - Actual Aggregated [MW]"].map(lambda x: float(str(x).replace(".","").replace(",","."))/1000)
df["offshore"] = df["Wind Offshore  - Actual Aggregated [MW]"].map(lambda x: float(str(x).replace(".","").replace(",","."))/1000)
df["start_time"] = df["MTU"]
df = df[["start_time","solar","onshore","offshore"]]
df["start_time"] = df["start_time"].map(lambda x: str(x)[6:10] + "-" + str(x)[3:5] + "-" + str(x)[0:2] + "T" + str(x)[11:13])
df = df.groupby('start_time').agg({'solar': 'mean', 'onshore': 'mean', 'offshore': 'mean'})
df = df[["solar","onshore","offshore"]]
print(df.index)
df.to_csv("germany_production.csv")
