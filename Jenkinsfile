pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
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

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t quantity-measurement-app .'
            }
        }

        stage('Deploy Container') {
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
                          quantity-measurement-app
                    '''
                }
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