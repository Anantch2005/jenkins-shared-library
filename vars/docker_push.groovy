def call(Map config = [:]) {

    def image         = config.image ?: error("image is required")
    def tag           = config.get('tag', 'latest')
    def registry      = config.get('registry', '')
    def credentialsId = config.credentialsId ?: error("credentialsId is required")

    echo """
========================================
 Docker Push
========================================
 Image      : ${image}
 Tag        : ${tag}
 Registry   : ${registry ?: "Docker Hub"}
========================================
"""

    docker.withRegistry(
        registry ? "https://${registry}" : "",
        credentialsId
    ) {

        sh "docker push ${image}:${tag}"

    }

    echo "Docker image '${image}:${tag}' pushed successfully."
}