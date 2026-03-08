# Docker + Kubernetes Practice Guide

## 1) Local Docker run (no Jenkins)
From repo root:

docker compose up --build -d

Apps:
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080

Stop:

docker compose down

## 2) Local Kubernetes run (no Jenkins)
Prerequisite: Docker Desktop Kubernetes (or Minikube) is running.

Build images locally:

docker build -t loan-system/backend:latest backend
docker build -t loan-system/frontend:latest frontend

Deploy manifests:

kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-config.yaml
kubectl apply -f k8s/02-mysql.yaml
kubectl apply -f k8s/03-backend.yaml
kubectl apply -f k8s/04-frontend.yaml

Check rollout:

kubectl -n loan-system rollout status deploy/mysql
kubectl -n loan-system rollout status deploy/loan-backend
kubectl -n loan-system rollout status deploy/loan-frontend
kubectl -n loan-system get pods,svc

Frontend access:
- NodePort service is 30080
- Open: http://localhost:30080

Cleanup:

kubectl delete namespace loan-system

## 3) Jenkins pipeline parameters
In Jenkins Build with Parameters:
- RUN_TESTS=true -> run backend tests
- BUILD_DOCKER=true -> build Docker images
- DEPLOY_K8S=true -> apply k8s manifests + wait for rollout
- RUN_APP=true -> run backend/frontend as local host processes (non-container mode)

Recommended practice run:
1. RUN_TESTS=false, BUILD_DOCKER=false, DEPLOY_K8S=false (fast CI)
2. RUN_TESTS=false, BUILD_DOCKER=true, DEPLOY_K8S=true (container + k8s CD)

## Notes
- Backend uses MySQL in both Docker Compose and Kubernetes.
- Frontend production build uses /api and Nginx reverse proxy to backend service.
- If your cluster cannot see local Docker images, use a registry push workflow later.