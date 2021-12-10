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
Note: The frontend makes use of backend code and that is why you need to do it in that order

## How to run the application

After the instillation process you can do the following

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

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.