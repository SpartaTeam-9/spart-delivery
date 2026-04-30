# Sparta Delivery 개발 가이드

## 팀원 소개
김유비(팀장) : User, Auth, Store, Area 담당
한혜수 : Order, Payment, Deliver, CICD 담당
이슬기 : Review 담당
조아영 : Menu 담당

## 개발 환경

| 항목 | 내용 |
| --- | --- |
| Java | 17 |
| Spring Boot | 3.5.13 |
| Build Tool | Gradle |
| Database | PostgreSQL 15.8 |
| ORM | Spring Data JPA |
| Security | Spring Security + JWT |
| Test DB | H2 |

## 실행 방법

1. Docker Desktop을 실행합니다.
2. IntelliJ IDEA에서 프로젝트를 엽니다.
3. `SpartaDeliveryApplication` 실행 버튼을 누릅니다.
4. Spring Boot가 `compose.yaml`을 감지하고 PostgreSQL 컨테이너를 함께 실행합니다.

기본 DB 설정은 [application.yaml](src/main/resources/application.yaml)에 정의되어 있습니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:delivery}
    username: ${DB_USERNAME:delivery_user}
    password: ${DB_PASSWORD:delivery_password}
```

별도 환경변수를 설정하지 않으면 로컬 기본값으로 실행됩니다.



이 문서는 프로젝트 소개보다 **팀원들이 스켈레톤 코드를 이어받아 각자 담당 기능을 바로 개발할 수 있게 돕는 것**을 목적으로 합니다.

- 로컬 실행 방법
- 패키지 구조 작성 기준
- 공통 응답/예외/엔티티 사용법
- 인증 기능 사용 방법
- Postman으로 JWT 인증 API 테스트하는 방법

## 참고 문서

- [팀 개발 환경 및 실행 가이드 Notion](https://aboard-woolen-7bf.notion.site/3449192e21f2808a98b0d839dd2b358d?source=copy_link)
- [Auth API Postman 테스트 컬렉션](https://dark-crater-170331.postman.co/workspace/My-Workspace~39dbdca4-c1b8-41d9-8a19-bf957a106d89/folder/16128519-dac99157-eeec-4d8c-962d-6c7b53ed81b0?action=share&creator=16128519&ctx=documentation)


```gradle
developmentOnly 'org.springframework.boot:spring-boot-docker-compose'
```

그래서 IntelliJ IDEA에서 애플리케이션 실행 버튼을 누르면 `compose.yaml`에 정의된 PostgreSQL 컨테이너가 함께 실행됩니다. 별도로 Docker 명령어를 입력하거나 DB를 직접 띄우는 번거로운 작업 없이 바로 애플리케이션을 실행할 수 있습니다.


### 주요 의존성과 사용 이유

| 의존성 | 사용 이유 |
| --- | --- |
| `spring-boot-starter-web` | REST API 개발 |
| `spring-boot-starter-data-jpa` | JPA 기반 엔티티, Repository 개발 |
| `postgresql` | 로컬/운영 DB 연결 |
| `spring-boot-starter-security` | 인증/인가 처리 |
| `spring-boot-starter-validation` | Request DTO 입력값 검증 |
| `jjwt-api`, `jjwt-impl`, `jjwt-jackson` | JWT access token 생성 및 검증 |
| `lombok` | 생성자, Getter, Builder 등 반복 코드 감소 |
| `spring-boot-docker-compose` | IntelliJ 실행 시 Docker Compose 기반 PostgreSQL 자동 실행 |
| `h2` | 테스트에서 외부 PostgreSQL에 의존하지 않기 위한 인메모리 DB |
| `spring-security-test` | Security 관련 테스트 지원 |
| `spring-boot-configuration-processor` | `@ConfigurationProperties` 메타데이터 생성 |


## 패키지 구조 작성 기준

큰 원칙은 **도메인별 패키지 분리 + 계층별 하위 패키지 분리**입니다.

현재 구조:

```text
com.sparta.spartadelivery
├── auth
│   ├── application
│   │   └── service
│   └── presentation
│       ├── controller
│       └── dto
│           ├── request
│           └── response
├── user
│   └── domain
│       ├── entity
│       └── repository
└── global
    ├── entity
    ├── exception
    ├── infrastructure
    │   └── config
    │       └── security
    └── presentation
        ├── advice
        ├── controller
        └── dto
```

팀원들이 새 도메인을 만들 때는 아래 구조를 기본으로 사용하면 됩니다.

```text
도메인명
├── application
│   └── service
├── domain
│   ├── entity
│   └── repository
└── presentation
    ├── controller
    └── dto
        ├── request
        └── response
```


SA 문서 기준 주요 도메인은 다음과 같습니다.

| 도메인 | 역할 |
| --- | --- |
| `user` | 사용자, 권한, 사용자 관리 |
| `auth` | 회원가입, 로그인, JWT 발급 |
| `address` | 사용자 배송지 관리 |
| `area` | 지역 관리 |
| `category` | 가게 카테고리 관리 |
| `store` | 가게 등록/수정/조회 |
| `menu` | 가게별 메뉴 관리, AI 설명 생성 연동 |
| `order` | 주문 생성, 주문 상태 관리 |
| `review` | 주문 완료 후 리뷰 및 평점 |
| `payment` | 주문 결제 정보 |
| `ai` | Gemini API 요청 및 응답 로그 관리 |

## 공통 클래스

### BaseEntity
모든 엔티티가 공통으로 가져야 하는 생성/수정/삭제 정보를 관리합니다.

### ApiResponse
모든 API 응답 형식을 통일하기 위한 DTO입니다.

### AppException
서비스 로직에서 의도적으로 예외를 발생시킬 때 사용합니다.

### ErrorCode
공통 에러의 HTTP 상태 코드와 메시지를 정의합니다.

### GlobalExceptionHandler

컨트롤러에서 발생한 예외를 공통 응답 형식으로 변환합니다.

## 인증 기능 인수인계

현재 구현된 인증 기능은 다음 흐름으로 동작합니다.

1. 회원가입 시 비밀번호를 BCrypt로 암호화해 저장합니다.
2. 로그인 성공 시 JWT access token을 발급합니다.
3. 인증이 필요한 API는 `Authorization: Bearer {token}` 헤더를 사용합니다.
4. 매 요청마다 JWT payload의 사용자 정보와 DB의 현재 사용자 정보를 다시 비교합니다.
5. 사용자가 삭제되었거나 role/username이 바뀌면 기존 토큰은 더 이상 신뢰하지 않습니다.

관련 클래스:

| 클래스 | 역할 |
| --- | --- |
| `AuthController` | 회원가입, 로그인, 현재 사용자 조회 API |
| `AuthService` | 회원가입/로그인 비즈니스 로직 |
| `JwtTokenProvider` | JWT 생성 및 payload 추출 |
| `JwtAuthenticationFilter` | 요청마다 Bearer 토큰 검증 |
| `UserPrincipal` | Spring Security에서 사용할 현재 사용자 정보 |
| `CustomUserDetailsService` | 로그인 시 email 기준 사용자 조회 |
| `SecurityConfig` | Security 필터 체인과 접근 정책 설정 |
| `RestAuthenticationEntryPoint` | 인증 실패 JSON 응답 처리 |
| `RestAccessDeniedHandler` | 인가 실패 JSON 응답 처리 |
| `JsonSecurityErrorResponder` | Security 예외를 `ApiResponse` 형식으로 변환 |

현재 로그인한 사용자 정보는 컨트롤러에서 아래처럼 꺼낼 수 있습니다.

```java
@GetMapping("/me")
public ResponseEntity<ApiResponse<Map<String, Object>>> me(
        @AuthenticationPrincipal UserPrincipal userPrincipal
) {
    ...
}
```

팀원들이 본인 API에서 현재 사용자 정보가 필요하면 같은 방식으로 `UserPrincipal`을 주입받으면 됩니다.

```java
@PostMapping("/stores")
public ResponseEntity<ApiResponse<ResStoreDto>> createStore(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        @Valid @RequestBody ReqCreateStoreDto request
) {
    Long currentUserId = userPrincipal.getId();
    String username = userPrincipal.getAccountName();
    ...
}

```


## 테스트 실행

전체 테스트:

```powershell
.\gradlew.bat test
```

특정 테스트만 실행:

```powershell
.\gradlew.bat test --tests com.sparta.spartadelivery.auth.*
```

```powershell
.\gradlew.bat test --tests com.sparta.spartadelivery.global.infrastructure.config.security.*
```

## 개발 시 약속

- 새 도메인은 SA 문서의 패키지 구조를 따릅니다.
- 컨트롤러 응답은 `ApiResponse`로 감쌉니다.
- 서비스에서 의도된 예외는 `AppException(ErrorCode...)`로 던집니다.
- Request DTO에는 `jakarta.validation` 어노테이션을 붙이고 메시지를 명확히 작성합니다.
- 엔티티는 필요한 경우 `BaseEntity`를 상속합니다.
- 삭제 처리는 가능하면 하드 삭제보다 `deletedAt`, `deletedBy` 기반 소프트 삭제를 우선 고려합니다.
- 인증이 필요한 API에서는 `@AuthenticationPrincipal UserPrincipal`로 현재 사용자를 꺼냅니다.
- 팀원이 추가한 핵심 비즈니스 로직은 의미 있는 테스트를 함께 작성합니다.
