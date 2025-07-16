/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.hardware;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * A serial data packet (brackets and spaces not included)
 * {MESSAGE_START hardwareAddress TEXT_START message TEXT_END MESSAGE_END}
 * 
 * message can be anything the component understands.  a bit, a byte array for character displays, etc.
 * 
 */
public class Message implements IMessage
{
  /**
   * Start of message
   * When sending a message, it must start with this byte
   * Immediately following this byte must be a 1 byte integer representing the hardware id 
   */
  public static final byte NESSAGE_START = 0x1;
  
  /**
   * After the hardware address, this byte marks the start of the value segment 
   * An unlimited number of bytes may follow
   */
  public static final byte TEXT_START = 0x2;
  
  /**
   * The text section ends at this byte 
   */
  public static final byte TEXT_END = 0x3;
  
  /**
   * Some messages may require a data separator in the text section, this is for that
   */
  public static final byte SEPARATOR = 0x1F;

  /**
   * End of message/transmission
   * 
   */
  public static final byte MESSAGE_END = 0x4;
  
  /**
   * Message footer 
   */
  private static final byte[] FOOTER = { TEXT_END, MESSAGE_END };

  /**
   * The address of the component on the device this message is for 
   */
  private final byte hardwareAddress;
  
  /**
   * The entire message
   */
  private final byte[] message;
  
  
  /**
   * 
   * @param hardwareAddress
   * @param message 
   */
  public Message( final int hardwareAddress, final byte[] message )
  {
    //..We want the least significant byte 
    this.hardwareAddress = (byte)( hardwareAddress & 0xFF );
    this.message = message;
  }
  
  
  @Override
  public byte[] getBytes()
  {
    try ( final ByteArrayOutputStream o = new ByteArrayOutputStream( message.length + 5 )) {

      final byte[] header = new byte[3];
      header[0] = NESSAGE_START;
      header[1] = hardwareAddress;
      header[2] = TEXT_START;

      o.write( header );
      o.write( message );
      o.write( FOOTER );
      
      return o.toByteArray();
    } catch( IOException e ) {
      return new byte[0];
    }
  }
}
