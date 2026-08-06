/**
 * dockerBuildWithCache.groovy
 *
 * Builds (and optionally pushes) a Docker image using BuildKit inline
 * cache, so subsequent builds can reuse layers from the registry.
 *
 * Usage:
 *   dockerBuildWithCache(
 *       image: 'myorg/myapp',
 *       tag: env.BUILD_NUMBER,
 *       dockerfile: 'Dockerfile',
 *       context: '.',
 *       push: true,
 *       credentialsId: 'docker-hub-creds',
 *       registry: 'registry.hub.docker.com',
 *       buildArgs: [ENV: 'production'],
 *       retries: 2
 *   )
 */
def call(Map config = [:]) {

    if (!config.image) {
        error("dockerBuildWithCache: 'image' is required")
    }

    String image         = config.image
    String tag           = config.tag ?: 'latest'
    String dockerfile    = config.dockerfile ?: 'Dockerfile'
    String context       = config.context ?: '.'
    boolean push          = config.containsKey('push') ? config.push : false
    String credentialsId = config.credentialsId
    String registry      = config.registry ?: ''
    Map buildArgs         = config.buildArgs ?: [:]
    int retries           = config.retries != null ? config.retries as int : 2
    String cacheTag       = config.cacheTag ?: "${image}:cache"

    if (push && !credentialsId) {
        error("dockerBuildWithCache: 'credentialsId' is required when push=true")
    }

    echo "========== Docker Build =========="
    echo "Image      : ${image}:${tag}"
    echo "Dockerfile : ${dockerfile}"
    echo "Context    : ${context}"
    echo "Push       : ${push}"
    echo "Cache from : ${cacheTag}"
    echo "==================================="

    String buildArgsStr = buildArgs
        .collect { k, v -> "--build-arg ${k}=${v}" }
        .join(' ')

    try {
        withEnv(['DOCKER_BUILDKIT=1']) {
            sh(script: "docker build --cache-from ${cacheTag} --build-arg BUILDKIT_INLINE_CACHE=1 ${buildArgsStr} -t ${image}:${tag} -f ${dockerfile} ${context}")
        }

        if (push) {
            docker.withRegistry(
                registry ? "https://${registry}" : '',
                credentialsId
            ) {
                retry(retries) {
                    sh(script: "docker push ${image}:${tag}")
                }

                // Refresh the cache tag so the next build has something
                // to pull from.
                sh(script: "docker tag ${image}:${tag} ${cacheTag}")
                retry(retries) {
                    sh(script: "docker push ${cacheTag}")
                }
            }
        }

        echo "Docker build completed: ${image}:${tag}"

    } catch (Exception ex) {
        error("dockerBuildWithCache failed for ${image}:${tag} - ${ex.getMessage()}")
    }
}
