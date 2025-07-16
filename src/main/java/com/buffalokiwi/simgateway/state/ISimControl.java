/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.state;

import com.buffalokiwi.simgateway.SimType;


/**
 * A control for some simulator 
 * 
 * A control is an input or state within the flight simulator.  
 * Controls are created through a transformation adapter and are based on commands received via the socket interface
 * 
 * @param <E> A list of expected control constants representing a control group.  ie: Engine state telemetry 
 */
public interface ISimControl<E extends Enum<E>>
{
  /**
   * Retrieve the sim that this control is for 
   * @return 
   */
  public SimType getSimType();
  
  
  /**
   * Get the control id software address
   * This is a unique identifier representing the command within SimGateway and used by hardware to identify data types
   * @return 
   */
  public int getSoftwareAddress();
    
  /**
   * Get the control display name 
   * @return 
   */
  public String getCaption();
  
  
  /**
   * Retrieve the control constant
   * @return 
   */
  public E getControl();
}
