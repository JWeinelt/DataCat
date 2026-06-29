pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    parameters {
        choice(
            name: 'BUILD_TYPE',
            choices: ['SNAPSHOT', 'BETA', 'RELEASE'],
            description: 'Type of build'
        )
    }

    environment {
        MAVEN_VERSION = ''
        APP_VERSION = ''
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Debug Workspace') {
            agent { label 'Linux-Build' }
            steps {
                sh 'pwd'
                sh 'ls -la'
                sh 'find . -maxdepth 3 -type f'
            }
        }

        stage('Prepare Maven Wrapper') {
            agent { label 'Linux-Build' }
            steps {
                sh 'chmod +x mvnw || true'
                sh './mvnw -v'
            }
        }

        stage('Clean & Build') {
            agent { label 'Linux-Build' }
            steps {
                sh './mvnw clean install -DskipTests'
            }
        }

        stage('Run Tests') {
            agent { label 'Linux-Build' }
            steps {
                sh './mvnw test'
            }
        }

        stage('Read Maven Version') {
            agent { label 'Linux-Build' }

            steps {
                script {
                    env.MAVEN_VERSION = sh(
                        script: "./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()

                    echo "Maven Version: ${env.MAVEN_VERSION}"
                }
            }
        }

        stage('Prepare Version') {
            agent { label 'Linux-Build' }

            steps {
                script {
                    def buildType = params.BUILD_TYPE ?: 'SNAPSHOT'

                    if (env.CHANGE_ID) {
                        buildType = 'SNAPSHOT'
                    }

                    if (env.BRANCH_NAME == 'main') {
                        buildType = 'SNAPSHOT'
                    }

                    switch (buildType) {
                        case "RELEASE":
                            env.APP_VERSION = env.MAVEN_VERSION
                            break

                        case "BETA":
                            env.APP_VERSION = "${env.MAVEN_VERSION}-beta"
                            break

                        default:
                            env.APP_VERSION = "${env.MAVEN_VERSION}-SNAPSHOT-${env.BUILD_NUMBER}"
                            break
                    }

                    echo "Build Type: ${buildType}"
                }
            }
        }

        stage('Build UI') {
            agent { label 'Linux-Build' }

            steps {
                sh './mvnw -pl ui -am clean package -DskipTests'
            }
        }

        stage('Build Flow') {
            agent { label 'Linux-Build' }

            steps {
                sh './mvnw -pl flow -am clean package -DskipTests'
            }
        }

        stage('Build Server') {
            agent { label 'Linux-Build' }

            steps {
                sh './mvnw -pl server -am clean package -DskipTests'
            }
        }

        stage('Build Launcher') {
            agent { label 'Linux-Build' }

            steps {
                sh './mvnw -pl launcher -am clean package -DskipTests'
            }
        }

        stage('Archive Snapshot Jars') {
            when {
                expression {
                    params.BUILD_TYPE == 'SNAPSHOT'
                }
            }

            agent { label 'Linux-Build' }

            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }

        stage('Create Linux App Image') {
            when {
                anyOf {
                    expression { params.BUILD_TYPE == 'BETA' }
                    expression { params.BUILD_TYPE == 'RELEASE' }
                }
            }

            agent { label 'Linux-Build' }

            steps {
                cleanWs()

                checkout scm

                sh 'chmod +x mvnw'

                sh './mvnw -pl ui -am clean package -DskipTests'

                sh """
                    rm -rf dist

                    jpackage \
                      --type app-image \
                      --name DataCat \
                      --input ui/target \
                      --main-jar DataCat.jar \
                      --main-class de.julianweinelt.datacat.DataCat \
                      --dest dist \
                      --app-version ${env.APP_VERSION}
                """

                sh """
                    cd dist
                    tar -czf DataCat-linux.tar.gz DataCat
                """

                archiveArtifacts artifacts: 'dist/DataCat-linux.tar.gz'
            }
        }

        stage('Create Windows Exe') {
            when {
                anyOf {
                    expression { params.BUILD_TYPE == 'BETA' }
                    expression { params.BUILD_TYPE == 'RELEASE' }
                }
            }

            agent { label 'windows-build' }

            steps {
                cleanWs()

                checkout scm

                bat 'mvnw.cmd -pl ui -am clean package -DskipTests'

                bat """
                    if exist dist rmdir /S /Q dist

                    jpackage ^
                      --type exe ^
                      --name DataCat ^
                      --input ui\\target ^
                      --main-jar DataCat.jar ^
                      --main-class de.julianweinelt.datacat.DataCat ^
                      --dest dist ^
                      --app-version ${env.APP_VERSION}
                """

                archiveArtifacts artifacts: 'dist/*.exe'
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
            echo "Build successful (${params.BUILD_TYPE})"
            echo "Version: ${env.APP_VERSION}"
        }

        failure {
            echo 'Build failed - check logs'
        }
    }
}