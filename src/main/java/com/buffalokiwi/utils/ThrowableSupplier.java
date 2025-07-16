
package com.buffalokiwi.utils;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowableSupplier<R,E extends Exception> extends Supplier<R>
{
  @Override
  default R get() throws RuntimeException
  {
    try {
      return getThrows();
    } catch( final RuntimeException e ) {
      throw e;
    } catch( final Exception e ) {
      throw new RuntimeException( e.getMessage(), e );
    }
  }
    
  public R getThrows() throws E;
}
