pipeline {
    agent any

    environment {
        DB_NAME = 'kaddemdb'
        DB_USER = 'root'
        DB_PASS = ''  // Définir un mot de passe sécurisé pour MySQL
        DB_PORT = '3306'
        MYSQL_CONTAINER = 'mysqldb'
        IMAGE_NAME = 'ramezzorgui/kaddem-app'
        IMAGE_TAG = '0.0.1'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        git branch: 'Département', url: 'https://github.com/ahmedth20/DEVOPS.git'
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Checkout : ${duration}s"
                    }
                }
            }
        }

        stage('Test Docker') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        set -e
                        echo "Vérification de Docker..."
                        whoami
                        docker version
                        docker ps
                        docker-compose ps  # List all containers managed by docker-compose
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Test Docker : ${duration}s"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh 'mvn clean'
                        sh 'mvn clean install'
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Build : ${duration}s"
                    }
                }
            }
        }

        stage('Test with Coverage') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        echo "Exécution des tests avec couverture..."
                        mvn test
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime)
                        echo "Durée de l'étape Test with Coverage : ${duration}ms"
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                            withSonarQubeEnv('SonarQube') {
                                sh """
                                mvn sonar:sonar
                                """
                            }
                        }
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape SonarQube Analysis : ${duration}s"
                    }
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    withCredentials([usernamePassword(credentialsId: 'nexus-credentials', 
                                                      usernameVariable: 'NEXUS_USER', 
                                                      passwordVariable: 'NEXUS_PASS')]) {
                        try {
                            sh """
                            echo "Déploiement vers Nexus (tests ignorés)..."
                            mvn deploy -DskipTests
                            """
                        } finally {
                            def endTime = System.currentTimeMillis()
                            def duration = (endTime - startTime) / 1000
                            echo "Durée de l'étape Deploy to Nexus : ${duration}s"
                        }
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    echo '🐳 Building Docker Image...'
                    sh 'docker-compose build'
                }
            }
        }

        stage('List Docker Images') {
            steps {
                script {
                    echo '📦 Listing Docker Images...'
                    sh 'docker images'
                }
            }
        }

        stage('Push to DockerHub') {
            steps {
                script {
                    echo '🚀 Pushing Docker Image to DockerHub...'
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PAT')]) {
                        sh '''
                            echo "$DOCKER_PAT" | docker login -u "$DOCKER_USER" --password-stdin
                            docker push ramezzorgui/kaddem-app:0.0.1
                        '''
                    }
                }
            }
        }
        stage('Scan Docker Image') {
            steps {
                script {
                    def pipelineReport = readJSON file: env.pipelineReportFile
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        set -e
                        echo "Vérification de la présence de l'image ${IMAGE_NAME}:${IMAGE_TAG}..."
                        if docker images | grep -q "${IMAGE_NAME}.*${IMAGE_TAG}"; then
                            echo "Image ${IMAGE_NAME}:${IMAGE_TAG} trouvée localement."
                        else
                            echo "Erreur : Image ${IMAGE_NAME}:${IMAGE_TAG} introuvable localement !"
                            exit 1
                        fi
                        echo "Scan de l'image avec Trivy..."
                        trivy image --exit-code 0 --severity HIGH,CRITICAL --scanners vuln --timeout 20m ${IMAGE_NAME}:${IMAGE_TAG} > trivy_output.txt 2>&1
                        trivy_output=$(cat trivy_output.txt | grep 'Total:' || echo "No vulnerabilities found")
                        echo "$trivy_output"
                        '''
                        pipelineReport['trivyResults'] = sh(script: 'cat trivy_output.txt | grep "Total:" || echo "No vulnerabilities found"', returnStdout: true).trim()
                    } finally {
                        def duration = (System.currentTimeMillis() - startTime) / 1000
                        pipelineReport['stages']['Scan Docker Image'] = duration
                        writeJSON file: env.pipelineReportFile, json: pipelineReport
                        echo "Durée de l'étape Scan Docker Image : ${duration}s"
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    echo '🚀 Deploying with Docker Compose...'
                    sh 'docker-compose down'  // Stop all services
                    sh 'docker-compose up -d'  // Start the containers again in detached mode
                }
            }
        }

        stage('Notify') {
            steps {
                script {
                    def currentBuildResult = currentBuild.result ?: 'SUCCESS'
                    def subject = "Build ${currentBuildResult}: ${currentBuild.fullDisplayName}"
                    def body = "The build ${currentBuild.fullDisplayName} finished with result: ${currentBuildResult}."

                    emailext (
                        subject: subject,
                        body: body,
                        to: 'ramez.zorgui@esprit.tn', // replace with actual recipient email
                        mimeType: 'text/html'
                    )
                }
            }
        }
    }

    post {
        always {
            script {
                def currentBuildResult = currentBuild.result ?: 'SUCCESS'
                def subject = "Build ${currentBuildResult}: ${currentBuild.fullDisplayName}"
                def body = "The build ${currentBuild.fullDisplayName} finished with result: ${currentBuildResult}."

                emailext (
                    subject: subject,
                    body: body,
                    to: 'ramez.zorgui@esprit.tn', // replace with actual recipient email
                    mimeType: 'text/html'
                )
            }
        }
    }
}
