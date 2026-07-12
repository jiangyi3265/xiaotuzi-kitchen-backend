# ============================================================================
# 小兔子厨房后端生产镜像
# JAR 由 GitHub Actions 构建并验证，服务器只负责组装运行镜像。
# 构建：docker build -t xiaotuzi-backend:latest -f RuoYi-Vue/Dockerfile RuoYi-Vue
# ============================================================================

FROM eclipse-temurin:17-jre
LABEL maintainer="xiaotuzi-kitchen"
WORKDIR /app
# 上传目录（与 RUOYI_PROFILE 保持一致，可挂载卷持久化）
RUN mkdir -p /home/ruoyi/uploadPath
COPY deploy-artifact/app.jar app.jar
EXPOSE 8080
# 生产 profile：druid(数据源)+prod(安全收紧)。敏感项通过环境变量注入。
ENV JAVA_OPTS="-Xms512m -Xmx1024m" \
    SPRING_PROFILES_ACTIVE="druid,prod"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]
