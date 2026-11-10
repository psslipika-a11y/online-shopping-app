
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
                withEnv([
                    'JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home',
                    'PATH=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home/bin:/usr/bin:/bin:/usr/sbin:/sbin'
                ]) {
                    withCredentials([string(
                        credentialsId: 'mysql-db-password',
                        variable: 'DB_PASSWORD'
                    )]) {
                        sh 'java -version'
                        sh 'javac -version'
                        sh 'chmod +x mvnw'
                        sh './mvnw clean package'
                    }
                }
            }
        }

stage('Build Docker Image') {
    steps {
        withEnv([
            'PATH=/Users/psslipika/.docker/bin:/opt/homebrew/bin:/usr/bin:/bin:/usr/sbin:/sbin'
        ]) {
            sh 'docker version'
            sh 'docker build -t online-shopping-app .'
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
