# Baby Lasagna User Manual
### Authors/Developers: Michael Ballard, Brian Leitch, Evan Osterberg, Maddison Winters

![Image of a Childlike Lasagna with Legs, Arms, and Eyes](./assets/baby_lasagna.png)


## What is Baby Lasagna?
Baby Lasagna is a level-based puzzle platformer.  
The goal is to make your way to an exit door without falling apart.  
Taking damage will cause Baby Lasagna to lose some of their lasagna layers.  
Baby Lasagna dies if too many layers are lost. 

## Install
### Windows:
Go to the releases page, download the ".exe" file for the latest release. Run the executable, and follow the installation steps. Run the game by clicking the desktop shortcut or searching in the start menu.  
Note: Depending on where the game was installed, you made need to run the game as administrator by right clicking on the game then clicking "Run as Administrator".  

### Linux:
Go to the releases page, download the ".exe" file for the latest release. On some systems (including Ubuntu), you can double-click on this file to begin installing. Otherwise, on any system with dpkg installed, open a terminal and run `sudo dpkg -i baby-lasagna_[VERSION]_amd64.deb`.  


## Main Menu
Click the X button to exit the game  
Click one of the numbered buttons to play the corresponding level  
A green button means the corresponding level has been completed  
A lock symbol means the level is not yet unlocked
A (...) symbol means that there is no level file corresponding to that level number (feel free to create your own with Tiled)  


## Pause Menu
Click "Resume" to resume the level  
Click "Restart" to restart the level  
Click "Exit" to exit the level and return to the main menu  


## Controls
Left: A or Left Arrow  
Right: D or Right Arrow  
Jump: Space, W, or Up Arrow  
Fast-Fall: S or Down Arrow  
Pause Menu: Escape  
Use Ability: Q  


## Win/Lose
To win, find and touch a black and gold door  
Making contact with pasta-spikes will cause you to lose one layer every 0.8s  
Upon losing all layers, you immediate die and therefore lose  


## Abilities
Using an ability consumes the top layer of Baby Lasagna  
If the top layer is Pasta, you place a layer of pasta in front of you which you can then bounce off of.  
If the top layer is Cheese, you sling a glob of cheese that will "splat" onto a wall. You can stick onto and jump off of this "splat."  
If the top layer is Meat, you place a chunk of meat in front of you. This chunk will remain their permanently and can be stood on.  
If the top layer is Pepper, you throw a pepper slice/wheel that rolls until it hits a wall, at which point it explodes. Cracked tiles are able to be destroyed by these explosions.  
If the top layer is anything else, no ability is used, and the top layer is simply discarded.  
