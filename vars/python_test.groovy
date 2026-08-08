// Python Test using Jenkins Shared Library

def call(Map config = [:]) {

    def requirements = config.get('requirements', 'requirements.txt')
    def testCommand  = config.get('testCommand', 'pytest')
    def junitReport  = config.get('junitReport', 'report.xml')
    def coverage     = config.get('coverage', true)
    def coverageFile = config.get('coverageFile', 'coverage.xml')

    echo """
========================================
          Python Test
========================================
 Requirements : ${requirements}
 Test Command : ${testCommand}
 JUnit Report : ${junitReport}
 Coverage     : ${coverage}
 Coverage XML : ${coverageFile}
========================================
"""

    try {

        sh """
            pip install -r ${requirements}
        """

        if (coverage) {

            sh """
                ${testCommand} \
                --junitxml=${junitReport} \
                --cov=. \
                --cov-report=xml:${coverageFile}
            """

        } else {

            sh """
                ${testCommand} \
                --junitxml=${junitReport}
            """
        }

        echo "Python tests completed successfully."

    } catch (Exception ex) {

        error("Python tests failed: ${ex}")
    }
}