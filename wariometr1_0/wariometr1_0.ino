// include the library code:
#include <LiquidCrystal.h>
#include <Wire.h>
#include <BMP085.h>
#include <MeetAndroid.h>


// Connect VCC of the BMP085 sensor to 3.3V (NOT 5.0V!)
// Connect GND to Ground
// Connect SCL to i2c clock - on '168/'328 Arduino Uno/Duemilanove/etc thats Analog 5
// Connect SDA to i2c data - on '168/'328 Arduino Uno/Duemilanove/etc thats Analog 4
// EOC is not used, it signifies an end of conversion
// XCLR is a reset pin, also not used here

// initialize the library with the numbers of the interface pins
LiquidCrystal lcd(12, 11, 10, 9,8,7);
BMP085 bmp;
MeetAndroid meetAndroid;

const int buttonPin = 2; 
const int buttonPin2 = 3; 
int lastButtonState = 0;  
int speakerOut = 6;
int ledOff = 4;

// these constants won't change.  But you can change the size of
// your LCD using them:
const int numRows = 2;
const int numCols = 16;


double  temp;
long  cisn;
double wys;
double wysBazowa;
double wysLiczona;
double Vopadania=0.5;//wartosc predokosci opadania aby zaczas sygnalizowac 
double Vwznoszenia=1;//wartosc wznoszenia aby sygnalizowac.
double v0; // do oblicznia predkosci 
double vWysl; //predkosc na podstawie wyliczonych wysokosci
double vcisn;//predkosc na podstawie cisnienia
long cisnBazowe; // cisnienie zapamietane z miejsca startu
long cisnTmp; // cisnienie do pomiaru predkosci do koreslenia dystansu w danej jednostce czasu

int wyswietlacz = 0; //okresla rodzaj wyswietlanego ekranu
int czasSerial=500; // Czestotliwosc wysyłania komunikatów BT
int czasLCD = 500; // Czestotliwosc wysyłania danych na ekran LCD
int czasWznoszenia=2000;// czas pomiaru predkosci wznoszenia
int czasDzwieku=10;

int dzwiekOFF=0;//o wyłaczone inne właczone

// Deklaracja czasu dla wysyłania komunikatów przez BT (aktualny czas + 0,2 s)
unsigned long czas_serial = millis() + czasSerial;
// Deklaracja czasu dla wyswietlacza diody (aktualny czas + 0,5 s)
unsigned long czas_lcd = millis() + czasLCD;
unsigned long czas_Wznoszenia = millis() +czasWznoszenia;
unsigned long czas_Dzwieku= millis() +czasDzwieku;

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
  pinMode(speakerOut, OUTPUT);
  pinMode(ledOff, OUTPUT);
  digitalWrite(ledOff, HIGH); // wlaczenie podswetlenia LCD
  
  meetAndroid.registerFunction(lcdoff, 'x');
    meetAndroid.registerFunction(bipoff, 'q');
  meetAndroid.registerFunction(wysF, 'w');
  meetAndroid.registerFunction(cisnF, 'c');
  meetAndroid.registerFunction(tempF, 't');
  meetAndroid.registerFunction(predpF, 'v');
  meetAndroid.registerFunction(androidLCD, 'Z');
 meetAndroid.registerFunction(androidWYS, 'Y');  
}

void loop() {
  meetAndroid.receive();

  temp=bmp.readTemperature();
  cisn=bmp.readPressure();
  wys=bmp.readAltitude();
  
if (digitalRead(buttonPin) != lastButtonState) {  
if(digitalRead(buttonPin) == HIGH){
  zmianaLCD();
}
//wylaczenie/wlaczenie wyswietlacza
if(digitalRead(buttonPin) == HIGH and digitalRead(buttonPin2)== HIGH){
   if (digitalRead(ledOff) == HIGH){
  digitalWrite(ledOff, LOW); // wylaczenie podswetlenia LCD
  }else{ 
    digitalWrite(ledOff, HIGH); // wylaczenie podswetlenia LCD
  }
}
}

lastButtonState = digitalRead(buttonPin);

 if (wyswietlacz==0){ //wlaczenie dzwiku wylaczenie
    if(digitalRead(buttonPin2) == HIGH){
       if (dzwiekOFF == 0){
         dzwiekOFF=1;
        }else{ 
          dzwiekOFF=0;
        }
    }
  } 

if (wyswietlacz==1){ //opcja liczenia wysokosci i zerowanie wysokosci
    if(digitalRead(buttonPin2) == HIGH){
      lcd.clear();
      wysBazowa=wys;
      cisnBazowe=cisn;
    }
  }
  
if (wyswietlacz==3){ //opcja liczenia wysokosci i zerowanie wysokosci na podstawie okreslenia cisnienia 
    if(digitalRead(buttonPin2) == HIGH){
      lcd.clear();
     if (dzwiekOFF == 0){
         dzwiekOFF=1;
        }else{ 
          dzwiekOFF=0;
        }
    }
  }
    // Pobranie aktualnego czasu
  unsigned long time = millis();
  
 if (time >= czas_Wznoszenia)
 { //Serial.println("Obliczamy Predkosc");
   spedW(bmp.readAltitude());// zawsze liczymy co 1s istotne
   //Serial.println(" Predkosc Obliczona");
   czas_Wznoszenia = millis() + czasWznoszenia;
 }
 ////////////////////////////////////
 
 if (vWysl >0 and dzwiekOFF==0){
   //wznoszenie
   if(vWysl> Vwznoszenia){
     tone(6,440);
     czasDzwieku=(vWysl*10)+200; if (czasDzwieku>900) {czasDzwieku=900;}
   }
 }
 if (vWysl < 0 and dzwiekOFF==0){
   //opadanie
   if((vWysl*(-1)) > Vopadania){
   tone(6,3729);
       czasDzwieku=(vWysl*10*-1)+200; if (czasDzwieku>900) {czasDzwieku=900;}
   }
 }
 if (time >= czas_Dzwieku)
 {
   noTone(6);
   czas_Dzwieku = millis() + czasDzwieku;
 }
//////////////////////////////////////
  // * SprawdĹş czy minÄ™Ĺ‚o do wysĹ‚ania danych 
 if (time >= czas_serial)
 {
meetAndroid.send("test");   
Serial.print("wario;");   
Serial.print(bmp.readTemperature());
 Serial.print(";");
 Serial.print(bmp.readPressure());
 Serial.print(";");
 Serial.print(bmp.readAltitude());
 Serial.print(";");
 Serial.print(bmp.readAltitude(cisnBazowe));
 Serial.print(";");
 Serial.print(vWysl);// wysokosc liczona
 Serial.println(";");
   czas_serial = millis() + czasSerial;
  }
 // * SprawdĹş czy minÄ™Ĺ‚o czas do wyswietlenia
  if (time >= czas_lcd)
  { 
    wysLiczona=wys-wysBazowa;
    displayStatus();  
    czas_lcd = millis() + czasLCD;
  }

}

void lcdoff(byte flag, byte numOfValues)
{  
  if (digitalRead(ledOff) == HIGH){
  digitalWrite(ledOff, LOW); // wylaczenie podswetlenia LCD
  }else{ 
    digitalWrite(ledOff, HIGH); // wylaczenie podswetlenia LCD
  }
} 
void bipoff(byte flag, byte numOfValues)
{  
  if (dzwiekOFF == 0){
         dzwiekOFF=1;
        }else{ 
          dzwiekOFF=0;
        }
} 
void wysF(byte flag, byte numOfValues)
{  
   meetAndroid.send(bmp.readAltitude());
}
void cisnF(byte flag, byte numOfValues)
{  
   meetAndroid.send(bmp.readPressure());
}
void tempF(byte flag, byte numOfValues)
{
 meetAndroid.send(bmp.readTemperature());  
}
void predpF(byte flag, byte numOfValues)
{  
   meetAndroid.send(vWysl);
}
void androidLCD(byte flag, byte numOfValues)
{  
   zmianaLCD();
}
//Zerowanie Wysokości 
void androidWYS(byte flag, byte numOfValues)
{  
   wysBazowa=wys;
   cisnBazowe=cisn;
}
//Zmiana ekrany LCD
void zmianaLCD(){
  lcd.clear();
  wyswietlacz++;
  if (wyswietlacz==5){wyswietlacz=0;}
}
//Pomiar predkosci
void spedW(double v1){
 
    vWysl=(v1-v0)/(czasWznoszenia/1000);//czasWznoszenia - wielkosc okresu w jakim wyliczamy predkosc dzielone przez 1000 z powodu uzywania z milisek
    v0=v1;
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
         case 4:
       lcd4();
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
   lcd.write("1Wys C: ");lcdPrintDouble(bmp.readAltitude(cisnBazowe),2); lcd.write(" m");
   lcd.setCursor(0, 1);
   lcd.print("Wys: ");lcdPrintDouble(wysLiczona,1);lcd.write(" m");
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
//AAL - ang. Above Aerodrome Level â€“ wysokoĹ›Ä‡ nad lotniskiem. Uzyskuje siÄ™ jÄ… poprzez ustawienie na wysokoĹ›ciomierzu rzeczywistego ciĹ›nienia atmosferycznego na poziomie lotniska (ciĹ›nienie to oznacza siÄ™ symbolem QFE). Po wylÄ…dowaniu wysokoĹ›ciomierz wskaĹĽe zero.
   lcd.home();
   lcd.write("3Dzwiek off ");  lcdPrintDouble(dzwiekOFF,1);//lcdPrintDouble(bmp.readAltitude(cisnBazowe),2); 
   lcd.setCursor(0, 1);
   lcd.print("Wm: ");lcd.print(wysLiczona);
   lcd.write(" V "); 
   lcdPrintDouble(vWysl,2); // 
   cisnTmp=cisn;
}
void lcd4()
{
//AAL - ang. Above Aerodrome Level â€“ wysokoĹ›Ä‡ nad lotniskiem. Uzyskuje siÄ™ jÄ… poprzez ustawienie na wysokoĹ›ciomierzu rzeczywistego ciĹ›nienia atmosferycznego na poziomie lotniska (ciĹ›nienie to oznacza siÄ™ symbolem QFE). Po wylÄ…dowaniu wysokoĹ›ciomierz wskaĹĽe zero.
   lcd.home();
   lcd.write("4Vwys ");  lcdPrintDouble(vWysl,2); lcd.write(" m/s");
   lcd.setCursor(0, 1);
   lcd.print("Wm: ");lcd.print(wysLiczona);
   lcd.write(" V "); 
   lcdPrintDouble(((bmp.readAltitude(cisn)-bmp.readAltitude(cisnTmp))/(czas_lcd/1000)),2); // (m/s) obliczamy predkosc wznoszenia opadania wysokosc wzgledna / czas z jakiego jest pobrana(wyswietlona na lcd / 1000 bo ma byc w s)
   cisnTmp=cisn;
}

//Procedury pobrane z internetu dziekuje ich twurcom za pomoc- nie pamietam żrudła
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

