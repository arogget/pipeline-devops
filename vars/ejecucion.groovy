def call(){
    pipeline {
        agent any
        environment {
            NEXUS_USER         = credentials('useradminnexus')
            NEXUS_PASSWORD     = credentials('userpasswordnexus')
        }
        parameters {
            choice( name:'compileTool', choices: ['Maven', 'Gradle'], description: 'Seleccione herramienta de compilacion' )
            text description: 'enviar los stages separados por coma ";" vacio si necesita todos los stages', name: 'stages'
        }
        stages {
            stage("Pipeline"){
                steps {
                    script{
                    switch(params.compileTool)
                        {
                            case 'Maven':
                                maven.call(params.stages);
                                
                            break;
                            case 'Gradle':
                                gradle.call(params.stages)
                                
                            break;
                        }
                    }
                }
                post{
                    success{
                        slackSend color: 'good', message: "[Alex] [${JOB_NAME}] [${BUILD_TAG}] Ejecucion Exitosa", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'slacka'
                    }
                    failure{
                        slackSend color: 'danger', message: "[Alex] [${env.JOB_NAME}] [${BUILD_TAG}] Ejecucion fallida en stage [${env.TAREA}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'slacka'
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