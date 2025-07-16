/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.state;


/**
 * A state change event
 * 
 * The system monitors inbound telemetry from the flight sim.
 * When a data point changes, a change event is created containing the control information, new value and old value
 * 
 * @author John Quinn
 */
public interface IStateEvent<T>
{
  /**
   * Get the control constant from Controls 
   * @return 
   */
  public ISimControl getControl();
  
  
  /**
   * Retrieve the event value 
   * @return 
   */
  public T getValue();
  
  
  /**
   * Retrieve the previous value 
   * @return 
   */
  public T getOldValue();
  
  
  /**
   * Convert the internal value to a string 
   * @return 
   */
  @Override
  public String toString();
  
  
  /**
   * Retrieve the event time as system time in milliseconds 
   * @return time 
   */
  public long getEventTimeMillis();
}
