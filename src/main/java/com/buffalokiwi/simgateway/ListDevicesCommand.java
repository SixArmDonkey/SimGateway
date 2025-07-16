/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */

package com.buffalokiwi.simgateway;

import com.buffalokiwi.socketserver.Command;
import com.buffalokiwi.socketserver.ICommandInput;
import com.fazecast.jSerialComm.SerialPort;

/**
 * Lists attached serial devices 
 */
public class ListDevicesCommand extends Command
{
  public static final String COMMAND = "listDevices";

  public ListDevicesCommand()
  {
    super( COMMAND );
    
  }

  
  @Override
  public String execute( final ICommandInput input ) throws Exception 
  {
    final SerialPort portList[] = SerialPort.getCommPorts();
    
    final StringBuilder out = new StringBuilder();
    
    for ( int i = 0; i < portList.length; i++ )
    {
      final int index = i + 1;
      final SerialPort port = portList[i];      
      out.append( index );
      out.append( ") " );
      out.append( port.getDescriptivePortName());
      out.append( " sn:" );
      out.append( port.getSerialNumber());
    }
    
    return out.toString();
  }
}
