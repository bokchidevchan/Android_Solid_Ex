# Solid_Ex

SOLID 원칙 학습을 위한 Android 예제 프로젝트

## 소개

객체지향 설계의 5대 원칙인 SOLID를 실제 코드로 배울 수 있다. 각 원칙마다 **위반 예제**와 **준수 예제**를 제공하여 차이점을 명확하게 이해할 수 있다.

## SOLID 원칙

### 1. SRP (Single Responsibility Principle) - 단일 책임 원칙

> 클래스는 단 하나의 이유로만 변경되어야 한다.

| 파일 | 설명 |
|------|------|
| `SrpViolationExample.kt` | ViewModel이 너무 많은 책임을 가진 예제 (포맷팅, 수수료 계산, 통계 등) |
| `SrpCorrectExample.kt` | 각 책임을 별도 클래스로 분리한 예제 (FeeCalculator, PaymentFormatter 등) |

### 2. OCP (Open-Closed Principle) - 개방-폐쇄 원칙

> 확장에는 열려있고, 수정에는 닫혀있어야 한다.

| 파일 | 설명 |
|------|------|
| `OcpViolationExample.kt` | 새 등급 추가 시 기존 코드 수정이 필요한 when 분기문 |
| `OcpCorrectExample.kt` | DiscountPolicy 인터페이스로 새 등급 추가 시 기존 코드 수정 불필요 |

### 3. LSP (Liskov Substitution Principle) - 리스코프 치환 원칙

> 자식 클래스는 부모 클래스를 대체할 수 있어야 한다.

| 파일 | 설명 |
|------|------|
| `LspViolationExample.kt` | Bird를 상속한 Penguin이 fly()에서 예외를 던지는 문제 |
| `LspCorrectExample.kt` | Flyable, Swimmable 인터페이스로 능력을 분리한 설계 |

### 4. ISP (Interface Segregation Principle) - 인터페이스 분리 원칙

> 클라이언트가 사용하지 않는 인터페이스를 강제하지 말라.

| 파일 | 설명 |
|------|------|
| `IspViolationExample.kt` | 모든 기능을 강제하는 거대한 Machine 인터페이스 |
| `IspCorrectExample.kt` | Printer, Scanner, Fax, Copier로 분리된 작은 인터페이스들 |

### 5. DIP (Dependency Inversion Principle) - 의존성 역전 원칙

> 고수준 모듈은 저수준 모듈에 의존하면 안 된다. 둘 다 추상화에 의존해야 한다.

| 파일 | 설명 |
|------|------|
| `DipViolationExample.kt` | UserService가 MySqlDatabase를 직접 생성하는 문제 |
| `DipCorrectExample.kt` | Database 인터페이스를 통해 의존성 주입 |

## 프로젝트 구조

```
app/src/main/java/.../
├── violation/
│   ├── srp/           # SRP 예제
│   ├── ocp/           # OCP 예제
│   ├── lsp/           # LSP 예제
│   ├── isp/           # ISP 예제
│   └── dip/           # DIP 예제
├── domain/
│   ├── model/         # 도메인 모델
│   ├── repository/    # Repository 인터페이스
│   └── usecase/       # UseCase
└── data/
    ├── api/           # API 인터페이스
    ├── dto/           # DTO
    └── repository/    # Repository 구현체
```

## 기술 스택

- Kotlin
- Jetpack Compose
- Hilt (의존성 주입)
- Clean Architecture (data, domain, presentation 계층 분리)

## 실행 방법

```bash
./gradlew assembleDebug
```

## 학습 방법

1. 각 원칙의 **Violation 파일**을 먼저 읽고 문제점을 파악한다
2. **Correct 파일**을 읽고 어떻게 개선했는지 비교한다
3. 주석에 작성된 설명을 참고하여 원칙을 이해한다