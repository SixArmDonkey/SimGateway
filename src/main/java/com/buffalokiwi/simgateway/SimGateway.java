/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway;

import com.buffalokiwi.simgateway.config.CommandLineArguments;
import com.buffalokiwi.simgateway.config.Config;
import com.buffalokiwi.simgateway.config.FileLocator;
import com.buffalokiwi.simgateway.dcs.EngineInfo;
import com.buffalokiwi.simgateway.hardware.DeviceFactory;
import com.buffalokiwi.simgateway.hardware.IComponent;
import com.buffalokiwi.simgateway.hardware.IDevice;
import com.buffalokiwi.simgateway.state.BooleanStateEvent;
import com.buffalokiwi.simgateway.state.EventStateManager;
import com.buffalokiwi.simgateway.state.ISimControl;
import com.buffalokiwi.simgateway.state.IStateEvent;
import com.buffalokiwi.simgateway.state.IStateEventManager;
import com.buffalokiwi.simgateway.state.StateChangeProcessor;
import com.buffalokiwi.socketserver.Client;
import com.buffalokiwi.socketserver.CommandPool;
import com.buffalokiwi.socketserver.ICommandPool;
import com.buffalokiwi.socketserver.QuickCommand;
import com.buffalokiwi.socketserver.QuitException;
import com.buffalokiwi.socketserver.Server;
import com.buffalokiwi.socketserver.ShutdownException;
import com.buffalokiwi.utils.Logs;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.buffalokiwi.simgateway.state.EventFactoryFunction;


/**
 * SimGateway Main Entry Point / Program Start
 * 
 * @author John Quinn
 */
public class SimGateway
{
  public static final SimType CURRENT_SIM = SimType.DCS_WORLD;
  
  /**
   * The program name
   */
  public static final String PROGRAM_NAME = "Sim Gateway";
  
  /**
   * Program version
   */
  public static final String PROGRAM_VERSION = "0.1";
  
  /**
   * Socket server host greeting 
   */
  public static final String SERVER_GREETING = "Greetings! " + PROGRAM_NAME + " " + PROGRAM_VERSION + " at your service";
  
  
  /**
   * Exit code: Success
   */
  public static final int EXIT_SUCCESS = 0;
  
  /**
   * Exit code: file locator failed to extract and/or locate a required configuration file 
   */
  public static final int EXIT_FILE_LOCATOR_FAIL = 1;
  
  /**
   * Configuration file cannot be read (not exists, not readable, not file)
   */
  public static final int EXIT_CONFIG_FILE_UNREADABLE = 2;
    
  /**
   * Configuration file has a parse error 
   */
  public static final int EXIT_CONFIG_FILE_PARSE_ERROR = 3;
  
  /**
   * Socket server failed to bind to port 
   */
  public static final int EXIT_SOCKET_BIND_FAILURE = 4;
  
  /**
   * Socket server configuration error
   */
  public static final int EXIT_SERVER_CONFIG_ERROR = 5;
  
  /**
   * This is not good.  If this appears, write a custom error handler and exit state for the cause
   */
  public static final int EXIT_GENERAL_FAILURE = 6;
  
  
  /**
   * Configuration filename to use
   * This is extracted from the jar and stored in the jar directory 
   */
  private static final String FILE_CONFIG = "config.json";
    
  /**
   * Log4j configuration file 
   * This is extracted from the jar and stored in the jar directory 
   */
  private static final String FILE_LOG4J = "log4j2.xml";
  
  /**
   * The license filename 
   * This is extracted from the jar and stored in the jar directory 
   */
  private static final String FILE_LICENSE = "LICENSE";
  
  /**
   * The log 
   */
  private static final Logger LOG = LogManager.getLogger( SimGateway.class );

  
  /**
   * Start Here
   * 
   * The main method is responsible for loading a configuration 
   * 
   * @param args the command line arguments
   */
  public static void main( String[] args )
  {
    //..Process the command line arguments into something useful 
    final CommandLineArguments arguments = CommandLineArguments.getInstance( args );
    
    //..Exits when help is printed 
    printHelpMessageIfRequested( arguments );
    
    //..Get the required config files or exit 
    //..The returned map is guaranteed to contain the passed file strings as keys 
    final Map<String,File> configFileMap = locateFiles( 
      arguments, 
      FILE_CONFIG, 
      FILE_LOG4J,
      FILE_LICENSE
    );
    
    //..Initialize the log system and say hello
    initLogs( arguments );
        
    //..This contains device ids from the configuration file 
    //..Each device matains a connection to the hardware, has an internal message queue and implements runnable.  
    //  run() processes the queue and writes to the device     
    //..The device factory MUST close all serial port connections when this shuts down.
    try ( final DeviceFactory deviceFactory = new DeviceFactory( CURRENT_SIM )) {
      
      final Config config = loadConfig( configFileMap, deviceFactory );
      
      //..Do stuff with the device factory resources 
      start( config, deviceFactory );
    } catch( Exception e ) {
      Logs.error( LOG, e, "General Failure" );
      Logs.debug( LOG, "If you are seeing this, you are a bad programmer.  You need to find",
        "the reason for this exception, fix it, write an error handler for it and create a specific exit state." );
      System.exit( EXIT_GENERAL_FAILURE );
    }
    
    System.exit( EXIT_SUCCESS );
  }
  
  
  /**
   * Loads and processes the config file 
   * @param configFileMap Files to read 
   * @param deviceFactory Device config is added to this  
   * @return 
   */
  private static Config loadConfig( final Map<String,File> configFileMap, final DeviceFactory deviceFactory )
  {
    //..Read the configuration file and load the device factory config 
    return readConfig( configFileMap.get( FILE_CONFIG ),
      //..The below functions are config handlers and are used to load pieces of the json config into vaarious things
      //..Loads devices from the config file into deviceIdList 
      json -> {
        try {
          deviceFactory.addDeviceList( json.getJsonArray( "devices" ));
        } catch( ClassCastException e ) {
          Logs.error( LOG, "config.json is missing devices array" );
        }
      }
    );        
  }
  
  
  private static StateChangeProcessor<IStateEvent> createStateChangeProcessor( 
    final LinkedBlockingQueue<IStateEvent> eventQueue,
    final DeviceFactory deviceFactory )
  {
    //..Reads the event queue and invokes state change handlers 
    return new StateChangeProcessor<>( eventQueue, 
      //..Each of the below handlers are called for each state change in the queue 
      event -> Logs.debug( LOG, "State change:", event.getControl(), "to", event.getValue(), "from", event.getOldValue()),
            
      //..Locate the device and hardware component we want to talk to and send it a message 
      event -> {
        //..Find the device 
        final IDevice device = deviceFactory.findDeviceBySoftwareAddress( event.getControl().getSoftwareAddress());
        if ( device == null )
          return;
        
        //..Find the control on the device 
        final IComponent component = device.getComponentBySoftwareAddress( event.getControl().getSoftwareAddress());
        if ( component == null )
          return;
        
        //..Converts object to byte array and writes the byte array to the hardware address on device 
        device.write( component.getHardwareAddress(), event.getValue().toString().getBytes());        
      }
    );    
  }
  
  
  /**
   * 
   * @param config
   * @param deviceFactory 
   */
  private static void start( final Config config, final DeviceFactory deviceFactory )
  {
    //..Contains state change events to be sent to hardware     
    final LinkedBlockingQueue<IStateEvent> eventQueue = new LinkedBlockingQueue<>();
    
    //..Some events require more specific event handler objects - like booleans.  we can add them here
    //..Anything not in the map will use GenericStateEvent
    final HashMap<Class, EventFactoryFunction<IStateEvent,Object>> factoryMap = new HashMap<>();
    factoryMap.put( Boolean.class, ( ISimControl control, Boolean value, Boolean oldValue ) -> new BooleanStateEvent( control, value, oldValue ));
    
    //..Registers state change events with the event queue 
    final EventStateManager stateManager = new EventStateManager( eventQueue, factoryMap );
      
    
    //..Listens for state changes in eventQueue and calls handlers that do things like write messages to devices 
    final StateChangeProcessor<IStateEvent> stateChangeProcessor = createStateChangeProcessor(
      eventQueue,
      deviceFactory
    );
    
    //..A  list of executors 
    final List<ExecutorService> executorList = new ArrayList<>();
    
    //..Poll for state changes (if not already doing so) 
    final ScheduledExecutorService stateChangeExecutor = Executors.newSingleThreadScheduledExecutor();
    stateChangeExecutor.scheduleAtFixedRate( stateChangeProcessor, 0, 100, TimeUnit.MILLISECONDS );
    executorList.add( stateChangeExecutor );
        
    //..Now create an executor for reach connected device    
    for ( final IDevice device : deviceFactory.getDeviceList())
    {
      final ScheduledExecutorService deviceWriteExecutor = Executors.newSingleThreadScheduledExecutor();
      deviceWriteExecutor.scheduleAtFixedRate( device, 0, 100, TimeUnit.MILLISECONDS );
      executorList.add( deviceWriteExecutor );
    }
    
    //..Create the socket server 
    final Server server = createServer( config, createCommands( stateManager, deviceFactory, executorList ));
    
    //..This is running on the main thread 
    try {
      server.run();
    } catch( IllegalArgumentException e ) {
      Logs.error( LOG, e, "Failed to bind to socket" );
      System.exit( EXIT_SOCKET_BIND_FAILURE );
    }    
  }
  
  
  /**
   * If the help text was requested on the command line, print it to StdOut and exit 
   * This method will exit with state EXIT_SUCCESS
   * 
   * @param arguments parsed command line arguments 
   */
  private static void printHelpMessageIfRequested( final CommandLineArguments arguments )
  {
    if ( !arguments.isHelp())
      return;
    
    arguments.usage();
    
    System.exit( EXIT_SUCCESS );
  }
  
  
  /**
   * Pass this a list of file constants to retrieve the requested file locations.
   * The locator will extract files from the jar when available and place them in the jar directory.
   * 
   * This method may exit with state EXIT_FILE_LOCATOR_FAIL 
   * 
   * @param arguments command line arguments
   * @param filesToLocate What we want to find (use FILE_ constants from Main)
   * @return
   * @throws FileNotFoundException
   * @throws IOException 
   */
  private static Map<String,File> locateFiles( CommandLineArguments arguments, final String ...filesToLocate ) 
  {
    final Map<String,File> out = new HashMap<>();
    final FileLocator locator = new FileLocator( arguments.isDev());
    
    for ( final String f : filesToLocate )
    {
      if ( f == null || f.isEmpty())
        continue;
      
      try {
        out.put(  f, locator.locateFile( f ));
      } catch( IOException e ) {
        System.err.println( "Failed to locate required configuration files.  " + e.getMessage());
        System.exit( EXIT_FILE_LOCATOR_FAIL );
      }        
    }
    
    return out;
  }
  
  
  /**
   * Initialize the main logging system 
   * Logs provides a generic interface for passing log information to a series of handlers 
   */
  private static void initLogs( final CommandLineArguments arguments )
  {
    //Logs.errorMessageHandler().add( t -> System.err.println( t ));
    Logs.info( LOG, "*** " + PROGRAM_NAME + "***" );
    Logs.printLoggerInfo( LOG );
    Logs.info( LOG, "Program Start" );
    if ( arguments.isDev())
      Logs.info( LOG, "Development mode enabled" );
  }  
  
  
  /**
   * Attempts to read the specified configuration file and maps the JSON content to a configuration object 
   * 
   * This method may exist with codes: 
   * EXIT_CONFIG_FILE_UNREADABLE 
   * EXIT_CONFIG_FILE_PARSE_ERROR
   * 
   * @param configFile File to read 
   * @return configuration object 
   */
  private static Config readConfig( final File configFile, final Consumer<JsonObject> ...handlerList )
  {
    Logs.debug( LOG, "Reading configuration file at", configFile.toString());
    
    try( final InputStream is = new FileInputStream( configFile )) {
      try ( final JsonReader reader = Json.createReader( is )) {
        
        final JsonObject configJson = reader.readObject();
        
        for ( final Consumer<JsonObject> handler : handlerList )
        {
          handler.accept( configJson );
        }
        
        return new Config( configJson );
      }
    } catch( IOException | IllegalStateException e ) {
      Logs.error( LOG, "The specified configuration file: " + configFile.toString() + " does not exist, is not readable or is a directory" );
      System.exit( EXIT_CONFIG_FILE_UNREADABLE );
    } catch( JsonParsingException e ) {
      Logs.error( LOG, e, "Failed to parse JSON in configuration file: " + configFile.toString());
      Logs.error( LOG, "Failed on line", e.getLocation().getLineNumber(), "and column", e.getLocation().getColumnNumber(), "with stream offset", e.getLocation().getStreamOffset());
      System.exit( EXIT_CONFIG_FILE_PARSE_ERROR );
    } catch( Exception e ) {
      Logs.error( LOG, e );
      System.exit( EXIT_CONFIG_FILE_PARSE_ERROR );
    }
    
    //..Should be unreachable 
    Logs.error( LOG, "The specified configuration file: " + configFile.toString() + " does not exist, is not readable or is a directory" );
    System.exit( EXIT_CONFIG_FILE_UNREADABLE );
    return null; 
  }
  
  
  /**
   * Creates the socket server 
   * @param config
   * @return 
   */
  private static Server createServer( Config config, final ICommandPool commands )
  {
    try {
      return new Server( Server.createConfig( 
        commands,
        ( socket, commandPool ) -> {
          return new Client( socket, commands, "" );
        }
      ).setPort( config.getServerPort()));
    } catch ( IOException | IllegalArgumentException e ) {
      Logs.error( LOG, e, "Server configuration error" );
      System.exit( EXIT_SERVER_CONFIG_ERROR );
    }
    
    //..Should be unreachable 
    return null;
  }
  
  
  
  private static ICommandPool createCommands( final IStateEventManager stateManager, final DeviceFactory deviceFactory,
    final List<ExecutorService> executorList )
  {
    return ( new CommandPool.Builder())
    .addCommand( new QuickCommand( "help", (input) -> { 
      return (new StringBuilder())
      .append( "\r\nAvailable Commands:\r\n" )
      .append( "          quit - Close the client\r\n" )
      .append( "     terminate - Shutdown the " + PROGRAM_NAME + " server\r\n" )
      .append( "   listDevices - List attached serial devices\r\n" )
      .append( " dcsEngineInfo - [value] Update DCS engine info state; comma-delimted list of 12 values  \r\n" )
      .append( "         write - [value] Write message to log \r\n" )
      .append( "      setState - [int address] [value]\r\n" )
      .toString();
    }))
    .addCommand( new QuickCommand( "helo", (input) -> { return SERVER_GREETING; } ))
    .addCommand( new QuickCommand( "quit", (input) -> { throw new QuitException(); } ))
    .addCommand( new QuickCommand( "terminate", (input) -> { 
      
      Logs.debug( LOG, "Stopping services..." );
      //..Shutdown executors 
      for ( final ExecutorService ex : executorList )
      {
        try {
          if ( !ex.awaitTermination( 5, TimeUnit.SECONDS ))
            ex.shutdownNow();
        } catch( InterruptedException e ) {
          ex.shutdownNow();
          Thread.currentThread().interrupt();
        }
      }
      
      Logs.debug( LOG, "Requesting shutdown" );
      //..Tell the server to shut down
      throw new ShutdownException(); 
    }))
    .addCommand( new QuickCommand( "write", (input) -> { Logs.info( LOG, input ); return input; }))
    .addCommand( new QuickCommand( "setState", (input) -> {
      final String[] parts = input.split( "=" );
      if ( parts.length != 2 )
        return "Expected format software address=value\r\n";
      
      final int address;
      try {
        address = Integer.parseInt( parts[0] );
        if ( address < 0 )
          throw new NumberFormatException();
      } catch( NumberFormatException e ) {
        return "Expected format software address=value - software address must be an unsigned integer\r\n";        
      }
      
      final IDevice device = deviceFactory.findDeviceBySoftwareAddress( address );
      if ( device == null )
        return "No device mapped to software address " + String.valueOf( address );
      
      device.write( address, parts[1].getBytes( StandardCharsets.US_ASCII ));
      
      return "ok";
    }))
    .addCommand( new ListDevicesCommand()) //..Lists connected com devices 
    .addCommand( new EngineInfoCommand( new EngineInfo( stateManager ))) //..engineInfo 
    .build();          
  }
}
