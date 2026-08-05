def call(image, tag) {
    sh '''
    docker build \
    -t ${config.image}:${config.tag} .
    '''
}