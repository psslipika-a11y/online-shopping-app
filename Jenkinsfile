
pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                withCredentials([string(
                    credentialsId: 'mysql-db-password',
                    variable: 'DB_PASSWORD'
                )]) {
                    sh 'echo "JAVA_HOME=$JAVA_HOME"'
                    sh 'which java'
                    sh 'java -version'
                    sh 'which javac || true'
                    sh 'javac -version || true'
                    sh 'chmod +x mvnw'
                    sh './mvnw clean test'
                }
            }
        }
    }

    post {
        success {
            echo 'Build and tests completed successfully!'
        }

        failure {
            echo 'Build or tests failed. Check the console output.'
        }
    }
}
