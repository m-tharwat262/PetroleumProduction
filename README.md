# <img src="screenshots/app_icon.png" alt="app icon" width="35"/> Petroleum Oil and Gas Production App
Calculate IPR and Travers curves that show the productive capacity and well performance and determine the pressure distribution and rates along oil and gas wells.

## IPR Curve
The app provides three methods to calculate the IPR curve
- **Vogel and Standing**
- **Fetkovich**
- **Jones et al**

## Travers Curve
You can get the Travers Curve Using the IPR data and outflow data
with different tubing size
- **1.995 in**
- **2.441 in**
- **2.992 in**

## Test the app
you can use the values provided in the tables below to test the app.

### IPR Test

Using **Vogel and Standing** method with no skin Effect.

| Header 1                          | Value| Unit   |
|-----------------------------------|------|--------|
| Average Reservoir Pressure (Pres) | 3200 | Psig   |
| Bubble Point Pressure (Pb)        | 2200 | Psig   |
| Pressure (Pwf)                    | 1340 | Psig   |
| Flow Rate (QI)                    | 1000 | STB/Day|

### Travers Curve Test

Using **IPR data** above and the outflow data in the table below to get the travers cure.

| Header 1                           | Value | Unit |
|------------------------------------|-------|------|
| Depth (d)                          | 7000  | Ft   |
| Well Head Pressure (Pwh)           | 200   | Psig |
| Gas Liquid Ration (GLR)            | 500   | Psig |
| American Petroleum Institute (API) | 35    | none |
| Gas Specific Gravity (SG)          | 0.65  | none |
| Water Fraction (Fw)                | 50    | none |
| diameter (d1)                      | 1.995 | in   |
| diameter (d2)                      | 1.995 | in   |
| diameter (d3)                      | 1.995 | in   |
| diameter (d4)                      | ..... | in   |
| diameter (d5)                      | ..... | in   |

Depth (d) = 7000 ft

Well Head Pressure = 200 Psig

Gas Liquid Ration (GLR) = 500 scf/STB

American Petroleum Institute (API) = 35

Gas Specific Gravity (SG) = 0.65

Water Fraction (Fw) = 50

Tubing Size Values :

d1 = 1.995 in

d2 = 2.441 in

d3 = 2.992 in

The Results will gives you graph contains both IPR and Travers Cureves Tubing Size Diameteres used like that :

![IPR curve](/screenshots/ipr_curve.png)![IPR curve](https://github.com/m-tharwat262/PetroleumProduction/blob/master/screenshots/ipr_curve.png)





Download App [here](https://pages.github.com/)


