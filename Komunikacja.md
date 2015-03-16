# Komunikacja i protokół #

Do komunikacji pomiędzy czujnikiem ciśnienia(adruino) **BMP085**  a telefonem wykorzystamy moduł bluetooth. Konieczne było opracowanie protokołu przy pomocy jakiego będziemy przekazywać dane.

**wario** - dla rozpoznania że komunikaty będą pochodziły od naszego urządzenia kazda linia z danymi będzie się zaczynała od wartości wario.

# Szczegóły #

$wario;temp;cisn;wysok

  * temp - wartość temperatury w C
  * cisn - wartość ciśnienia atmosferycznego w Pa
  * wysok - wartość wysokości obliczona na podstawie biblioteki czujnika przy założeniu ciśnienia odniesienia na poziomie morza 101325 Pa
Według wzoru

```
altitude = 44330 * (1.0 - pow(pressure /sealevelPressure,0.1903));

```