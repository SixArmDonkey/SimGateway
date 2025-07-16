/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;

import com.buffalokiwi.utils.IJobPool;
import com.buffalokiwi.utils.JobPool;
import com.buffalokiwi.utils.Logs;
import com.buffalokiwi.utils.ThrowableBiFunction;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author John Quinn
 */
public class Server implements Runnable, AutoCloseable
{
  private static final Logger LOG = LogManager.getLogger( Server.class );
  
  
  /**
   * The server configuration builder
   */
  public static class ServerConfig 
  {
    /**
     * Min port number
     */
    public static final int MIN_PORT = 1024;
    
    /**
     * Max port number 
     */
    public static final int MAX_PORT = 65534;
    
    /**
     * Default server socket 
     */
    public static final int DEFAULT_PORT = 4201;
    
    /**
     * accept timeout
     * This is how long the socket server will wait for a client connection before
     * throwing an exception.  
     * This should be set to a reasonably small non-zero value to allow the server thread to 
     * terminate at some point.
     * This is used so the server doesn't block forever while waiting for connections
     */
    public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 5000;
    
    /**
     * Available client commands 
     */
    private final ICommandPool commands;
    
    /**
     * Program to run when the client connects
     */
    private final ThrowableBiFunction<Socket,ICommandPool,IClient,Exception> createClient;
    
    /**
     * Program to run when the client disconnects 
     */
    private final Runnable onClose;
    
    /**
     * Server port 
     */
    private int port = DEFAULT_PORT;
    
    /**
     * Default client inactivity timeout
     */
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT_MILLIS;
    

    /**
     * Socket server configuration 
     * @param commands Available client comments 
     * @param createClient Program to run when client connects
     * @param onClose Program to run when client disconnects
     */
    public ServerConfig( final ICommandPool commands, 
      final ThrowableBiFunction<Socket,ICommandPool,IClient,Exception> createClient )
    {
      this( commands, createClient, null );
    }

    
    /**
     * Socket server configuration 
     * @param commands Available client comments 
     * @param createClient Program to run when client connects
     * @param onClose Program to run when client disconnects
     */
    public ServerConfig( final ICommandPool commands, 
      final ThrowableBiFunction<Socket,ICommandPool,IClient,Exception> createClient, final Runnable onClose )
    {
      if ( commands == null)
        throw new IllegalArgumentException( "commands must not be null" );
      else if ( createClient == null )
        throw new IllegalArgumentException( "createClient must not be null" );
      
      this.commands = commands;
      this.createClient = createClient;
      this.onClose = onClose;
    }
    
    
    /**
     * Sets the socket server port 
     * @param port 
     */
    public ServerConfig setPort( int port )
    {
      if ( !isPortNumberValid( port ))
        throw new IllegalArgumentException( "port must be between 1024 and 65534 inclusive" );
      this.port = port;
      
      return this;
    }
    
    
    /**
     * Specify the time in milliseconds the socket server should block while waiting for client connections.
     * This should be a reasonably small value like 5000 (the default)
     * @param millis 
     */
    public ServerConfig setBlockingDuration( int millis )
    {
      if ( millis < 0 )
        throw new IllegalArgumentException( "Socket server blocking duration must be unsigned" );
      
      this.socketTimeout = millis;
      
      return this;
    }
  
    
    /**
     * Test that port is between port min and port max 
     * @param port port
     * @return 
     */
    private boolean isPortNumberValid( final int port )
    {
      return ( port >= MIN_PORT && port <= MAX_PORT );      
    }    
  }
  
  /**
   * No error 
   */
  private static final int E_NONE = 0;
  
  /**
   * If the client thorws an illegal argument or io exception, this error state is used
   */
  private static final int E_CLIENT_ASSERT_FAIL = 1;
  
  /**
   * If the server ran out of memory 
   */
  private static final int E_OUT_OF_MEMORY = 2;
  
  /**
   * The socket server.  YAY!
   */
  private final ServerSocket server;

  /**
   * Contains active client threads
   */
  private final IJobPool jobPool;
  
  /**
   * Optional program to run when a client disconnects
   */
  private final Runnable onClose;
  
  /**
   * Server uptime or null if server is not up
   */
  private Uptime uptime = null;
  
  /**
   * Supplier for creating client instances 
   */
  private final ThrowableBiFunction<Socket,ICommandPool,IClient,Exception> createClient;
  
  /**
   * If the server is running
   */
  private AtomicBoolean listening = new AtomicBoolean( false );
  
  /**
   * The server config
   */
  private ServerConfig config;
  
  /**
   * Server failure reason code
   */
  private int errorState = E_NONE;

  
  /**
   * @param commands available client commands 
   * @param createClient client program to run 
   * @return configuration
   */
  public static ServerConfig createConfig( final ICommandPool commands, 
      final ThrowableBiFunction<Socket,ICommandPool,IClient,Exception> createClient )
  {
    return new ServerConfig( commands, createClient, null );
  }

  
  /**
   * @param commands available client commands 
   * @param createClient client program to run 
   * @param onClose optional client close event 
   * @return configuration
   */
  public static ServerConfig createConfig( final ICommandPool commands, 
      final ThrowableBiFunction<Socket,ICommandPool,IClient,Exception> createClient, final Runnable onClose )
  {
    return new ServerConfig( commands, createClient, onClose );
  }
  
  
  /**
   * @param config
   * @throws IOException
   * @throws IllegalArgumentException 
   */
  public Server( final ServerConfig config ) throws IOException, IllegalArgumentException
  {
    //..20 threads for now 
    jobPool = new JobPool( 20, "Client Thread Executor" );    
    
    server = new ServerSocket( config.port );
    server.setSoTimeout( config.socketTimeout );    
    this.createClient = config.createClient;
    
    if ( config.onClose == null )
      this.onClose = () -> {};
    else
      this.onClose = config.onClose;
    
    this.config = config;
  }
  
  
  /**
   * Starts the socket server and waits for client connections
   */
  @Override
  public void run()
  {
    uptime = new Uptime();
    listening.set( true );
    
    Logs.info( LOG, "Server started on port " + config.port );

    while ( listening.get())
    {
      try {
        final Socket socket = server.accept();
        
        Logs.info( LOG, "Accepted client connection" );
        
        //..Start the new client thread 
        jobPool.submit( createClientJob( socket ));
      } catch( SocketTimeoutException e ) {
        //..do nothing
      } catch( IllegalArgumentException | IOException e ) {
        Logs.error( LOG, "Socket closed" );
        Logs.debug( LOG, e, "Failed to create a new client socket" );
        errorState = E_CLIENT_ASSERT_FAIL;
        close();
        break;
      } catch( OutOfMemoryError e ) {
        Logs.error( LOG, e, "Ran out of memory while creating a new client socket" );
        errorState = E_OUT_OF_MEMORY;
        close();
        break;
      } catch( Exception e ) {
        Logs.error( LOG, e, "Client Exception" );
        //..Continue listening 
      }
    }
    
    Logs.info( LOG, "Server shutting down.  Uptime:", uptime );
    
    uptime = null;
  }

  
  /**
   * Shuts down the socket server, job pool and runs the onClose job if available
   */
  @Override
  public void close()
  {
    listening.set( false );
    
    try {
      jobPool.getExecutor().shutdown();
    } catch( Exception e ) {
      Logs.error( LOG, e, "Failed to shutdown job pool" );
    }
    
    try {
      server.close();    
    } catch( IOException e ) {
      Logs.error( LOG, e, "Failed to close socket server" );
    }
    
    if ( onClose != null )
      onClose.run();
  }
  
  
  /**
   * Retrieve the error state 
   * @return 
   */
  public int getErrorState()
  {
    return errorState;
  }
  
  
  /**
   * Get the server uptime
   * @return 
   */
  public Uptime getUptime() throws Exception
  {
    if ( uptime == null )
      throw new Exception( "Server is not online" );
    
    return uptime;
  }
  
  
  /**
   * Creates a runnable containing the client program/runtime 
   * @param socket 
   * @return 
   */
  private Runnable createClientJob( final Socket socket )
  {
    return () -> {
      //..Attempt to retrieve a new client connection instance 
      try {
        createAndRunClientProgram( socket );
      } catch( NullPointerException e ) {
        //..createClient returned null.            
        Logs.error( LOG, e, "createClient must not return null." );
      } finally {
        try {
          socket.close();
        } catch( IOException e ) {
          Logs.error( LOG, e, "Server failed to close client socket" );
        }
      }
    };
  }
  
  
  /**
   * Creates a thread monitor for the client.
   * This ensures that the client thread is cleaned up
   * @param socket
   * @throws NullPointerException 
   */
  private void createAndRunClientProgram( final Socket socket ) throws NullPointerException 
  {
    Thread monitor = null;
    //..try with resources for client i/o streams 
    try ( final IClient client = createClient.apply( socket, config.commands )) {
      monitor = new Thread(() -> {
        while( client.isRunning())
        {
          try {
            Thread.sleep( 5000L );
            
            if ( client.isExpired())
            {
              try {
                client.close();
              } catch( Exception e ) {} 
              
              break;
            }
          } catch( InterruptedException e ) {
            try {
              client.close();
            } catch( Exception e1 ) {}
            
            break;
          }
        }
      });
      
      monitor.setName( "Client monitor " + client.getUUID());
      monitor.start();
      
      //..Run the client program 
      runClientProgram( client );
    } catch ( Exception e ) {
      //..Failed to create a new IClient instance 
      Logs.error( LOG, e, "Failed to close the client input/output streams" );
    } finally {
      if ( monitor != null )
        monitor.interrupt();
    }
  }
  
  
  /**
   * Runs the client program 
   * This should be placed in a thread 
   * @param client 
   */
  private void runClientProgram( final IClient client )
  {
    final String d = (new Date()).toString();
    //..Successful connection
    Logs.info( LOG, d + " Client", client.getUUID(), "successfully connected" );

    //..Run the client program
    try {
      client.run();
    } catch( ShutdownException e ) {
      Logs.info( LOG, "Shutdown" );
      close();
    } catch( Exception e ) {
      Logs.error( LOG, e, "Failed to execute client program for client", client.getUUID());
    }

    //..Successful disconnect
    Logs.info( LOG, "Client", client.getUUID(), "disconnected after", client.getUptime().getText());
  }  
}
