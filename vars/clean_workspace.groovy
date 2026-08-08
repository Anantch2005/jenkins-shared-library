// Clean Jenkins Workspace using Shared Library

def call(Map config = [:]) {

    def deleteDirs = config.get('deleteDirs', true)
    def disableDeferredWipeout = config.get('disableDeferredWipeout', true)
    def notFailBuild = config.get('notFailBuild', true)

echo """
========================================
         Clean Workspace
========================================
 Delete Directories : ${deleteDirs}
 Deferred Wipeout   : ${disableDeferredWipeout}
 Fail Build         : ${!notFailBuild}
========================================

"""

    try {

        cleanWs(
            deleteDirs: deleteDirs,
            disableDeferredWipeout: disableDeferredWipeout,
            notFailBuild: notFailBuild
        )

        echo "Workspace cleaned successfully."

    } catch (Exception ex) {

        error("""
========================================
 Workspace Cleanup Failed
========================================
${ex.getMessage()}
========================================
""")

    }

}