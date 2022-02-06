def call(){
    pipeline {
        agent any
        environment {
            NEXUS_USER         = credentials('useradminnexus')
            NEXUS_PASSWORD     = credentials('userpasswordnexus')
        }
        parameters {
            choice(
                name:'compileTool',
                choices: ['Maven', 'Gradle'],
                description: 'Seleccione herramienta de compilacion'
            )
        }
        stages {
            stage("Pipeline"){
                steps {
                    script{
                    switch(params.compileTool)
                        {
                            case 'Maven':
                                def ejecucion = load 'maven.groovy'
                                ejecucion.call()
                            break;
                            case 'Gradle':
                                def ejecucion = load 'gradle.groovy'
                                ejecucion.call()
                            break;
                        }
                    }
                }
                post{
                    success{
                        slackSend color: 'good', message: "[Alex] [${JOB_NAME}] [${BUILD_TAG}] Ejecucion Exitosa", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
                    }
                    failure{
                        slackSend color: 'danger', message: "[Alex] [${env.JOB_NAME}] [${BUILD_TAG}] Ejecucion fallida en stage [${env.TAREA}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
                    }
                }
            }
        }
    }
}


def verifyBranchName(){
	if(env.GIT_BRANCH.contains('feature-') || env.GIT_BRANCH.contains('develop')) {
		return 'CI'
	} else {
		return 'CD'
	}
}

return this;