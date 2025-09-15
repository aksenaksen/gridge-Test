# Instagram Clone API

Spring Boot로 구현한 Instagram 클론 프로젝트입니다.

## 🚀 주요 기능

### 인증 & 사용자 관리
- 회원가입 (필수 약관 동의)
- 로그인/로그아웃
- JWT 토큰 기반 인증
- OAuth2 로그인 (네이버)
- 비밀번호 변경
- 사용자 정지/탈퇴

### 게시물 관리
- 게시물 등록 (이미지 업로드 포함)
- 게시물 조회/목록 조회
- 게시물 수정/삭제
- 관리자 게시물 강제 삭제

### 좋아요 시스템
- 게시물 좋아요/취소
- 좋아요 수 조회

### 댓글 시스템
- 댓글 등록/수정/삭제
- 댓글 목록 조회 (페이지네이션)
- 대댓글 지원 (계층 구조)
- 관리자 댓글 차단

### 팔로우 시스템
- 사용자 팔로우/언팔로우
- 사용자 차단

### 신고 시스템
- 게시물/댓글 신고
- 관리자 신고 처리 (차단/거부)

### 피드 읽기
- 팔로우한 사용자들의 게시물 피드

## 🛠 기술 스택

- **Backend**: Spring Boot 3.5.5, Java 24
- **Database**: H2 (로컬), MySQL 8.0 (운영)
- **Cache**: Redis 7
- **Authentication**: JWT, Spring Security, OAuth2
- **File Storage**: AWS S3
- **Documentation**: Swagger/OpenAPI 3
- **Build**: Gradle
- **Containerization**: Docker, Docker Compose

## 📁 프로젝트 구조

```
src/main/java/com/example/instagram/
├── auth/                    # 인증 관련
│   ├── application/        # 서비스 로직
│   ├── domain/            # 도메인 모델
│   ├── filter/            # 필터
│   └── presentation/      # 컨트롤러
├── user/                   # 사용자 관리
├── feed/                   # 게시물 관리
├── comment/               # 댓글 관리
├── subscription/          # 팔로우 관리
├── report/                # 신고 관리
├── feed_read/             # 피드 읽기
└── common/                # 공통 설정
    ├── config/           # 설정 클래스
    └── exception/        # 예외 처리
```

## 🚀 시작하기

### 사전 요구사항

- Java 24+
- Docker & Docker Compose
- Gradle

### 로컬 환경 실행

1. **저장소 클론**
```bash
git clone <repository-url>
cd instagram
```

2. **의존성 설치 및 빌드**
```bash
./gradlew clean build -x test
```

3. **Docker 이미지 빌드**
```bash
docker build -t aksenaksen/instagram:local .
```

4. **서비스 실행**
```bash
docker-compose -f docker-compose-local.yml up -d
```

5. **애플리케이션 접속**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

### 기본 계정

- **관리자 계정**: `admin / admin`

## 📖 API 문서

### Swagger UI
애플리케이션 실행 후 http://localhost:8080/swagger-ui.html 에서 API 문서를 확인할 수 있습니다.

### Postman Collection
`src/main/resources/instagram-api-postman-collection.json` 파일을 Postman에 임포트하여 사용할 수 있습니다.

**환경 변수 설정:**
- `base_url`: http://localhost:8080/api
- `access_token`: 로그인 후 발급받은 JWT 토큰

## 🔧 환경 설정

### 로컬 개발 환경
- **Database**: H2 메모리 데이터베이스 -> 배포에서는 mysql
- **Cache**: Redis (Docker)
- **File Storage**: AWS S3

### 환경 변수
```yaml
# JWT 설정
JWT_SECRET_KEY: base64_encoded_secret_key

# AWS S3 설정
AWS_ACCESS_KEY: your_aws_access_key
AWS_SECRET_KEY: your_aws_secret_key

# OAuth2 설정 (네이버)
NAVER_CLIENT_ID: your_naver_client_id
NAVER_CLIENT_SECRET: your_naver_client_secret
```

## 🗄 데이터베이스 스키마

주요 테이블:
- `users`: 사용자 정보
- `user_agreements`: 사용자 약관 동의
- `feeds`: 게시물
- `feed_images`: 게시물 이미지
- `comments`: 댓글 (계층 구조)
- `feed_likes`: 게시물 좋아요
- `subscriptions`: 팔로우 관계
- `reports`: 신고

## 🔐 인증 시스템

### JWT 토큰
- **Access Token**: 1시간 유효, API 인증용
- **Refresh Token**: 24시간 유효, 토큰 갱신용

### 보안 기능
- CORS 설정
- CSRF 보호
- XSS 보호
- 요청 크기 제한

## 📱 주요 API 엔드포인트

### 인증
- `POST /api/auth/login` - 로그인
- `POST /api/auth/refresh` - 토큰 갱신

### 사용자
- `POST /api/users` - 회원가입
- `GET /api/users/{userId}` - 사용자 조회
- `PUT /api/users/password` - 비밀번호 변경

### 게시물
- `POST /api/feeds` - 게시물 등록
- `GET /api/feeds` - 게시물 목록
- `GET /api/feeds/{feedId}` - 게시물 상세
- `PUT /api/feeds/{feedId}` - 게시물 수정
- `PATCH /api/feeds/{feedId}` - 게시물 삭제

### 좋아요
- `POST /api/feeds/{feedId}/like` - 좋아요 등록
- `DELETE /api/feeds/{feedId}/like` - 좋아요 취소
- `GET /api/feeds/{feedId}/like-count` - 좋아요 수 조회

### 댓글
- `POST /api/comments` - 댓글 등록
- `GET /api/comments/feeds/{feedId}` - 댓글 목록
- `PATCH /api/comments/{commentId}` - 댓글 수정/삭제

## 🐳 Docker 배포

### 프로덕션 환경
```bash
# 이미지 빌드
docker build -t aksenaksen/instagram:latest .

# Docker Hub 업로드
docker push aksenaksen/instagram:latest

# 서비스 실행
docker-compose up -d
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다.

---

## 🌐 실제 서버 주소

**운영 서버**: 52.79.44.67:8080
