/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A Client Command 
 * @author John Quinn
 */
public abstract class Command implements ICommand 
{
  private final String command;
  private final Set<CommandProperty> properties;
  private final boolean shouldQuit;

  /**
   * Execute the command and retrieve the response to send to the client.
   * @param input argument payload
   * @return payload 
   * @throws Exception 
   */
  public abstract String execute( ICommandInput input ) throws Exception;

  
  
  public Command( final String command )
  {
    this( command, new HashSet<>());
  }
  
  
  public Command( final String command, final Set<CommandProperty> properties )
  {
    this( command, properties, false );
  }
  
  
  public Command( final String command, final Set<CommandProperty> properties, final boolean shouldQuit )
  {
    if ( command == null || command.trim().isEmpty())
      throw new IllegalArgumentException( "command must not be null or empty" );
    else if ( properties == null )
      throw new IllegalArgumentException( "properties must not be null" );
    
    this.command = command.trim();
    this.properties = Collections.unmodifiableSet( new HashSet<>( properties ));
    this.shouldQuit = shouldQuit;
  }
  
  /**
   * If the connection should close after writing all available data.
   * @return should quit
   */
  public boolean isQuit()
  {
    return shouldQuit;
  }
  
  
  /**
   * Retrieve the command string used to identify this command.
   * @return command string 
   */
  @Override
  public String getCommand()
  {
    return command;
  }

  
  /**
   * Retrieve a list of properties used to define the behavior of this command 
   * @return 
   */
  @Override
  public Set<CommandProperty> getCommandProperties()
  {
    return properties;
  }
  
  
  /**
   * Check to see if some property is set 
   * @param prop property
   * @return is set 
   */
  @Override 
  public boolean hasProperty( final CommandProperty prop )
  {
    return properties.contains( prop );
  }
}
