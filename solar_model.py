import matplotlib.pyplot as plt
import pandas as pd
import tensorflow
from keras import Sequential
from keras.layers import Dense, Dropout
from keras.optimizers import Adam
from sklearn.impute import SimpleImputer
import seaborn as sns
pd.plotting.register_matplotlib_converters()

df = pd.read_csv("data.csv",index_col="start_time")

places = ["hamburg","köln","kassel","leipzig","augsburg"]
_features = ["_temp","_direct_normal_irradiance"]
features = ["azimuth_angle","elevation_angle"]
for city in places:
    for feature in _features:
        features.append(city + feature)


print(df["solar"].describe())

train_dataset = df#.sample(frac=0.8)
test_dataset = df.drop(train_dataset.index)

xTrain = train_dataset[features].to_numpy()
xTest = test_dataset[features].to_numpy()

yTrain = train_dataset["solar"].to_numpy()
yTest = test_dataset["solar"].to_numpy()

model = Sequential([
    Dense(len(features),activation="relu",input_shape=[len(features)],batch_size=1),
    Dropout(rate=0.2),
    Dense(512, activation='relu'),
    Dense(1,batch_size=1)
])

def mae(guess, target):
    return abs(guess - target)

def mre(guess, target):
    return guess - target

def mse(guess,target):
    return pow(mae(guess,target),2)

model.compile(
    loss="mean_absolute_error",
    optimizer=Adam(learning_rate=0.05),
    metrics=[mae,mse,mre]
)

hist = model.fit(xTrain, yTrain, epochs=1) # , validation_data=(xTest, yTest), batch_size=1)

converter = tensorflow.lite.TFLiteConverter.from_keras_model(model)
tfmodel = converter.convert()

open("solar_model.tflite","wb").write(tfmodel)