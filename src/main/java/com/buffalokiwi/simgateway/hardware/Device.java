/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */

package com.buffalokiwi.simgateway.hardware;

import com.buffalokiwi.simgateway.SimType;
import com.buffalokiwi.utils.Logs;
import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.BiFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A Device is physical hardware that we can connect with 
 * 
 * Each device maintains its own message queue for writing to devices 
 * The device object itself is runnable.  We can process the message queue at fixed intervals to write messages to the device,
 * and as long as the queue has items, this will continue to process indefinitely.  When the queue is empty, we can simply
 * terminate the thread (the serial data channel is still open, the queue still exists, but the thread terminates - this object will exist until the device itself is closed)
 * Then we simply restart the thread later either on a scheduler or when an data point comes in that needs to be enqueued
 * 
 */
public class Device implements IDevice
{
  public static class Builder
  {
    private SimType sim = null;
    private String name = "";
    private String description = "";
    private String serial = "";
    private List<IComponent> componentList = null;
    private SerialPort port = null;
    private BiFunction<Integer,byte[],IMessage> messageFactory = (hardwareAddress, message) -> new Message( hardwareAddress, message );
    
    public Builder()
    {
      
    }
    
    
    public Builder setMessageFactory( final BiFunction<Integer,byte[],IMessage> messageFactory )
    {
      this.messageFactory = messageFactory;
      return this;
    }
    
    
    /**
     * Not sure that I like this...
     * 
     * Sets the serial data channel implementation - this would come from JSerialComm 
     * 
     * @param port
     * @return 
     */
    public Builder setSerialPort( final SerialPort port )
    {
      this.port = port;
      return this;
    }

    
    public Builder setSimType( final SimType t )
    {
      this.sim = t;
      return this;
    }
    
    
    public Builder setName( final String name )
    {
      this.name = name;
      return this;
    }
    
    
    public Builder setDescription( final String desc )
    {
      this.description = desc;
      return this;
    }
    
    
    public Builder setSerial( final String serial )
    {
      this.serial = serial;
      return this;
    }
    
    
    public Builder setComponentList( final List<IComponent> cl )
    {
      this.componentList = cl;
      return this;
    }
    
    
    public Device build() throws IOException
    {
      return new Device( this );
    }
  }
  
  
  /**
   * Represents a message in the queue waiting to be written to the device 
   */
  private static class QueueEntry 
  {
    private final int hardwareAddress;
    private final byte[] message;
    
    QueueEntry( final int hardwareAddress, final byte[] message )
    {
      this.hardwareAddress = hardwareAddress;
      this.message = message;
    }
    
    
    /**
     * the component this is for 
     * @return 
     */
    public int getHardwareAddress()
    {
      return hardwareAddress;
    }
    
    
    /**
     * The entire payload with headers 
     * @return 
     */
    public byte[] getMessage()
    {
      return message;
    }
  }
  
  private static final Logger LOG = LogManager.getLogger( Device.class );
  private static final int QUEUE_CAPACITY = 10;
  
  
  private final SimType sim;
  private final String name;
  private final String description;
  private final String serial;
  private final List<IComponent> componentList;
  private final boolean hasSerial;
  private final SerialPort port;
  private final HashMap<Integer,IComponent> componentAddressMap = new HashMap<>();
  private final BiFunction<Integer,byte[],IMessage> messageFactory;
  
  
  //..This is the message queue
  //..There might be some timing issues, etc and if we start getting backed up on messages
  //..This always adds messages to the end of the queue
  //..If the queue is full, this pops then adds.
  private final LinkedBlockingDeque<QueueEntry> messageQueue = new LinkedBlockingDeque<>( QUEUE_CAPACITY );
  
  
  
  protected Device( final Builder b ) throws IOException
  {
    if ( b.sim == null )
      throw new IllegalArgumentException( "Device SimType must not be null" );
    else if ( b.name == null )
      throw new IllegalArgumentException( "Devce name must not be null" );
    else if ( b.description == null )
      throw new IllegalArgumentException( "Device description must not be null" );
    else if ( b.serial == null )
      throw new IllegalArgumentException( "Device serial must not be null" );
    else if ( b.componentList == null )
      throw new IllegalArgumentException( "Device component list must not be null" );
    else if ( b.name.isEmpty() && b.serial.isEmpty())
      throw new IllegalArgumentException( "Device name and serial must not both be empty" );
    else if ( b.port == null )
      throw new IllegalArgumentException( "Device serial port must not be null" );
    else if ( b.messageFactory == null )
      throw new IllegalArgumentException( "Device message factory must not be null" );
    
    sim = b.sim;
    name = b.name;
    description = b.description;
    serial = b.serial;
    componentList = Collections.unmodifiableList( new ArrayList<>( b.componentList ));
    hasSerial = serial != null && !serial.trim().isEmpty();
    port = b.port;
    messageFactory = b.messageFactory;
    
    for ( final IComponent c : componentList )
    {
      componentAddressMap.put( c.getAddress(), c );
    }
    
    port.setComPortParameters( 9600, 8, 1, 0 );
    //port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0 );
    
    if ( !port.openPort())
      throw new IOException( "Failed to connect to device on " + port.getPortDescription());
    else
      Logs.info( LOG, "Connected to device", name, "sn", serial, "on port", port.getPortDescription());
  }
  
  
  @Override
  public SimType getSim()
  {
    return sim;
  }
  
  
  @Override
  public String getName()
  {
    return name;
  }
  
  
  @Override
  public String getSerial()
  {
    return serial;
  }
  
  
  @Override
  public String getDescription()
  {
    return description;
  }
  
  
  @Override
  public List<IComponent> getComponentList()
  {
    return componentList;
  }
  
  
  @Override
  public boolean hasSerial()
  {
    return hasSerial;
  }
  
  
  @Override
  public SerialPort getSerialPort()
  {
    return port;
  }
  
  
  @Override
  public void close() throws Exception
  {
    if ( !port.closePort())
      throw new IOException( "Device " + getName() + " sn: " + getSerial() + " - failed to close port" );
  }
  
  
  @Override
  public IComponent getComponentBySoftwareAddress( final int address )
  {
    if ( !componentAddressMap.containsKey( address ))
      return null;
      //throw new ComponentNotFoundException( "Component with software address " + String.valueOf( address ) + " not found on device " + getName() + " sn:" + getSerial());
    
    return componentAddressMap.get( address );
  }
  
  
  @Override
  public void write( final int hardwareAddress, final byte[] bytes )
  {
    
    final QueueEntry entry = new QueueEntry(
      hardwareAddress,
      bytes
    );
    
    //..Add the message to the end of the dequeue unless it's full, then pop
    while( !messageQueue.offerLast( entry ))
    {
      try {
        messageQueue.pop();
        Logs.info( LOG, "Device", getName(), "sn", getSerial(), "message queue full - removing head" );
      } catch( NoSuchElementException e ) {
        //..do nothing, it's empty
      }
    }
  }
  
  
  /**
   * Reads messages from the queue and sends them to the device 
   */
  @Override
  public void run()
  {
    try {
      while ( true )
      {
        final QueueEntry entry = messageQueue.pop();
        if ( entry == null )
          break;
        
        //..Send the data to the device 
        //..This converts everything to a happy little byte array the controller can understand 
        final IMessage message = messageFactory.apply( entry.getHardwareAddress(), entry.getMessage());
        if ( message == null )
        {
          Logs.error( LOG, "Message factory for device", getName(), "sn", getSerial(), "returned null - message abandoned" );
          return;
        }
        
        if ( !port.isOpen())
          port.openPort();
        
        port.getOutputStream().write( message.getBytes());
      }
    } catch( NoSuchElementException e ) {
      //..Do nothing, queue is empty
    } catch( IOException e ) {
      Logs.error( LOG, e, "Failed to send data to device", getName(), "sn", getSerial());
    }
  }
}
