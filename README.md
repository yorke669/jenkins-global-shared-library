# jenkins-global-shared-library
jenkins 自动构建，通过global-shared-library 模板，进行自动生成脚本完成构建和发布

包括：拉取代码、拉取参数、根据分支合并参数、编译、镜像打包、生成k8s yaml、发布
整体思路：研发人员，只需要关注代码，填写仓库类型（java、python、web等），具体镜像的打包方法和发布环境（K8S还是普通docker启动还是直接supervisor进程管理）都是有运维和经理决定

如下图，每个jenkins任务，填写仓库地址和global-shared-library地址

构建参加放到global-shared-librar中，结构如下：

- 仓库
  - resources 放置的项目参数，由运维维护
  - src/com/wict/jenkins	代码
  - vars	jenkins调用入口

<img width="970" alt="image" src="https://github.com/yorkexing/jenkins-global-shared-library/assets/15082551/d79b1275-f1ca-4676-b52b-849be65b0798">

<img width="1028" alt="image" src="https://github.com/yorkexing/jenkins-global-shared-library/assets/15082551/e37f7e40-8718-45c2-8e0e-03a4f4abeb06">




PS：groovy 不熟悉，jenkins也很不好调试，代码有大量优化空间
