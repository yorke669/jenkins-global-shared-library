package com.wict.jenkins

class DefaultTools implements Serializable {
    def delete(jenkins, path, pipelineParameters) {
        jenkins.sh "ls -l ${path}"

        try {
           jenkins.sh "docker images  | grep 'dev-\\|test-\\|master-\\|<none>' | awk '{position=\$1\":\"\$2; print position}' | xargs docker rmi\n"
        } catch (Exception e) {
            jenkins.echo "发现异常：" + e.toString()
        }
    }
}

