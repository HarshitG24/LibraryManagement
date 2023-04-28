# Final Project - Distributed Library Management System (DLMS)
The Distributed Library Management System (DLMS) is an innovative and comprehensive solution designed to streamline the process of loaning and returning books in a distributed environment.

##Team Members:
1. Nishtha Goswami - goswami.ni@northeastern.edu
2. Harshit Gajjar - gajjar.h@northeastern.edu
3. Mihir Mesia - mesia.m@northeastern.edu

## Implementation details
1. This project contains both server and client code. The client code can be found in <b>client</b> folder. And the server code can be found in <b>src</b>.
2. In the resources folder, the server jar is available that you can run. The application
   jars is called <b>distributed-systems-0.0.1-SNAPSHOT.jar</b>.

# How to Run

1. Server:
If you simply want to run the jar file:
<pre>java -jar -Dserver.port={port} resources/distributed-systems-0.0.1-SNAPSHOT.jar</pre>

Otherwise, to build the Springboot project and run it:
<pre>
mvn clean install
mvn clean compile
mvn package
</pre>

Then run multiple servers in different terminals using the below command:
<pre>java -jar -Dserver.port={port} target/distributed-systems-0.0.1-SNAPSHOT.jar</pre>

2. Client: Our Client is a React application so you will need to run the below commands in order to build it.
<pre>
cd client
npm i
npm run build
</pre>

Then run multiple clients using below command:
<pre>REACT_APP_SERVER_PORT={backendPort} npm start</pre>

## Example: How to Use the Program

Consider the following example:

Server: <pre>java -jar -Dserver.port=8081 target/distributed-systems-0.0.1-SNAPSHOT.jar</pre>

Client: <pre>REACT_APP_SERVER_PORT=8081 npm start</pre>

1. <b>port</b> is the port to connect to the remote server.
2. <b>backendPort</b> is the server port number you want to connect your client with.

