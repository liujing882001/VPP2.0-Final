# VPP-es_schedule

## 部署流程

- 代码位置：`/projects/vpp/VPP-es_schedule`
- 代码更新（同时更新项目和配置）:

  ```
  sh update_codes.sh
  ```

- 构建镜像：
  在测试环境下构建镜像，同时镜像会保存在/projects/vpp/images/vpp-es_schedule-c1.tar

  ```
  sh build.sh
  ```

- 传输镜像（非 test 环境）
  到 images 路径下操作

  ```
  scp -r vpp-es_schedule-c1.tar user@ip:/dt_app/VPP/VPP-es_schedule
  ```

- 更新配置（非 test 环境）
  到 VPP-es_schedule 路径下操作

  更新对应环境的配置文件 env (test|production|china|demo)
  检查是否存在 ./config/vpp-es_schedule-cfgs/env
  **并替换成最新的配置文件！！**

- 启动容器
  根据环境启动 env (test|production|china|demo)

  ```
  sh run.sh env
  ```

- 挂载说明

  ./config/vpp-es_schedule-cfgs:挂载该环境的模型配置文件 **（重要！注意更新模型时请手动替换）**

  ./result:挂载模型运行结果

  ./logs:挂载容器日志

  - 日志位置：`/projects/vpp/VPP-es_schedule/logs`
  - 当天日志：`service`
  - 过往日志：`service.yyyy-MM-dd.log`
