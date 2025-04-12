pipeline {
    agent any
     tools {
        maven "Maven"
        jdk "JAVA_HOME"
    }

    environment {
        registryCredentials = "nexus"
        registry = "localhost:8083"
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
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    git branch: 'Equipe', url: 'https://github.com/ahmedth20/DEVOPS.git'
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

        stage('Test') {
            steps {
                script {
                    sh 'mvn test'
                }
            }
        }

        /*  stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        } */

        /* stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t $registry/$IMAGE_NAME .'
                }
            }
        } */

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

        stage('Run Application') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh '''
                        docker pull $registry/$IMAGE_NAME

                        if [ "$(docker images -q $registry/$IMAGE_NAME)" == "" ]; then
                            echo "Erreur: L'image Docker n'a pas été téléchargée correctement"
                            exit 1
                        fi

                        echo "Création du fichier docker-compose.yml..."
                        cat <<EOF > docker-compose.yml
                        version: '3.8'
                        services:
                          db:
                            image: mysql:8
                            container_name: mysql-test
                            restart: always
                            environment:
                              MYSQL_DATABASE: ${DB_NAME}
                              MYSQL_ROOT_PASSWORD: ${DB_PASS}
                            ports:
                              - "3306:3306"
                          app:
                            image: ${registry}/${IMAGE_NAME}
                            container_name: springboot-app
                            depends_on:
                              - db
                            ports:
                              - "8080:8080"
                        EOF

                        echo "Démarrage des services avec Docker Compose..."
                        docker-compose up -d
                        '''
                    }
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
}


