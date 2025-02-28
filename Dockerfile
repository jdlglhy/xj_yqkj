FROM openjdk:8

# 挂载目录
VOLUME /data/project
# 创建目录
RUN mkdir -p /data/project
# 指定路径
WORKDIR /data/project
# 复制jar文件和配置文件到路径
COPY ./jar/*.jar /data/project/joolun.jar
#COPY ./jar/application.yml /data/project/application.yml
#COPY ./jar/application-druid.yml /project/joolun/application-druid.yml
# 启动应用
ENTRYPOINT ["java","-jar","yqkj-admin.jar"]