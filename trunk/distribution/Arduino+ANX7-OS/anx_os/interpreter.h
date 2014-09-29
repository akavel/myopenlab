#ifndef INTERPRETER_H
#define INTERPRETER_H

/* Pinbelegung
  Arduino   | ANX-7 Function
  --------------------------
  PIN 2     = DI1
  PIN 3     = DI2
  PIN 4     = DI3
  PIN 5     = DI4
  PIN 6     = DI5
  PIN 7     = DI6
  PIN 8     = DI7

  PIN 9     = Relais 1
  PIN 10    = Relais 2
  PIN 11    = Relais 3
  PIN 12    = Relais 4

  ADC-IN 0  = ADC 1
  ADC-IN 1  = ADC 2
  ADC-IN 2  = ADC 3
  ADC-IN 3  = ADC 4

  ADC-IN 4  = TWI/I2C SDA
  ADC-IN 5  = TWI/I2C SCL
*/

int DI1=2;
int DI2=3;
int DI3=4;
int DI4=5;
int DI5=6;
int DI6=7;
int DI7=8;

int relais1=9;
int relais2=10;
int relais3=11;
int relais4=12;

int adc1=0;
int adc2=1;
int adc3=2;
int adc4=3;


#define CMD_PUSH_UB_C                  1
#define CMD_PUSH_SB_C                  2
#define CMD_PUSH_UI_C                  3
#define CMD_PUSH_SI_C                  4
#define CMD_PUSH_UL_C                  5
#define CMD_PUSH_SL_C                  6
#define CMD_LCD_CLEAR                  10
#define CMD_LCD_GOTO                   11
#define CMD_LCD_PRINT_UB               12
#define CMD_LCD_PRINT_SB               13
#define CMD_LCD_PRINT_UI               14
#define CMD_LCD_PRINT_SI               15
#define CMD_LCD_PRINT_UL               16
#define CMD_LCD_PRINT_SL               17
#define CMD_ADD_B                      18
#define CMD_ADD_I                      19
#define CMD_SUB_B                      20
#define CMD_SUB_I                      21
#define CMD_MUL_B                      22
#define CMD_MUL_I                      23
#define CMD_DIV_B                      24
#define CMD_DIV_I                      25

#define CMD_SET_RELAIS                 26
#define CMD_CLR_RELAIS                 27
#define CMD_SET_LED                    28
#define CMD_CLR_LED                    29


#define CMD_GET_BUTTONS                30
#define CMD_D_INPUT                    31
#define CMD_A_INPUT                    32
    
#define CMD_JMP                        33

#define CMD_CMP_UB                     34
#define CMD_CMP_SB                     35
#define CMD_CMP_UI                     36
#define CMD_CMP_SI                     37

#define CMD_IF_A_GL_B                  38 // =
#define CMD_IF_A_KL_B                  39 // <
#define CMD_IF_A_GR_B                  40 // >
#define CMD_IF_A_KL_GL_B               41 // <=
#define CMD_IF_A_GR_GL_B               42 // >=
#define CMD_IF_A_UN_B                  43 // !=
    
#define CMD_LOAD_B                     44
#define CMD_LOAD_I                     45
#define CMD_STORE_B                    46
#define CMD_STORE_I                    47
        
#define CMD_INC_B                      48
#define CMD_INC_I                      49
#define CMD_DEC_B                      50
#define CMD_DEC_I                      51
        
#define CMD_INT_TO_BYTE                52
#define CMD_BYTE_TO_INT                53   

#define CMD_JMP_IF_TRUE                54
#define CMD_JMP_IF_FALSE               55   
    
#define CMD_AND                        56   
#define CMD_OR                         57   
#define CMD_XOR                        58   
#define CMD_NOT                        59       
#define CMD_LOGIC_NOT                  60
#define CMD_IS_BIT_SET_I               61    
    

#define CMD_DELAY_MS                   100


//struct ANX_IO_FRONT_DATA data;
#define ON  1
#define OFF 0


unsigned char start=false;

unsigned char flag_AgleichB=false;
unsigned char flag_AKleinerB=false;
unsigned char flag_AGroesserB=false;
//unsigned char byteStream[]={4,244,1,47,10,33,0,8,4,1,0,52,26,45,10,100,4,1,0,52,27,45,10,100,33,0,5};
//unsigned short byteStreamCounter=sizeof(byteStream);

unsigned short interPreterValue=0;


unsigned char registers[255];
unsigned char stack[6];
unsigned char stackCounter=0;
unsigned short c;
unsigned short pc;

unsigned char byteStream[512];
unsigned short byteStreamCounter=0;


int bytesToInt(unsigned char high, unsigned char low)
{
  int value=0;
  value |=(high<<8) & 0xFF00 ; 
  value |=low & 0xFF;

  return value;
}

void compare_SI()
{
  char p1=stack[--stackCounter];
  char p2=stack[--stackCounter];
  char p3=stack[--stackCounter];
  char p4=stack[--stackCounter];

  signed short value2=0;
  value2 |=(p1<<8)  ; // !Achtung Vorzeichen beachten->hier kein & 0x00FF;
  value2 |=p2 & 0xFF;

  signed short value1=0;
  value1 |=(p3<<8) ; // !Achtung Vorzeichen beachten->hier kein & 0x00FF;
  value1 |=p4 & 0xFF;

  //Alle Flags rurücksetzen
  flag_AgleichB=false;
  flag_AKleinerB=false;
  flag_AGroesserB=false;

  if (value1==value2) flag_AgleichB=true;
  if (value1<value2) flag_AKleinerB=true;
  if (value1>value2) flag_AGroesserB=true;
}


void interpreter_start()
{
  start=true;
  stackCounter=0;
  pc=0;
}

void interpreter_stop()
{
    start=false;
    digitalWrite(relais1, LOW); 
    digitalWrite(relais2, LOW); 
    digitalWrite(relais3, LOW); 
    digitalWrite(relais4, LOW); 
    
    
}

void interpreter_evaluate()
{    
  
  //while(pc<byteStreamCounter)
  if (start==true && pc<byteStreamCounter)
  {
    switch(byteStream[pc++])
    {
      // STACK FUNCTIONS
      case CMD_PUSH_UB_C: 
      {
        stack[stackCounter++] = byteStream[pc++];
        break;
      }
      case CMD_PUSH_SB_C: 
      {
          stack[stackCounter++] = byteStream[pc++];
          break;
      }
      case CMD_PUSH_UI_C: 
      {
          stack[stackCounter++] = byteStream[pc++];
          stack[stackCounter++] = byteStream[pc++];
          break;
      }
      case CMD_PUSH_SI_C: 
      {
          stack[stackCounter++] = byteStream[pc++];
          stack[stackCounter++] = byteStream[pc++];
          break;
      }
			

      // ARITHMETISCHE OPERATIONEN
      case CMD_ADD_B: 
      {
          unsigned char p2=stack[--stackCounter];
	  unsigned char p1=stack[--stackCounter];
          
	  stack[stackCounter++]=p1+p2;
          break;
      }
      case CMD_ADD_I:
      {
          unsigned char p1=stack[--stackCounter];
          unsigned char p2=stack[--stackCounter];
	  unsigned char p3=stack[--stackCounter];
          unsigned char p4=stack[--stackCounter];
          int value2=bytesToInt(p1,p2);
	  int value1=bytesToInt(p3,p4);
                    
          int result=value1+value2;					
					
	  stack[stackCounter++]=result ;// Low
	  stack[stackCounter++]=result>>8; // High
          break;
      }
      case CMD_SUB_B: 
      {
          unsigned char p2=stack[--stackCounter];
	  unsigned char p1=stack[--stackCounter];
          
	  stack[stackCounter++]=p1-p2;
          break;
      }
      case CMD_SUB_I:
      {
          unsigned char p1=stack[--stackCounter];
          unsigned char p2=stack[--stackCounter];
					unsigned char p3=stack[--stackCounter];
          unsigned char p4=stack[--stackCounter];
          int value2=bytesToInt(p1,p2);
	  int value1=bytesToInt(p3,p4);
                    
          int result=value1-value2;					
					
	  stack[stackCounter++]=result ;// Low
	  stack[stackCounter++]=result>>8; // High
          break;
      }
      case CMD_MUL_B: 
      {
          unsigned char p1=stack[--stackCounter];
	  unsigned char p2=stack[--stackCounter];
          
	  stack[stackCounter++]=p1*p2;
          break;
      }
      case CMD_MUL_I:
      {
          unsigned char p1=stack[--stackCounter];
          unsigned char p2=stack[--stackCounter];
	  unsigned char p3=stack[--stackCounter];
          unsigned char p4=stack[--stackCounter];
          int value2=bytesToInt(p1,p2);
	  int value1=bytesToInt(p3,p4);
                    
          int result=value1*value2;					
					
	  stack[stackCounter++]=result ;// Low
	  stack[stackCounter++]=result>>8; // High
          break;
      }
      case CMD_DIV_B: 
      {
          unsigned char p2=stack[--stackCounter];
	  unsigned char p1=stack[--stackCounter];
          
	  stack[stackCounter++]=p1/p2;
          break;
      }
      case CMD_DIV_I:
      {
          unsigned char p1=stack[--stackCounter];
          unsigned char p2=stack[--stackCounter];
	  unsigned char p3=stack[--stackCounter];
          unsigned char p4=stack[--stackCounter];
          int value2=bytesToInt(p1,p2);
	  int value1=bytesToInt(p3,p4);
                    
          int result=value1/value2;					
					
          stack[stackCounter++]=result ;// Low
	  stack[stackCounter++]=result>>8; // High
          break;
      }


      // LCD FUNCTIONS
      case CMD_LCD_CLEAR:
      {
	break;
      }
      case CMD_LCD_PRINT_UB: 
      {
          unsigned char p1=stack[--stackCounter];
          
	  interPreterValue=p1;
          break;
      }
      case CMD_LCD_PRINT_SB: 
      {
          signed char p1=stack[--stackCounter];

          interPreterValue=p1;
          break;
      }
      case CMD_LCD_PRINT_UI: 
      {
          unsigned char  high=stack[--stackCounter];
          unsigned char  low=stack[--stackCounter];

          unsigned int value=0;
          value|=(high<<8);
          value|=low ;

				  interPreterValue=value;
          break;
      }
      case CMD_LCD_PRINT_SI: 
      {
          unsigned char  high=stack[--stackCounter];
          unsigned char  low=stack[--stackCounter];

          int value=0;
          value|=(high<<8); 
          value|=low ;

          interPreterValue=value;

          break;
      }

      // IO OPERATIONEN
      case CMD_SET_RELAIS    : // OK
      {                    
          unsigned char p1=stack[--stackCounter];  // Relais Nr 1-4
          
          if (p1>=1 && p1<=4)
          {
	    switch(p1)
	    {
              case 1 : digitalWrite(relais1, HIGH); break;
	      case 2 : digitalWrite(relais2, HIGH); break;
	      case 3 : digitalWrite(relais3, HIGH); break;
	      case 4 : digitalWrite(relais4, HIGH); break;
	    }
          }                    
          break;
      }                
      case CMD_CLR_RELAIS    : // OK
      {                    
          unsigned char p1=stack[--stackCounter];  // Relais Nr 1-4
          
          if (p1>=1 && p1<=4)
          {
            switch(p1)
	    {
              case 1 : digitalWrite(relais1, LOW); break;
	      case 2 : digitalWrite(relais2, LOW); break;
	      case 3 : digitalWrite(relais3, LOW); break;
	      case 4 : digitalWrite(relais4, LOW); break;
	    }
          }                    
          break;
      }    			

      case CMD_SET_LED    : // OK
      {                    
          unsigned char p1=stack[--stackCounter];  // Relais Nr 1-4
          
          if (p1>=1 && p1<=14)
          {
            //setLED(p1-1, ON);
          }                    
          break;
      }                
      case CMD_CLR_LED    : // OK
      {                    
          unsigned char p1=stack[--stackCounter];  // Relais Nr 1-4
          
          if (p1>=1 && p1<=14)
          {
	      //setLED(p1-1, OFF);
          }                    
          break;
      }
      case CMD_DELAY_MS    : // OK
      {                                        
          unsigned char p1=stack[--stackCounter];  
          unsigned char p2=stack[--stackCounter];  
          
          unsigned int value=0;
          value |=(p1<<8)  ; // !Achtung Vorzeichen beachten->hier kein & 0x00FF;
          value |=p2 & 0xFF;
                  
          delay(value);

        break;
      }
      case CMD_JMP:
      {
          unsigned char p1=byteStream[pc++];
          unsigned char p2=byteStream[pc++];
          
          unsigned int value=0;
          value |=(p1<<8)  ; // !Achtung Vorzeichen beachten->hier kein & 0x00FF;
          value |=p2 & 0xFF;
          
          // enthält Ziel Adresse
          pc = value;
          break;
      }
      case CMD_GET_BUTTONS:
      {
        /*anx_io_front_getInputData(&data);

	//byteDataArrayRegisterHolding[c++]=data.taste;	
	//byteDataArrayRegisterHolding[c++]=interPreterValue;
	//byteDataArrayRegisterHolding[c++]=data.adc1;	
	//byteDataArrayRegisterHolding[c++]=data.adc2;

	unsigned char result=0;
	if (data.taste==0b00000001) result=1;
	if (data.taste==0b00000010) result=2;
	if (data.taste==0b00000100) result=3;
	if (data.taste==0b00001000) result=4;
	if (data.taste==0b00010000) result=5;

	stack[stackCounter++]=result;*/
	stack[stackCounter++]=0; // Arduino hat keine Buttons
        
        break;
      }
      case CMD_D_INPUT:
      {
        unsigned char result=0;    

        unsigned char p1=stack[--stackCounter]; // Input Nr
        if (p1>=1 && p1<=7)
        {                
          switch(p1)
          {
            case 1 : if(!digitalRead(DI1)) result=1; break;
            case 2 : if(!digitalRead(DI2)) result=1; break;
            case 3 : if(!digitalRead(DI3)) result=1; break;
            case 4 : if(!digitalRead(DI4)) result=1; break;
            case 5 : if(!digitalRead(DI5)) result=1; break;
            case 6 : if(!digitalRead(DI6)) result=1; break;
            case 7 : if(!digitalRead(DI7)) result=1; break;
          }            
        }

        stack[stackCounter++] = result;          
        break;
      }

      case CMD_A_INPUT:
      { 
        unsigned char p1=stack[--stackCounter]; // Input Nr
        if (p1>=1 && p1<=4) // ADC Nr 1-4
        {
            unsigned int result=analogRead(p1-1);
            
            stack[stackCounter++] = (result & 0xFF);     // LOW
            stack[stackCounter++] = ((result>>8)& 0xFF); // HIGH
        }
        break;
      }                 
	       
      // COMPARATOREN
      case CMD_CMP_UB: // OK
      {                   
          unsigned char p2=stack[--stackCounter];
          unsigned char p1=stack[--stackCounter];
          
          // Alle Flags rurücksetzen
          flag_AgleichB=false;
          flag_AKleinerB=false;
          flag_AGroesserB=false;
          int value1=p1 & 0xFF;
          int value2=p2 & 0xFF;
                              
          if (p1==p2) {flag_AgleichB=true; }
          if (p1<p2)  {flag_AKleinerB=true; }
          if (p1>p2)  {flag_AGroesserB=true;}
          
          break;
      }
      case CMD_CMP_SB: // OK
      {                    
          signed char p2=stack[--stackCounter];
          signed char p1=stack[--stackCounter];
          
          // Alle Flags rurücksetzen
          flag_AgleichB=false;
          flag_AKleinerB=false;
          flag_AGroesserB=false;
                              
          if (p1==p2) {flag_AgleichB=true; }
          if (p1<p2)  {flag_AKleinerB=true; }
          if (p1>p2)  {flag_AGroesserB=true;}
          
          break;
      }
      case CMD_CMP_UI: // OK
      {                   
          unsigned char p1=stack[--stackCounter];
          unsigned char p2=stack[--stackCounter];
          unsigned char p3=stack[--stackCounter];
          unsigned char p4=stack[--stackCounter];

          unsigned int value2=bytesToInt(p1,p2);
	  unsigned int value1=bytesToInt(p3,p4);
                              
          // Alle Flags rurücksetzen
          flag_AgleichB=false;
          flag_AKleinerB=false;
          flag_AGroesserB=false;

                              
          if (value1==value2) {flag_AgleichB=true; }
          if (value1<value2)  {flag_AKleinerB=true; }
          if (value1>value2)  {flag_AGroesserB=true;}

          break;
      }
      case CMD_CMP_SI: // OK
      {                   
          unsigned char p1=stack[--stackCounter];
          unsigned char p2=stack[--stackCounter];
          unsigned char p3=stack[--stackCounter];
          unsigned char p4=stack[--stackCounter];

          signed int value2=bytesToInt(p1,p2);
	  signed int value1=bytesToInt(p3,p4);
                              
          // Alle Flags rurücksetzen
          flag_AgleichB=false;
          flag_AKleinerB=false;
          flag_AGroesserB=false;

          if (value1==value2) {flag_AgleichB=true;}
          if (value1<value2)  {flag_AKleinerB=true; }
          if (value1>value2)  {flag_AGroesserB=true;}


          break;
      }   


      // Abfragen
      case CMD_IF_A_GL_B:
        {
            compare_SI();
            if (flag_AgleichB)
            {                    
                stack[stackCounter++]=0;
                stack[stackCounter++]=1;
            }else 
            {
                stack[stackCounter++]=0;
                stack[stackCounter++]=0;
            }
            break;
        }
        case CMD_IF_A_UN_B:
        {
            compare_SI();
            if (flag_AgleichB==false)
            {                    
                stack[stackCounter++]=0;
                stack[stackCounter++]=1;
            }else 
            {
                stack[stackCounter++]=0;
                stack[stackCounter++]=0;
            }
            break;
        }                
        case CMD_IF_A_KL_B:
        {
            compare_SI();
            if (flag_AKleinerB)
            {                    
                stack[stackCounter++]=0;
                stack[stackCounter++]=1;
            }else 
            {
                stack[stackCounter++]=0;
                stack[stackCounter++]=0;
            }
            break;
        }
        case CMD_IF_A_GR_B:
        {
            compare_SI();
            if (flag_AGroesserB)
            {                    
                stack[stackCounter++]=0;
                stack[stackCounter++]=1;
            }else 
            {
                stack[stackCounter++]=0;
                stack[stackCounter++]=0;
            }
            break;
        }
        case CMD_IF_A_KL_GL_B:
        {
            compare_SI();
            if (flag_AKleinerB || flag_AgleichB)
            {                    
                stack[stackCounter++]=0;
                stack[stackCounter++]=1;
            }else 
            {
                stack[stackCounter++]=0;
                stack[stackCounter++]=0;
            }
            break;
        }
        case CMD_IF_A_GR_GL_B:
        {
            compare_SI();
            if (flag_AGroesserB || flag_AgleichB)
            {                    
                stack[stackCounter++]=0;
                stack[stackCounter++]=1;
            }else 
            {
                stack[stackCounter++]=0;
                stack[stackCounter++]=0;
            }
            break;
        }
        case CMD_JMP_IF_TRUE:
        {          
            unsigned char a1=stack[--stackCounter];
            unsigned char a2=stack[--stackCounter];

            unsigned char p1=byteStream[pc++];
            unsigned char p2=byteStream[pc++];

            if (a1>0) // HIGH-BYTE nicht berücjksichtigen!
            {                    
          
              unsigned int value=0;
              value |=(p1<<8)  ; // !Achtung Vorzeichen beachten->hier kein & 0x00FF;
              value |=p2 & 0xFF;
          
            // enthält Ziel Adresse
              pc = value;
            }
            break;
        }
        case CMD_JMP_IF_FALSE:
        {
            unsigned char a1=stack[--stackCounter];
            unsigned char a2=stack[--stackCounter];

            unsigned char p1=byteStream[pc++];
            unsigned char p2=byteStream[pc++];

            if (a1==0) // HIGH-BYTE nicht berücjksichtigen
            {                    
              unsigned int value=0;
              value |=(p1<<8)  ; // !Achtung Vorzeichen beachten->hier kein & 0x00FF;
              value |=p2 & 0xFF;
          
            // enthält Ziel Adresse
              pc = value;
            }
            break;
        }	       

	  
      // STORE/LOAD Operationen
      case CMD_STORE_B: // Speichert einen Stack Byte ab einer Registeradresse
      {
          unsigned char adr = byteStream[pc++];// Register Adresse
          if (stackCounter > 0)
          {              
              registers[adr] = (char) stack[--stackCounter];
          } else
          {
              registers[adr] = 0;
          }
          break;
      }                
      case CMD_LOAD_B: // Lädt ein Byte aus einer Registeradresse in den Stack
      {
          unsigned char adr = byteStream[pc++];// Register Adresse
          
          stack[stackCounter++]=registers[adr];
          break;
      }   
      case CMD_STORE_I: // Speichert einen Stack Byte ab einer Registeradresse
      {
          unsigned char adr = byteStream[pc++];// Register Adresse
          if (stackCounter > 0)
          {              
              registers[adr+0] = stack[--stackCounter];
              registers[adr+1] = stack[--stackCounter];
          } else
          {
              registers[adr+0] = 0;
							registers[adr+1] = 0;
          }
          break;
      }
      case CMD_LOAD_I: // Lädt ein Byte aus einer Registeradresse in den Stack
      {
          unsigned char adr= byteStream[pc++];// Register Adresse          
          stack[stackCounter++]=registers[adr+1];
          stack[stackCounter++]=registers[adr+0];
          break;
      }       
			
      // INC / DEC      			        
      case CMD_INC_B: 
      {
          unsigned char adr= byteStream[pc++];// Register Adresse
                    
          registers[adr]++;
          break;
      }                
      
      case CMD_DEC_B: 
      {
          unsigned char param = byteStream[pc++];// Register Adresse
          char adr=(char)param;
          
          registers[adr]--;
          break;
      }                
      
      case CMD_INC_I: 
      {
          unsigned char adr = byteStream[pc++];// Register Adresse
                              
          unsigned char p1=registers[adr+0];
          unsigned char p2=registers[adr+1];
          
          int value=0;
          value |=(p1<<8)  ; // !Achtung Vorzeichen beachten->hier kein & 0x00FF;
          value |=p2 & 0xFF;
          
          value++;
          
          registers[adr+1] = (value & 0xFF);     // LOW
          registers[adr+0] = ((value>>8)& 0xFF); // HIGH
          break;
      }                
      
      case CMD_DEC_I: 
      {
          unsigned char adr = byteStream[pc++];// Register Adresse
                                        
          unsigned char p1=registers[adr+0];
          unsigned char p2=registers[adr+1];
          
          int value=0;
          value |=(p1<<8)  ; // !Achtung Vorzeichen beachten->hier kein & 0x00FF;
          value |=p2 & 0xFF;
          
          value--;
          
          registers[adr+1] =  (value & 0xFF);     // LOW
          registers[adr+0] = ((value>>8)& 0xFF); // HIGH   
          break;
      }   
      case CMD_INT_TO_BYTE: // OK
      {                    
        // Highbyte entfernen
        unsigned char p1=stack[--stackCounter];
        unsigned char p2=stack[--stackCounter];
                    
        stack[stackCounter++] = p2;
      	break;
      }      
      case CMD_BYTE_TO_INT:
      {
          unsigned char p1=stack[--stackCounter];
                    
          stack[stackCounter++] = p1;
          stack[stackCounter++] = 0;
          break;                    
      }
      case CMD_IS_BIT_SET_I:
      {
        unsigned char p1=stack[--stackCounter];
        unsigned char p2=stack[--stackCounter];
        unsigned char p3=stack[--stackCounter];
        unsigned char p4=stack[--stackCounter];
                    
        signed int value2=0;
        value2 |=(p1<<8)  ; // !Achtung Vorzeichen beachten->hier kein & 0x00FF;
        value2 |=p2 & 0xFF;
                    
        signed int value1=0;
        value1 |=(p3<<8) ; // !Achtung Vorzeichen beachten->hier kein & 0x00FF;
        value1 |=p4 & 0xFF;
                    
        signed int result=0;
        if ((value1 & (1<<value2))>0)
        {
          result=1;
        } else result=0;
                    
        // In der richtigen Reihenvolge wieder in den Stack Pushen
        stack[stackCounter++] = (result & 0xFF);     // LOW
        stack[stackCounter++] = ((result>>8)& 0xFF); // HIGH                   
      }                

    }
  }  

}



#endif /* _INTERPRETER_H_ */
