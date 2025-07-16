/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.state;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;


/**
 * Reads state changes from the queue and invokes all registered data handlers with the dequeued payload 
 * @author John Quinn
 */
public class StateChangeProcessor<T extends IStateEvent> implements Runnable
{
  private final Consumer<T>[] handlerList;
  private final LinkedBlockingQueue<T> eventQueue;
  
  
  public StateChangeProcessor( final LinkedBlockingQueue<T> eventQueue, final Consumer<T> ...handlerList )
  {
    this.handlerList = handlerList;
    this.eventQueue = eventQueue;
  }
  
    
  @Override
  public void run()
  {
    while( true )
    {
      final T event = eventQueue.poll();
      if ( event == null )
        break;
      
      for ( final Consumer<T> handler : handlerList )
      {
        handler.accept( event );
      }
    }
  }
}
