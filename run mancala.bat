echo off

cd .\backend\target

start java -jar mancala-backend-1.0-SNAPSHOT.jar

cd ..
cd ..

cd .\frontend\target

start java -jar mancala-frontend-1.0-SNAPSHOT.jar