# 使用OpenJDK 8作为基础镜像
FROM openjdk:8-jre-alpine

# 设置工作目录
WORKDIR /app

# 设置环境变量
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE="dev"
ENV SERVER_PORT="8080"

# 复制应用jar文件
COPY target/saas-saler-admin-0.0.1-SNAPSHOT.jar /app/app.jar

# 暴露端口
EXPOSE ${SERVER_PORT}

# 启动应用
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dfile.encoding=UTF-8 -Dserver.port=${SERVER_PORT} -jar /app/app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
