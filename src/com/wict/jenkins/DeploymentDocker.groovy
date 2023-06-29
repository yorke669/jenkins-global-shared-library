package com.wict.jenkins

class DeploymentDocker implements Serializable {
    def deployment(jenkins, pipelineParameters) {
    
            jenkins.echo "-----docker deployment, branch : ${pipelineParameters.branch} , env: ${pipelineParameters.env}  start ----- "

            def remoteHostList = pipelineParameters.wictDeployHost.split(',');
            remoteHostList.each {
                def remote = [:]
                remote.name = pipelineParameters.wictDeployRoot
                remote.host = it
                remote.allowAnyHosts = true
                remote.user = pipelineParameters.wictDeployRoot
                remote.password = pipelineParameters.wictDeployPassword

                def deployCommand = pipelineParameters.wictDeployCommand + " ${pipelineParameters.harborHost}/${pipelineParameters.harborWarehouse}/${pipelineParameters.wictDeployServerName}:${pipelineParameters.dockerTag}"
                /**
                 * 需要 ssh pipline 插件支持
                 */
                jenkins.echo it
                jenkins.sshCommand remote: remote, command: deployCommand
            }

            jenkins.echo "-----docker deployment,  branch : ${pipelineParameters.branch} , env: ${pipelineParameters.env}  end ----- "
        
    }
}

