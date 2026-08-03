pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    environment {
        SERVER_IP = '51.20.113.199.nip.io'
    }

    stages {

        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                sh 'docker build -t quantity-measurement-app .'
            }
        }

        stage('Deploy Backend Container') {
            steps {
                withCredentials([
                    string(credentialsId: 'google-client-id', variable: 'GOOGLE_CLIENT_ID'),
                    string(credentialsId: 'google-client-secret', variable: 'GOOGLE_CLIENT_SECRET'),
                    string(credentialsId: 'jwt-secret', variable: 'JWT_SECRET')
                ]) {
                    sh '''
                        docker stop quantity-measurement-app-container || true
                        docker rm quantity-measurement-app-container || true
                        docker run -d \
                          --name quantity-measurement-app-container \
                          --restart always \
                          -p 8081:8080 \
                          -e GOOGLE_CLIENT_ID=$GOOGLE_CLIENT_ID \
                          -e GOOGLE_CLIENT_SECRET=$GOOGLE_CLIENT_SECRET \
                          -e JWT_SECRET=$JWT_SECRET \
                          -e FRONTEND_URL=http://${SERVER_IP} \
                          -e CORS_ALLOWED_ORIGINS=http://${SERVER_IP},http://localhost:3000 \
                          quantity-measurement-app
                    '''
                }
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                dir('qm-frontend') {
                    sh '''
                        docker build \
                          --build-arg VITE_API_BASE_URL=http://${SERVER_IP}:8081 \
                          -t quantity-measurement-frontend .
                    '''
                }
            }
        }

        stage('Deploy Frontend Container') {
            steps {
                sh '''
                    docker stop quantity-measurement-frontend-container || true
                    docker rm quantity-measurement-frontend-container || true
                    docker run -d \
                      --name quantity-measurement-frontend-container \
                      --restart always \
                      -p 80:80 \
                      quantity-measurement-frontend
                '''
            }
        }

        stage('List Docker Images') {
            steps {
                sh 'docker images'
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully!'
        }

        failure {
            echo 'Pipeline failed!'
        }
    }
}