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

        stage('Build UI') {
            steps {
                sh './mvnw -pl ui -am clean package -DskipTests'
            }
        }
        stage('Create Linux App Image') {
            agent {
                label 'Linux-Build'
            }
        
            steps {
                deleteDir()
                checkout scm
        
                sh 'ls -la'
                sh 'chmod +x mvnw'
        
                sh './mvnw -pl ui -am clean package -DskipTests'
                sh '''
                    rm -rf dist
        
                    jpackage \
                      --type app-image \
                      --name DataCat \
                      --input ui/target \
                      --main-jar DataCat.jar \
                      --main-class de.julianweinelt.datacat.DataCat \
                      --dest dist \
                      --app-version ${BUILD_NUMBER}
                '''
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'dist/**', fingerprint: true
            }
        }
    }

    post {
        always {
            script {
                if (fileExists('**/target/surefire-reports')) {
                    junit '**/target/surefire-reports/*.xml'
                } else {
                    echo 'No test reports found - skipping JUnit step'
                }
            }
        }

        success {
            echo 'Build successful'
        }

        failure {
            echo 'Build failed - check logs'
        }
    }
}
