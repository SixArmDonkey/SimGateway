
package com.buffalokiwi.utils;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowableFunction<T,R,E extends Exception> extends Function<T,R>
{
  @Override
  default R apply( final T t ) throws RuntimeException
  {
    try {
      return applyThrows( t );
    } catch( final RuntimeException e ) {
      throw e;
    } catch( final Exception e ) {
      throw new RuntimeException( e.getMessage(), e );
    }
  }
    
  public R applyThrows( final T t ) throws E;
}
