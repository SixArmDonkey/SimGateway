
package com.buffalokiwi.socketserver;

import java.util.Date;

/**
 * Keeps track of elapsed time
 * @author John Quinn
 */
public class Uptime implements IUptime
{
  protected class Calc {
    public String string = "";
    public long seconds = 0;
  }

  /**
   * Start time
   */
  protected final long start = System.currentTimeMillis();

  /**
   * Calendar date/time this started
   */
  protected final Date startDate = new Date();

  /**
   * Seconds
   */
  protected final static long[] seconds = new long[7];

  /**
   * Units
   */
  protected final static String[] units = new String[7];


  static {
    seconds[0] = 1;
    seconds[1] = 60;
    seconds[2] = 3600;
    seconds[3] = 86400;
    seconds[4] = 604800;
    seconds[5] = 2629743;
    seconds[6] = 31556926;

    units[0] = "second";
    units[1] = "minute";
    units[2] = "hour";
    units[3] = "day";
    units[4] = "week";
    units[5] = "month";
    units[6] = "year";
  }
  
  
  public Uptime()
  {
    
  }
  

  /**
   * Get the elapsed time as a string
   * @return elapsed time
   */
  @Override
  public String toString()
  {
    return getText();
  }
    
    
  /**
   * Retrieve the elapsed time as a string 
   * @return elapsed time
   */
  @Override
  public String getText()    
  {
    final StringBuilder out = new StringBuilder();

    long elapsed = getElapsedSeconds();

    while( elapsed > 0 )
    {
      final Calc c = calcTime( elapsed );
      if ( c.string.isEmpty())
        break;

      out.append( c.string );
      out.append( ' ' );
      elapsed = c.seconds;
    }

    return out.toString();
  }


  public String test( long elapsed )
  {
    final StringBuilder out = new StringBuilder();

    while( elapsed > 0 )
    {
      final Calc c = calcTime( elapsed );
      if ( c.string.isEmpty())
        break;

      out.append( c.string );
      out.append( ' ' );
      elapsed = c.seconds;
    }

    return out.toString();
  }


  /**
   * Retrieve the start date/time
   * @return start date
   */
  public Date getStartDate()
  {
    return startDate;
  }



  /**
   * Calculate the update as a string
   * @param n
   * @return
   */
  protected Calc calcTime( final long n )
  {
    final Calc out = new Calc();

    if ( n < 1 )
      return out;

    out.seconds = n;

    for ( int i = 0; i < seconds.length; i++ )
    {
      if ( i + 1 == seconds.length || out.seconds <= 0 )
        break;

      if ( out.seconds >= seconds[i] && out.seconds < seconds[i + 1] )
      {
        int diff = Math.round( out.seconds / seconds[i] );
        out.seconds -= ( diff * seconds[i] );
        if ( out.seconds < 1 )
          out.seconds = 0;

        final StringBuilder s = new StringBuilder();

        s.append( diff );
        s.append( ' ' );
        s.append( units[i] );
        if ( diff > 1 )
          s.append( 's' );

        out.string = s.toString();

        return out;
      }
    }

    out.string = String.valueOf( n ) + " seconds";
    return out;
  }

  
  /**
   * Retrieve the elapsed milliseconds
   * @return elapsed time
   */
  public long getElapsedMillis()
  {
    return System.currentTimeMillis() - start;
  }
  

  /**
   * Retrieve the elapsed seconds
   * @return elapsed time
   */
  public long getElapsedSeconds()
  {
    return ( System.currentTimeMillis() - start ) / 1000L;
  }
}
