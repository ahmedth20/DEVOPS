pipeline {
    agent any
    
    tools {
        maven 'M2_Home' // Doit correspondre au nom défini dans Jenkins
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            echo 'Build terminé.'
        }
    }
}
