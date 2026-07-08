# ============================================================================
# 小兔子厨房 后端（RuoYi-Vue / Spring Boot）生产镜像
# 多阶段构建：Maven 编译打包 -> 精简 JRE 运行
# 构建：docker build -t xiaotuzi-backend:latest -f RuoYi-Vue/Dockerfile RuoYi-Vue
# ============================================================================

# ---- 构建阶段 ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
# 先拷 pom 利用依赖缓存
COPY pom.xml ./
COPY ruoyi-admin/pom.xml ruoyi-admin/
COPY ruoyi-common/pom.xml ruoyi-common/
COPY ruoyi-framework/pom.xml ruoyi-framework/
COPY ruoyi-system/pom.xml ruoyi-system/
COPY ruoyi-quartz/pom.xml ruoyi-quartz/
COPY ruoyi-generator/pom.xml ruoyi-generator/
RUN mvn -q -B dependency:go-offline || true
# 再拷源码打包
COPY . .
RUN mvn -q -B clean package -Dmaven.test.skip=true

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre
LABEL maintainer="xiaotuzi-kitchen"
WORKDIR /app
# 上传目录（与 RUOYI_PROFILE 保持一致，可挂载卷持久化）
RUN mkdir -p /home/ruoyi/uploadPath
COPY --from=build /build/ruoyi-admin/target/ruoyi-admin.jar app.jar
EXPOSE 8080
# 生产 profile：druid(数据源)+prod(安全收紧)。敏感项通过环境变量注入。
ENV JAVA_OPTS="-Xms512m -Xmx1024m" \
    SPRING_PROFILES_ACTIVE="druid,prod"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]
