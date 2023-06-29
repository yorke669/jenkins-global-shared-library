package com.wict.jenkins

class DeploymentSSH implements Serializable {
    def deployment(jenkins, pipelineParameters) {
        if (pipelineParameters.env == 'master') {
            jenkins.echo 'env is master deployment ssh ignore'
        } else {
            jenkins.echo "-----ssh deployment, branch : ${pipelineParameters.branch} , env: ${pipelineParameters.env}  start ----- "

            def remoteHostList = pipelineParameters.wictDeployHost.split(',');
            remoteHostList.each {
                def remote = [:]
                remote.name = pipelineParameters.wictDeployRoot
                remote.host = it
                remote.allowAnyHosts = true
                remote.user = pipelineParameters.wictDeployRoot
                remote.password = pipelineParameters.wictDeployPassword

                /**
                 * 需要 SSH Pipeline Steps插件支持
                 */
                jenkins.echo it
                jenkins.sshPut remote: remote, from: pipelineParameters.wictJarFile, into: pipelineParameters.wictDeployPath
                jenkins.sshCommand remote: remote, command: pipelineParameters.wictDeployCommand
            }

            jenkins.echo "-----ssh deployment,  branch : ${pipelineParameters.branch} , env: ${pipelineParameters.env}  end ----- "
        }
    }
}

