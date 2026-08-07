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
 Format     : ${format}
========================================
"""

    try {

        // Build the command as a list of tokens and join with a single
        // space at the end — avoids backslash/newline continuation bugs
        // entirely and guarantees the image is always the last argument.
        List<String> parts = [
            'trivy', 'image',
            '--severity', severity,
            '--exit-code', exitCode,
            '--format', format
        ]

        if (ignoreFile?.trim()) {
            parts << '--ignorefile' << ignoreFile
        }

        if (output?.trim()) {
            parts << '--output' << output
        }

        // Image must always be the last argument
        parts << "${image}:${tag}"

        String command = parts.join(' ')

        echo "Running command:"
        echo command

        sh command

        echo "Trivy scan completed successfully."

    } catch (Exception ex) {

        error("""
========================================
 Trivy Scan Failed
========================================
${ex.getMessage()}
========================================
""")

    }
}