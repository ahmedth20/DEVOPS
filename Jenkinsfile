pipeline {
    agent any
    
    stages {
        stage('Setup MySQL for Tests') {
            steps {
                script {
                    echo 'Cleaning up any existing MySQL container...'
                    // Supprime le conteneur s'il existe déjà
                    sh '''
                        docker stop mysql-test || true
                        docker rm mysql-test || true
                    '''
                    
                    echo 'Starting MySQL container for tests...'
                    // Démarre un nouveau conteneur MySQL
                    sh '''
                        docker run -d --name mysql-test \
                            -e MYSQL_ROOT_PASSWORD= \
                            -e MYSQL_DATABASE=kaddemdb \
                            -p 3306:3306 \
                            mysql:latest
                    '''
                    // Attend que MySQL soit prêt (timeout de 30s)
                    sh '''
                        timeout 30s bash -c "until docker exec mysql-test mysqladmin -uroot status; do sleep 2; done"
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                // Clean and compile the project
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                // Run unit tests with MySQL running
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    // Perform SonarQube analysis
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        echo "Déploiement vers Nexus (tests ignorés)..."
                        mvn deploy -DskipTests
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Deploy to Nexus : ${duration}s"
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    echo '🐳 Building Docker Image...'
                    sh 'docker build -t ramezzorgui/kaddem-app:0.0.1 .'
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

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    echo '🚀 Deploying with Docker Compose...'
                    sh 'docker compose up -d'
                }
            }
        }
    }

    post {
        always {
            // Nettoyage : arrête et supprime le conteneur MySQL temporaire
            script {
                echo 'Cleaning up MySQL test container...'
                sh 'docker stop mysql-test || true'
                sh 'docker rm mysql-test || true'
            }
        }
    }
}
