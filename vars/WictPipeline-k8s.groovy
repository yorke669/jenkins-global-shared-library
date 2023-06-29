//基于K8S的master-slave模式进行构建

def call(Map pipelineParameters) {
    log.info pipelineParameters
    def defaultTools = new com.wict.jenkins.DefaultTools()
    def readProperties = new com.wict.jenkins.ReadProperties()
    def defaultClone = new com.wict.jenkins.DefaultClone()
    def defaultBuild = new com.wict.jenkins.DefaultBuild()
    def dockerBuild = new com.wict.jenkins.DockerBuild()
    def deployment = new com.wict.jenkins.Deployment()
    pipelineParameters.branch = env.BRANCH_NAME
    pipelineParameters = readProperties.read(this, pipelineParameters)
    def label = "jnlp-slave"

    node(label) {
        try {
            container(label) {

                stage('Get a Maven project') {
                    echo "branch: ${env.BRANCH_NAME}"
                    echo "current SHA: ${env.GIT_COMMIT}"
                    echo "previous SHA: ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT}"
                    echo "${pipelineParameters}"
                }

//                stage('build user check ') {
//                    try {
//                        wrap([$class: 'BuildUser']) {
//                            echo "full name is $BUILD_USER"
//                            echo "user id is $BUILD_USER_ID"
//                            echo "user email is $BUILD_USER_EMAIL"
//                            script {
//                                pipelineParameters.trigger_user = "$BUILD_USER"
//                            }
//                        }
//
//                    } catch (e) {
//                        pipelineParameters.trigger_user = 'webhook'
//                    }
//
//                    echo "==================   trigger by  $pipelineParameters.trigger_user  ==================  "
//
//                    if (pipelineParameters.trigger_user != 'webhook') {
//                        script {
//                            pipelineParameters.isBuild = input message: '是否需要构建', ok: '确认',
//                                    parameters: [choice(choices:"true\nfalse\n",description:'是否重新构建',name:'isBuild')]
//                        }
//                        echo "pipelineParameters.isBuild : ${pipelineParameters.isBuild}"
//                    }
//                }


                stage('wait for exec check') {
                    echo '==================  customize test  ================'
                    //sh "env"
                    sh "kubectl get pod -n kube-wict -o wide"
                }

                stage('clone') {
                    git credentialsId: "${pipelineParameters.gitKey}", url: "${pipelineParameters.gitUrl}", branch: "${pipelineParameters.branch}"
                    sh "ls"
                    script {
                        git_tag = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                        pipelineParameters.build_tag = "${pipelineParameters.env}-${env.build_tag}-${git_tag}"
                        defaultClone.clone(this, pipelineParameters, git_tag);
                    }
                    echo "build_tag: ${pipelineParameters.build_tag}"
                    echo "dockerTag: ${pipelineParameters.dockerTag}"
                    sh "mkdir -p /tmp/jenkins/${pipelineParameters.build_tag}"
                    sh "cp -R /opt/share/python/* /tmp/jenkins/${pipelineParameters.build_tag}"
                }

                stage('build') {
                    script {
                        defaultBuild.build(this, pipelineParameters);
                    }
                }

                stage('docker') {
                    script {
                        dockerBuild.dockerBuild(this, pipelineParameters);
                    }
                }

                stage('deploy') {
                    script {
                        deployment.deployment(this, pipelineParameters);
                    }
                }
            }

        } catch (e) {
            echo "异常：" + e.toString()
            throw e
        } finally {
            echo "***************** finally *****************"
            script {
                defaultTools.delete(this, "/tmp/jenkins/${pipelineParameters.build_tag}", pipelineParameters);
            }
        }
    }


}

