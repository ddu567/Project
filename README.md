# 👛 예약 구매 프로젝트

### 🖥 프로젝트
- 소개: 예약 구매 기능이 있는 쇼핑몰 사이트입니다.
- 개발 기간: 2024년 4월 17일 ~ 5월 16일
- 개발 인원: 개인 프로젝트 (백엔드 1명)

### ⚙ 개발 환경

- 언어: Java(JDK 21.0.3)
- 프레임워크: Spring boot(3.2.5), JPA
- 빌드 도구: Gradle(8.7)
- DB: MySQL(8.0), Redis
- 서버: Eureka Server, API Gateway
- 라이브러리: lombok, Spring Cloud, Spring Security, OAuth2
- 기타: Docker, MSA

### 📌 주요 기능

- 이메일 인증 회원가입: Google SMTP와 Redis를 이용한 이메일 인증
- JWT 인증: 로그인 및 로그아웃 기능 구현
- 환경 구축: Docker를 이용한 환경 구축
- MSA 모듈화: API Gateway, Eureka Server, Feign Client 사용
- 재고 관리 및 예약 구매: Redis를 활용한 재고 관리, 예약 구매 및 결제 기능

### 🗺️ ERD
<img src="https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F97709cb5-46e9-448b-ad6e-4c22c8a31e96%2F5f15c7af-69e3-4dcc-bb26-7838cddedd76%2FdrawSQL-image-export-2024-05-16.png?table=block&id=5b19b059-bbb6-4189-846b-e524d5da2018&spaceId=97709cb5-46e9-448b-ad6e-4c22c8a31e96&width=2000&userId=2cdee0ef-ede8-4e55-90ce-85af03d7e14d&cache=v2" width="50%">

### 📃 API 명세
[API 명세 보기](https://documenter.getpostman.com/view/34409747/2sA3JT3ykB) 
