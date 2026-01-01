package io.github.bokchidevchan.solid_ex.violation.dip

/**
 * ❌ DIP (Dependency Inversion Principle) 위반 예제
 *
 * DIP 원칙: 고수준 모듈은 저수준 모듈에 의존하면 안 된다. 둘 다 추상화에 의존해야 한다.
 * → 구체 클래스가 아니라 인터페이스에 의존하라
 */

// ========================================
// ❌ 위반 예제: 구체 클래스에 직접 의존
// ========================================

/**
 * 저수준 모듈: MySQL 데이터베이스
 */
class MySqlDatabase {
    fun save(data: String) = println("MySQL에 저장: $data")
    fun load(): String = "MySQL 데이터"
}

/**
 * ❌ DIP 위반: 고수준 모듈이 저수준 모듈에 직접 의존
 */
class UserService {
    // ❌ 구체 클래스를 직접 생성!
    private val database = MySqlDatabase()

    fun saveUser(name: String) {
        database.save(name)
    }

    fun getUser(): String {
        return database.load()
    }
}

// ========================================
// ❌ 문제점: 변경과 테스트가 어려움
// ========================================

/**
 * ❌ 문제 1: PostgreSQL로 바꾸고 싶다면?
 * → UserService 코드를 수정해야 함!
 */
class PostgreSqlDatabase {
    fun save(data: String) = println("PostgreSQL에 저장: $data")
    fun load(): String = "PostgreSQL 데이터"
}

// UserService 내부의 MySqlDatabase를 PostgreSqlDatabase로 바꿔야 함
// → 고수준 모듈(UserService)이 저수준 모듈(Database) 변경에 영향받음!

/**
 * ❌ 문제 2: 테스트하고 싶다면?
 * → 실제 DB가 필요함! Mock 불가!
 */
fun testUserService() {
    val service = UserService()
    // 실제 MySQL 연결이 필요함
    // 테스트가 느리고 불안정
    // Mock DB로 교체할 방법이 없음!
}

/**
 * ❌ 문제점 정리:
 *
 * 1. UserService가 MySqlDatabase에 직접 의존
 * 2. DB를 바꾸려면 UserService 코드 수정 필요
 * 3. 테스트할 때 Mock DB를 주입할 수 없음
 *
 * → 고수준 모듈이 저수준 모듈에 의존 = DIP 위반
 */
