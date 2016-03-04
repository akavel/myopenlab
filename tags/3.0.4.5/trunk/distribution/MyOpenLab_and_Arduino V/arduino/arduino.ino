/*
 * Simple Demo for MyopenLab and Arduino
 * by Carmelo Salafia cswi@gmx.de
 *
 */



int val;
unsigned char cmd;

int in1=2;
int in2=3;
int in3=4;
int in4=5;
int in5=6;
int in6=7;

int out1=8;
int out2=9;
int out3=10;
int out4=11;
int out5=12;
int out6=13;

unsigned char inputs;

void setup()
{
  Serial.begin(9600);

  pinMode(in1, INPUT);
  pinMode(in2, INPUT);
  pinMode(in3, INPUT);
  pinMode(in4, INPUT);
  pinMode(in5, INPUT);
  pinMode(in6, INPUT);
  
  pinMode(out1, OUTPUT);
  pinMode(out2, OUTPUT);
  pinMode(out3, OUTPUT);
  pinMode(out4, OUTPUT);  
  pinMode(out5, OUTPUT);
  pinMode(out6, OUTPUT);  
    
  // Enable Pull-Up Resis
  digitalWrite(in1,HIGH);
  digitalWrite(in2,HIGH);
  digitalWrite(in3,HIGH);
  digitalWrite(in4,HIGH);
  digitalWrite(in5,HIGH);
  digitalWrite(in6,HIGH);
  
  // All LED's off
  digitalWrite(out1,LOW);
  digitalWrite(out2,LOW);
  digitalWrite(out3,LOW);
  digitalWrite(out4,LOW);  
  digitalWrite(out5,LOW);
  digitalWrite(out6,LOW);  
  
  // Arduino is Ready now
  //Serial.print(0xFF,BYTE);
  
  Serial.flush();
  while(Serial.available() )  { Serial.read(); }
}


void sendShort(int val)
{
Serial.write(val>>8); // HIGH Byte
Serial.write(val & 0xFF); // LOW Byte      
//  Serial.print(val , HEX); //
}

void loop()
{
  if (Serial.available())  // if Arduino is Polled!
  {
    cmd = Serial.read();
    
    // Serial.print(cmd);

    if ( (cmd & (1<<0))>0) digitalWrite(out1,1); else digitalWrite(out1,0);
    if ( (cmd & (1<<1))>0) digitalWrite(out2,1); else digitalWrite(out2,0);
    if ( (cmd & (1<<2))>0) digitalWrite(out3,1); else digitalWrite(out3,0);
    if ( (cmd & (1<<3))>0) digitalWrite(out4,1); else digitalWrite(out4,0);
    if ( (cmd & (1<<4))>0) digitalWrite(out5,1); else digitalWrite(out5,0);
    if ( (cmd & (1<<5))>0) digitalWrite(out6,1); else digitalWrite(out6,0);
    
    // Read Inputs
    inputs=0;
    if(!digitalRead(in1)==1) inputs|=(1<<0); else inputs&=~(1<<0);
    if(!digitalRead(in2)==1) inputs|=(1<<1); else inputs&=~(1<<1);
    if(!digitalRead(in3)==1) inputs|=(1<<2); else inputs&=~(1<<2);
    if(!digitalRead(in4)==1) inputs|=(1<<3); else inputs&=~(1<<3);
    if(!digitalRead(in5)==1) inputs|=(1<<4); else inputs&=~(1<<4);
    if(!digitalRead(in6)==1) inputs|=(1<<5); else inputs&=~(1<<5);

    // Send input Values Binary
    Serial.write(inputs);
    
    // Send ADC 0-5 values
    sendShort(analogRead(0));
    sendShort(analogRead(1));   
    sendShort(analogRead(2));    
    sendShort(analogRead(3));
    sendShort(analogRead(4));  
    sendShort(analogRead(5)); 
  } 
  
  /*if(!digitalRead(in1)==1) digitalWrite(out2  ,1); else digitalWrite(out2  ,0);
  if(!digitalRead(in2)==1) digitalWrite(out3  ,1); else digitalWrite(out3  ,0);
  if(!digitalRead(in3)==1) digitalWrite(out4  ,1); else digitalWrite(out4  ,0);
  if(!digitalRead(in4)==1) digitalWrite(out5  ,1); else digitalWrite(out5  ,0);
  
  Serial.println(analogRead(0));
  Serial.println(analogRead(1));
  Serial.println(analogRead(2));
  Serial.println(analogRead(3));
  
 digitalWrite(out2  ,1);
  digitalWrite(out3  ,1);
  digitalWrite(out4  ,1);
  digitalWrite(out5  ,1);
  delay(500);
  digitalWrite(out2  ,0);
  digitalWrite(out3  ,0);
  digitalWrite(out4  ,0);
  digitalWrite(out5  ,0);

  delay(500);  */
}

