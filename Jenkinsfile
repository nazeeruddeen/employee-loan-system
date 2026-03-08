pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    parameters {
        booleanParam(name: 'RUN_TESTS', defaultValue: false, description: 'Run backend tests')
        booleanParam(name: 'RUN_APP', defaultValue: false, description: 'Start backend (8080) and frontend (4200) after build')
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
