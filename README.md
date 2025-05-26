# Product Microservice

> **목적** – OrderHub 플랫폼의 **상품 카탈로그**, **카테고리 계층**, **할인 정책**과 **주문 접수**를 관리하는 서비스입니다.
> REST API를 통해 상품·주문 데이터를 노출하고, 변경 사항은 **도메인 이벤트**(Kafka)로 퍼블리시하여 다른 마이크로서비스(재고, 결제, 배송, 미디어 저장 등)가 비동기적으로 반응할 수 있습니다.

---

## ✨ 핵심 기능

| 영역       | 기능                                                                                          |
| -------- | ------------------------------------------------------------------------------------------- |
| **상품**   | CRUD, **소프트 삭제**, JSONB 기반 **속성(attributes)**, 대표 **이미지 메타데이터** 관리                          |
| **카테고리** | 3‑레벨 트리 `MAJOR → MIDDLE → MINOR`, 계층 무결성 검증                                                 |
| **할인**   | ① *ProductDiscount* (단품·금액 한도)<br>② *BundleDiscount* (묶음 상품)<br>③ *OrderDiscount* (장바구니 전체) |
| **주문**   | 아이템 추가·수정·취소, 상태 롤‑업 `PENDING → PROCESSING → SHIPPED`                                       |
| **이벤트**  | `product‑updated`, `order‑created` 등 Kafka 토픽 발행 – 예: **ProductCreatedEvent** 가 이미지 서비스 트리거 |

---

## 🏗️ 기술 스택

| 레이어            | 기술                                                                        |
| -------------- | ------------------------------------------------------------------------- |
| Runtime        | **Java 21**, Spring Boot 3                                                |
| Persistence    | Spring Data JPA + Hibernate, **PostgreSQL** (JSONB via *hibernate‑types*) |
| Messaging      | **Apache Kafka** (spring‑kafka)                                           |
| Build / DevOps | Gradle 8, Docker & Docker Compose                                         |
| Testing        | JUnit 5, Testcontainers                                                   |

---

## ⚙️ 아키텍처 개요

```
┌────────────┐       REST/JSON        ┌───────────────┐
│   Client   │  ───────────────────▶ │ Product API   │
└────────────┘                       │  Controller   │
                                     └──────┬────────┘
                                            ▼
                                   ┌──────────────────┐
                                   │ Application      │
                                   │ Service Layer    │
                                   └──────┬───────────┘
                                            ▼
                                   ┌──────────────────┐
                                   │ Domain Model     │
                                   └──────┬───────────┘
   Kafka      ▲  product‑updated            ▼            ▲  DB
 Topics  ◀────┘                        ┌──────────────┐  └──▶ PostgreSQL
                                      │ Repository   │
                                      └──────────────┘
```

* HTTP, 비즈니스 로직, 영속성 계층을 **클린 레이어링**으로 분리.
* 도메인 엔티티가 불변식을 강제(예: `MINOR` 카테고리는 자식을 가질 수 없음).
* `@PrePersist` / `@PreUpdate` 로 **감사 필드**(`createdAt`, `updatedAt`) 자동 채움.

---

## 📚 도메인 클래스 다이어그램

```mermaid\ classDiagram

class Product {
  +Long id
  +String name
  +String price
  +SaleStatus saleStatus
  +ConditionStatus conditionStatus
  +Map attributes
  +Category category
  +ProductImage image
}
Product --> "1" Category
Product --> "0..1" ProductImage

class ProductImage {
  +String imageUrl
}

class Category {
  +Long id
  +String name
  +CategoryType type
  +Category parent
}
Category --> "*" Category : children

class Order {
  +Long id
  +OrderStatus status
  +List~OrderItem~ items
}
Order --> "*" OrderItem
OrderItem --> "1" Product

class OrderItem {
  +Long id
  +Order order
  +Product product
  +int quantity
  +Integer price
  +OrderItemStatus status
  +Instant createdAt
  +Instant updatedAt
}

class OrderItemStatus {
  +PENDING
  +PROCESSING
  +CANCELLED
}

class ProductDiscount {
  +DiscountType type
  +Integer discountValue
}
ProductDiscount --> "1" Product

class BundleDiscount {
  +discountValue
  +discountType
}
BundleDiscount --> "*" Product : bundleProducts
```

---

## 🔌 REST API 개요

> **Base path**  `/api`

| Method | Path                | 설명                               |
| ------ | ------------------- | -------------------------------- |
| GET    | `/products`         | 페이지네이션, 카테고리·상태·키워드 필터           |
| GET    | `/products/deleted` | 소프트 삭제 상품 목록                     |
| GET    | `/products/{id}`    | 상품 상세                            |
| POST   | `/products`         | 상품 생성 (multipart 이미지 업로드)        |
| PATCH  | `/products/{id}`    | 상품 정보 수정                         |
| DELETE | `/products/{id}`    | 상품 소프트 삭제 (`SaleStatus.DELETED`) |
| POST   | `/orders`           | 주문 생성(아이템 포함)                    |
| PATCH  | `/orders/{id}`      | 수량 변경 / 아이템 추가                   |
| DELETE | `/orders/{id}`      | 주문 취소                            |

컨트롤러 코드는 생략되었으며, 표준 Spring MVC 패턴을 따릅니다.

---

## 🚀 시작하기 (로컬)

### 1️⃣ Prerequisites

* **JDK 17+**
* **Docker & Docker Compose** (PostgreSQL, Kafka 구동용)

### 2️⃣ 클론 & 빌드

```bash
git clone https://github.com/orderhub/product-service.git
cd product-service
gradle clean build -x test  # 최초 실행 시 테스트 생략
```

### 3️⃣ 인프라 기동

```bash
docker compose up -d postgres kafka zookeeper
```

### 4️⃣ 서비스 실행

```bash
SPRING_PROFILES_ACTIVE=local \
DB_HOST=localhost DB_PORT=5432 DB_USER=orderhub DB_PASS=secret \
KAFKA_BOOTSTRAP=localhost:9092 \
java -jar build/libs/product-service-*.jar
```

`http://localhost:8080` 에서 서비스 이용 가능.

---

## 🛠️ 설정값

| 설정키                               | 기본값                                         | 설명                |
| --------------------------------- | ------------------------------------------- | ----------------- |
| `spring.datasource.url`           | `jdbc:postgresql://localhost:5432/orderhub` | Postgres JDBC URL |
| `spring.kafka.bootstrap-servers`  | `localhost:9092`                            | Kafka 브로커         |
| `orderhub.topics.product-updated` | `product-updated`                           | 상품 변경 이벤트 토픽      |
| `orderhub.topics.order-created`   | `order-created`                             | 주문 생성 이벤트 토픽      |

민감 정보는 **환경 변수**(`DB_PASS` 등)로 오버라이드 가능합니다.

---

## 🧪 테스트

```bash
gradle test                             # 단위 테스트
TESTCONTAINERS=true gradle integrationTest  # Postgres + Kafka 컨테이너로 통합 테스트
```

---

## 📦 배포

컨테이너 이미지는 **Jib** 로 빌드됩니다.

```bash
gradle jibDockerBuild
```

레지스트리 푸시 후 `deploy/` Helm 차트로 쿠버네티스에 배포하세요.

---

## 📝 기여 가이드

1. 포크 & 브랜치 생성 `feat/<name>`
2. **Conventional Commits** 규칙으로 커밋
3. PR 생성 – **이유**와 **내용**을 상세히 작성
4. CI ✅ & 리뷰를 기다려주세요

---

## © 2025 OrderHub
