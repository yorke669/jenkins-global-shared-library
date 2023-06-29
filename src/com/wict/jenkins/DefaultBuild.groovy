package com.wict.jenkins

class DefaultBuild implements Serializable {
    def build(jenkins, pipelineParameters) {

        if ("true".equals(pipelineParameters.isBuild)) {
            if ("java".equals(pipelineParameters.buildType)) {
                jenkins.sh "mvn ${pipelineParameters.wictBuildCommand}"
                jenkins.sh "pwd & ls -a"
            } else if ("web".equals(pipelineParameters.buildType)) {
                //jenkins.sh "${pipelineParameters.wictBuildCommand}"
                jenkins.sh "docker run  -v ${pipelineParameters.jenkins_workspace}:/opt/web  -w /opt/web --rm node:16.18.0 sh -c ' yarn -v && node -v && ${pipelineParameters.wictBuildCommand}'"
                jenkins.sh "ls -a"
            } else if ("web-yarn".equals(pipelineParameters.buildType)) {
                //jenkins.sh "yarn -v && node -v && ls && ${pipelineParameters.wictBuildCommand}"
                jenkins.sh "docker run  -v ${pipelineParameters.jenkins_workspace}:/opt/web  -w /opt/web --rm node:16.18.0 sh -c ' yarn -v && node -v && ${pipelineParameters.wictBuildCommand}'"
                jenkins.sh "ls -a"
            }else {
                jenkins.echo "build ignore pipelineParameters.buildType : ${pipelineParameters.buildType}"
            }
        } else {
            jenkins.echo "build ignore isBuild is not true"
            pipelineParameters.dockerBuild = false
        }

    }
}

