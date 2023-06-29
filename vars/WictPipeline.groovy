#!groovy

def call(Map pipelineParameters) {
    log.info pipelineParameters
    // http://192.168.9.214:8080/env-vars.html/ jenkins 自带参数
    def defaultTools = new com.wict.jenkins.DefaultTools()
    def readProperties = new com.wict.jenkins.ReadProperties()
    def defaultClone = new com.wict.jenkins.DefaultClone()
    def defaultBuild = new com.wict.jenkins.DefaultBuild()
    def dockerBuild = new com.wict.jenkins.DockerBuild()
    def deployment = new com.wict.jenkins.Deployment()
    pipelineParameters.branch = env.BRANCH_NAME
    pipelineParameters.jenkins_home = env.JENKINS_HOME
    pipelineParameters = readProperties.read(this, pipelineParameters)

    pipeline {
        agent any

        //环境变量，初始确定后一般不需更改
        tools {
            maven "maven"
            nodejs "nodejs"
        }


        stages {
            stage('确认环境') {
               // when { branch 'master' }
                when {
                    expression {
                        return pipelineParameters.env == 'master';
                    }
                }
                steps {
                    script {
                        input(
                                message: '是否确认发布生产环境？',
                                ok:'确定'
//                                parameters: [
//                                        choice(choices:'dev\ntest\nprod', description:'发布到什么环境？', name:'ENV'),
//                                        string(defaultValue:'', description:'', name:'myparam')
//                                ],
//                                submitter:'admin,admin2,releaseGroup',
//                                submutterParameter:'APPROVER'
                        )
                    }
                    echo '确认环境完成'
                }
            }

            stage('代码获取') {
                steps {
                    sh "mvn -v"
                    sh "node -v"

                    git credentialsId: "${pipelineParameters.gitKey}", url: "${pipelineParameters.gitUrl}", branch: "${pipelineParameters.branch}"
                    sh "pwd"
                    sh "ls"
                    script {
                        git_tag = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                        jenkins_workspace = sh(returnStdout: true, script: 'pwd').trim()
                        pipelineParameters.build_tag = "${env.build_tag}_${pipelineParameters.env}_${git_tag}"
                        pipelineParameters.jenkins_workspace = "${jenkins_workspace}"
                        log.info pipelineParameters
                        defaultClone.clone(this, pipelineParameters, git_tag);
                    }
                    echo "build_tag: ${pipelineParameters.build_tag}"
                    echo "dockerTag: ${pipelineParameters.dockerTag}"

                    echo '代码获取完成'
                }
            }

            stage('编译') {
                steps {
                    script {
                        defaultBuild.build(this, pipelineParameters);
                    }
                    echo '代码获取完成'
                }
            }

            stage('镜像打包') {
                steps {
                    script {
                        dockerBuild.dockerBuild(this, pipelineParameters);
                    }
                    echo '镜像打包完成'
                }
            }

            stage('环境部署') {
                steps {
                    script {
                        deployment.deployment(this, pipelineParameters);
                    }
                    echo '环境部署完成'
                }
            }

            stage('清理环境') {
                steps {
                    script {
                        try {
                            sh "docker images  | grep 'dev-\\|test-\\|master-\\|none' | awk '{position=\$1\":\"\$2; print position}' | xargs docker rmi\n"
                        } catch (e) {
                            echo "异常：" + e.toString()
                        }
                    }
                    echo '清理环境完成'
                }
            }
        }

    }
}