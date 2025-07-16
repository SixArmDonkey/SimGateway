/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;

/**
 *
 * @author John Quinn
 */
public class CommandInput implements ICommandInput
{
  private final String line;
  private final String command;
  private final String payload;
  
  public CommandInput( final String line )
  {
    this( line, true );
  }
  
  
  public CommandInput( final String line, final boolean containsCommand )
  {
    if( line == null )
      throw new IllegalArgumentException( "cmd cannot be null");
    
    this.line = line;
    
    if ( containsCommand )
    {
      //..Split the command into command and optional data
      final String[] parts = line.split( " ", 2 );

      command = parts[0].trim();

      if ( parts.length > 1 )
        payload = parts[1].trim();
      else
        payload = "";

    }
    else
    {
      payload = line;
      command = "";
    }
  }
  
  
  public String getCommand()
  {
    return command;
  }
  
  
  public String getPayload()
  {
    return payload;
  }
  
  
  public String getLine()
  {
    return line;
  }
}
