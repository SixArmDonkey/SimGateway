/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the 
 * terms and conditions defined in file 'LICENSE', which is part 
 * of this source code package.
 */
package com.buffalokiwi.simgateway.config;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;


/**
 * A list of available command line arguments 
 * 
 * @author John Quinn
 */
public class CommandLineArguments 
{
  /**
   * Help command
   */
  public static final String CMD_HELP = "help";
  
  /**
   * Dev mode command
   */
  public static final String CMD_DEV = "dev";
  
  /**
   * Specify config file command 
   */
  public static final String CMD_CONFIG = "config";
  
  /**
   * Extract file command
   */
  public static final String CMD_EXTRACT = "file";
  
  
  /**
   * Displays a help message listing command line options and usages 
   */
  @Parameter( names = "--" + CMD_HELP, help = true )
  private boolean help = false;
  
  /**
   * When enabled, the program will load the development/default set of configuration files located 
   * in the source code directory src/main/resources, which will be bundled in the built jar file
   */
  @Parameter( names = "--" + CMD_DEV, description = "Load development configuration from src/main/resources" )
  private boolean isDev = false;
  
  /**
   * Override the default config file with a custom config file 
   */
  @Parameter( names = "--" + CMD_CONFIG, description = "The configuration file to use", converter = FileArgumentConverter.class )
  private File configFile = null;
  
  /**
   * Certain files are located in the src/main/resources package, and may need to be extracted the first time 
   * the program is run, or if someone decides to delete a required file.
   * This will locate and extract the specified file and place it next to the jar file 
   */
  @Parameter( names = "--" + CMD_EXTRACT, description = "File to process", converter = FileArgumentConverter.class )
  private File extractFile = null;  
  
  /**
   * The JCommander instance used to build this 
   */
  private JCommander jc;
  
  
  /**
   * Retrieve the command line arguments instance 
   * @param args command line arguments from main() 
   * @return 
   */
  public static CommandLineArguments getInstance( String args[] )
  {
    final CommandLineArguments arguments = new CommandLineArguments();
    final JCommander jc = JCommander.newBuilder().addObject( arguments ).build();
    jc.parse( args );
    
    if ( arguments.isHelp())
      arguments.jc = jc;
    
    return arguments;
  }
  
  
  /**
   * No new instance access
   */
  protected CommandLineArguments()
  {
    
  }
  
  
  /**
   * If the help command is enabled 
   * @return 
   */
  public boolean isHelp()
  {
    return help;
  }
  
  
  /**
   * If the dev command is enabled 
   * @return 
   */
  public boolean isDev()
  {
    return isDev;
  }
  
  
  /**
   * Test and retrieve the specified configuration file or the program default, which should be located next to the
   * jar directory 
   * @return
   * @throws FileNotFoundException
   * @throws IOException 
   */
  public File getConfigFile() throws FileNotFoundException, IOException
  {
    if ( configFile == null )
    {
      try {
        final File jarFile = new File( getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        final File resolvedFile = jarFile.getParentFile().toPath().resolve( "config.json" ).toFile();
        
        checkFileExistsOrThrowException( resolvedFile, "configuration" );
      } catch( URISyntaxException e ) {
        throw new FileNotFoundException( "The configuration file name was not specified, "
          + "and config.json was not found in the JAR directory" );
      }
    }
    
    checkFileExistsOrThrowException( configFile, "configuration" );    
    return configFile;
  }
  
  
  /**
   * Returns a file name to extract from src/main/resources or null if nothing was specified 
   * @return 
   */
  public File getFileToExtract()
  {
    return extractFile;
  }
  
  
  /**
   * Print usage to stdout
   */
  public void usage()
  {
    jc.usage();
  }
  
  
  /**
   * Given a file, test that it exists and is readable.  Otherwise throw an exception
   * @param file File to test
   * @param type Type of file - this is a string used in the exception message and should be something like "configuration"
   * @throws IOException 
   */
  private void checkFileExistsOrThrowException( final File file, final String type ) throws IOException
  {
    if ( !file.exists() || !file.canRead())
      throw new IOException( "The specified " + type + " file " + file.toString() + " does not exist or is not readable" );
  }
}
