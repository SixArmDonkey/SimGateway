package com.buffalokiwi.simgateway.state;


/**
 * A functional interface defining a factory for creating IStateEvent instances 
 * State events are used to track the state of various data points 
 * 
 * @param <T> The IStateEvent type 
 * @param <U> The value data type 
 */
@FunctionalInterface
public interface EventFactoryFunction<T extends IStateEvent, U>
{
  /**
   * Create a state event instance for a specific data type 
   * @param control Control being update
   * @param value The current value
   * @param oldValue The former value 
   * @return 
   */
  T create( final ISimControl control, final U value, final U oldValue );
}
