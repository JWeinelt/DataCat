pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Debug Workspace') {
            steps {
                sh 'pwd'
                sh 'ls -la'
                sh 'find . -maxdepth 3 -type f'
            }
        }

        stage('Prepare Maven Wrapper') {
            steps {
                sh 'chmod +x mvnw || true'
                sh './mvnw -v'
            }
        }

        stage('Clean & Build') {
            steps {
                sh './mvnw clean install -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                sh './mvnw test'
            }
        }

        stage('Package Artifacts') {
            steps {
                sh './mvnw package -DskipTests'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }

        success {
            echo 'Build successful'
        }

        failure {
            echo 'Build failed - check logs'
        }
    }
}
