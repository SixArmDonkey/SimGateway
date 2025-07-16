SimGateway: A Real-Time IoT Platform for Flight Simulators

SimGateway is a complete, end-to-end IoT platform designed to bridge real-time telemetry data from flight simulators like DCS World to custom-built physical hardware. The project was born from a desire to create a more immersive simulation experience by having in-game events drive physical gauges, lights, and displays on a custom-built cockpit panel.

This project is a practical demonstration of architecting a real-time, event-driven, and fault-tolerant system that manages the entire data lifecycle—from in-game data extraction to low-level hardware communication.

Quick Start Guide

This guide provides a simple, end-to-end example of sending a radio frequency from DCS World to a physical LCD screen connected via an Arduino.
1. Hardware Setup (Arduino)

First, upload this C++ sketch to your Arduino. This code initializes an LCD screen and listens on the serial port for messages from the SimGateway server.

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

2. SimGateway Configuration (config.json)

Create a config.json file in the same directory as the SimGateway JAR file. This tells the gateway about your hardware.

{
  "server": {
    "port": 4201
  },
  "devices": [
    {
      "name": "Arduino Uno",
      "description": "Main Instrument Panel",
      "components": {
        "lcd": {
          "main_lcd": {
            "description": "The main LCD for radio display",
            "address": 1,
            "sim": {
              "dcs": 101
            }
          }
        }
      }
    }
  ]
}

3. DCS World Export Script (Export.lua)

Place this LUA script in your Scripts/DCS-Export-Script folder. It will connect to the SimGateway server and send the COMM1 radio frequency data.

-- SimGateway Export Script for DCS World

local socket = require("socket")
local host, port = "127.0.0.1", 4201
local tcp = socket.tcp()

-- Connect to the SimGateway server
tcp:connect(host, port)
tcp:settimeout(0)

function LuaExportStart()
  -- Optional: Send a startup message
end

function LuaExportAfterNextFrame()
  local radio_data = LoGetRadioInfo()
  if radio_data and radio_data.comm.freq then
    local freq_str = string.format("%.3f", radio_data.comm.freq / 1000000)
    -- The payload is "software_address=value"
    local payload = "101=" .. freq_str .. "\n"
    tcp:send(payload)
  end
end

function LuaExportStop()
  tcp:close()
end

4. Run It!

    Plug in your Arduino.

    Start the SimGateway.jar application.

    Launch DCS World.

As you change the COMM1 radio frequency in your aircraft, you will see it update in real-time on your physical LCD screen.
Configuration Details

The config.json file is used to define the server settings and all connected hardware.

    server:

        port: The TCP port the gateway server will listen on for data from the simulator.

    devices: An array of physical hardware controllers connected to the system.

        name: The descriptive name of the serial port (e.g., "Arduino Uno").

        serial: (Optional) The unique serial number of the device, used to distinguish between multiple identical devices.

        description: A human-readable description.

        components: An object defining all the individual components (switches, lights, displays) on that hardware device.

            lcd / toggle / etc.: The type of component.

                main_lcd: A unique name for the component.

                    description: A human-readable description.

                    address: The hardware address (an integer from 0-255) that the Arduino firmware will listen for. This is the address you use in your switch statement in the Arduino code.

                    sim: An object that maps the component to a specific simulator's data.

                        dcs: The name of the simulator.

                        101: The software address (or controlId) from the simulator's export data. This is the key the LUA script sends in its payload (e.g., "101=124.850").

Core Features & Architecture

The platform is built on a decoupled, asynchronous architecture to ensure high performance and reliability. It consists of three main components that work together:

    LUA Export Script (The Data Source): A custom LUA script running within the flight simulator that extracts real-time telemetry data (like radio frequencies, engine RPM, etc.) and sends it via a TCP socket.

    SimGateway Server (The Bridge): A multi-threaded Java application that acts as the central hub. It listens for incoming data from the simulator, processes it through an event-driven backend, and translates it into commands for the hardware.

    Arduino Firmware (The Hardware Client): A C++ application running on an Arduino microcontroller that receives commands from the SimGateway server over a serial connection and drives the physical hardware (like LCDs, LEDs, and gauges).

The core architectural philosophy is based on a decoupled, message-queued, event-driven model.
Key Architectural Concepts

    Custom Binary Protocol: To ensure reliable communication between the Java server and the Arduino hardware, I designed a lightweight, custom binary protocol. Messages are framed with START and END bytes and include a hardware address and payload, ensuring that data is never misinterpreted over the serial stream.

    Event-Driven Backend: The Java server is not a simple pass-through. When telemetry is received, it is placed onto a thread-safe LinkedBlockingDeque. A separate StateChangeProcessor consumes events from this queue, decoupling the low-level network I/O from the application's core logic. This ensures that a flood of data from the simulator will not block the system.

    Hardware Abstraction: The DeviceFactory and Device classes create a hardware abstraction layer. The core application logic doesn't know about serial ports or baud rates; it simply sends a high-level command like device.write(hardwareAddress, payload). This makes the system modular and extensible.

    Low-Level State Machine: The Arduino firmware implements a finite state machine to reliably parse the incoming binary protocol from the serial buffer, ensuring that even in a noisy environment, messages are correctly read and acted upon.

How It Works

    The LUA script in DCS World exports engine telemetry every frame.

    The Java SimGateway server receives this data on a TCP socket.

    The server processes the data and identifies a state change (e.g., the radio frequency has changed).

    An IStateEvent is created and placed onto the central event queue.

    The StateChangeProcessor consumes the event, identifies which physical hardware component needs to be updated (e.g., the LCD display with hardware address 0x01), and looks up the device responsible for it.

    The SimGateway then uses the custom binary protocol to assemble a message and sends it to the appropriate Device's message queue.

    The Device thread writes the byte array to the correct serial port.

    The Arduino firmware's state machine parses the binary message from the serial buffer and writes the new frequency to the physical LCD screen.

This entire project demonstrates a full-stack engineering capability, from high-level server architecture in Java down to low-level firmware development in C++ and custom hardware integration.

