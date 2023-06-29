package com.wict.jenkins

class Deployment implements Serializable {
    def deployment(jenkins, pipelineParameters) {
        String deployType = pipelineParameters.wictDeployType
        if ("ssh".equals(deployType)) {
            DeploymentSSH deploymentSSH = new DeploymentSSH();
            deploymentSSH.deployment(jenkins, pipelineParameters);
        } else if ("k8s".equals(deployType)) {
            DeploymentK8S deploymentK8S = new DeploymentK8S();
            deploymentK8S.deployment(jenkins, pipelineParameters);
        } else if ("docker".equals(deployType)) {
            DeploymentDocker deploymentDocker = new DeploymentDocker();
            deploymentDocker.deployment(jenkins, pipelineParameters);
        } else {
            jenkins.echo "wictDeployType is ${pipelineParameters.wictDeployType} , deployment ignore"
        }
        //jenkins.sh "whoami"
        //jenkins.sh "chmod 777 -R /tmp/jenkins/${pipelineParameters.build_tag}"
    }
}

