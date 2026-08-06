// SonarQube Analysis using Jenkins Shared Library
def call(Map config){

    docker.image(config.image).inside('-u root:root'){

        withSonarQubeEnv(config.server){

            sh 'sonar-scanner'

        }

    }

}