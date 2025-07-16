/**
 * This file is part of the buffalokiwi utils package, and is subject to the 
 * terms and conditions defined in file 'LICENSE.txt', which is part 
 * of this source code package.
 *
 * Copyright (c) 2017 John Quinn <johnquinn3@gmail.com>
 */


package com.buffalokiwi.utils;

import org.apache.logging.log4j.Logger;

/**
 *
 * @author John Quinn
 */
public class Logs
{
  private static final IEventHandlerStack<Object> handlerStack = new EventHandlerStack<>();
  
  
  
  
  /**
   * Retrieve the handler stack for when messages are written using error() or
   * fatal()
   * @return stack
   */
  public static IEventHandlerStack<Object> errorMessageHandler()
  {
    return handlerStack;
  }
  
  
  public static void printLoggerInfo( final Logger log )
  {
    info( log, "Logger Level" );
    
    if ( log.isWarnEnabled())
      info( log, " Warn:", "enabled" );
    else
      info( log, " Warn:", "disabled" );
    
    if ( log.isErrorEnabled())
      info( log, "Error:", "enabled" );
    else
      info( log, "Error:", "disabled" );
    
    if ( log.isFatalEnabled())
      info( log, "Fatal:", "enabled" );
    else
      info( log, "Fatal:", "disabled" );
      
    if ( log.isDebugEnabled())
      info( log, "Debug:", "enabled" );
    else
      info( log, "Debug:", "disabled" );
      
    if ( log.isTraceEnabled())
      info( log, "Trace:", "enabled" );
    else
      info( log, "Trace:", "disabled" );
  }
  
  
  
  /**
   * Logger a message with trace log level.
   *
   * @param log Logger to write to
   * @param message log this message
   */
  public static void trace( final Logger log, final Object... message )
  {
    if ( log.isTraceEnabled())
      log.trace( concat( message ));
  }
  
  
  /**
   * Concat some objects 
   * @param message messages
   * @return string 
   */
  private static String concat( final Object... message )
  {
    final StringBuilder s = new StringBuilder();
    
    for ( final Object o : message )
    {
      if ( o != null )
      {
        final String msg = o.toString();
        if ( msg != null && message.length > 10000 )
        {
          s.append( msg.substring( 0, 10000 ));
        }
        else
          s.append( msg );        
      }
      else
        s.append( o );
      
      
      s.append( ' ' );
    }
    
    return s.toString();
  }
  
  

  /**
   * Logger an error with trace log level.
   *
   * @param log Logger to write to
   * @param message log this message
   * @param t log this cause
   */
  public static void trace( final Logger log, final Throwable t, final Object... message )
  {
    if ( !log.isTraceEnabled())
      return;
    
    log.trace( t.getMessage(), t );
    if ( message.length > 0 )
      log.trace( concat( message ));
  }

  
  /**
   * Logger a message with debug log level.
   *
   * @param message log this message
   */
  public static void debug( final Logger log, final Object... message )
  {
    if ( log.isDebugEnabled())
      log.debug( concat( message ));
  }

  
  /**
   * Logger an error with debug log level.
   *
   * @param log Logger to write to
   * @param message log this message
   * @param t log this cause
   */
  public static void debug( final Logger log, final Throwable t, final Object... message )
  {
    if ( !log.isDebugEnabled())
      return;
    
    log.debug( t.getMessage(), t );
    if ( message.length > 0 )
      log.debug( concat( message ));    
  }

  
  /**
   * Logger a message with info log level.
   *
   * @param log Logger to write to
   * @param message log this message
   */
  public static void info( final Logger log, final Object... message )
  {
    if ( log.isInfoEnabled())
      log.info( concat( message ));
  }

  
  /**
   * Logger an error with info log level.
   *
   * @param log Logger to write to
   * @param message log this message
   * @param t log this cause
   */
  public static void info( final Logger log, final Throwable t, final Object... message )
  {
    if ( log.isInfoEnabled())
    {
      log.info( t.getMessage(), t );
      if ( message.length > 0 )
        log.info( concat( message ));    
    }
  }

  
  /**
   * Logger a message with warn log level.
   *
   * @param log Logger to write to
   * @param message log this message
   */
  public static void warn( final Logger log, final Object... message )
  {
    if ( log.isWarnEnabled())
      log.warn( concat( message ));
  }

  
  /**
   * Logger an error with warn log level.
   *
   * @param log Logger to write to
   * @param message log this message
   * @param t log this cause
   */
  public static void warn( final Logger log, final Throwable t, final Object... message )
  {
    if ( log.isWarnEnabled())
    {
      log.warn( t.getMessage(), t );
      if ( message.length > 0 )
        log.warn( concat( message ));
    }
  }

  
  /**
   * Logger a message with error log level.
   *
   * @param log Logger to write to
   * @param message log this message
   */
  public static void error( final Logger log, final Object... message )
  {
    if ( log.isErrorEnabled())
    {
      final String msg = concat( message );
      log.error( msg );
      writeToHandlers( msg, "" );
    }
  }
  
  
  private static void writeToHandlers( final String msg1, final String msg2 )
  {
    final StringBuilder b = new StringBuilder();
    
    if ( msg1 != null && !msg1.isEmpty())
    {
      b.append( msg1 );
    }
    
    
    if ( msg2 != null && !msg2.isEmpty())
    {
      if ( b.length() > 0 )
        b.append( " (" ).append( msg2 ).append( ")" );
      else
        b.append( msg2 );
    }
    
    if ( b.length() > 0 )
      handlerStack.execute( b.toString());
  }

  
  /**
   * Logger an error with error log level.
   * 
   * @param log Logger to write to
   * @param message log this message
   * @param t log this cause
   */
  public static void error( final Logger log, final Throwable t, final Object... message )
  {
    if ( log.isErrorEnabled())
    {
      log.error( t.getMessage(), t );
      
      String msg = "";
      if ( message.length > 0 )
      {
        msg = concat( message );
        log.error( msg );        
      }
      
      writeToHandlers( t.getMessage(), msg );
    }
  }

  
  /**
   * Logger a message with fatal log level.
   * @param log Logger to write to
   * @param message log this message
   */
  public static void fatal( final Logger log, final Object... message )
  {
    if ( log.isFatalEnabled())
    {
      final String msg = concat( message );
      log.fatal( msg );    
      writeToHandlers( msg, "" );
    }
  }

  
  /**
   * Logger an error with fatal log level.
   *
   * @param log Logger to write to
   * @param message log this message
   * @param t log this cause
   */
  public static void fatal( final Logger log, final Throwable t, final Object... message )
  {
    if ( log.isFatalEnabled())
    {
      log.fatal( t.getMessage(), t );
      
      String msg = "";
      if ( message.length > 0 )
      {
        msg = concat( message );
        log.fatal( msg );
      }
      
      writeToHandlers( t.toString(), msg );
    }
  }
}
