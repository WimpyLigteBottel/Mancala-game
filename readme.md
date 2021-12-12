# Mancala-game

The mancala games are a family of two-player turn-based strategy board games played with small stones, beans, or seeds and rows of holes or pits in the earth, a board or other playing surface. The objective is usually to capture all or some set of the opponent's pieces.

Versions of the game date back to the 7th century and evidence suggests the game existed in Ancient Egypt. It is among the oldest known games to still be widely played today.

## What is this project about

I made a very simple spring-boot Mancala game which you can access via browser to play. This was
a tech assessment that I had to do.

## Installation

You will need to have the following on your system installed.

- Java 17 (Open JDK)
- Maven 3

Step 1: mvn clean install the backend first
```bash
mvn clean install 
```

Step 2: mvn clean install the front-end second
```bash
mvn clean install 
```

(Lazy mode step)
```
If you are on windows you should be able to just run
the "docker-compose-build.bat" and it will build
and run the docker images.

NOTE!!!!:
YOU WILL NEED TO UPDATE THE application.properties in the frontend project to have correct backend ip address...

By making it your own ip address it should work otherwise you will need to configure it correctly.

```

## How to run the application

After the installation process you can do the following

Windows

'run mancala.bat'
```bat
echo off
cd .\backend\target
start java -jar mancala-backend-1.0-SNAPSHOT.jar
cd ..
cd ..
cd .\frontend\target
start java -jar mancala-frontend-1.0-SNAPSHOT.jar
```

Docker:

Note: you will need to update the frontend application.properties as follows

File: frontend/src/main/resources/application.properties
```properties
backend.base.url=http://<insert your ip address>:8080/v1

Example:
backend.base.url=http://192.168.0.111:8080/v1
```
Reason for this is since you are starting up in separate docker containers it won't know how to talk to each other
unless you setup the network correctly so that it can talk to each other


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.