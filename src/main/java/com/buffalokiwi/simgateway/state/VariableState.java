/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.state;

import java.util.concurrent.atomic.AtomicReference;


/**
 * Maintains the state of a single data point 
 * 
 * @author John Quinn
 */
public class VariableState<T> 
{
  /**
   * The control 
   */
  private final ISimControl control;
  
  /**
   * The state manager monitors registered data points for changes
   */
  private final IStateEventManager stateManager;
  
  /**
   * The control value
   */
  private final AtomicReference<T> value;
  
  
  /**
   * 
   * @param control The SimGateway control definition
   * @param stateManager Monitors registered data points for changes.  This state object may be registered with the state manager 
   * @param initialValue The initial value
   */
  public VariableState( final ISimControl control, final IStateEventManager stateManager, final T initialValue )
  {
    this.control = control;
    this.stateManager = stateManager;
    value = new AtomicReference<>( initialValue );
  }
  
  
  /**
   * Sets the value 
   * @param value 
   */
  public void set( final T value )
  {
    final T formattedValue = formatValue( value );
    if ( !this.value.equals( formattedValue ))
    {
      //..Do state change
      registerEvent( control, formattedValue, this.value.get());
    }
    this.value.getAndSet( formattedValue );
  }
  
  
  /**
   * Retrieve the current value 
   * @return 
   */
  public T get()
  {
    return value.get();
  }
  
  
  /**
   * Adds a state change event for this data point to the state manager 
   * @param control 
   * @param value
   * @param oldValue 
   */
  protected void registerEvent( final ISimControl control, final T value, final T oldValue )
  {
    stateManager.registerEvent( control, value, oldValue );
  }
  
  
  
  /**
   * Retrieve the state manager 
   * @return 
   */
  protected final IStateEventManager getStateManager()
  {
    return stateManager;
  }
    
  
  /**
   * A way to transform the value.
   * Pass through by default.
   * @param value
   * @return 
   */
  protected T formatValue( T value )
  {
    return value;
  }
}
