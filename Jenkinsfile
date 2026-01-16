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

        /* ================= AWS ================= */

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

        /* ================= BACKEND ================= */

        stage('Build Backend (Maven)') {
            steps {
                dir('backend') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Prepare Backend JAR (SAFE COPY)') {
            steps {
                dir('backend') {
                    bat '''
                    echo Preparing backend JAR...
                    dir target

                    for %%f in (target\\*SNAPSHOT.jar) do (
                        echo Copying %%f to app.jar
                        copy /Y %%f target\\app.jar
                    )

                    echo Verifying app.jar
                    dir target\\app.jar
                    '''
                }
            }
        }


        /* ================= FRONTEND ================= */

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

        /* ================= DOCKER ================= */

        stage('Docker Version Check') {
            steps {
                bat '%DOCKER_PATH% --version'
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

        stage('Build Docker Images') {
            steps {
                bat """
                %DOCKER_PATH% build --no-cache -t loan-backend-comp:1.0 backend
                %DOCKER_PATH% build --no-cache -t loan-frontend-comp:2.0 frontend
                """
            }
        }

        stage('Tag Docker Images') {
            steps {
                bat """
                %DOCKER_PATH% tag loan-backend-comp:1.0 ^
                %ECR_REGISTRY%/loan-backend-comp:1.0

                %DOCKER_PATH% tag loan-frontend-comp:2.0 ^
                %ECR_REGISTRY%/loan-frontend-comp:2.0
                """
            }
        }

        stage('Push Images to ECR') {
            steps {
                bat """
                %DOCKER_PATH% push %ECR_REGISTRY%/loan-backend-comp:1.0
                %DOCKER_PATH% push %ECR_REGISTRY%/loan-frontend-comp:2.0
                """
            }
        }

        /* ================= VERIFICATION ================= */

        stage('Show Backend & Frontend Dockerfiles') {
            steps {
                dir('backend') {
                    bat 'type Dockerfile'
                }
                dir('frontend') {
                    bat 'type Dockerfile'
                }
            }
        }
    }

    post {
        success {
            echo '✅ CI pipeline SUCCESS: Backend 1.0 & Frontend 2.0 pushed to ECR'
        }
        failure {
            echo '❌ CI pipeline FAILED: Check logs above'
        }
    }
}
