#include "WProgram.h"

/*
 * ModBus RTU Slave Device 
 * by Carmelo Salafia cswi@gmx.de
 *
 * An Example how to use Arduino with ModBus 
 */

#include "interpreter.h"
#include <EEPROM.h>



void setRelaisX(unsigned char relaisNr, unsigned char value);
void signalBlink();
void setup();
int waitForByte();
void writeByteToEEPROM(unsigned short adress, byte value);
void loop();
int val;
unsigned long time;
unsigned long timeOld;
unsigned char timeOut=false;

void setRelaisX(unsigned char relaisNr, unsigned char value)
{
 switch(relaisNr)
   {
     case 1 : digitalWrite(relais1,value);break;
     case 2 : digitalWrite(relais2,value);break;
     case 3 : digitalWrite(relais3,value);break;
     case 4 : digitalWrite(relais4,value);break;
   }  
}


void signalBlink()
{
  int i;
  int delayval=100;
  
  for (i=1;i<5;i++)
  {
    setRelaisX(i,1);
    delay(delayval);    
  }
  for (i=5;i>0;i--)
  {
    setRelaisX(i,0);
    delay(delayval);    
  }
}


void setup()
{
  Serial.begin(9600);

  pinMode(DI1, INPUT);
  pinMode(DI2, INPUT);
  pinMode(DI3, INPUT);
  pinMode(DI4, INPUT);
  pinMode(DI5, INPUT);
  pinMode(DI6, INPUT);
  pinMode(DI7, INPUT);
  
  pinMode(relais1, OUTPUT);
  pinMode(relais2, OUTPUT);
  pinMode(relais3, OUTPUT);
  pinMode(relais4, OUTPUT);


  // Enable Pull-Up Resis
  digitalWrite(DI1,1);
  digitalWrite(DI2,1);
  digitalWrite(DI3,1);
  digitalWrite(DI4,1);
  digitalWrite(DI5,1);
  digitalWrite(DI6,1);
  digitalWrite(DI7,1);
  
  signalBlink();

  Serial.print("Ready");
  while(Serial.available() )  { Serial.read(); } 
  Serial.flush();  
  
  // Load the Programm from EEPROM into RAM
  
  byteStreamCounter=0;
  
  
  byteStreamCounter=EEPROM.read(1)<<8; // Load High Byte
  byteStreamCounter|=EEPROM.read(0);    // Load Low Byte
  
  //Serial.println(">");
  //Serial.println(EEPROM.read(0), HEX);
  //Serial.println("-");
  //Serial.println(EEPROM.read(1), HEX);
  if (byteStreamCounter>512-2) byteStreamCounter=0;
  Serial.println(byteStreamCounter, DEC);
    
   if (byteStreamCounter>0)
   {  
    byte ch;
    for (int i=0;i<byteStreamCounter;i++) 
    {
      ch=EEPROM.read(i+2);
      byteStream[i]=ch;
    }   
    interpreter_start();
  }
  
}


int waitForByte()
{
  int result=0;
  timeOld=millis();
  while(1)  
  { 
    
    time=millis();    
    if (time-timeOld>5000) {timeOut=true;break;}
      
    if(Serial.available())
    {
      result=Serial.read(); // read length
      Serial.print(result,BYTE);   
      delay(2);
      Serial.flush();
      return result;
    }      
  }
}  


void writeByteToEEPROM(unsigned short adress, byte value)
{
  byte ch=EEPROM.read(adress);    
  if (ch!=value) // Only write if a Byte Changed! -> Saves the EEPROM
  {
    EEPROM.write(adress, value); // HIGH Byte  
  }
}

void loop()
{
  if (Serial.available()) 
  {
    val = Serial.read();
    Serial.print(0xFF, BYTE); // Send Byte : ready for Command
    delay(2);
    Serial.flush();
    
    
    if (val==0x01) interpreter_start();
    if (val==0x02) interpreter_stop();
    if (val==0x03) 
    {
      byteStreamCounter=0;
      
      
      unsigned char high= waitForByte();      
      if (timeOut) goto ende;
      unsigned char low = waitForByte();
      if (timeOut) goto ende;
      
      unsigned short len=high<<8;
      len|=low;
      
      interpreter_stop();
      
      timeOld=millis();
      //val = Serial.read();
      while(byteStreamCounter<len)
      {
        
        time=millis();    
        if (time-timeOld>5000) {timeOut=true;break;}
        
        if(Serial.available())
        {
          val = Serial.read(); 
          byteStream[byteStreamCounter++]=val;
    
          Serial.print(val,BYTE);
          delay(2);
          Serial.flush();
        }
      }
      
    }
    
    ende:
    // Copy Data from byteStream (RAM) to EEPROM (ONLY IF MESSAGE 100% Correkt!)
    

    if (timeOut==false)
    {     
      // Size of EEPROM Data
      
      writeByteToEEPROM(0,byteStreamCounter & 0xFF);
      writeByteToEEPROM(1,(byteStreamCounter>>8) & 0xFF);
      
    
      int i;
      for (i=0;i<byteStreamCounter;i++) 
      {
        writeByteToEEPROM(i+2,byteStream[i]);
      }
      
    }

    
    if (timeOut) signalBlink();  

    // wait if other Bytes transmitted
    while(Serial.available() )  { Serial.read(); }
      
    Serial.flush(); 
  }
  
  
  interpreter_evaluate();
  
}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

