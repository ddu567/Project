# 기반 이미지 설정
FROM openjdk:21-jdk-slim
ARG JAR_FILE=target/*.jar
# 빌드된 JAR 파일을 컨테이너 내부의 /app 디렉토리로 복사
COPY ${JAR_FILE} gateway-service.jar
# 포트 설정
EXPOSE 8084
# 애플리케이션 실행 명령
ENTRYPOINT ["java", "-jar", "/gateway-service.jar"]






