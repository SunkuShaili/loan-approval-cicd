pipeline {
    agent any

    tools {
        jdk 'Java17'
        maven 'maven-3.9.12'
        nodejs 'NodeJS-20'
    }

    environment {
        AWS_ACCOUNT_ID = '666694853488'
        AWS_REGION     = 'ap-south-2'
        ECR_REGISTRY   = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        DOCKER_PATH    = '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe"'
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

        stage('Build Backend (Maven)') {
            steps {
                dir('backend') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Prepare Backend JAR') {
            steps {
                dir('backend') {
                    bat 'copy target\\*SNAPSHOT.jar target\\app.jar'
                }
            }
        }

        stage('Build Frontend (Angular)') {
            steps {
                dir('frontend') {
                    bat '''
                    node -v
                    npm -v
                    npm install
                    npm run build
                    '''
                }
            }
        }

        stage('Docker Version Check') {
            steps {
                bat '''
                %DOCKER_PATH% --version
                %DOCKER_PATH% compose version
                '''
            }
        }

        stage('Login to AWS ECR') {
            steps {
                withCredentials([
                    [$class: 'AmazonWebServicesCredentialsBinding',
                     credentialsId: 'aws-jenkins']
                ]) {
                    bat """
                    aws ecr get-login-password --region %AWS_REGION% ^
                    | %DOCKER_PATH% login ^
                    --username AWS ^
                    --password-stdin %ECR_REGISTRY%
                    """
                }
            }
        }

        stage('Build Docker Images (Compose)') {
            steps {
                bat '%DOCKER_PATH% compose build'
            }
        }

        stage('Tag Docker Images') {
            steps {
                bat """
                %DOCKER_PATH% tag loan-backend-comp:1.0 ^
                %ECR_REGISTRY%/loan-backend-comp:1.0

                %DOCKER_PATH% tag loan-frontend-comp:1.0 ^
                %ECR_REGISTRY%/loan-frontend-comp:1.0
                """
            }
        }

        stage('Push Images to ECR') {
            steps {
                bat """
                %DOCKER_PATH% push %ECR_REGISTRY%/loan-backend-comp:1.0
                %DOCKER_PATH% push %ECR_REGISTRY%/loan-frontend-comp:1.0
                """
            }
        }

    stage('Show Frontend Dockerfile') {
        steps {
            dir('frontend') {
                bat 'type Dockerfile'
            }
        }
    }


    }

    
    post {
        success {
            echo '✅ CI Pipeline completed successfully. Images pushed to ECR.'
        }
        failure {
            echo '❌ Pipeline failed. Check logs above.'
        }
    }
}
