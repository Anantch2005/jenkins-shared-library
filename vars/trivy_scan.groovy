// Trivy Image Scan using Jenkins Shared Library

def call(Map config = [:]) {

    def image      = config.image ?: error("image is required")
    def tag        = config.get('tag', 'latest')
    def severity   = config.get('severity', 'CRITICAL,HIGH')
    def exitCode   = config.get('exitCode', '0')
    def ignoreFile = config.get('ignoreFile', '')
    def format     = config.get('format', 'table')
    def output     = config.get('output', '')

    echo """
========================================
           Trivy Image Scan
========================================
 Image      : ${image}:${tag}
 Severity   : ${severity}
 Exit Code  : ${exitCode}
 Format      : ${format}
========================================
"""

    try {

        def command = """
        trivy image \
        --severity ${severity} \
        --exit-code ${exitCode} \
        --format ${format}
        """

        if (ignoreFile?.trim()) {
            command += " --ignorefile ${ignoreFile}"
        }

        if (output?.trim()) {
            command += " --output ${output}"
        }

        command += " ${image}:${tag}"

        sh command

        echo "Trivy scan completed successfully."

    }

    catch (Exception ex) {

        error("Trivy scan failed: ${ex}")

    }

}