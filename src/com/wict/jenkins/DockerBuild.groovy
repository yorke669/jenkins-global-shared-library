package com.wict.jenkins

class DockerBuild implements Serializable {
    def dockerBuild(jenkins, pipelineParameters) {
        jenkins.echo " ------  docker build start ${pipelineParameters.dockerBuild}------ "
        if ("true".equals(pipelineParameters.dockerBuild)) {
            createBuildDocker(jenkins, pipelineParameters);
        } else {
            jenkins.echo 'dockerBuild is false , docker build ignore'
        }
    }


    def createBuildDocker(jenkins, pipelineParameters) {
        jenkins.echo " ------  docker build createBuildDocker start ------ "

        //jenkins.sh "docker login  ${pipelineParameters.harborHost}:${pipelineParameters.harborPort} -u ${pipelineParameters.harborUser} -p ${pipelineParameters.harborPassword} "

        if ("java".equals(pipelineParameters.buildType)) {
            if("tduck-platform-pro".equals(pipelineParameters.wictDeployServerName)){
                jenkins.sh "python3 ${pipelineParameters.jenkins_workspace}@libs/global-shared-library/resources/python/docker_springboot_build.tduck.platform.pro.py ${pipelineParameters.harborHost} ${pipelineParameters.harborPort} ${pipelineParameters.wictJarFile} ${pipelineParameters.wictDeployServerName}"
            } else if ("wihds-medical-data-sync".equals(pipelineParameters.wictDeployServerName)) {
                jenkins.sh "python3 ${pipelineParameters.jenkins_workspace}@libs/global-shared-library/resources/python/docker_springboot_build.medical.data.sync.py ${pipelineParameters.harborHost} ${pipelineParameters.harborPort} ${pipelineParameters.wictJarFile} ${pipelineParameters.wictDeployServerName}"
            } else if ("dpas-cloud-server".equals(pipelineParameters.wictDeployServerName)) {
                jenkins.sh "python3 ${pipelineParameters.jenkins_workspace}@libs/global-shared-library/resources/python/docker-springboot-build.dpas-cloud-server.py ${pipelineParameters.harborHost} ${pipelineParameters.harborPort} ${pipelineParameters.wictJarFile} ${pipelineParameters.wictDeployServerName}"
            } else{
                jenkins.sh "python3 ${pipelineParameters.jenkins_workspace}@libs/global-shared-library/resources/python/docker_springboot_build.py ${pipelineParameters.harborHost} ${pipelineParameters.harborPort} ${pipelineParameters.wictJarFile} ${pipelineParameters.wictDeployServerName}"
            }
            jenkins.sh "cat Dockerfile"
        } else if ("web".equals(pipelineParameters.buildType) || "web-yarn".equals(pipelineParameters.buildType)) {
            jenkins.sh "python3 ${pipelineParameters.jenkins_workspace}@libs/global-shared-library/resources/python/docker_web_build.py ${pipelineParameters.harborHost} ${pipelineParameters.harborPort} ${pipelineParameters.webBuildFile}"
            jenkins.sh "cat Dockerfile"
        }

        //docker login harbor.cqxyy.net -u admin -p Harbor12345
        jenkins.sh "docker build  -t ${pipelineParameters.harborHost}/${pipelineParameters.harborWarehouse}/${pipelineParameters.wictDeployServerName}:${pipelineParameters.dockerTag} ."
        jenkins.sh "docker push ${pipelineParameters.harborHost}/${pipelineParameters.harborWarehouse}/${pipelineParameters.wictDeployServerName}:${pipelineParameters.dockerTag}"
    }


}

