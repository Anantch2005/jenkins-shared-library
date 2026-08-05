def call(image, tag) {
    sh '''
    docker build -t ${image}:${tag} .
    '''
}