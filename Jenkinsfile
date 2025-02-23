pipeline {
    agent any
    
    tools {
        maven 'Maven' // Doit correspondre au nom défini dans Jenkins
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
