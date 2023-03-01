import math
from math import cos, degrees,sin,asin,acos,radians

from numpy import arcsin

dfa = []
dfb = []
#Germany
latitude = 51

for day in range(0,53):
    declination_angle = -23.45 * cos(radians((360 / 365) * (day*7 + 11)))
    dfa.append([])
    dfb.append([])
    for hour in range(24):
        local_hour = 15 * (hour - 11)
        a = sin(radians(declination_angle)) * sin(radians(latitude))
        b = cos(radians(declination_angle)) * cos(radians(latitude)) * cos(radians(local_hour))
        alpha = degrees(asin(a + b))
        gamma = degrees(acos((a-b)/cos(radians(alpha))))
        beta = gamma
        if local_hour >= 0:
            beta = 360 - gamma
        dfa[day].append(alpha)
        dfb[day].append(beta)

with open("sun_azimuth_angles","w+") as file:
    for week in dfb:
        for hour in week:
            file.write(str(int(hour)) + " ")
        file.write("\n")

with open("sun_elevation_angles","w+") as file:
    for week in dfa:
        for hour in week:
            file.write(str(int(hour)) + " ")
        file.write("\n")