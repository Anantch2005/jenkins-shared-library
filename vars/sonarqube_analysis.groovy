// SonarQube Analysis using Jenkins Shared Library
def call(Map config = [:]) {

    def server  = config.get('server', 'SonarQube')
    def scanner = config.get('scanner', 'sonar-scanner')
    def options = config.get('options', '')

    echo """
========================================
        SonarQube Analysis
========================================
 Server  : ${server}
 Scanner : ${scanner}
========================================
"""

    try {

        withSonarQubeEnv(server) {

            sh """
            ${scanner} ${options}
            """

        }

        echo "SonarQube analysis completed successfully."

    } catch (Exception ex) {

        error("SonarQube analysis failed: ${ex}")

    }

}