# SimGateway: A Real-Time IoT Platform for Flight Simulators

SimGateway is a complete, end-to-end **IoT platform** designed to bridge real-time telemetry data from flight simulators like **DCS World** to custom-built physical hardware. The project was born from a desire to create a more immersive simulation experience by having in-game events drive physical gauges, lights, and displays on a custom-built cockpit panel.

This project is a practical demonstration of architecting a **real-time, event-driven, and fault-tolerant system** that manages the entire data lifecycle—from in-game data extraction to low-level hardware communication.

---

## Quick Start Guide

This guide provides a simple, end-to-end example of sending a radio frequency from DCS World to a physical LCD screen connected via an Arduino.

### 1. Hardware Setup (Arduino)

First, upload this C++ sketch to your Arduino. This code initializes an LCD screen and listens on the serial port for messages from the SimGateway server.

```cpp
#include <LiquidCrystal.h>

// LCD Pin configuration
const uint8_t RS = 12, EN = 11, D4 = 5, D5 = 4, D6 = 3, D7 = 2;
LiquidCrystal lcd(RS, EN, D4, D5, D6, D7);

// Serial communication constants from the SimGateway protocol
const uint8_t MESSAGE_START = 0x1;
const uint8_t TEXT_START    = 0x2;
const uint8_t TEXT_END      = 0x3;
const uint8_t MESSAGE_END   = 0x4;

// Message processing state machine
byte state = 0;
char buffer[17];
byte bufferIndex = 0;

void setup() {
  Serial.begin(9600);
  lcd.begin(16, 2);
  lcd.print("SimGateway OK");
}

void loop() {
  if (Serial.available()) {
    byte input = Serial.read();

    switch(state) {
      case 0: // Waiting for start of message
        if (input == MESSAGE_START) state = 1;
        break;
      case 1: // Waiting for hardware address (we ignore it for this simple example)
        state = 2;
        break;
      case 2: // Waiting for start of text
        if (input == TEXT_START) {
          bufferIndex = 0;
          memset(buffer, 0, 17);
          state = 3;
        } else {
          state = 0; // Invalid sequence
        }
        break;
      case 3: // Reading payload
        if (input == TEXT_END) {
          state = 4;
        } else if (bufferIndex < 16) {
          buffer[bufferIndex++] = input;
        }
        break;
      case 4: // Waiting for end of message
        if (input == MESSAGE_END) {
          lcd.clear();
          lcd.setCursor(0, 0);
          lcd.print("COMM 1:");
          lcd.setCursor(0, 1);
          lcd.print(buffer);
        }
        state = 0; // Reset for next message
        break;
    }
  }
}
