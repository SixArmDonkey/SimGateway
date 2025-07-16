/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the 
 * terms and conditions defined in file 'LICENSE', which is part 
 * of this source code package.
 */
package com.buffalokiwi.simgateway.config;

import com.buffalokiwi.utils.JarExtractor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;


/**
 * This will locate a file to use, and will choose the first file found in the following series:
 * 
 * 1) Checks jar directory
 * 2) Extracts the file from the jar into the jar directory and uses that
 * 
 * @author John Quinn
 */
public class FileLocator
{
  /**
   * If dev mode is enabled
   */
  private final boolean isDev;
  
  
  /**
   * @param isDev If dev mode is enabled 
   */
  public FileLocator( final boolean isDev )
  {
    this.isDev = isDev;
  }
  
  
  /**
   * Locate a file
   * @param filename the thing 
   * @return
   * @throws FileNotFoundException
   * @throws IOException 
   */
  public File locateFile( final String filename ) throws FileNotFoundException, IOException
  {
    try {
      return findFile( filename );
    } catch( FileNotFoundException e ) {
      //..Do nothing      
    }
    
    return JarExtractor.extractConfig( filename );    
  }
  

  /**
   * Retrieve the config file being used
   * @return
   * @throws FileNotFoundException
   */
  private File findFile( final String filename ) throws FileNotFoundException
  {
    //..Get a list of potential locations 
    final File[] files;
    
    //..This is stupid
    if ( isDev )
    {
      files = new File[1];
      files[0] = getDevFilename( filename );
    }
    else
    {
      files = new File[1];
      files[0] = getFilename( filename );
    }
    
    //..Check the list of files 
    for ( final File f : files )
    {
      if ( f.exists() && f.isFile() && f.canRead())
      {
        return f;
      }
    }

    //..Nothin.
    throw new FileNotFoundException( "Config file not found, is not a file, or is not readable" );
  }  
  
  
  /**
   * Retrieve the local filename to use
   * @param filename 
   * @return Filename
   */
  private File getFilename( final String filename )
  {
    return Paths.get(".").toAbsolutePath().normalize().resolve( filename ).toFile();      
  }  
  
  
  /**
   * Attempt to locate the developer configuration filename 
   * @param filename 
   * @return 
   */
  private File getDevFilename( final String filename )
  {
    return new File( Paths.get(".").toAbsolutePath().normalize().toString()
      + File.separator + "src" 
      + File.separator + "main" 
      + File.separator + "resources" 
      + File.separator + filename );
  }  
}
