pipeline {
    agent any

    tools {
        jdk 'Java17'
        maven 'maven-3.9.12'
    }

    stages {

       stage('AWS Identity Check') {
            steps {
                bat 'aws --version'
                bat 'aws sts get-caller-identity'
            }
        }


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
