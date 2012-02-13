// include the library code:
#include <LiquidCrystal.h>
#include <Wire.h>
#include <BMP085.h>



// Connect VCC of the BMP085 sensor to 3.3V (NOT 5.0V!)
// Connect GND to Ground
// Connect SCL to i2c clock - on '168/'328 Arduino Uno/Duemilanove/etc thats Analog 5
// Connect SDA to i2c data - on '168/'328 Arduino Uno/Duemilanove/etc thats Analog 4
// EOC is not used, it signifies an end of conversion
// XCLR is a reset pin, also not used here

// initialize the library with the numbers of the interface pins
LiquidCrystal lcd(12, 11, 10, 9,8,7);
BMP085 bmp;

const int buttonPin = 2; 
const int buttonPin2 = 3; 

String tempString="TEMP: ";
String cisnString="Cisnienie: ";
String wysString="Wysok: ";
// these constants won't change.  But you can change the size of
// your LCD using them:
const int numRows = 2;
const int numCols = 16;


double  temp;
long  cisn;
double wys;
double wysBazowa;
double wysLiczona;
int wyswietlacz = 0; //okresla rodzaj wyswietlanego ekranu

// Deklaracja czasu dla serwomechanizmu (aktualny czas + 0,02 s)
unsigned long servo_time = millis() + 200;
// Deklaracja czasu dla przełączenia diody (aktualny czas + 0,5 s)
unsigned long led_time = millis() + 10000;

void setup() {
  // set up the LCD's number of columns and rows: 
  lcd.begin(numCols,numRows);
  lcd.write("Wariometr v0.1");
  lcd.setCursor(0, 1);
  lcd.write("Arekp");
  Serial.begin(9600);
  bmp.begin();  
    // initialize the pushbutton pin as an input:
  pinMode(buttonPin, INPUT);     
  pinMode(buttonPin2, INPUT);
}

void loop() {
  temp=bmp.readTemperature();
  cisn=bmp.readPressure();
  wys=bmp.readAltitude();
  
if(digitalRead(buttonPin) == HIGH){
  lcd.clear();
  wyswietlacz++;
  if (wyswietlacz==4){wyswietlacz=0;}
}

if (wyswietlacz==1){ //opcja liczenia wysokosci i zerowanie wysokosci
    if(digitalRead(buttonPin2) == HIGH){
      lcd.clear();
      wysBazowa=wys;
    }
  }

// wysLiczona=wys-wysBazowa; //obliczmy wysokość wzgledem pozycji początkowej

// Pobranie aktualnego czasu
  unsigned long time = millis();
  // * Sprawdź czy minęło 0,02 s
 if (time >= servo_time)
 {
Serial.print("jestesmy na ekranie: "); Serial.println(wyswietlacz);
 //  printDouble(temp,2);
   // Serial.println(bmp.readTemperature());
    printDouble(wysBazowa,1);
    printDouble(wys,1);
     printDouble(wysLiczona,1);
    //Serial.println(bmp.readPressure());
    //   Serial.println(temp+tem);
   // Serial.println(cisnString+cisn);
   servo_time = millis() + 200;
  }
 // * Sprawdź czy minęło 0,5 s
  if (time >= led_time)
  { 
    wysLiczona=wys-wysBazowa;
    Serial.println("wyswietlacz");
   // wysLiczona=wysBazowa-wys; //obliczmy wysokość wzgledem pozycji początkowej
    displayStatus();
    led_time = millis() + 1000;
  }

}

void displayStatus(){
 // Serial.print("EKRAN: "); Serial.println(wyswietlacz);
  switch (wyswietlacz)
    {
      case 0:
        lcdStart();
        break;
      case 1:
       lcdWysokosc();
        break;
         case 2:
       lcd2();
        break;
         case 3:
       lcd3();
        break;
      default:
        lcdStart();
        break;
    }
}
void lcdStart()
{

   lcd.home();
   lcd.write("0T ");  lcdPrintDouble(temp,2); lcd.write(" A ");lcdPrintDouble(wys,2); 
   lcd.setCursor(0, 1);
   lcd.print("Cisn: ");lcd.print(cisn);lcd.write(" Pa");
}
void lcdWysokosc()
{

   lcd.home();
   //lcd.write("1W:");  lcdPrintDouble(wysBazowa,2);
   lcd.write("1Wys: ");lcdPrintDouble(wys,1); lcd.write(" m");
   lcd.setCursor(0, 1);
   lcd.print("Wysokosc: ");lcdPrintDouble(wysLiczona,1);lcd.write(" m");
}
void lcd2()
{

   lcd.home();
   lcd.write("2Temp: ");  lcdPrintDouble(temp,2); lcd.write(" *C");
   lcd.setCursor(0, 1);
   lcd.print("Cisnie: ");lcd.print(cisn/100);lcd.print(" hPa");
}
void lcd3()
{

   lcd.home();
   lcd.write("3T ");  lcdPrintDouble(temp,2); lcd.write(" A ");lcdPrintDouble(wys,2); 
   lcd.setCursor(0, 1);
   lcd.print("Wysokosc: ");lcd.print(wysLiczona);
}

 void lcdPrintDouble( double val, byte precision){
  // prints val on a ver 0012 text lcd with number of decimal places determine by precision
  // precision is a number from 0 to 6 indicating the desired decimial places
  // example: printDouble( 3.1415, 2); // prints 3.14 (two decimal places)

  if(val < 0.0){
    lcd.print('-');
    val = -val;
  }

  lcd.print (int(val));  //prints the int part
  if( precision > 0) {
    lcd.print("."); // print the decimal point
    unsigned long frac;
    unsigned long mult = 1;
    byte padding = precision -1;
    while(precision--)
  mult *=10;

    if(val >= 0)
 frac = (val - int(val)) * mult;
    else
 frac = (int(val)- val ) * mult;
    unsigned long frac1 = frac;
    while( frac1 /= 10 )
 padding--;
    while(  padding--)
 lcd.print("0");
    lcd.print(frac,DEC) ;
  }
}

void printDouble( double val, unsigned int precision){
// prints val with number of decimal places determine by precision
// NOTE: precision is 1 followed by the number of zeros for the desired number of decimial places
// example: printDouble( 3.1415, 100); // prints 3.14 (two decimal places)
String wy;
    Serial.print (int(val));  //prints the int part
    Serial.print("."); // print the decimal point
    unsigned int frac;
    if(val >= 0)
	  frac = (val - int(val)) * precision;
    else
	  frac = (int(val)- val ) * precision;
    Serial.println(frac,DEC) ;
}
