pipeline {
    agent any

    tools {
        jdk 'Java17'
        maven 'maven-3.9.12'
    }

    stages {

       stage('AWS Identity Check') {
            steps {
                withCredentials([
                    [$class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-jenkins']
                ]) {
                    bat 'aws sts get-caller-identity'
                }
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


        stage('Docker Check') {
            steps {
                bat '''
                "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" --version
                "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" compose version
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                bat '''
                "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" compose build
                '''
            }
        }


        stage('List Docker Images') {
            steps {
                bat '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" images'
            }
        }



        stage('Tag Backend Image') {
            steps {
                bat '''
                "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" tag loan-backend-com:latest ^
                666694853488.dkr.ecr.ap-south-2.amazonaws.com/loan-backend-comp:latest
                '''
            }
        }



        stage('Tag Frontend Image') {
            steps {
                bat '''
                "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" tag loan-frontend-com:latest ^
                666694853488.dkr.ecr.ap-south-2.amazonaws.com/loan-frontend-comp:latest
                '''
            }
        }



        stage('Verify Tagged Images') {
            steps {
                bat '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" images | findstr loan'
            }
        }





        stage('Deploy Containers') {
            steps {
                bat '''
                "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" compose up -d
                '''
            }
        }


        stage('Login to AWS ECR') {
            steps {
                withCredentials([
                    [$class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-jenkins']
                ]) {
                    bat '''
                    aws ecr get-login-password --region ap-south-2 ^
                    | "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" login ^
                    --username AWS ^
                    --password-stdin 666694853488.dkr.ecr.ap-south-2.amazonaws.com
                    '''

                }
            }
        }






      }
}
