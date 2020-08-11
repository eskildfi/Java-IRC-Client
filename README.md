# IRC client written in Java

Usage:
```
javac IRC.java
java IRC <server> <port> <nick> <username> <real name>
```

Use full commands to message server.

* Join channel:

   `JOIN <channel>`
   
* Message channel:

   `PRIVMSG <channel> :<message>`
     
* Message user:

   `PRIVMSG <nick> :<message>`  

Does not support SSL.