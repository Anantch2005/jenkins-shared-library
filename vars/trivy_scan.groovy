def call(Map config){

    docker.image('aquasec/trivy:latest').inside('''
        --entrypoint=''
        -u root:root
        -v /var/run/docker.sock:/var/run/docker.sock
    '''){

        sh """
        trivy image ${config.image}
        """

    }

}