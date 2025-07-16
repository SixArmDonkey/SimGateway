/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;

import com.buffalokiwi.utils.ThrowableFunction;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author John Quinn
 */
public class QuickCommand extends Command
{
  private final ThrowableFunction<String,String,Exception> execute;
  
  
  public QuickCommand( final String command, final ThrowableFunction<String,String,Exception> execute )
  {
    this( command, new HashSet<>(), execute );
  }
  
  
  public QuickCommand( final String command, final Set<CommandProperty> properties, final ThrowableFunction<String,String,Exception> execute )  
  {
    this( command, properties, execute, false );
  }
  
  
  public QuickCommand( final String command, final Set<CommandProperty> properties, final ThrowableFunction<String,String,Exception> execute, final boolean shouldQuit )  
  {
    super( command, properties, shouldQuit );
    if ( execute == null )
      throw new IllegalArgumentException( "execute must not be null" );
    
    this.execute = execute;    
  }
  
  
  /**
   * Execute the command and retrieve the response to send to the client.
   * @param input argument payload
   * @return payload 
   * @throws Exception 
   */
  @Override
  public String execute( ICommandInput input ) throws Exception
  {
    return execute.apply( input.getPayload());
  }
}
