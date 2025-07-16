
#include<HID.h>
#include<LiquidCrystal.h>

//..LCD Pin configuration 
const uint8_t RS = 12;
const uint8_t EN = 11;
const uint8_t D4 = 5;
const uint8_t D5 = 4;
const uint8_t D6 = 3;
const uint8_t D7 = 2;

//..The Main LCD Screen
LiquidCrystal lcd( RS, EN, D4, D5, D6, D7 );

//..Serial activity led pin
const uint8_t LED_SERIAL_ACTIVITY = 10;

//..Serial communication constants 
const uint8_t MESSAGE_START = 0x1;  //..A new message is started - the next byte is the hardware address
const uint8_t TEXT_START = 0x2; //..After 1 byte of hardware, this is expected.  Following this is up to VALUE_BYTES of data then TEXT_END followed by MESSAGE_END 
const uint8_t TEXT_END = 0x3;  //..end of payload 
const uint8_t SEPARATOR = 0x1F; //..Unused 
const uint8_t MESSAGE_END = 0x4; //..End of message

//Message buffers 
const uint8_t HEADER_BYTES = 0x3; //..Total bytes in the message header 
const uint8_t VALUE_BYTES = 0x10; //..Max bytes in the text section of the message
const uint8_t FOOTER_BYTES = 0x2; //..Size of footer section in message [TEXT_END,MESSAGE_END]
const uint8_t MAX_BYTES = HEADER_BYTES + VALUE_BYTES + FOOTER_BYTES;

uint8_t header[HEADER_BYTES]; //..The header contents
uint8_t value[VALUE_BYTES];   //..The text section contents 

//..The message processing loop states
const uint8_t STATE_NONE = 0x1;
const uint8_t STATE_GET_HARDWARE_ADDRESS = 0x2;
const uint8_t STATE_GET_PAYLOAD = 0x3;
const uint8_t STATE_IN_MESSAGE = 0x4;


//..The current message processing state
uint8_t curState = STATE_NONE;

//..The current hardware address 
uint8_t hardwareAddress = 0;

//..The current message read index 
volatile uint8_t readIndex = 0;

//..Current header read index 
volatile uint8_t headerIndex = 0;

//..Current value read index 
volatile uint8_t valueIndex = 0;



void setup() 
{
  pinMode( LED_SERIAL_ACTIVITY, OUTPUT );
  Serial.begin( 9600 );
  lcd.begin( 16, 1 );
  lcd.print( "OK" );
}


void loop() 
{
  if ( !Serial.available())
    return;

  uint8_t input = Serial.read();

  //..Header 3 bytes:
  //  MESSAGE_START
  //  hardware address 
  //  TEXT_START
  //..Read up to VALUE_BYTES


  if ( curState == STATE_NONE && input == MESSAGE_START )
  {
    //..This is the start of a new message
    resetMessageState();
    curState = STATE_GET_HARDWARE_ADDRESS;
    digitalWrite( LED_SERIAL_ACTIVITY, HIGH );
  }
  else if ( curState == STATE_GET_HARDWARE_ADDRESS && input != TEXT_START )
  {
    hardwareAddress = curState;    
  }
  else if ( curState == STATE_GET_HARDWARE_ADDRESS && input == TEXT_START )
  {
    curState = STATE_GET_PAYLOAD;
  }
  else if ( curState == STATE_GET_PAYLOAD && ( input == TEXT_END || valueIndex >= VALUE_BYTES - 1 ))
  {
    curState = STATE_IN_MESSAGE;
  }
  else if ( curState == STATE_GET_PAYLOAD )
  {
    value[readIndex] = input;
  }
  else if ( curState == STATE_IN_MESSAGE && input == MESSAGE_END )
  {
    writeValueToLCD();
    resetMessageState();
    digitalWrite( LED_SERIAL_ACTIVITY, LOW );
  }

  //..Read index position determines which array we write to 
  if ( readIndex >= 0 && readIndex < HEADER_BYTES )
  {
    header[headerIndex++] = input;
  }
  else if ( readIndex >= HEADER_BYTES && readIndex < VALUE_BYTES && curState == STATE_GET_PAYLOAD )
  {
    value[valueIndex++] = input;
  }
    
  readIndex++;

  if ( readIndex >= MAX_BYTES )
  {
    curState = STATE_IN_MESSAGE;
    writeValueToLCD();
  }



}

void writeValueToLCD()
{
  
  uint8_t lcdBank = 0;
  
  for ( uint8_t i = 0; i < 8; i++ )
  {
    lcd.setCursor( i, 0 );
    if ( i >= valueIndex || value[i] == 0x0 )
      lcd.print( " " );
    else
      lcd.print((char)value[i] );  
    
    
  }
}


void resetMessageState() 
{
  curState = STATE_NONE;
  readIndex = 0;
  headerIndex = 0;
  valueIndex = 0;  

  //..Reset the hardware address 
  hardwareAddress = 0;

  //..Reset the header
  for ( uint8_t i = 0; i < HEADER_BYTES; i++ )
  {
    header[i] = 0;
  }

  //..Reset the payload 
  for ( uint8_t i = 0; i < VALUE_BYTES; i++ )
  {
    value[i] = 0;
  }
}
