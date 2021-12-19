call docker image build --tag=frontend:latest .
call docker image prune --filter="dangling=true"