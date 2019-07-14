## 用户PDF文件导出
##### 系统接口层级
* gradle gradle配置
* k8s 打包和部署k8s目录
* k8s.lib_image Docker打包运行环境镜像，使用JDK8
* src.main.java springboot
* src.resource springboot和jasper配置
* tmp 导出文件缓存目录

##### Jasper
```
工具 https://community.jaspersoft.com/project/jaspersoft-studio
```

##### 项目配置打包和启动
```sh
gradle 
gradle build
gradle bootRun
```

##### 项目部署
```sh
gradle build
cd k8s
./build_linux.sh 
```


