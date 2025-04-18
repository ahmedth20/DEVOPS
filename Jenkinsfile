pipeline {
    agent any
     tools {
        maven "Maven"
        jdk "JAVA_HOME"
    }

    environment {
        registryCredentials = "nexus"
        registry = "localhost:8081"
        DB_NAME = 'Kaddemdb'
        DB_USER = 'root'
        DB_PASS = 'ahmedequipe'
        DB_PORT = '3306'
        MYSQL_CONTAINER = 'mysql-test'
        IMAGE_NAME = "springbootapp:1.0"
        DOCKER_REGISTRY_URL = "http://localhost:8083"  // URL en HTTPS pour Nexus
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = 'localhost:8081'
        NEXUS_REPOSITORY = "Maven"
        NEXUS_CREDENTIAL_ID = "nexus"
        ARTIFACT_VERSION = "${BUILD_NUMBER}"

        PROMETHEUS_CONTAINER = 'prometheus'
        GRAFANA_CONTAINER = 'grafana'
        DOCKER_IMAGE = "ahmedth234/thabtiahmed_4twin5_g1_kaddem"
        CONTAINER_NAME = "kaddemapp"
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-credentials'
        DOCKERHUB_REPO = "ahmedth234/ThabtiAhmed_4TWIN5_G1_kaddem"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    git branch: 'ThabtiAhmed_4TWIN5_G2', url: 'https://github.com/ahmedth20/DEVOPS.git'
                }
            }
        }

        stage('Start MySQL') {
            steps {
                script {
                    sh '''
                    WSL_DOCKER="docker"

                    if [ $(wsl $WSL_DOCKER ps -a -q -f name=$MYSQL_CONTAINER) ]; then
                        if [ $(wsl $WSL_DOCKER ps -q -f name=$MYSQL_CONTAINER) ]; then
                            echo "Le conteneur MySQL est déjà en cours d'exécution."
                        else
                            echo "Le conteneur MySQL existe mais est arrêté. Redémarrage..."
                            wsl $WSL_DOCKER start $MYSQL_CONTAINER
                        fi
                    else
                        echo "Démarrage de MySQL..."
                        wsl $WSL_DOCKER run --name $MYSQL_CONTAINER \
                            -e MYSQL_DATABASE=$DB_NAME \
                            -e MYSQL_ROOT_PASSWORD=$DB_PASS \
                            -p $DB_PORT:3306 \
                            -d mysql:8
                    fi

                    echo "Attente de MySQL (10 sec)..."
                    sleep 10

                    wsl $WSL_DOCKER ps | grep $MYSQL_CONTAINER || (echo "MySQL n'a pas démarré !" && exit 1)
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh 'mvn clean package'
                }
            }
        }
        
     /*   stage('Docker Compose Up') {
            steps {
                script {
                    sh '''
                    echo "🔧 Lancement des services via Docker Compose..."
                    docker-compose down
                    docker compose -f docker-compose.yml up -d
                    '''
                }
            }
        } */

        stage('Test') {
            steps {
                script {
                    sh 'mvn test'
                }
            }
        }

          stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }


   stage("publish to nexus") {
            steps {
                script {
                    // Read POM xml file using 'readMavenPom' step , this step 'readMavenPom' is included in: https://plugins.jenkins.io/pipeline-utility-steps
                    pom = readMavenPom file: "pom.xml";
                    // Find built artifact under target folder
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    // Print some info from the artifact found
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    // Extract the path from the File found
                    artifactPath = filesByGlob[0].path;
                    // Assign to a boolean response verifying If the artifact name exists
                    artifactExists = fileExists artifactPath;

                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";

                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            groupId: pom.groupId,
                            version: ARTIFACT_VERSION,
                            repository: NEXUS_REPOSITORY,
                            credentialsId: NEXUS_CREDENTIAL_ID,
                            artifacts: [
                                // Artifact generated such as .jar, .ear and .war files.
                                [artifactId: pom.artifactId,
                                classifier: '',
                                file: artifactPath,
                                type: pom.packaging]
                            ]
                        );

                    } else {
                        error "*** File: ${artifactPath}, could not be found";
                    }
                }
            }
        }

        /*     stage('Run Application') {
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
        }*/

      stage('Prometheus and Grafana') {
    steps {
        script {
            sh '''
            # Démarrer Prometheus si non démarré
            if [ "$(docker ps -q -f name=prometheus)" = "" ]; then
                if [ "$(docker ps -aq -f name=prometheus)" = "" ]; then
                    echo " Lancement de Prometheus..."
                    docker run -d --name prometheus -p 9090:9090 prom/prometheus
                else
                    echo " Conteneur Prometheus existe mais est arrêté. Redémarrage..."
                    docker start prometheus
                fi
            else
                echo " Prometheus est déjà en cours d'exécution."
            fi

            # Démarrer Grafana si non démarré
            if [ "$(docker ps -q -f name=grafana)" = "" ]; then
                if [ "$(docker ps -aq -f name=grafana)" = "" ]; then
                    echo " Lancement de Grafana..."
                    docker run -d --name grafana -p 3000:3000 grafana/grafana
                else
                    echo " Conteneur Grafana existe mais est arrêté. Redémarrage..."
                    docker start grafana
                fi
            else
                echo " Grafana est déjà en cours d'exécution."
            fi
            '''
        }
    }
}


        stage('Cleanup') {
            steps {
                script {
                    sh 'docker stop $MYSQL_CONTAINER || true'
                    sh 'docker rm $MYSQL_CONTAINER || true'  // Supprimer le conteneur après l'arrêt
                }
            }
        }
    }
post {
    success {
        emailext (
            subject: " Build Reussi - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            body: """
Le build a reussi 

Job : ${env.JOB_NAME}
Build : ${env.BUILD_NUMBER}
Durée : ${currentBuild.durationString}
Auteur : ${sh(script: "git log -1 --pretty=format:'%an'", returnStdout: true).trim()}""",
            to: "thabtiahmed0@gmail.com",
             mimeType: 'text/html; charset=UTF-8',  // Ajoutez cette ligne
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
        )
    }

    failure {
        emailext (
            subject: "Echec du build - ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
            body: """Le build a echoue 
            - Job: ${env.JOB_NAME}
            - Build: ${env.BUILD_NUMBER}
            - Duree: ${currentBuild.durationString}
            - Auteur: ${sh(script: "git log -1 --pretty=format:'%an'", returnStdout: true).trim()}""",
            to: "thabtiahmed0@gmail.com"
        )
    }
}

}

