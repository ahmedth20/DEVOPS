pipeline {
    agent any
 environment {
        DOCKER_IMAGE = "aymenkhelifa278/kaddem"
        CONTAINER_NAME = "kaddem_app"
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-credentials'
        DOCKERHUB_REPO = "aymenkhelifa278/kaddem"
    }
    stages {
        stage('Build') {
            steps {
                // Clean and compile the project
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                // Run unit tests
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
            stage('Build & Push Docker Image') {
                  steps {
                      script {
                          echo 'Building Docker Image'
                          sh "docker build -t $DOCKER_IMAGE -f Dockerfile ."

                          echo 'Logging into Docker Hub'
                          withCredentials([usernamePassword(credentialsId: DOCKERHUB_CREDENTIALS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                              sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                          }

                          echo 'Pushing Docker Image'
                          sh "docker push $DOCKER_IMAGE"
                      }
                  }
              }

             stage('build app'){

              steps {

                             sh 'docker-compose build'
                         }
             }
               stage('run app') {
                   steps {
                       sh 'docker-compose down -v --remove-orphans || true'
                       sh 'docker-compose up -d'
                   }
               }
             stage('Notification') {
                steps {
                      withCredentials([
                    //  string(credentialsId: 'ACc326c987c855a8deff2b97cc4c9e65e5', variable: 'TWILIO_SID'),
                    //  string(credentialsId: 'f8b777d709fdaf1b645cffbf7125a1c3', variable: 'TWILIO_TOKEN'),
                     // string(credentialsId: '+12523769186', variable: 'TWILIO_FROM'),
                     // string(credentialsId: '+21693747226', variable: 'TWILIO_TO')
                    ]) {
                      sh '''
                           curl -X POST https://api.twilio.com/2010-04-01/Accounts/ACc326c987c855a8deff2b97cc4c9e65e5/Messages.json \
                           --data-urlencode "Body=Pipeline terminé avec succès pour Kaddem !" \
                           --data-urlencode "From=+12523769186" \
                           --data-urlencode "To=+21693747226" \
                             -u ACc326c987c855a8deff2b97cc4c9e65e5:f8b777d709fdaf1b645cffbf7125a1c3
                               '''
        }
    }
}

    }
}
