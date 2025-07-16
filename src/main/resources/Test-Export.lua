
package.path  = package.path..";"..lfs.currentdir().."/LuaSocket/?.lua"
package.cpath = package.cpath..";"..lfs.currentdir().."/LuaSocket/?.dll"

local log = nil
local version = nil
local socket = require( "socket" )
local Sock = socket.try( socket.connect( "localhost", 4201 ))
Sock:setoption( "tcp-nodelay", true )
Sock:setoption( "keepalive", true )

function WriteLog( msg )
  if ( log ) then 
    log:write( msg )
    log:write( "\n" )
  end 

  socket.try( Sock:send( "multitest\n" .. msg .. "\n.\n" ))
end

function LuaExportStart()
  version = LoGetVersionInfo();
  log = io.open( "C:/Users/Windows Sucks/Saved Games/DCS/Logs/lua.log", "w" )  
  WriteLog( "**** Start" )
  
  for k,v in pairs( _G ) do
    WriteLog( k )
  end

  WriteLog( "" )
  WriteLog( "\nDisplay Text" )

  for i = 1, 30, 1 do 
    WriteLog( i .. ": " .. list_indication( i ))
  end





  --Unknown 

  WriteLog( "\nFMData" )
  local fm = LoGetFMData()
  for k,v in pairs( fm ) do
    WriteLog( k )
  end 

  local slip = LoGetSlipBallPosition()
  if ( slip ~= nil ) then
    WriteLog( "\nSlip" )
    for k, v in pairs( slip ) do
      WriteLog( k )
    end 
  end 

  -- Maybe all that nav shit needs to be enabled 
  local nav = LoGetNavigationInfo()
  if nav ~= nil then
    WriteLog( "\nGetNavigationInfo" )
    for k,v in pairs( nav ) do
      WriteLog( k )
    end
  end 

  local tws = LoGetTWSInfo()
  if ( tws ~= nil ) then 
    WriteLog( "\nLoGetTWSInfo" )
    for k,v in pairs( tws ) do
      WriteLog( k ) 
    end
  end 


  WriteLog( "\n*******" )
  WriteLog( "Extracted Info\n" )
  
  
  local vertVelocity = LoGetVerticalVelocity()
  WriteLog( "Vertical Velocity: " .. vertVelocity )
  
  WriteLog( "\nCTRL PNL HSI" )
  local cp = LoGetControlPanel_HSI() 
  if ( cp ~= nil ) then
    for k,v in pairs( cp ) do
      WriteLog( k )  
    end 
  end

  WriteLog( "\nMCP State" )
  local mcp = LoGetMCPState()
  if ( mcp.RightTailPlaneFailure ) then
    WriteLog( "mcp.RightTailPlaneFailure: 1" )
  else
    WriteLog( "mcp.RightTailPlaneFailure: 0" )
  end 

  if ( mcp.EOSFailure ) then
    WriteLog( "mcp.EOSFailure: 1" )
  else
    WriteLog( "mcp.EOSFailure: 0" )
  end

  if ( mcp.HUDFailure ) then
    WriteLog( "mcp.HUDFailure: 1" )
  else
    WriteLog( "mcp.HUDFailure: 0" )
  end 
  
  if ( mcp.LeftEngineFailure ) then
    WriteLog( "mcp.LeftEngineFailure: 1" )
  else
    WriteLog( "mcp.LeftEngineFailure: 0" )
  end
  
  if ( mcp.RightEngineFailure ) then
    WriteLog( "mcp.RightEngineFailure: 1" )
  else
    WriteLog( "mcp.RightEngineFailure: 0" )
  end
  
  if ( mcp.MLWSFailure ) then
    WriteLog( "mcp.MLWSFailure: 1" )
  else
    WriteLog( "mcp.MLWSFailure: 0" )
  end
  
  if ( mcp.MFDFailure ) then
    WriteLog( "mcp.MFDFailure: 1" )
  else
    WriteLog( "mcp.MFDFailure: 0" )
  end
  
  if ( mcp.RadarFailure ) then
    WriteLog( "mcp.RadarFailure: 1" )
  else
    WriteLog( "mcp.RadarFailure: 0" )
  end
  
  if ( mcp.HelmetFailure ) then
    WriteLog( "mcp.HelmetFailure: 1" )
  else
    WriteLog( "mcp.HelmetFailure: 0" )
  end
  
  if ( mcp.RightAileronFailure ) then
    WriteLog( "mcp.RightAileronFailure: 1" )
  else
    WriteLog( "mcp.RightAileronFailure: 0" )
  end
  
  if ( mcp.HydraulicsFailure ) then
    WriteLog( "mcp.HydraulicsFailure: 1" )
  else
    WriteLog( "mcp.HydraulicsFailure: 0" )
  end
  
  if ( mcp.AutopilotFailure ) then
    WriteLog( "mcp.AutopilotFailure: 1" )
  else
    WriteLog( "mcp.AutopilotFailure: 0" )
  end
  
  if ( mcp.FuelTankDamage ) then
    WriteLog( "mcp.FuelTankDamage: 1" )
  else
    WriteLog( "mcp.FuelTankDamage: 0" )
  end
  
  if ( mcp.LeftAileronFailure ) then
    WriteLog( "mcp.LeftAileronFailure: 1" )
  else
    WriteLog( "mcp.LeftAileronFailure: 0" )
  end
  
  if ( mcp.CanopyOpen ) then
    WriteLog( "mcp.CanopyOpen: 1" )
  else
    WriteLog( "mcp.CanopyOpen: 0" )
  end
  
  if ( mcp.ECMFailure ) then
    WriteLog( "mcp.ECMFailure: 1" )
  else
    WriteLog( "mcp.ECMFailure: 0" )
  end
  
  if ( mcp.GearFailure ) then
    WriteLog( "mcp.GearFailure: 1" )
  else
    WriteLog( "mcp.GearFailure: 0" )
  end
  
  if ( mcp.RWSFailure ) then
    WriteLog( "mcp.RWSFailure: 1" )
  else
    WriteLog( "mcp.RWSFailure: 0" )
  end
  
  if ( mcp.ACSFailure ) then
    WriteLog( "mcp.ACSFailure: 1" )
  else
    WriteLog( "mcp.ACSFailure: 0" )
  end
  
  if ( mcp.LeftTailPlaneFailure ) then
    WriteLog( "mcp.LeftTailPlaneFailure: 1" )
  else
    WriteLog( "mcp.LeftTailPlaneFailure: 0" )
  end

  WriteLog( "Cockpit Params:" )
  WriteLog( list_cockpit_params())


  WriteLog( "\LoGetSelfData" )
  local selfData = LoGetSelfData()
  WriteLog( "selfData.Pitch: " .. selfData.Pitch )
  WriteLog( "selfData.Type.level1: " .. selfData.Type.level1 )
  WriteLog( "selfData.Type.level2: " .. selfData.Type.level2 )
  WriteLog( "selfData.Type.level3: " .. selfData.Type.level3 )
  WriteLog( "selfData.Type.level4: " .. selfData.Type.level4 )
  WriteLog( "selfData.Country: " .. selfData.Country )
  WriteLog( "selfData.Flags.Jamming: " .. tostring( selfData.Flags.Jamming ))
  WriteLog( "selfData.Flags.IRJamming: " .. tostring( selfData.Flags.IRJamming ))
  WriteLog( "selfData.Flags.Born: " .. tostring( selfData.Flags.Born ))
  WriteLog( "selfData.Flags.Static: " .. tostring( selfData.Flags.Static ))
  WriteLog( "selfData.Flags.Invisible: " .. tostring( selfData.Flags.Invisible ))
  WriteLog( "selfData.Flags.Human: " .. tostring( selfData.Flags.Human ))
  WriteLog( "selfData.Flags.AI_ON: " .. tostring( selfData.Flags.AI_ON ))
  WriteLog( "selfData.Flags.RadarActive: " .. tostring( selfData.Flags.RadarActive ))
  WriteLog( "selfData.GroupName: " .. selfData.GroupName )
 -- WriteLog( "selfData.PositionAsMatrix.x: " .. selfData.PositionAsMatrix.x )

  for k,v in pairs( selfData.PositionAsMatrix.x ) do
    WriteLog( "* " .. k )
  end 

  --WriteLog( "selfData.PositionAsMatrix.y: " .. selfData.PositionAsMatrix.y )
  --WriteLog( "selfData.PositionAsMatrix.z: " .. selfData.PositionAsMatrix.z )
  --WriteLog( "selfData.PositionAsMatrix.p: " .. selfData.PositionAsMatrix.p )
  WriteLog( "selfData.Coalition: " .. selfData.Coalition )
  WriteLog( "selfData.Heading: " .. selfData.Heading )
  WriteLog( "selfData.Name: " .. selfData.Name )
  WriteLog( "selfData.Position.x: " .. selfData.Position.x )
  WriteLog( "selfData.Position.y: " .. selfData.Position.y )
  WriteLog( "selfData.Position.z: " .. selfData.Position.z )
  WriteLog( "selfData.UnitName: " .. selfData.UnitName )
  WriteLog( "selfData.LatLongAlt.Lat: " .. selfData.LatLongAlt.Lat )
  WriteLog( "selfData.LatLongAlt.Long: " .. selfData.LatLongAlt.Long )
  WriteLog( "selfData.LatLongAlt.Alt: " .. selfData.LatLongAlt.Alt )
  WriteLog( "selfData.CoalitionID: " .. selfData.CoalitionID )
  WriteLog( "selfData.Bank: " .. selfData.Bank )
  WriteLog( "LoGetAngleOfSideSlip: " .. LoGetAngleOfSideSlip())
  WriteLog( "LoGetADIPitchBankYaw: " .. LoGetADIPitchBankYaw())


  local engineInfo = LoGetEngineInfo()
  WriteLog( "engineInfo.fuel_external: " .. engineInfo.fuel_external )
  WriteLog( "engineInfo.fuel_internal: " .. engineInfo.fuel_internal )
  WriteLog( "engineInfo.Temperature.Left: " .. engineInfo.Temperature.left )
  WriteLog( "engineInfo.Temperature.Right: " .. engineInfo.Temperature.right )
  WriteLog( "engineInfo.RPM.Left: " .. engineInfo.RPM.left )
  WriteLog( "engineInfo.RPM.Right: " .. engineInfo.RPM.right )
  WriteLog( "engineInfo.FuelConsumption.Left: " .. engineInfo.FuelConsumption.left )
  WriteLog( "engineInfo.FuelConsumption.Right: " .. engineInfo.FuelConsumption.right )
  WriteLog( "engineInfo.EngineStart.Left: " .. engineInfo.EngineStart.left )
  WriteLog( "engineInfo.EngineStart.Right: " .. engineInfo.EngineStart.right )
  WriteLog( "engineInfo.HydraulicPressure.Left: " .. engineInfo.HydraulicPressure.left )
  WriteLog( "engineInfo.HydraulicPressure.Right: " .. engineInfo.HydraulicPressure.right )
    
  WriteLog( "\nMech Info" )
  local mechInfo = LoGetMechInfo()  
  WriteLog( "mechInfo.speedbrakes.status: " .. mechInfo.speedbrakes.status )
  WriteLog( "mechInfo.speedbrakes.value: " .. mechInfo.speedbrakes.value )
  WriteLog( "mechInfo.parachute.status: " .. mechInfo.parachute.status )
  WriteLog( "mechInfo.parachute.value: " .. mechInfo.parachute.value )
  WriteLog( "mechInfo.canopy.status: " .. mechInfo.canopy.status )
  WriteLog( "mechInfo.canopy.value: " .. mechInfo.canopy.value )
  WriteLog( "mechInfo.wheelbrakes.status: " .. mechInfo.wheelbrakes.status )
  WriteLog( "mechInfo.wheelbrakes.value: " .. mechInfo.wheelbrakes.value )
  WriteLog( "mechInfo.gear.status " .. mechInfo.gear.status )
  WriteLog( "mechInfo.gear.value: " .. mechInfo.gear.value )
  WriteLog( "mechInfo.gear.nose.rod: " .. mechInfo.gear.nose.rod )
  WriteLog( "mechInfo.gear.main.left.rod: " .. mechInfo.gear.main.left.rod )
  WriteLog( "mechInfo.gear.main.right.rod: " .. mechInfo.gear.main.right.rod )
  WriteLog( "mechInfo.controlsurfaces.eleron.left: " .. mechInfo.controlsurfaces.eleron.left )
  WriteLog( "mechInfo.controlsurfaces.eleron.right: " .. mechInfo.controlsurfaces.eleron.right )
  WriteLog( "mechInfo.controlsurfaces.elevator.left: " .. mechInfo.controlsurfaces.elevator.left )
  WriteLog( "mechInfo.controlsurfaces.elevator.right: " .. mechInfo.controlsurfaces.elevator.right )
  WriteLog( "mechInfo.controlsurfaces.rudder.left: " .. mechInfo.controlsurfaces.rudder.left )
  WriteLog( "mechInfo.controlsurfaces.rudder.right: " .. mechInfo.controlsurfaces.rudder.right )
  WriteLog( "mechInfo.refuelingboom.status: " .. mechInfo.refuelingboom.status )
  WriteLog( "mechInfo.refuelingboom.value: " .. mechInfo.refuelingboom.value )
  WriteLog( "mechInfo.flaps.status: " .. mechInfo.flaps.status )
  WriteLog( "mechInfo.flaps.value: " .. mechInfo.flaps.value )  

  local coords = LoGeoCoordinatesToLoCoordinates()
  WriteLog( "\nGeoCoordinatesToLoCoordinates" )
  WriteLog( "coords.x: " .. coords.x )
  WriteLog( "coords.y: " .. coords.y )
  WriteLog( "coords.z: " .. coords.z )

  WriteLog( "\nRadar Altimiter: " .. LoGetRadarAltimeter() )

  WriteLog( "\nGetClickableElements" )
  
  local ce = GetClickableElements()
  
  -- There are a lot of controls
  -- We can iterate over PTN_[N] (0..2xx) and test exists
  -- If so, grab the label from hint and map that to some standard in the host program 
  
  for k,v in pairs( ce ) do 
    WriteLog( k .. " (" .. ce[k].device .. ") = " .. ce[k].hint )
  end 
  
  --WriteLog( ce.PTN_199.stop_action )
  --WriteLog( ce.PTN_199.action )
  --WriteLog( ce.PTN_199.arg_value )
  --WriteLog( ce.PTN_199.device )
  --WriteLog( ce.PTN_199.class )
  --WriteLog( ce.PTN_199.arg ) 
  --WriteLog( ce.PTN_199.hint )
  
  

end

local ct = 0

function LuaExportBeforeNextFrame()
  WriteLog(  math.fmod( ct, 5 ) )
  ct = ct + 1
  if math.fmod( ct, 5 ) == 0 then
    for i = 1, 30, 1 do 
      WriteLog( i .. ": " .. list_indication( i ))
    end 
  end 
end


function LuaEExportAfterNextFrame()

end

function LuaExportStop()
  WriteLog( "**** Done" )  

  if ( log ) then
    log:close()
    log = nul
  end 

  socket.try( Sock:send( "quit" ))
  Sock:close()
end

function LuaExportAcvitityNextEvent( t )


  return t + 30 
end

