package io.github.bokchidevchan.solid_ex.violation.dip

/**
 * ✅ DIP (Dependency Inversion Principle) 준수 예제
 *
 * DIP 원칙: 고수준 모듈은 저수준 모듈에 의존하면 안 된다. 둘 다 추상화에 의존해야 한다.
 *
 * 해결 방법: 인터페이스를 사이에 두고 의존
 * → 고수준(UserService) → 인터페이스(Database) ← 저수준(MySql, PostgreSql)
 */

// ========================================
// ✅ 1단계: 인터페이스(추상화) 정의
// ========================================

/**
 * ✅ 추상화: 데이터베이스 인터페이스
 * 고수준, 저수준 모듈 모두 이 인터페이스에 의존
 */
interface Database {
    fun save(data: String)
    fun load(): String
}

// ========================================
// ✅ 2단계: 저수준 모듈이 인터페이스 구현
// ========================================

/**
 * ✅ MySQL 구현
 */
class CorrectMySqlDatabase : Database {
    override fun save(data: String) = println("MySQL에 저장: $data")
    override fun load(): String = "MySQL 데이터"
}

/**
 * ✅ PostgreSQL 구현
 */
class CorrectPostgreSqlDatabase : Database {
    override fun save(data: String) = println("PostgreSQL에 저장: $data")
    override fun load(): String = "PostgreSQL 데이터"
}

/**
 * ✅ 테스트용 Mock 구현
 */
class MockDatabase : Database {
    var savedData: String? = null

    override fun save(data: String) {
        savedData = data  // 실제 DB 없이 저장
    }

    override fun load(): String = savedData ?: "Mock 데이터"
}

// ========================================
// ✅ 3단계: 고수준 모듈이 인터페이스에 의존
// ========================================

/**
 * ✅ DIP 준수: 인터페이스에 의존
 * 생성자로 Database를 주입받음
 */
class CorrectUserService(
    private val database: Database  // ✅ 인터페이스에 의존!
) {
    fun saveUser(name: String) {
        database.save(name)
    }

    fun getUser(): String {
        return database.load()
    }
}

// ========================================
// ✅ 장점: 유연한 교체와 쉬운 테스트
// ========================================

fun correctMain() {
    // ✅ MySQL 사용
    val mysqlService = CorrectUserService(CorrectMySqlDatabase())
    mysqlService.saveUser("홍길동")

    // ✅ PostgreSQL로 교체 - UserService 코드 수정 없음!
    val postgresService = CorrectUserService(CorrectPostgreSqlDatabase())
    postgresService.saveUser("김철수")

    // ✅ 테스트도 쉬움!
    testCorrectUserService()
}

/**
 * ✅ 테스트: Mock을 주입해서 빠르고 안정적인 테스트
 */
fun testCorrectUserService() {
    // ✅ Mock DB 주입
    val mockDb = MockDatabase()
    val service = CorrectUserService(mockDb)

    // 테스트 실행
    service.saveUser("테스트유저")

    // 검증
    assert(mockDb.savedData == "테스트유저")
    println("✅ 테스트 통과!")
}

/**
 * ✅ 의존성 방향 비교:
 *
 * ❌ 위반:
 * UserService → MySqlDatabase
 * (고수준이 저수준에 직접 의존)
 *
 * ✅ 준수:
 * UserService → Database(인터페이스) ← MySqlDatabase
 *               Database(인터페이스) ← PostgreSqlDatabase
 *               Database(인터페이스) ← MockDatabase
 * (둘 다 추상화에 의존, 의존성 역전!)
 */

/**
 * ✅ 장점 정리:
 *
 * 1. 유연성: DB 교체 시 UserService 수정 불필요
 * 2. 테스트 용이: Mock 주입으로 빠른 단위 테스트
 * 3. DI 프레임워크 호환: Hilt, Koin 등 사용 가능
 * 4. 낮은 결합도: 고수준이 저수준 변경에 영향 안 받음
 *
 * → 인터페이스에 의존 = DIP 준수
 */
