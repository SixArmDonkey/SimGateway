/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;



import com.buffalokiwi.utils.BuildableObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Contains various lists of commands sorted by some group.
 * This can be used to separate commands by permission level if desired.
 * 
 * Groups can be cumulative where each higher group id contains the commands for each 
 * lower id.
 * 
 * ie 
 * 2 has commands plus commands for groups 1 and 0.
 * 
 * 
 * @param <R> Subclass Type
 * @param <B> Inner builder subclass type 
 * 
 * @author John Quinn
 */
public class CommandPool<R extends CommandPool, B extends CommandPool.Builder> extends BuildableObject<R,B> implements ICommandPool 
{
  
  /**
   * Log instance 
   */
  private final static Log LOG = LogFactory.getLog( CommandPool.class );

  
  
  

  //////////////////////////////////////////////////////////////////////////////
  // BEGIN BUILDER                                                            //
  //////////////////////////////////////////////////////////////////////////////
  

  /**
   * Builder 
   * @param <T> Subclass Type 
   * @param <R> Outer class Type 
   */
  public static class Builder<T extends Builder, R extends CommandPool> extends BuildableObject.Builder<T,R> 
  {
    private final Map<Integer,Map<String,ICommand>> commandGroups = new HashMap<>();
    private boolean cumulative = false;
    
    
    public T setCumulative( final boolean on )
    {
      cumulative = on;
      return getReference();
    }
    
    
    public T addGroup( final int id )
    {
      if ( !commandGroups.containsKey( id ))
        commandGroups.put( id, new HashMap<>());
      
      return getReference();
    }
    
    
    public T addCommands( final Map<Integer,Map<String,ICommand>> commands )
    {
      if ( commands == null )
        throw new IllegalArgumentException( "commands must not be null" );
      
      for ( final Map.Entry<Integer,Map<String,ICommand>> entry : commands.entrySet())
      {
        addCommands( entry.getKey(), new ArrayList<>( entry.getValue().values()));
      }
      
      return getReference();
    }
    
    
    public T addCommands( final int groupId, final List<ICommand> commands )
    {
      if ( commands == null )
        throw new IllegalArgumentException( "commands must not be null" );
      
      addGroup( groupId );
      final Map<String,ICommand> cmds = commandGroups.get( groupId );
      
      for ( final ICommand command : commands )
      {
        if ( command == null )
          throw new IllegalArgumentException( "Command entries must not be null" );
        
        cmds.put( command.getCommand(), command );
      }
      
      return getReference();
    }
    
    
    public T addCommand( final ICommand command )
    {
      return addCommand( DEFAULT_GROUP_ID, command );
    }
    
    
    public T addCommand( final int groupId, final ICommand command )
    {
      addGroup( groupId );
      
      final Map<String,ICommand> cmds = commandGroups.get( groupId );
      
      if ( command == null )
        throw new IllegalArgumentException( "Command entries must not be null" );

      cmds.put( command.getCommand(), command );
      
      return getReference();
    }
    
    
    
    /**
     * Build the object 
     * @return Built object 
     */
    @Override
    public R build() 
    {
      return (R)(new CommandPool( Builder.class, this ));
    }    
  }
  

  //////////////////////////////////////////////////////////////////////////////
  // END BUILDER                                                              //
  //////////////////////////////////////////////////////////////////////////////


  //////////////////////////////////////////////////////////////////////////////
  // CommandPool Properties                                                         
  //////////////////////////////////////////////////////////////////////////////
  
  private final Map<Integer,Map<String,ICommand>> commandGroups;
  
  private final List<Integer> groupIds;
  private final boolean cumulative;
  private final Map<Integer,Set<Integer>> cGroups;

  /**
   * Constructor.
   * Creates an immutable object instance based on the builder properties.
   * @param builderClass Builder class type
   * @param b Builder instance 
   */
  protected CommandPool( final Class<? extends CommandPool.Builder> builderClass, final Builder b )
  {
    super( builderClass, b );
    
    //..Builder groups
    final Map<Integer,Map<String,ICommand>> cmdGrp = b.commandGroups;    
    
    cumulative = b.cumulative;
    
    if ( cumulative )
    {
      //..temp map 
      final HashMap<Integer,Set<Integer>> temp = new HashMap<>();
      
      //..For each group 
      for ( final Integer id : cmdGrp.keySet())
      {
        //..A set of ids that have a value less than the current group id 
        final Set<Integer> ids = new HashSet<>();
        
        //..Find group id's that are less than the current group id and add them to the set 
        for ( final Integer id1 : cmdGrp.keySet())
        {
          if ( id1 < id )
            ids.add( id1 );
        }
        
        //..Plop the new ids in the set 
        temp.put( id, Collections.unmodifiableSet( ids ));
      }
      
      cGroups = Collections.unmodifiableMap( temp );
    }
    else
    {
      cGroups = Collections.unmodifiableMap( new HashMap<>());
    }    
    
    
    
    //..Final group map 
    final Map<Integer,Map<String,ICommand>> out = new HashMap<>();
        
    //..Loop the commands 
    for ( final Map.Entry<Integer,Map<String,ICommand>> entry : cmdGrp.entrySet())
    {
      //..Get the group id 
      final int groupId = entry.getKey();
      
      //..The list of commands for the group 
      final Map<String,ICommand> cmds = entry.getValue();
      
      //..If this is cumulative, add the other group id's commands to the current list 
      if ( cGroups.containsKey( groupId ))
      {
        for ( final Integer gid : cGroups.keySet())
        {
          if ( cmdGrp.containsKey( gid ))
            cmds.putAll( cmdGrp.get( gid ));
        }
      }
      
      out.put( groupId, Collections.unmodifiableMap( cmds ));
    }
    
    commandGroups = Collections.unmodifiableMap( out );
    groupIds = Collections.unmodifiableList( new ArrayList<>( commandGroups.keySet()));
  }
  

  
  //////////////////////////////////////////////////////////////////////////////
  // CommandPool Methods                                                            
  //////////////////////////////////////////////////////////////////////////////  
  
  
  
  @Override
  public boolean isCumulative()
  {
    return cumulative;
  }
  
  
  @Override
  public List<Integer> getGroupIds()
  {
    return groupIds;
  }
  
  
  /**
   * Retrieve a list of commands for some group
   * @param groupId Group id 
   * @return 
   * @thows IllegalArgumentException if groupId does not exist 
   */
  @Override
  public Map<String,ICommand> getCommands( final int groupId ) throws IllegalArgumentException
  {
    if ( !commandGroups.containsKey( groupId ))
      throw new IllegalArgumentException( String.format( "%d is an invalid group id", groupId ));
    
    return commandGroups.get( groupId );
  }
  
  
  /**
   * Convert the immutable instance into a mutable builder instance.
   * @return Builder
   */
  @Override
  public B toBuilder() 
  {
    return (B)super.toBuilder()
      .setCumulative( cumulative )
      .addCommands( commandGroups );      
  }    
}
