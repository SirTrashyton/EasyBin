# EasyBin
A simple Java program to send Files from desktop-to-desktop.

# Purpose
Desktop to desktop file sharing without email, instead with a simple 4 digit PIN.

# How do I use it?
You must run the EasyBin server in order for clients to connect.  
I would include a JAR file, however I do not have the means to host a server.   
This works through TCP sockets, so local or portforward'd adresses will work.
The IP for the EasyBin server is located in ``"/mich/Pref" as "SERVER_IP".``  
Upon client connection a 4 digit PIN is assigned. This is your tag/username/identifier/PIN.  
To send a file to a user, (1) enter their PIN. (2) select file. (3) click 'send'.  
You may copy this project into your IDE and:  
  run ``"/server/BinServer.java"``for the server . 
  run ``"/client/LaunchScreen.java"`` for the clients.. 

# What have I learned
Short term project planning and execution.  
Basic networking by sending Java objects over the net .   
File reading and writing .   
Multi-threading techniques .   
JavaFX use for cross-platform desktop applications .  

# Libraries Used
KryoNet .  
JavaFX . 

# Additional Info
I made this program with the goal of the simpliest, no extra thrills file-sharing program. 
Maximum file sizes can be changed as the startup parameters.
If you are interested in an expanded version of this application, message me.

# Screenshots
![alt text](https://i.gyazo.com/8dfc8f1e316774d02c86cf3fc202baa6.jpg)
![alt text](https://i.gyazo.com/e0c5ace6a905c0e7c6716021aaee5286.jpg)
![alt text](https://i.gyazo.com/7f6f9859919d827e11eed4f6e94f69c7.jpg)

