def call(Map config = [:]) {

    def image       = config.image ?: error("image is required")
    def tag         = config.get('tag', 'latest')
    def dockerfile  = config.get('dockerfile', 'Dockerfile')
    def context     = config.get('context', '.')

    echo """
========================================
 Docker Build
========================================
 Image      : ${image}
 Tag        : ${tag}
 Dockerfile : ${dockerfile}
 Context    : ${context}
========================================
"""

    if (!fileExists(dockerfile)) {
        error("Dockerfile not found: ${dockerfile}")
    }

    sh """
    docker build \
      -t ${image}:${tag} \
      -f ${dockerfile} \
      ${context}
    """

    echo "Docker image '${image}:${tag}' built successfully."

    return "${image}:${tag}"
}