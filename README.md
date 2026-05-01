# 🚚 배달 주문 관리 플랫폼

> 특정 지역 기반의 음식점 배달 주문, 결제 및 리뷰 관리 기능을 제공하는 **백엔드 플랫폼**입니다

<hr>

## ✨ 주요 특징

### 1. 권한별 특화 기능
* **4단계 권한 체계:** `MASTER`, `MANAGER`, `OWNER`, `CUSTOMER`로 세분화된 접근 제어를 수행합니다.
* **사용자별 인터페이스:** 메뉴 관리, 주문 상태 제어(5분 이내 취소 정책), 리뷰 작성 등 역할에 따른 맞춤형 기능을 제공합니다.

### 2. 데이터 무결성 및 조회 성능 최적화
* **Soft Delete:** 모든 엔티티에 `deleted_at` 필드를 적용하여 데이터를 물리적으로 삭제하지 않고 안전하게 보존합니다.
* **성능 최적화:** 리뷰 평점 컬럼의 반정규화 및 QueryDSL을 이용한 복합 검색 필터를 통해 트래픽 증가 시에도 안정적인 응답 속도를 보장합니다.

### 3. MSA 전환을 고려한 예비 설계
* **점진적 확장성:** 초기 관리 효율을 위해 **Monolithic Architecture**로 시작하되, 향후 서비스별 DB 분리가 용이하도록 설계되었습니다.
* **식별자 전략:** 유저를 제외한 모든 테이블에 **UUID PK**를 적용하여 마이크로서비스 전환 시 데이터 통합 및 마이그레이션 편의성을 확보했습니다.

### 4. 보안 체계
* **보안 강화:** Spring Security + JWT 환경에서 매 요청 시 DB 권한을 재검증하여 토큰 탈취 및 권한 변조 위험을 최소화합니다.

<hr>

## 🛠 주요 기능 상세

### 🔐 인증 및 보안
* **회원가입**: 서비스 이용을 위한 신규 사용자 계정 생성 기능
* **아이디 중복 확인**: 가입 시 실시간 중복 체크를 통해 데이터 충돌 및 중복 가입 방지
* **로그인**: 등록된 인증 정보를 통한 JWT 기반 서비스 접속 권한 획득
* **로그아웃**: 사용 중인 세션을 안전하게 종료하여 개인정보 및 계정 보안 유지

### 👤 사용자 관리
* **회원 정보 조회 및 수정**: 본인의 프로필(닉네임, 이메일 등) 확인 및 정보 최신화
* **비밀번호 변경**: 계정 보안 강화를 위한 주기적 또는 필요 시 암호 재설정
* **회원 탈퇴**: 사용자 요청 시 계정 삭제 처리 (이력 보존을 위한 Soft Delete 적용)
* **회원 목록 조회**: (관리자 전용) 플랫폼 내 전체 가입 사용자 현황 파악
* **사용자 권한 변경**: (관리자 전용) 역할(`CUSTOMER`, `OWNER`, `MANAGER`, `MASTER`) 조정 및 제어

### 📍 주소 및 지역 관리
* **주소 관리**: 배송지(집, 회사 등) 등록, 수정, 삭제 및 다중 주소 목록 관리
* **대표 주소 설정**: 주문 시 자동으로 선택될 메인 배송지 지정 기능
* **지역 관리**: (관리자 전용) 서비스 운영 지역(예: 광화문 등) 설정 및 배달 가능 구역 관리

### 🏪 가맹점 및 메뉴 운영
* **가게 관리**: 음식점 신규 등록, 매장 정보 수정, 상세 프로필 조회 및 운영 중단(폐점) 처리
* **메뉴 관리**: 가게별 판매 상품 구성, 가격 설정, 메뉴 정보 업데이트 및 품절/숨김 관리

### 📦 주문 관리
* **음식 주문**: 메뉴와 배송지를 결합하여 실시간 주문 데이터 생성
* **주문 내역 및 상세 조회**: 과거 주문 이력 확인 및 현재 진행 상태의 상세 정보 모니터링
* **주문 상태 관리**: (점주 전용) '접수 - 조리 중 - 배달 중 - 완료' 등 실시간 단계 전환
* **주문 취소**: 사용자 실수 및 가맹점 상황 고려, 주문 후 **5분 이내** 직접 취소 정책 적용
* **주문 수정 및 삭제**: (관리자 전용) 특이 상황 발생 시 주문 정보 강제 변경 및 정제

### 💳 결제 시스템
* **결제 처리**: 확정된 주문에 대한 카드 결제 승인 및 거래 내역 생성
* **결제 상태 및 내역 조회**: 결제 진행 단계 확인 및 과거 결제 증빙 기록 관리
* **결제 취소**: 주문 취소 시 연동된 결제 건의 환불 및 승인 무효화 처리

### ⭐ 리뷰 및 평점
* **리뷰 관리**: 주문 완료(`COMPLETED`) 건에 한해 실사용자 리뷰 작성, 수정 및 삭제
* **평점 등록**: 서비스 및 맛에 대한 점수(1~5점) 부여 및 가게별 평균 평점 자동 반영

### 💡 기타
* **운영 안정성**: 모든 기능은 사용자 권한(`Role`)에 따라 철저히 분리되어 내부 보안을 강화
* **데이터 보존**: 물리적 삭제 대신 **이력 보존(Soft Delete)** 방식을 채택하여 데이터 신뢰도를 확보
* **실무 지향 UX**: **5분 취소 제한** 및 **대표 주소 설정** 등 실제 배달 서비스의 사용자 경험을 적극 반영

<hr>

## 📂 개발 환경

| 항목 | 내용 |
| --- | --- |
| Java | 17 |
| Spring Boot | 3.5.13 |
| Build Tool | Gradle |
| Database | PostgreSQL 15.8 |
| ORM | Spring Data JPA |
| Security | Spring Security + JWT |
| Test DB | H2 |

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

## 🚀 서비스 구성 및 실행 방법

### 사전 요구사항

- Docker & Docker Compose 설치
- Java 17을 기준으로 합니다.

### 실행 방법

```bash
# 1. 프로젝트를 다운받습니다.
git clone https://github.com/SpartaTeam-9/spart-delivery.git
cd spart-delivery

# 2. env 파일에서 설정을 자신의 설정에 맞게 수정합니다.
- DB_URL
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET_KEY

# 3. Docker compose로 인프라를 실행합니다.
docker-compose up -d

# 4. 앱 빌드 및 실행
./gradlew clean build
./gradlew bootRun

# 5. 서비스 종료
docker-compose down
```

### 빠른 실행 방법

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

### 패키지 구조 작성 기준
큰 원칙은 **도메인별 패키지 분리 + 계층별 하위 패키지 분리**입니다.
*   **계층별 역할 분담**: 컨트롤러(Presentation), 서비스(Service), 엔티티(Domain)로 계층을 나누어 각자의 로직에만 집중하도록 설계하였습니다.
*   **DTO 중심의 데이터 교환**: 클라이언트의 요청(Request)과 서버의 응답(Response)을 DTO로 엄격히 분리하여 데이터의 독립성과 보안을 강화했습니다.
*   **전역 공통 설정**: 예외 처리(Exception), 보안(Security), 공통 코드(Global)를 별도로 관리하여 코드 중복을 최소화하고 시스템 일관성을 유지합니다.

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
주요 도메인은 다음과 같습니다.

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

<hr>

## 🗄️ ERD
- **UUID 기반 PK 적용**: 확장성과 보안을 위해 사용자 테이블을 제외한 전 테이블에 UUID를 기본키로 사용합니다.
- **물리적 FK 제약 제거**: 성능 최적화와 서비스 간 결합도를 낮추기 위해 물리적 외래키(FK)를 생성하지 않았습니다.
- **논리적 비식별 관계**: 테이블 간 관계는 물리적 제약 없이 로직 상에서만 연결되는 '논리적 관계'로 관리하여 유연성을 확보했습니다.
  <br><br>[ERD 다이어그램 보러가기](https://www.erdcloud.com/d/DowNmJGFxc5x9BERB)
<hr>


## 공통 클래스 사용법

아래 클래스들은 모든 팀원이 함께 사용하는 공통 기반 코드입니다.

### BaseEntity

위치: [BaseEntity.java](src/main/java/com/sparta/spartadelivery/global/entity/BaseEntity.java)

모든 엔티티가 공통으로 가져야 하는 생성/수정/삭제 정보를 관리합니다.

제공 필드:

- `createdAt`
- `createdBy`
- `updatedAt`
- `updatedBy`
- `deletedAt`
- `deletedBy`

엔티티를 만들 때는 아래처럼 상속하면 됩니다.

```java
@Entity
public class StoreEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
```

소프트 삭제가 필요할 때는 `markDeleted()`를 사용합니다.

```java
store.markDeleted(currentUsername);
```

삭제 여부 확인은 `isDeleted()`로 합니다.

```java
if (store.isDeleted()) {
        throw new AppException(ErrorCode.INVALID_REQUEST);
}
```

`createdBy`, `updatedBy`는 [JpaAuditingConfig.java](src/main/java/com/sparta/spartadelivery/global/infrastructure/config/JpaAuditingConfig.java)를 통해 현재 로그인한 사용자의 username 기준으로 기록됩니다. 인증 정보가 없는 시스템 작업은 `SYSTEM`으로 기록됩니다.

### ApiResponse

위치: [ApiResponse.java](src/main/java/com/sparta/spartadelivery/global/presentation/dto/ApiResponse.java)

모든 API 응답 형식을 통일하기 위한 DTO입니다.

성공 응답 예시:

```java
return ResponseEntity.ok(
        ApiResponse.success(200, "SUCCESS", response)
);
```

응답 형태:

```json
{
  "status": 200,
  "message": "SUCCESS",
  "data": {},
  "errors": null
}
```

실패 응답은 대부분 `GlobalExceptionHandler`가 처리하므로 컨트롤러에서 직접 만들 필요가 거의 없습니다.

### AppException

위치: [AppException.java](src/main/java/com/sparta/spartadelivery/global/exception/AppException.java)

서비스 로직에서 의도적으로 예외를 발생시킬 때 사용합니다.

```java
if (!store.isOwnedBy(user)) {
        throw new AppException(ErrorCode.ACCESS_DENIED);
}
```

기본 메시지 대신 상황별 메시지를 직접 주고 싶으면 아래처럼 사용할 수 있습니다.

```java
throw new AppException(ErrorCode.INVALID_REQUEST, "영업 중인 가게만 메뉴를 등록할 수 있습니다.");
```

### ErrorCode

위치: [ErrorCode.java](src/main/java/com/sparta/spartadelivery/global/exception/ErrorCode.java)

공통 에러의 HTTP 상태 코드와 메시지를 정의합니다.

새로운 도메인 에러가 필요하면 `ErrorCode`에 먼저 추가하고, 서비스에서는 `AppException`으로 던집니다.

```java
STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND")
```

```java
throw new AppException(ErrorCode.STORE_NOT_FOUND);
```

### GlobalExceptionHandler

위치: [GlobalExceptionHandler.java](src/main/java/com/sparta/spartadelivery/global/presentation/advice/GlobalExceptionHandler.java)

컨트롤러에서 발생한 예외를 공통 응답 형식으로 변환합니다.

처리 대상:

- `AppException`
- Request DTO validation 예외
- `AccessDeniedException`
- 예상하지 못한 `Exception`

DTO 검증 실패 응답 예시:

```json
{
  "status": 400,
  "message": "VALIDATION_ERROR",
  "data": null,
  "errors": [
    {
      "field": "username",
      "message": "사용자 ID는 4~10자여야 합니다."
    }
  ]
}
```

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

## Postman으로 인증 API 테스트하기

Auth API 테스트 컬렉션:

- [Auth API Postman 테스트 컬렉션](https://dark-crater-170331.postman.co/workspace/My-Workspace~39dbdca4-c1b8-41d9-8a19-bf957a106d89/folder/16128519-dac99157-eeec-4d8c-962d-6c7b53ed81b0?action=share&creator=16128519&ctx=documentation)

### 기본 테스트 순서

1. 회원가입 API를 호출합니다.
2. 로그인 API를 호출합니다.
3. 로그인 응답의 `accessToken` 값을 복사합니다.
4. Postman의 `Authorization` 탭으로 이동합니다.
5. `Auth Type`을 `Bearer Token`으로 선택합니다.
6. `Token` 필드에 복사한 access token을 붙여넣습니다.
7. 본인이 개발한 인증 필요 API를 호출합니다.

로그인 응답 예시:

```json
{
  "status": 200,
  "message": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "user01",
    "role": "CUSTOMER"
  },
  "errors": null
}
```

Postman에서 인증이 필요한 API를 직접 호출할 때는 아래 헤더가 들어가야 합니다.

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 현재 사용자 조회 테스트

로그인 후 받은 access token으로 아래 API를 호출하면 현재 로그인한 사용자 정보를 확인할 수 있습니다.

```http
GET /api/v1/auth/me
Authorization: Bearer {accessToken}
```

응답 예시:

```json
{
  "status": 200,
  "message": "SUCCESS",
  "data": {
    "id": 1,
    "username": "user01",
    "nickname": "유저01",
    "email": "user01@example.com",
    "role": "CUSTOMER"
  },
  "errors": null
}
```

이 API가 성공하면 `JwtAuthenticationFilter`가 토큰을 검증하고, `SecurityContext`에 `UserPrincipal`을 정상 저장했다는 뜻입니다.

## 👥 팀원 소개

<div align="center">

| 김유비 | 한혜수 | 이슬기 | 조아영 |
|:---:|:---:|:---:|:---:|
| [![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)](https://github.com/kimyubi) | [![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)](https://github.com/hyesuhan) | [![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)](https://github.com/skdev0619) | [![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)](https://github.com/look516) |
| 팀장<br/>사용자 · 인증<br/>가게 | 주문 · 결제<br/> 주소 <br/> CI/CD · 배포 | 지역<br/>리뷰 | 메뉴<br/>옵션 |

</div>





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
