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
import java.util.HashMap;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The device factory converts the configuration json file into the device and component instances that define the hardware 
 */
public class DeviceFactory implements IDeviceLocator, AutoCloseable
{
  /**
   * A DTO representing the device itself 
   */
  private final class DeviceId implements IDeviceId
  {
    private final String name;
    private final String serial;
    private final String description;
    private final boolean hasSerial;
    
    /**
     * @param name Human friendly device name
     * @param serial The device-identified serial number of the micro controller 
     * @param description A human friendly description
     */
    public DeviceId( final String name, final String serial, final String description )
    {
      if ( name == null || name.isEmpty())
        throw new IllegalArgumentException( "device name must not be null or empty" );
      
      this.name = name;
      this.serial = ( serial == null ) ? "" : serial;
      this.description = ( description == null ) ? "" : description;
      hasSerial = serial != null && !serial.trim().isEmpty();
    }
    
    @Override
    public boolean hasSerial() {
      return hasSerial;
    }

    @Override
    public String getSerial() {
      return serial;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getDescription() {
      return description;
    }
  }
  
  
  /**
   * The master list of devices 
   */
  private final ArrayList<IDevice> deviceList = new ArrayList<>();
  
  /**
   * Map of software address => IDevice 
   * This is how we translate the configured component constants to a component on the device itself
   */
  private final HashMap<Integer,IDevice> softAddressMap = new HashMap<>();
    
  /**
   * The log 
   */
  private static final Logger LOG = LogManager.getLogger(DeviceFactory.class );
    
  /**
   * The type of sim this device supports 
   */
  private final SimType sim;
  
  
  /**
   * @param currentSim Current sim
   */
  public DeviceFactory( final SimType currentSim )
  {
    super();
    sim = currentSim;
  }

  
  /**
   * Close the device serial communication channel 
   * @throws Exception 
   */
  @Override
  public void close() throws Exception
  {
    for ( final IDevice d : deviceList )
    {
      try {
        d.close();
      } catch( Exception e ) {
        Logs.error( LOG, e );
      }
    }
  }
  
  
  /**
   * Retrieve the complete list of supported connected devices
   * @return 
   */
  public List<IDevice> getDeviceList()
  {
    return new ArrayList<>( deviceList );
  }
  
  
  /**
   * Adds device ids from a json array 
   * 
   * Like this:
   *
   * name should be whatever the device identifies itself as
   * serial is optional and is a way to identify devices with the same name, but may require manual configuration
   * 
   * "devices": [
   *    {
   *      "mame" : "Arduino Uno",
   *      "serial" : "442383131393513132F1",
   *      "description" : "Prototype development LCD"
   *    }
   *  ]
   * 
   * @param deviceList 
   */
  public void addDeviceList( final JsonArray deviceList )
  {
    int deviceIndex = -1;
    
    for ( final JsonValue entry : deviceList )
    {
      deviceIndex++;
      
      if ( !( entry instanceof JsonObject ))
      {
        final String type = ( entry == null ) ? "null" : entry.getValueType().toString();
        Logs.error( LOG, "config.json devices array entries must be objects.  Got ", type, "at device index", deviceIndex );
        continue;
      }
      
      final JsonObject o = (JsonObject)entry;
      
      try {
              
        
        final String devName = o.getString( "name", "" );
        final String devSerial = o.getString( "serial", "" );
        final String devDesc = o.getString( "description", "" );
        
        final DeviceId dev = new DeviceId( devName, devSerial, devDesc );
        
        if ( devName.trim().isEmpty() && devSerial.trim().isEmpty())
        {
          Logs.error( LOG, "config.json devices array error at index", deviceIndex, "- all devices must have a non-empty name or non-empty serial key used to identify the hardware controller" );
          continue;
        }
        
        //..Only load live devices 
        if ( matchesLiveDevice( dev ))
        {
          Logs.info( LOG, "Found device", dev.getName(), "sn:" + dev.getSerial());
          
          if ( o.get( "components" ) instanceof JsonObject )
          {
            //..need to set the result on the device
            final List<IComponent> componentList = createComponents( o.getJsonObject( "components" ), deviceIndex );
            
            try {
              
              final IDevice device = new Device.Builder()
                .setSerialPort( getLiveDevice( dev ))
                .setName( dev.getName())
                .setDescription( dev.getDescription())
                .setSerial( dev.getSerial())
                .setSimType( sim )
                .setComponentList( componentList )
                .build();
              
              for ( final IComponent c : componentList )
              {
                softAddressMap.put( c.getAddress(), device );
              }
              
              this.deviceList.add( device );
              
            } catch( IllegalArgumentException e ) {
              Logs.error( LOG, e, "Failed to create Device at config.json.devices index", deviceIndex );
            } catch( DeviceNotFoundException e ) {
              Logs.error( LOG, e, "Failed to locate Device at config.json.devices index", deviceIndex, "with name \"", dev.getName(), "\" and serial \"", dev.getSerial(), "\"" );
            } catch( IOException e ) {
              Logs.error( LOG, e, "Failed to connect to device at config.json.devices index", deviceIndex, "with name \"", dev.getName(), "\" and serial \"", dev.getSerial(), "\"" );
            }
          }
        }
        else
        {
          Logs.error( LOG, "Configured device", dev.getName(), "sn:" + dev.getSerial(), "not found - is it plugged in?" );
        }
      } catch( IllegalArgumentException e ) {
        Logs.error( LOG, e, "config.json devices array entry at index", deviceIndex, "is missing name, serial or description" );
      }
    }
  }


  /**
   * Locates a device by the unique software address (control id)
   * @param address
   * @return device or null 
   */
  @Override
  public IDevice findDeviceBySoftwareAddress( final int address )
  {
    if ( !softAddressMap.containsKey( address ))
      return null;
    
    return softAddressMap.get( address );
  }

  
  /**
   * Find a device by serial or name
   * Returns the first match by either 
   * @param serialOrName 
   * @return
   * @throws DeviceNotFoundException 
   */
  public IDeviceId getDeviceId( final String serialOrName ) throws DeviceNotFoundException
  {
    for ( final IDevice entry : deviceList )
    {
      if (( entry.hasSerial() && entry.getSerial().equals( serialOrName ))
        || ( !entry.hasSerial() && entry.getName().equals( serialOrName )))
      {
        return entry;
      }
    }
    
    throw new DeviceNotFoundException();
  }
  
  
  /**
   * Retrieve the live serial data channel for the device 
   * @param serialOrName device serial or configured name 
   * @return
   * @throws DeviceNotFoundException 
   */
  public SerialPort getLiveDevice( final String serialOrName ) throws DeviceNotFoundException
  {
    return getLiveDevice( getDeviceId( serialOrName ));
  }
  
  
  /**
   * Retrieve the live serial data channel for the device 
   * @param dev The device id instance 
   * @return
   * @throws DeviceNotFoundException 
   */
  public SerialPort getLiveDevice( final IDeviceId dev ) throws DeviceNotFoundException
  {
    for ( final SerialPort port : SerialPort.getCommPorts())
    {
      if ( dev.hasSerial() && port.getSerialNumber().equals( dev.getSerial()))
      {
        //..Check that these match 
        //    sp.setComPortParameters(9600, 8, 1, 0);
        port.setComPortTimeouts( SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0 );
        return port;
      }
      else if ( !dev.hasSerial() && port.getDescriptivePortName().equals( dev.getName()))
      {
        return port;
      }
    }
    
    throw new DeviceNotFoundException();
  }

  
  /**
   * Test if the device id matches a connected device  
   * @param dev device id 
   * @return 
   */
  private boolean matchesLiveDevice( final IDeviceId dev )
  {
    for ( final SerialPort port : SerialPort.getCommPorts())
    {
      if ( dev.hasSerial() && port.getSerialNumber().equals( dev.getSerial()))
      {
        return true;
      }
      else if ( !dev.hasSerial() && port.getDescriptivePortName().equals( dev.getName()))
      {
        return true;
      }
    }
    
    return false;
  }
  
  
  /**
   * 
   * Create a list of components by type based on the json config 
   * 
    "lcd" : {                                         component type,  Keys are component names from Component enum 
      "main" : {                                      component name
        "description": "The main LCD display",        component description
        "address": 1,                                 component hardware address
        "sim": {                                      supported sims object.  this is a constant "sim"
          "dcs": 1                                    Supported sim software address. keys are sim names from SimType. values are controlId from the Control enum in the appropriate package 
        }
      }
    }
   * @param o 
   */
  private List<IComponent> createComponents( final JsonObject o, final int deviceIndex )
  {
    final ArrayList<IComponent> out = new ArrayList<>();
    
    if ( o == null )
      return out;
    
    
    int index = -1;
    
    //..Each entry in keySet is a component type 
    for ( final String key : o.keySet())
    {
      index++;
      final ComponentType ct = ComponentType.fromName( key );
      if ( ct == null )
      {
        Logs.error( LOG, "Config section error: devices at index", deviceIndex, "components at index", index, "- key is not a valid ComponentType" );
        continue;
      }
      else if ( !( o.get( key ) instanceof JsonObject ))
      {
        Logs.error( LOG, "Config section error: devices at index", deviceIndex, "components at index", index, "- value must be an object" );
        continue;
      }
      
      
      //..Each component type key has an object value
      final JsonObject component = o.getJsonObject( key );
      
      for ( final String componentKey : component.keySet())
      {
        if ( !( component.get( componentKey ) instanceof JsonObject ))
        {
          Logs.error( LOG, "Config section error: devices at index", deviceIndex, "components." + key + "." + componentKey, "at index", index, " - missing sim key.  This must be an object containing sim names and addresses.  sim:{dcs:1}" );
          continue;
        }
        
        final JsonObject c = component.getJsonObject( componentKey );
        
        if ( !( c.get( "sim" ) instanceof JsonObject ))
        {
          Logs.error( LOG, "Config section error: devices at index", deviceIndex, "components." + key + "." + componentKey, "at index", index, " - missing sim key.  This must be an object containing sim names and addresses.  sim:{dcs:1}" );
          continue;
        }

        //..Get the software address
        final int softwareAddress = c.getInt( "address", -1 );
        if ( softwareAddress < 0 )
        {
          Logs.error( LOG, "Config section error: devices at index", deviceIndex, "components." + key + "." + componentKey, "at index", index, " - sim software address must be an unsigned integer" );
          continue;
        }

        //..Get the hardware address
        final int hardwareAddress = getHardwareAddressForCurrentSim( c.getJsonObject( "sim" ));
        if ( hardwareAddress == -2 )
        {
          //..Unsupported by the current sim 
          continue;
        }
        else if ( hardwareAddress < 0 )
        {
          Logs.error( LOG, "Config section error: devices at index", deviceIndex, "components." + key + "." + componentKey, "at index", index, " - sim hardware address for", sim.getName(), "must be an unsigned integer" );
          continue;
        }

        //..Create the component builder
        try {
          final IComponent newComponent = new Component.Builder()
            .setSim( sim )
            .setType( ct )
            .setName( componentKey )
            .setDescription( c.getString( "description", "" ))
            .setAddress( softwareAddress )
            .setHardwareAddress( hardwareAddress )
            .build();
          
          Logs.info( LOG, "Found component", newComponent.getName(), "-", newComponent.getDescription(), "at address", newComponent.getAddress());
          
          out.add( newComponent );
          
           
        } catch( IllegalArgumentException e ) {
          Logs.error( LOG, e, "Config section error: devices at index", deviceIndex, "components." + key + "." + componentKey, "at index ", index );
        }
      }
    }
    
    return out;
  }
  
  
  /**
   * Test if the component supports the current sim runtime 
   * @param o This is the sim object 
   * @return 
   */
  private int getHardwareAddressForCurrentSim( final JsonObject o )
  {
    for ( final String key : o.keySet())
    {
      if ( sim.getName().equalsIgnoreCase( key ))
      {
        return o.getInt( key, -1 );
      }
    }
    
    return -2;
  }
}
