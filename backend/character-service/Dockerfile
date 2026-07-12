FROM eclipse-temurin:17-jre
WORKDIR /app
# 在容器内创建角色头像的落盘夹子
RUN mkdir -p /app/uploads/characters

COPY character-service/target/character-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9003
ENTRYPOINT ["java", "-jar", "app.jar"]