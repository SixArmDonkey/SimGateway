/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;

import java.util.Set;

/**
 * Defines a command used for a client connection.
 * @author John Quinn
 */
public interface ICommand 
{
  /**
   * Retrieve the command string used to identify this command.
   * @return command string 
   */
  public String getCommand();
  
  /**
   * Retrieve a list of properties used to define the behavior of this command 
   * @return 
   */
  public Set<CommandProperty> getCommandProperties();
  
  
  /**
   * Check to see if some property is set 
   * @param prop property
   * @return is set 
   */
  public boolean hasProperty( final CommandProperty prop );
  
  
  /**
   * Execute the command and retrieve the response to send to the client.
   * @param input argument payload
   * @return payload 
   * @throws Exception 
   */
  public String execute( ICommandInput input ) throws Exception;
  
  /**
   * If the connection should close after writing all available data.
   * @return should quit
   */
  public boolean isQuit();
}
