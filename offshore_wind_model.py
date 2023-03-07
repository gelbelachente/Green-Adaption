import pandas as pd
import tensorflow
from keras import Sequential
from keras.layers import Dense, Dropout
from keras.optimizers import Adam
from sklearn.impute import SimpleImputer
pd.plotting.register_matplotlib_converters()



df = pd.read_csv("data.csv")

places = ["hamburg","rügen_island","sylt"]
_features = ["_temp","_wind_speed_100m"]
features = []
for city in places:
    for feature in _features:
        features.append(city + feature)

print(df["offshore"].describe())

train_dataset = df#.sample(frac=0.8)
test_dataset = df.drop(train_dataset.index)

_xTrain = train_dataset[features]
_xTest = test_dataset[features]

yTrain = train_dataset["offshore"].to_numpy()
yTest = test_dataset["offshore"].to_numpy()

model = Sequential([
    Dense(len(features),activation="relu",input_shape=[len(features)]),
    Dropout(rate=0.2),
    Dense(512, activation='relu'),
    Dense(1)
])

def mae(guess, target):
    return abs(guess - target)

def mse(guess,target):
    return pow(mae(guess,target),2)

def mre(guess, target):
    return guess - target

model.compile(
    loss=mae,
    optimizer=Adam(learning_rate=0.05),
    metrics=[mae,mse,mre]
)

hist = model.fit(_xTrain, yTrain, epochs=4) #, validation_data=(_xTest, yTest))


converter = tensorflow.lite.TFLiteConverter.from_keras_model(model)
tfmodel = converter.convert()

open("offshore_model.tflite","wb").write(tfmodel)