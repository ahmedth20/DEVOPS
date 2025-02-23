pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven'  // Assure-toi que Maven est bien installé sur Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/mon-utilisateur/mon-projet.git'  // Remplace avec ton dépôt Git
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    def mvnHome = tool 'Maven'
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        sh 'mvn clean test'
                    }
                }
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml' // Collecte les résultats des tests
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
        failure {
            echo "Build échoué ! Vérifie les logs."
        }
        success {
            echo "Build réussi avec succès 🎉"
        }
    }
}
