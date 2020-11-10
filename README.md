# SimpleFly
Fly plugin originally created for the Spinalcraft Minecraft Server

## Dependencies
- Vault (For Permissions)


## Features
- Automatic daily fly time reset
- When not flying, time is paused
- Remaining fly time displayed on action bar
- Can preset how long a permission rank can fly in minutes

### Commands
```
/sfly : Enables or Disables flight
/aft <UserName> <time> : adds time in minutes to a player (can use negative numbers to subtract time)
/rft <hour> : sets the daily reset time for the plugin (between 1 and 24 are valid times for hour)
```

### Permissions
```
simplefly.fly : allows a player to use the fly command
simplefly.aft : allows a player to add time to another players timer
simplefly.rft : allows for a player to be able to set the reset time
simplefly.notify : allows players get a message when the fly time resets
```