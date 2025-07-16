/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */

package com.buffalokiwi.simgateway.hardware;


/**
 * Used to identify a serial device
 * This would be something loaded from a configuration file 
 */
public interface IDeviceId 
{
  /**
   * If the device has a serial number 
   * @return 
   */
  public boolean hasSerial();
  
  
  /**
   * Retrieve the device serial or an empty string 
   * @return 
   */
  public String getSerial();
         
  
  /**
   * Retrieve the device friendly name 
   * @return 
   */
  public String getName();
  
  
  /**
   * Retrieve the description 
   * @return 
   */
  public String getDescription();
}
