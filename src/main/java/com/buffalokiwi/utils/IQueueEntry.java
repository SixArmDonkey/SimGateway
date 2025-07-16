
package com.buffalokiwi.utils;

/**
 * An entry in a queue
 * @param <T>
 */
public interface IQueueEntry<T>
{
  public static <T> IQueueEntry<T> create( final String clientId, final T t )
  {
    return new IQueueEntry<T>()
    {
      @Override
      public String getClientId()
      {
        return clientId;
      }
      
      
      @Override
      public boolean isPoisonPill()
      {
        return t == null;
      }

      @Override
      public T getEntry()
      {
        return t;
      }
    };
  }
  
  
  /**
   * If the queue is at the end, and it should terminate, this must return true.
   * @return 
   */
  public boolean isPoisonPill();
  
  
  /**
   * Retrieve the entry 
   * @return 
   */
  public T getEntry();
  
  
  public String getClientId();
}
