/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.state;


/**
 * A state event 
 * @author John Quinn
 * @param <T>
 */
public class GenericStateEvent<T> implements IStateEvent<T>
{
  private final long eventTime;
  private final ISimControl control;
  private final T value;
  private final T oldValue;
  
  
  /**
   * @param control
   * @param value
   * @param oldValue 
   */
  public GenericStateEvent( final ISimControl control, final T value, final T oldValue )
  {
    eventTime = System.currentTimeMillis();
    this.control = control;
    this.value = value;
    this.oldValue = oldValue;
  }
    
    
  /**
   * Get the control constant from Controls 
   * @return 
   */
  @Override
  public ISimControl getControl()
  {
    return control;
  }
  
  
  /**
   * Retrieve the event value 
   * @return 
   */
  @Override
  public T getValue()
  {
    return value;
  }
  
  
  /**
   * Retrieve the previous value 
   * @return 
   */
  @Override
  public T getOldValue()
  {
    return oldValue;
  }
  
  
  /**
   * Convert the internal value to a string 
   * @return 
   */
  @Override
  public String toString()
  {
    if ( value == null )
      return "";
    
    return value.toString();
  }

  
  /**
   * Retrieve the event time as system time in milliseconds 
   * @return time 
   */
  @Override
  public long getEventTimeMillis()
  {
    return eventTime;
  }
}
