/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway;

import com.buffalokiwi.simgateway.dcs.EngineInfo;
import com.buffalokiwi.socketserver.Command;
import com.buffalokiwi.socketserver.ICommandInput;


/**
 * @author John Quinn
 */
public class EngineInfoCommand extends Command
{
  public static final String COMMAND = "dcsEngineInfo";
  
  private final EngineInfo info;

  public EngineInfoCommand( final EngineInfo info )
  {
    super( COMMAND );
    
    this.info = info;
  }

  
  @Override
  public String execute( final ICommandInput input ) throws Exception 
  {
    info.update( input.getPayload());
    return "";
  }
}
