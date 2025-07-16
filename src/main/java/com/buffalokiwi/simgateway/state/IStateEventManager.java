/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.state;


/**
 * Creates and registers state events 
 * @author John Quinn
 */
public interface IStateEventManager 
{
  /**
   * Register an event 
   * @param <T> Type 
   * @param control
   * @param value
   * @param oldValue 
   */
  public <T> void registerEvent( final ISimControl control, final T value, final T oldValue );  
}
