
package com.buffalokiwi.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;



/**
 * Extracts files from the jar 
 */
public class JarExtractor 
{
  /**
   * Extract some file from the jar file if it does not yet exist.
   * @param filename The filename to attempt to extract 
   * @return String filename
   * @throws IOException
   * @throws FileNotFoundException if the file is not found in the jar 
   */
  public static File extractConfig( final String filename ) throws IOException,
    FileNotFoundException
  {
    //..Get a new config file 
    final File cfg = getNextUnusedConfigFile( filename );
    
    //..Find the file in the jar 
    try ( final InputStream is = ConfigLocator.class.getResourceAsStream( '/' + filename )) 
    {
      if ( is != null )
      {
        //..Make directories if necessary 
        cfg.getParentFile().mkdirs();
        
        //..Create the new file 
        cfg.createNewFile();
        
        //..Get the config file content from the jar 
        //  and write it to the new file
        try ( final FileOutputStream f = new FileOutputStream( cfg )) {
          int read;
          byte[] bytes = new byte[1024];
          while (( read = is.read( bytes )) != -1 )
          {
            f.write( bytes, 0, read );
          }
        }
      }
      else
      {
        //..No stuffs in the jar... awww shucks.
        throw new FileNotFoundException( 
          "Failed to locate packed resources in jar (" + cfg + ")" );
      }
    }
    
    return cfg;
  }
  
  
  
  
  /**
   * Retrieve the local filename to use for the config file 
   * @return Filename
   */
  private static File getLocalFilename( final String filename )
  {
    return new File( Paths.get(".").toAbsolutePath().normalize().toString() 
      + File.separator + filename );
  }
  
  
  /**
   * Retrieve the next unused config filename for extraction 
   * @return a filename that does not exist
   */
  private static File getNextUnusedConfigFile( final String filename )
  {
    File cfg = getLocalFilename( filename );
    
    int index = 0;
    while ( cfg.exists())
    {
      cfg = new File( cfg.toString() + "." + String.valueOf( index++ ));
    }

    return cfg;    
  }
}
