FROM eclipse-temurin:17-jre
WORKDIR /app
# 在容器内创建作业/作品图片的落盘夹子
RUN mkdir -p /app/uploads/works

# 复制打包好的 jar 进去
COPY work-service/target/work-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9002
ENTRYPOINT ["java", "-jar", "app.jar"]