# SPRING ADVANCED
Spring Boot 기반의 일정 관리 애플리케이션입니다.  
JWT 인증, 역할 기반 접근 제어, AOP 로깅 등을 포함한 백엔드 프로젝트입니다.

---

## 주요 기능

### 인증
- 회원가입 / 로그인 (JWT Bearer 토큰 발급)
- `JwtFilter`로 모든 요청 인증 처리

### 일정 관리
- 일정 생성, 단건 조회, 목록 조회(페이징), 수정

### 담당자(Manager)
- 일정 작성자가 다른 유저를 담당자로 등록 / 삭제
- 본인을 담당자로 등록 불가
- 일정 작성자 검증은 인터셉터(`TodoOwnerCheckInterceptor`)에서 처리

### 댓글
- 댓글 등록 / 목록 조회

### 관리자 기능
- `AdminCheckInterceptor` → `/admin/**` 경로는 ADMIN 역할만 접근 가능
- 유저 역할 변경 (`PATCH /admin/users/{userId}`)
- 댓글 강제 삭제 (`DELETE /admin/comments/{commentId}`)

### AOP 로깅
- `UserAdminService`, `CommentAdminController` 호출 시 요청 ID, 유저 ID, 시각, URL, Body 자동 로깅

---

## 패키지 구조

```
src/main/java/org/example/expert
├── client              # 외부 날씨 API
├── domain
│   ├── auth            # 회원가입 / 로그인
│   ├── comment         # 댓글
│   ├── manager         # 담당자
│   ├── todo            # 일정
│   └── user            # 유저 / 관리자
└── global
    ├── aop             # 로깅 AOP
    ├── config          # Filter, MVC, JPA 설정
    ├── exception       # 전역 예외 처리
    ├── interceptor     # 관리자 / 일정 소유자 검증
    ├── resolver        # AuthUser 아규먼트 리졸버
    ├── security        # JwtFilter, PasswordEncoder
    └── util            # JwtUtil
```

## 테스트

- **단위 테스트** : Mockito를 활용한 Service / Component 테스트
- **통합 테스트** : MockMvc를 활용한 Controller 전 계층 테스트

<img width="758" height="556" alt="스크린샷 2026-03-05 오후 3 15 34" src="https://github.com/user-attachments/assets/cf1b9401-a847-4a77-b779-17d64bab0bfb" />
<img width="758" height="173" alt="스크린샷 2026-03-05 오후 3 15 52" src="https://github.com/user-attachments/assets/2d496250-e8b1-442d-a9b1-ce27a2b101d9" />
