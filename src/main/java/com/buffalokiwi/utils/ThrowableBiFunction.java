/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.utils;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ThrowableBiFunction<T,U,R,E extends Exception> extends BiFunction<T,U,R>
{
  @Override
  default R apply( final T t, final U u ) throws RuntimeException
  {
    try {
      return applyThrows( t, u );
    } catch( final RuntimeException e ) {
      throw e;
    } catch( final Exception e ) {
      throw new RuntimeException( e.getMessage(), e );
    }
  }
    
  public R applyThrows( final T t, final U u ) throws E;
}
