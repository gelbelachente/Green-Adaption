import pandas as pd
import tensorflow
from keras import Sequential
from keras.layers import Dense, Dropout
from keras.optimizers import Adam
from sklearn.impute import SimpleImputer
pd.plotting.register_matplotlib_converters()

df = pd.read_csv("data.csv")

places = ["Rostock","Husum","Bremerhaven","Berlin","Hannover","Dortmund","Kassel","Leipzig","Frankfurt","Stuttgart","Nuernberg","Muenchen"]
_features = ["_rel_humidity","_sunshine","_temperature"]
features = ["azimuth_angle","elevation_angle"]
for city in places:
    for feature in _features:
        features.append(city + feature)

print(df["solar"].describe())

train_dataset = df.sample(frac=0.8)
test_dataset = df.drop(train_dataset.index)

imputer = SimpleImputer()
_xTrain = train_dataset[features]
_xTest = test_dataset[features]
xTrain = pd.DataFrame(imputer.fit_transform(_xTrain))
xTest = pd.DataFrame(imputer.fit_transform(_xTest))
xTrain.columns = _xTrain.columns
xTest.columns = _xTest.columns

yTrain = train_dataset["solar"].to_numpy()
yTest = test_dataset["solar"].to_numpy()

model = Sequential([
    Dense(len(features),activation="relu",input_shape=[len(features)]),
    Dropout(rate=0.2),
    Dense(512, activation='relu'),
    Dropout(rate=0.2),
    Dense(512, activation='relu'),
    Dropout(rate=0.2),
    Dense(512, activation='relu'),
    Dense(1)
])

def mae(guess, target):
    return abs(guess - target)

def mre(guess, target):
    return guess - target

def mse(guess,target):
    return pow(mae(guess,target),2)

model.compile(
    loss=mae,
    optimizer=Adam(learning_rate=0.05),
    metrics=[mae,mse,mre]
)

hist = model.fit(xTrain, yTrain, epochs=4, validation_data=(xTest, yTest))


keras_file = "solar_model.h5"
tensorflow.keras.models.save_model(model,keras_file)
converter = tensorflow.lite.TFLiteConverter.from_keras_model(model)
tfmodel = converter.convert()

open("solar_model.tflite","wb").write(tfmodel)