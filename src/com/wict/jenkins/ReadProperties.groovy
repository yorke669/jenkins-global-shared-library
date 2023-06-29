package com.wict.jenkins

class ReadProperties implements Serializable {
    def read(jenkins, pipelineParameters) {

        if (pipelineParameters.env == null) {
            pipelineParameters.env = pipelineParameters.branch
        }


        // Load default properties
        Properties defaultProperties = readProperties(jenkins, "WictPipeline-default.properties")
        Properties projectProperties = readProperties(jenkins,
                pipelineParameters.wictProject + "/WictPipeline" + ".properties"
        )

        Properties projectBranchProperties = readProperties(jenkins,
                pipelineParameters.wictProject + "/WictPipeline-" + pipelineParameters.env + ".properties"
        )

        defaultProperties.putAll(projectProperties);
        if (projectBranchProperties != null) {
            defaultProperties.putAll(projectBranchProperties);
        }
        defaultProperties.putAll(pipelineParameters);
        createJavaOpts(defaultProperties);


        defaultProperties.harborWarehouse = defaultProperties.harborWarehouse + "-" + defaultProperties.env

        printParameters(jenkins, defaultProperties);

        return defaultProperties;
    }

    def createJavaOpts(defaultProperties) {

        if ("java".equals(defaultProperties.buildType) && !defaultProperties.javaOpts) {
            def javaOpts = "";
            if (defaultProperties.nacosUrl) {
                javaOpts = javaOpts + " -Dcas-wict-nacos-url=" + defaultProperties.nacosUrl;
                javaOpts = javaOpts + " -Dcas-wict-nacos-namespace=" + defaultProperties.nacosNamespace;
            }

            if (defaultProperties.sofaPort != null) {
                javaOpts = javaOpts + " -Dserver.port=" + defaultProperties.deployPort;
                javaOpts = javaOpts + " -Dcom.alipay.sofa.rpc.rest.port=" + defaultProperties.sofaPort;
            } else {
                javaOpts = javaOpts + " -Dserver.port=" + defaultProperties.deployPort;
                javaOpts = javaOpts + " -Dspring.cloud.nacos.discovery.metadata.zone=" + defaultProperties.env;
            }

            if ("true".equals(defaultProperties.swAgent)) {
                javaOpts = javaOpts + " -javaagent:/skywalking/agent/skywalking-agent.jar";
                javaOpts = javaOpts + " -DSW_AGENT_NAMESPACE=" + defaultProperties.project;
                javaOpts = javaOpts + " -DSW_AGENT_NAME=" + defaultProperties.wictDeployServerName;
                javaOpts = javaOpts + " -DSW_AGENT_COLLECTOR_BACKEND_SERVICES=" + defaultProperties.swUrl;
                if(!defaultProperties.resourcesMaxMemory) {
                    defaultProperties.resourcesMaxMemory='768Mi';
                }
            }

            defaultProperties.javaOpts = javaOpts;
        }
    }

    def readProperties(jenkins, String filePath) {
        try {
            Properties properties = new Properties()
            String propertiesString = jenkins.libraryResource(filePath);
            properties.load(new StringReader(propertiesString));
            return properties;
        } catch (Exception e) {
            jenkins.echo "readProperties error!:" + e.toString()
        }

        return null;
    }


    void echo(jenkins, message) {
        jenkins.echo message
    }

    void printParameters(jenkins, properties) {
        def parameters = "PipelineBuilder parameters:\n"
        properties.each {
            parameters += "  ${it.key}: ${it.value}\n"
        }
        echo(jenkins, parameters)
    }
}

