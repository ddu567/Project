# 기반 이미지 설정
FROM openjdk:21-ea-oracle
# 작업 디렉토리 설정
WORKDIR /app
# 빌드된 JAR 파일을 컨테이너 내부의 /app 디렉토리로 복사
COPY build/libs/project-0.0.1-SNAPSHOT.jar /app/app.jar
# 애플리케이션 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
