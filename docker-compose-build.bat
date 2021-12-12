cd backend
call "build docker.bat"
cd..
cd frontend
call "build docker.bat"
cd..
call docker-compose up -d
pause