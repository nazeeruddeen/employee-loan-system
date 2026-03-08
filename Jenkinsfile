pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    parameters {
        booleanParam(name: 'RUN_TESTS', defaultValue: false, description: 'Run backend tests')
        booleanParam(name: 'RUN_APP', defaultValue: false, description: 'Start backend (8080) and frontend (4200) using local processes after build')
        booleanParam(name: 'BUILD_DOCKER', defaultValue: false, description: 'Build Docker images for backend and frontend')
        booleanParam(name: 'DEPLOY_K8S', defaultValue: false, description: 'Deploy to local Kubernetes using manifests under k8s/')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Tools') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
                bat 'npm -v'
                bat 'node -v'
                script {
                    if (params.BUILD_DOCKER || params.DEPLOY_K8S) {
                        bat 'docker version'
                    }
                    if (params.DEPLOY_K8S) {
                        bat 'minikube version'
                        bat 'kubectl version --client'
                    }
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    script {
                        if (params.RUN_TESTS) {
                            bat 'mvn -B clean verify'
                        } else {
                            bat 'mvn -B clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    bat 'npm ci'
                    bat 'npm run build -- --configuration production'
                }
            }
        }

        stage('Build Docker Images') {
            when {
                expression { return params.BUILD_DOCKER || params.DEPLOY_K8S }
            }
            steps {
                bat 'docker build -t loan-system/backend:latest backend'
                bat 'docker build -t loan-system/frontend:latest frontend'
            }
        }

        stage('Prepare Kubernetes') {
            when {
                expression { return params.DEPLOY_K8S }
            }
            steps {
                bat 'minikube start --driver=docker'
                bat 'kubectl config use-context minikube'
                bat 'kubectl cluster-info'
                bat 'kubectl get nodes'
                bat 'minikube image load loan-system/backend:latest'
                bat 'minikube image load loan-system/frontend:latest'
            }
        }

        stage('Deploy Kubernetes') {
            when {
                expression { return params.DEPLOY_K8S }
            }
            steps {
                bat 'kubectl apply -f k8s/00-namespace.yaml'
                bat 'kubectl apply -f k8s/01-config.yaml'
                bat 'kubectl apply -f k8s/02-mysql.yaml'
                bat 'kubectl apply -f k8s/03-backend.yaml'
                bat 'kubectl apply -f k8s/04-frontend.yaml'

                bat '''
                    kubectl -n loan-system rollout status deploy/mysql --timeout=240s
                    if errorlevel 1 exit /b 1

                    kubectl -n loan-system rollout status deploy/loan-backend --timeout=300s
                    if errorlevel 1 (
                        echo Backend rollout failed. Dumping diagnostics...
                        kubectl -n loan-system get pods -o wide
                        kubectl -n loan-system describe deployment loan-backend
                        kubectl -n loan-system logs deployment/loan-backend --tail=200
                        exit /b 1
                    )

                    kubectl -n loan-system rollout status deploy/loan-frontend --timeout=240s
                    if errorlevel 1 (
                        echo Frontend rollout failed. Dumping diagnostics...
                        kubectl -n loan-system get pods -o wide
                        kubectl -n loan-system describe deployment loan-frontend
                        kubectl -n loan-system logs deployment/loan-frontend --tail=200
                        exit /b 1
                    )
                '''
                bat 'kubectl -n loan-system get pods -o wide'
                bat 'kubectl -n loan-system get svc'
            }
        }

        stage('Run Application (Optional)') {
            when {
                expression { return params.RUN_APP }
            }
            steps {
                bat '''
                    if exist backend\\backend.log del /f /q backend\\backend.log
                    if exist frontend\\frontend.log del /f /q frontend\\frontend.log

                    set "JAR_FILE="
                    for /f "delims=" %%i in ('dir /b /a-d backend\\target\\*.jar ^| findstr /v /i "original"') do (
                        set "JAR_FILE=backend\\target\\%%i"
                        goto jarFound
                    )

                    :jarFound
                    if not defined JAR_FILE (
                        echo Backend jar not found in backend\\target
                        exit /b 1
                    )

                    start "loan-backend" /min cmd /c "java -jar %JAR_FILE% > backend\\backend.log 2>&1"
                    start "loan-frontend" /min cmd /c "cd frontend && npm run start -- --host 0.0.0.0 --port 4200 > frontend.log 2>&1"
                '''
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'backend/target/*.jar,frontend/dist/**,backend/backend.log,frontend/frontend.log', allowEmptyArchive: true
        }
    }
}

