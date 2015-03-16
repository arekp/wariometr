# Założenia dla projektu #

Tutaj postaram się opisać co ma robić takie cudo, albo inaczej do czego dążę żeby potem móc od siebie tego wymagać :)


## Hardware ##
Jako że nie jestem żadnym elektronikiem, a zawsze chciałem z poziomu oprogramowania dotknąć fizycznego świata mam zamiar skorzystać z modułu startowego opartego na arduiono UNO. Układ ten pozwala na proste oprogramowanie mikroprocesora i wyciągniecie z niego parametrów z rożnych czujników.

i tak cały wariometr będzie składał się :
  * arduiono UNO http://www.arduino.cc/
  * Czujnika ciśnienia BMP085
  * Wyświetlacza LCD 2x16
  * Bluetooth Shield dla Arduino - dopiero w fazie drugiej do komunikacji z androidem - zrezugnowałem z tego na rzecz tańszego Wireless Bluetooth RS232
  * dodatkowo klika przycisków, jakąś diodę, brzęczek, no i pewnie jakiś rezystor

## Software ##

Funkcjonalności oprogramowania na wariometrze :
  * Wyświetlenie cisnienia\_na LCD
  * Wyświetlenie temperatury
  * Wyświetlenie wysokości
  * Określenie wysokości względnej, czyli w stosunku do danego punktu
  * Informacje dzwiękowe przy wznoszeniu i opadaniu
  * Wyświetlanie prędkości wznoszenia i opadania
  * Przekazywanie wszystkich parametrów przez bluethooth



Funkcjonalności oprogramowania na androidzie :
  * Śledzenie trasy lotu GPS
  * Prezentacja kompasu
  * Prezentacja wszystkich parametrów z wariometru
  * Gromadzenie danych jako dziennik lotów
  * Wyliczanie dystansu, maksymalnej wysokości
  * Możliwość śledzenia trasy loty na podstawie tras innych pilotów
  * Eksport danych (może jakiś portal, cvs, i co jeszcze przyjdzie do głowy)
  * i co jeszcze będzie potrzebne

## Kosztorys ##

  * Arduino UNO - 110 PLN
  * Czujnik ciśnienia BMP085 - 67PLN
  * Wyświetlacze LCD 2x13 - 20PLN
  * Wireless Bluetooth RS232 - 25PLN
  * Płytka i podzespoły biper, przycisk itd - 30PLN