pipeline {
    agent any

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/SunkuShaili/loan-approval-cicd.git'
            }
        }

        stage('Verify Workspace') {
            steps {
                bat 'dir'
            }
        }

        stage('Build Backend (Maven)') {
            steps {
                dir('backend') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                bat 'docker compose build'
            }
        }

        stage('Deploy Containers') {
            steps {
                bat 'docker compose up -d'
            }
        }
    }
}
