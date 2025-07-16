/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */

package com.buffalokiwi.simgateway.hardware;

import com.buffalokiwi.simgateway.SimType;


/**
 * A hardware component we can talk to 
 * 
 * A component is something that resides on a physical device - like an LCD screen or an indicator light, or a button or whatever. 
 * 
 * Components are grouped and associated with IDevice instances 
 * When sending data to a device, we specify the hardware address, which lets us send values to specific components on the device itself
 */
public interface IComponent 
{
  /**
   * The component type 
   * @return 
   */
  public ComponentType getType();
  
  
  /**
   * The human-friendly name 
   * @return 
   */
  public String getName();
  
  
  /**
   * A description for humans 
   * @return 
   */
  public String getDescription();
  
  
  /**
   * The software address.  This is a constant from SimGateway and is found in ISimControl 
   * @return 
   */
  public int getAddress();
  
  
  /**
   * The hardware address is a unique identifier specified by the device itself
   * @return 
   */
  public int getHardwareAddress();  
  
  
  /**
   * The type of flight sim this component works with 
   * Not sure this is even needed.  It's not referenced anywhere.
   * @return 
   */
  public SimType getSimType();
}
