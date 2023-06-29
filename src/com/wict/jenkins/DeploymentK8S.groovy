package com.wict.jenkins

class DeploymentK8S implements Serializable {
    def deployment(jenkins, pipelineParameters) {
        jenkins.echo "-----k8s deployment,  branch : ${pipelineParameters.branch} ,  env: ${pipelineParameters.env}   start ----- "

        if ("java".equals(pipelineParameters.buildType)) {
            jenkins.sh "python3 ${pipelineParameters.jenkins_workspace}@libs/global-shared-library/resources/python/kube_springboot_build.py ${pipelineParameters.wictDeployServerName} ${pipelineParameters.harborHost}/${pipelineParameters.harborWarehouse}/${pipelineParameters.wictDeployServerName}:${pipelineParameters.dockerTag} ${pipelineParameters.env} ${pipelineParameters.deployActuator} ${pipelineParameters.build_tag} ${pipelineParameters.project} '${pipelineParameters.javaOpts}' ${pipelineParameters.resourcesMaxMemory} "
            //jenkins.sh "cat k8s.yaml"
            //jenkins.kubernetesDeploy configs: "k8s.yaml", kubeconfigId: "${pipelineParameters.k8sKey}"

            jenkins.sh "docker run --rm -v /opt/k8s-deploy-config/${pipelineParameters.k8sKey}:/root/.kube/config  -v ${pipelineParameters.jenkins_workspace}/k8s.yaml:/opt/k8sdeploy/k8s.yaml harbor.cqxyy.net/wict/kubectl:1.2 sh -c 'cat k8s.yaml & kubectl apply -f k8s.yaml'"

        } else if ("web".equals(pipelineParameters.buildType) || "web-yarn".equals(pipelineParameters.buildType)) {
            jenkins.sh "python3 ${pipelineParameters.jenkins_workspace}@libs/global-shared-library/resources/python/kube_web_build.py ${pipelineParameters.wictDeployServerName} ${pipelineParameters.harborHost}/${pipelineParameters.harborWarehouse}/${pipelineParameters.wictDeployServerName}:${pipelineParameters.dockerTag} ${pipelineParameters.deployPort} ${pipelineParameters.env}  ${pipelineParameters.build_tag} ${pipelineParameters.project}"
            jenkins.sh "docker run --rm -v /opt/k8s-deploy-config/${pipelineParameters.k8sKey}:/root/.kube/config  -v ${pipelineParameters.jenkins_workspace}/k8s.yaml:/opt/k8sdeploy/k8s.yaml  harbor.cqxyy.net/wict/kubectl:1.2 sh -c 'cat k8s.yaml & kubectl apply -f k8s.yaml'"

        } else if ("python".equals(pipelineParameters.buildType)) {
            jenkins.sh "python3 ${pipelineParameters.jenkins_workspace}@libs/global-shared-library/resources/python/kube_python_build.py ${pipelineParameters.wictDeployServerName} ${pipelineParameters.harborHost}/${pipelineParameters.harborWarehouse}/${pipelineParameters.wictDeployServerName}:${pipelineParameters.dockerTag} ${pipelineParameters.deployPort} ${pipelineParameters.env}  ${pipelineParameters.build_tag} ${pipelineParameters.project} '${pipelineParameters.javaOpts}'"
            jenkins.sh "docker run --rm -v /opt/k8s-deploy-config/${pipelineParameters.k8sKey}:/root/.kube/config  -v ${pipelineParameters.jenkins_workspace}/k8s.yaml:/opt/k8sdeploy/k8s.yaml  harbor.cqxyy.net/wict/kubectl:1.2 sh -c 'cat k8s.yaml & kubectl apply -f k8s.yaml'"
        }
        jenkins.echo "-----k8s deployment,  branch : ${pipelineParameters.branch} ,  env: ${pipelineParameters.env}  end ----- "
    }
}

