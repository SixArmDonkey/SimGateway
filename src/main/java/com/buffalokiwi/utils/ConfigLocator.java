/**
 * This file is part of the buffalokiwi utils package, and is subject to the 
 * terms and conditions defined in file 'LICENSE.txt', which is part 
 * of this source code package.
 *
 * Copyright (c) 2017 John Quinn <johnquinn3@gmail.com>
 */


package com.buffalokiwi.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Attempts to locate a file somewhere on the filesystem.
 * If not found, the file will be extracted from the jar (if exists).
 * 
 * @author John Quinn
 */
public class ConfigLocator 
{
  /**
   * The configuration filename used 
   */
  final String filename;
  
  /**
   * If this is in src/main/resources
   */
  private boolean isDevConfig = false;

  
  /**
   * Return the crc32 of a string
   * @param str String 
   * @return hash
   */
  public static long crc32( final String str )
  {
    final Checksum sum = new CRC32();
    sum.update( str.getBytes(), 0, str.length());
    return sum.getValue();
  }

  
  /**
   * @param filename  Filename to locate
   */
  public ConfigLocator( final String filename )
  {
    this( filename, false );
  }

      
  /**
   * @param filename Base name to use 
   * @param isDev If this is a development runtime 
   */
  public ConfigLocator( final String filename, boolean isDev )
  {
    if ( filename.trim().isEmpty())
      throw new IllegalArgumentException( "filename cannot be empty" );
    
    this.filename = filename;
    this.isDevConfig = isDev;
  }
  
  
  /**
   * Retrieve the correct configuration file to use 
   * @param extract Toggle extracting the file from the jar if it is not found 
   * @return Configuration file
   * @throws FileNotFoundException 
   * @throws IOException for errors when extracting a config from the jar
   */
  public File getFile( final boolean extract ) throws FileNotFoundException,
    IOException
  {
    try {
      return findConfigFile();
    } catch( FileNotFoundException e ) {
      if ( !extract )
        throw e;
      
      return JarExtractor.extractConfig( filename );
    }
  }

  /**
   * Attempt to locate the developer configuration filename 
   * @return 
   */
  private File getDevFilename()
  {
    return new File( Paths.get(".").toAbsolutePath().normalize().toString()
      + File.separator + "src" 
      + File.separator + "main" 
      + File.separator + "resources" 
      + File.separator + filename );
  }
  
  
  /**
   * Retrieve the /etc pathname for the config file
   * @return Filename 
   */
  private File getEtcFilename()
  {
    return new File( File.separator + "etc" + File.separator + filename );
  }
  
  
  /**
   * Retrieve the local filename to use for the config file 
   * @return Filename
   */
  private File getLocalFilename()
  {
    return new File( Paths.get(".").toAbsolutePath().normalize().toString() 
      + File.separator + filename );
  }
  
  
  /**
   * Retrieve the config file being used
   * @return
   * @throws FileNotFoundException
   */
  private File findConfigFile() throws FileNotFoundException
  {
    //..Get a list of potential locations 
    final File[] files;
    
    //..This is stupid
    if ( isDevConfig )
    {
      files = new File[1];
      files[0] = getDevFilename();
    }
    else
    {
      files = new File[2];
      files[0] = getEtcFilename();
      files[1] = getLocalFilename();
    }
    
    //..Check the list of files 
    for ( final File f : files )
    {
      if ( f.exists())
      {
        return f;
      }
    }

    //..Nothin.
    throw new FileNotFoundException( "Config file not found" );
  }
  
  
  /**
   * If the config is a dev config 
   * @return is dev 
   */
  public boolean isDevConfig()
  {
    return isDevConfig;
  }
}
