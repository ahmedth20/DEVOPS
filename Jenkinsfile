pipeline {
    agent any
    
    stages {
        stage('Setup MySQL for Tests') {
            steps {
                script {
                    echo 'Cleaning up any existing MySQL containers on port 3306...'
                    // Arrête et supprime tous les conteneurs utilisant le port 3306
                    sh '''
                        docker ps -a -q --filter "expose=3306" | xargs -r docker stop || true
                        docker ps -a -q --filter "expose=3306" | xargs -r docker rm || true
                    '''
                    
                    echo 'Starting MySQL container for tests (MySQL 5.7)...'
                    sh '''
                        docker run -d --name mysql-test \
                            -e MYSQL_ROOT_PASSWORD= \
                            -e MYSQL_DATABASE=kaddemdb \
                            -e MYSQL_ALLOW_EMPTY_PASSWORD=yes \
                            -p 3306:3306 \
                            mysql:5.7
                    '''
                    sh 'docker logs mysql-test'
                    sh '''
                        timeout 60s bash -c "until docker exec mysql-test mysqladmin -uroot -h 127.0.0.1 status; do sleep 2; echo 'Waiting for MySQL...'; done"
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
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

        stage('Cleanup Before Docker Compose') {
            steps {
                script {
                    echo 'Cleaning up MySQL test container before Docker Compose...'
                    sh '''
                        docker stop mysql-test || true
                        docker rm mysql-test || true
                    '''
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
            script {
                echo 'Final cleanup of MySQL test container...'
                sh 'docker stop mysql-test || true'
                sh 'docker rm mysql-test || true'
            }
        }
    }
}
