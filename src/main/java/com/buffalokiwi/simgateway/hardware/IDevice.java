/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */

package com.buffalokiwi.simgateway.hardware;

import com.buffalokiwi.simgateway.SimType;
import com.fazecast.jSerialComm.SerialPort;
import java.util.List;


/**
 * A device is a physical piece of hardware.
 * 
 * The device object maintains a list of available components, and the associated software addresses for referencing 
 * within SimGateway
 */
public interface IDevice extends IDeviceId, AutoCloseable, Runnable
{
  /**
   * Supported SIM
   * @return 
   */  
  public SimType getSim();
  
  
  /**
   * A list of available components on the device 
   * @return 
   */
  public List<IComponent> getComponentList();
  
  
  /**
   * The serial port that the device is currently connected
   * @return 
   */
  public SerialPort getSerialPort();
  
  
  /**
   * Retrieve an on-board component by the configured SimGateway software address
   * @param address
   * @return 
   */
  public IComponent getComponentBySoftwareAddress( final int address );
  
  
  /**
   * Write a new hardware value to the device for a specific component 
   * @param hardwareAddress This is the device-defined hardware address 
   * @param bytes The bytes to write 
   */
  public void write( final int hardwareAddress, final byte[] bytes );
}
        