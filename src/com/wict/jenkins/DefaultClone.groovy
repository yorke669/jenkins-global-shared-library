package com.wict.jenkins

class DefaultClone implements Serializable {
    def clone(jenkins, pipelineParameters, git_tag) {

        if ("master".equals(pipelineParameters.branch)) {
            /**
             * 这里后续打成正式版本
             * 解析package.json里面的版本号
             */
            if (pipelineParameters.dockerTag == null) {
                pipelineParameters.dockerTag = pipelineParameters.env + "-" + git_tag
            }
        } else {
            if (pipelineParameters.dockerTag == null) {
                pipelineParameters.dockerTag = pipelineParameters.env + "-" + git_tag
            }
        }

    }
}

