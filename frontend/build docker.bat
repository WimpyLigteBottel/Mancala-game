call mvn clean package
call docker image build --tag=backend:latest .
call docker image prune --filter="dangling=true"