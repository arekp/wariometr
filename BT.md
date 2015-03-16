# Podłączenie bluetooth w arduino #

Pierwsze testy podłączenia BT powiodły się zestawiłem połączenie z ubuntu :)


# Szczegóły #
Jak zwykle żeby nie zapomnieć jak to się robi. Pewnie dróg jest wiele ja znalazłem taką.

na początek sprawdzamy czy nasz komputer widzi BT

```
hcitool scan
```
Odpowiedzą będzie lista widoczny BT w naszym zasięgu.

```
Scanning ...
	00:11:12:14:00:93	linvor
```

Następnie trzeba podpiąć do ubuntu z uprawnieniami root.

```
sudo rfcomm bind /dev/rfcomm0 00:11:12:14:00:93
```

Sprawdzenie poprawności podmontowania można sprawdzić wykonując polecenie rfcomm bez parametrów

no i odczytywanie danych

```
gtkterm --port /dev/rfcomm0 -s 9600
lub 
screen /dev/rfcomm0 -s 9600
```

I na koniec czasami przyda sie zwolnienie połaczenia

```
sudo rfcomm release /dev/rfcomm0
```

Jak na razie działa :)