/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.state;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Registers state change events and puts them into a queue
 * @author John Quinn
 */
public class EventStateManager implements IStateEventManager
{
  private final LinkedBlockingQueue<IStateEvent> eventQueue;
    
  private final Map<Class, EventFactoryFunction<IStateEvent,Object>> factoryMap;
  
  
  /**
   * @param eventQueue Where to write the component update events.  This queue is read by the StateChangeProcessor
   * @param factoryMap A way to create custom IStateEvent instances by data type.  A map of class => factory 
   */
  public EventStateManager( final LinkedBlockingQueue<IStateEvent> eventQueue, 
    final Map<Class, EventFactoryFunction<IStateEvent,Object>> factoryMap )
  {
    this.eventQueue = eventQueue;
    this.factoryMap = factoryMap;
  }
   
    
  /**
   * Register an event 
   * @param <T> The value data type
   * @param control Control the value is for
   * @param value The current value
   * @param oldValue The former value  
   */
  @Override
  public <T> void registerEvent( final ISimControl control, final T value, final T oldValue )
  {
    //..Check for specific type and use that 
    if ( factoryMap.containsKey( value.getClass()))
    {
      eventQueue.add( factoryMap.get( value.getClass()).create( control, value, oldValue ));
      return;
    }
    
    //..Use the generic type 
    eventQueue.add( new GenericStateEvent<>( control, value, oldValue ));
  }
}
