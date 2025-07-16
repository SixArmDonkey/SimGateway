/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;

import com.buffalokiwi.simgateway.SimGateway;
import com.buffalokiwi.utils.Logs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A generic client connection for the socket server.
 * This is a thread.
 * This will listen to
 * @author John Quinn
 */
public class Client implements IClient
{
  private static enum InputMode {
    COMMAND,
    MULTILINE
  };
  
  /**
   * String used to terminate multiline input mode 
   */
  private static final String MULTILINE_TERMINATOR = ".";
  
  /**
   * Logger
   */
  private static final Logger LOG = LogManager.getLogger( Client.class );
  private static final int MAX_EXCEPTIONS = 5;
  
    
  
  
  /**
   * A pool of available commands
   */
  private final ICommandPool commands;
  
  /**
   * Socket connection provided from the server
   */
  private final Socket socket;
  
  /**
   * Socket output stream 
   */
  private final PrintWriter outputStream;
  
  /**
   * Socket input stream
   */
  private final BufferedReader inputStream;
  
  /**
   * Client thread uptime tracker.
   * This includes thread wait times.
   */
  private final Uptime uptime = new Uptime();
  
  /**
   * UUID 
   */
  private final String uuid = UUID.randomUUID().toString();
  
  /**
   * If the client should continue listening for input
   */
  private volatile boolean running = true;
  
  private volatile long lastCommandTime = 0;
  
  
  private final String promptText;
  
  private final boolean bigEndian;
  
  
  public Client( final Socket socket, final ICommandPool commands, final String promptText) throws IOException
  {
    this( socket, commands, promptText, true );
  }
  
  
  /**
   * Create a new Client connection instance
   * @param socket
   * @param commands 
   */
  public Client( final Socket socket, final ICommandPool commands, final String promptText, final boolean bigEndian ) throws IOException
  {
    if ( socket == null )
      throw new IllegalArgumentException( "socket must not be null" );    
    else if ( commands == null )
      throw new IllegalArgumentException( "commands must not be null" );
    
    this.promptText = promptText;
    this.socket = socket;
    this.commands = commands;
    outputStream = new PrintWriter( socket.getOutputStream(), true );
    inputStream = new BufferedReader( new InputStreamReader( socket.getInputStream()));
    this.bigEndian = bigEndian;
  }   
  
  
  @Override
  public void close() throws Exception 
  {
    running = false;
    outputStream.close();
    
    try {
      inputStream.close();
    } catch( IOException e ) {
      LOG.error( "failed to close socket input stream", e );
    }
  }
  
  public boolean isExpired()
  {
    //return System.currentTimeMillis() - lastCommandTime > 1800000;
    return false; //..The client never expires 
  }
  
  public boolean isRunning()
  {
    return running;
  }
  
  
  @Override 
  public void run() throws Exception 
  {
    try {
      //..Total number of exceptions encountered 
      int exceptionCount = 0;

      //..Tell the user hello
      final String promptText = getPromptText();
      if ( !promptText.isEmpty())
        outputStream.println( getPromptText());
     

      //..Input mode 
      InputMode inputMode = InputMode.COMMAND;

      //..Buffer used for multiline input commands 
      final StringBuilder multiBuffer = new StringBuilder();

      //..The command being processed
      ICommand command = null;

      //..Command line 
      String cmd;

      lastCommandTime = System.currentTimeMillis();

      //..Get a line from the input stream 
      while ((( cmd = inputStream.readLine()) != null ) && ( running ))
      {
        lastCommandTime = System.currentTimeMillis();
        try {

          //..DO something based on input mode
          switch( inputMode )
          {
            //..User should be entering some command with optional arguments 
            case COMMAND:
              //..Get the command line 
              final ICommandInput input = new CommandInput( cmd.trim());

              //..Get the command 
              command = getCommand( input.getCommand());

              //..Check for multiline input 
              if ( command.hasProperty( CommandProperty.MULTILINE ))
              {
                //..This command requires multiline input, set the input mode 
                inputMode = InputMode.MULTILINE;
              }
              else
              {
                //..Execute the command now 
                executeCommand( command, input );
              }
            break;

            //..Multiline input mode is enabled and the user should be sending a payload with potentially more than one line 
            case MULTILINE:
              //..User can terminate by sending the terminator string 
              if ( cmd.equals( MULTILINE_TERMINATOR ))
              {
                //..Reset the input mode to default 
                inputMode = InputMode.COMMAND;

                //..Execute the command with the multiline buffer 
                executeCommand( command, new CommandInput( multiBuffer.toString(), false ));
              }
              else
              {
                //..Append whatever the user sent to the multiline buffer 
                multiBuffer.append( cmd );
                multiBuffer.append( '\n' );
              }
            break;
          }

          //..Clear the multiline buffer if the input mode is not multiline 
          if ( !inputMode.equals( InputMode.MULTILINE ))
          {
            //..Clear the multiline buffer
            multiBuffer.setLength( 0 );
          }

        } catch( QuitException e ) {
          //..The user wants to disconnect 
          //..That's cool.
          Logs.info( LOG, "Client", getUUID(), "requested to be disconnected" );
          running = false;
          break;
        } catch( ShutdownException e ) {
          //..Allow shutdown exceptions to bubble up
          running = false;
          throw e;
        } catch( Exception e ) {
          //..Reset the input mode
          inputMode = InputMode.COMMAND;
          //..Clear the multiline buffer
          multiBuffer.setLength( 0 );

          //..Tell the user something went wrong
          outputStream.println( "Invalid command" );

          //..No command found or something bad hapened 
          Logs.error( LOG, e, "Failed to process command:", cmd );

          //..Don't let them spam bad commands forever...
          if ( ++exceptionCount == MAX_EXCEPTIONS )
          {
            running = false;
            break;
          }
        }
      }
    } finally {
      running = false;
    }
  }
  
  
  /**
   * Retrieve the client connection uptime 
   * @return uptime
   */
  @Override
  public IUptime getUptime()
  {
    return uptime;
  }
  
  
  /**
   * Retrieve the UUID used to identify the client connection 
   * @return UUID 
   */
  @Override
  public String getUUID()
  {
    return uuid;
  }
  
  
  /**
   * Retrieve the initial prompt text 
   * @return text 
   */
  @Override
  public String getPromptText()
  {
    return promptText;
  }
  
  
  /**
   * Retrieve a command
   * @param command command string 
   * @return command object 
   * @throws IllegalArgumentException 
   */
  private ICommand getCommand( final String command ) throws IllegalArgumentException 
  {
    final Map<String,ICommand> cmds = commands.getCommands( ICommandPool.DEFAULT_GROUP_ID );
    if ( !cmds.containsKey( command ))
      throw new IllegalArgumentException( "Invalid command" );
    
    return cmds.get( command );
  }
  
  
  private void executeCommand( final ICommand command, final ICommandInput input ) throws Exception
  {
    //..Execute the command 
    final String output = command.execute( input );

    //..Print the output if necessary 
    if ( output != null && !output.isEmpty())
    {
      final ByteBuffer b = ByteBuffer.allocate( 8 );
      
      if ( bigEndian )
        b.order( ByteOrder.BIG_ENDIAN);
      else
        b.order( ByteOrder.LITTLE_ENDIAN);
      
      long len = output.length();
      
      b.putLong( len );
      
      System.out.println( ByteBuffer.wrap(Arrays.copyOfRange( b.array(), 0, 4 )).order( ByteOrder.LITTLE_ENDIAN ).getInt());
      
      
      Logs.error( LOG, "******************************************************" );
      
      Logs.error( LOG, output.length());
      Logs.error( LOG, output );
      
      Logs.error( LOG, "******************************************************" );
      
      final String length = new String( Arrays.copyOfRange( b.array(), 0, 4 ), StandardCharsets.US_ASCII );
      outputStream.println( length + output );    
    }
    
    if ( command.isQuit())
      throw new QuitException();
  }
}
