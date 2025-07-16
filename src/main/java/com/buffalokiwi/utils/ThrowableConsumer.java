
package com.buffalokiwi.utils;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowableConsumer<T,E extends Exception> extends Consumer<T>
{
  @Override
  default void accept( final T t ) throws RuntimeException
  {   
    try {
      acceptThrows( t );
    } catch( final RuntimeException e ) {
      throw e;
    } catch( final Exception e ) {
      throw new RuntimeException( e.getMessage(), e );
    }
  }
    
  public void acceptThrows( T t ) throws E;
}
