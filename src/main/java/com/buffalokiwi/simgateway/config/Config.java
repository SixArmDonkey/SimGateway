/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the 
 * terms and conditions defined in file 'LICENSE', which is part 
 * of this source code package.
 */
package com.buffalokiwi.simgateway.config;

import javax.json.JsonException;
import javax.json.JsonObject;


/**
 * Main configuration processor 
 * 
 * @author John Quinn
 */
public class Config 
{
  /**
   * Default socket server port 
   */
  public static final int DEFAULT_SERVER_PORT = 4201;
  
  /**
   * Socket server port number 
   */
  private int serverPort;
   
  
  /**
   * Read a JSON object and create the configuration object
   * @param json
   * @throws JsonException
   * @throws IllegalArgumentException 
   */
  public Config( final JsonObject json ) throws JsonException, IllegalArgumentException
  {
    if ( json == null )
      throw new IllegalArgumentException( "Cannot create configuration object when supplied JSON object is null" );
    
    final JsonObject server = getServerObject( json );
    
    serverPort = server.getInt( "port", DEFAULT_SERVER_PORT );
  }

  
  /**
   * Get the socket server port 
   * @return 
   */
  public int getServerPort()
  {
    return serverPort;
  }
  
  
  /**
   * Attempts to retrieve the "server" object containing the socket server configuration properties
   * @param json configuration JSON
   * @return server object 
   */
  private JsonObject getServerObject( final JsonObject json )
  {
    final JsonObject server = json.getJsonObject( "server" );
    if ( server == null )
      throw new JsonException( "Configuration JSON is missing server object server:{host,port}" );
    
    return server;
  }
}
