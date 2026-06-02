pipeline {
    agent any

    environment {
        AWS_ACCOUNT_ID = "566167301816"
        AWS_REGION     = "ap-south-1"
        ECR_REPO       = "myapp"
        ECR_URL        = "566167301816.dkr.ecr.ap-south-1.amazonaws.com"
        IMAGE_TAG      = "${BUILD_NUMBER}"
        EKS_CLUSTER    = "cicd-cluster"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh """
                docker build -t ${ECR_REPO}:${IMAGE_TAG} .

                docker tag ${ECR_REPO}:${IMAGE_TAG} \
                ${ECR_URL}/${ECR_REPO}:${IMAGE_TAG}

                docker tag ${ECR_REPO}:${IMAGE_TAG} \
                ${ECR_URL}/${ECR_REPO}:latest
                """
            }
        }

        stage('Push to ECR') {
            steps {
                sh """
                aws ecr get-login-password --region ${AWS_REGION} | \
                docker login --username AWS --password-stdin ${ECR_URL}

                docker push ${ECR_URL}/${ECR_REPO}:${IMAGE_TAG}
                docker push ${ECR_URL}/${ECR_REPO}:latest
                """
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh """
                aws eks update-kubeconfig \
                  --region ${AWS_REGION} \
                  --name ${EKS_CLUSTER}

                kubectl apply -f k8s/deployment.yaml
                kubectl apply -f k8s/service.yaml

                kubectl set image deployment/myapp-deployment \
                  myapp=${ECR_URL}/${ECR_REPO}:${IMAGE_TAG}

                kubectl rollout status deployment/myapp-deployment --timeout=180s
                """
            }
        }

        stage('Verify') {
            steps {
                sh '''
                kubectl get pods
                kubectl get svc
                '''
            }
        }
    }

    post {
        success {
            echo "BUILD #${BUILD_NUMBER} DEPLOYED SUCCESSFULLY"
        }
        failure {
            echo "BUILD FAILED — check console output"
        }
    }
}
