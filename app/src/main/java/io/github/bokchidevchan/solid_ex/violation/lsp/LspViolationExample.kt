package io.github.bokchidevchan.solid_ex.violation.lsp

/**
 * ❌ LSP (Liskov Substitution Principle) 위반 예제
 *
 * LSP 원칙: 자식 클래스는 부모 타입으로 대체 가능해야 한다.
 * 즉, 부모 타입을 사용하는 코드에서 자식 타입을 넣어도 동일하게 동작해야 함.
 *
 * 이 예제들은 자식 클래스가 부모의 계약(contract)을 위반합니다:
 * 1. 부모가 던지지 않는 예외를 던짐
 * 2. 부모의 반환값 의미를 변경함
 * 3. 부모의 전제조건(precondition)을 강화함
 * 4. 부모의 불변성(invariant)을 위반함
 */

// ========================================
// 예제 1: 결제 수단 계층 구조
// ========================================

/**
 * 부모 클래스: 모든 결제 수단의 기본 동작 정의
 */
open class PaymentMethod(
    val name: String,
    protected open val balance: Int = Int.MAX_VALUE
) {
    /**
     * 수수료 계산
     * 계약: 항상 0 이상의 정수를 반환 (예외 없음)
     */
    open fun calculateFee(amount: Int): Int {
        require(amount >= 0) { "금액은 0 이상이어야 합니다" }
        return (amount * 0.01).toInt()  // 기본 1% 수수료
    }

    /**
     * 결제 가능 여부 확인
     * 계약: amount만 확인하면 됨, 항상 Boolean 반환
     */
    open fun canProcess(amount: Int): Boolean {
        return amount > 0 && amount <= balance
    }

    /**
     * 결제 처리
     * 계약: canProcess가 true면 결제 성공, 부수효과 없음
     */
    open fun process(amount: Int): PaymentResult {
        if (!canProcess(amount)) {
            return PaymentResult.Failed("결제 불가")
        }
        return PaymentResult.Success(transactionId = "TXN_${System.currentTimeMillis()}")
    }

    /**
     * 환불 처리
     * 계약: 항상 환불 가능, Boolean 반환
     */
    open fun refund(transactionId: String): Boolean {
        return true
    }
}

/**
 * ❌ LSP 위반: 상품권 결제
 * 부모의 계약을 여러 곳에서 위반
 */
class GiftCardPayment(
    name: String,
    override val balance: Int,
    private val expiryTimestamp: Long
) : PaymentMethod(name, balance) {

    /**
     * ❌ LSP 위반 1: 예외를 던짐
     * 부모는 예외를 던지지 않지만, 자식이 예외를 던짐
     * 부모 타입으로 사용하는 코드에서 예상치 못한 크래시 발생!
     */
    override fun calculateFee(amount: Int): Int {
        if (isExpired()) {
            // ❌ 부모는 예외를 던지지 않는데 자식이 던짐!
            throw IllegalStateException("만료된 상품권입니다")
        }
        return 0  // 상품권은 수수료 없음
    }

    /**
     * ❌ LSP 위반 2: 전제조건 강화
     * 부모는 amount만 확인하지만, 자식은 만료일도 확인
     * 부모 타입으로 사용할 때 예상과 다르게 동작!
     */
    override fun canProcess(amount: Int): Boolean {
        // ❌ 부모보다 더 엄격한 조건 추가!
        if (isExpired()) {
            return false
        }
        return amount > 0 && amount <= balance
    }

    /**
     * ❌ LSP 위반 3: 부수효과 추가
     * 부모의 process()는 부수효과가 없지만,
     * 자식은 내부 상태를 변경함 (잔액 차감)
     */
    private var remainingBalance: Int = balance

    override fun process(amount: Int): PaymentResult {
        if (!canProcess(amount)) {
            return PaymentResult.Failed("결제 불가")
        }
        // ❌ 부모에 없는 부수효과!
        remainingBalance -= amount
        return PaymentResult.Success(transactionId = "GIFT_${System.currentTimeMillis()}")
    }

    /**
     * ❌ LSP 위반 4: 반환 의미 변경
     * 부모는 항상 환불 가능하지만, 자식은 조건부로 false 반환
     * 부모를 신뢰하고 작성된 코드가 깨짐!
     */
    override fun refund(transactionId: String): Boolean {
        // ❌ 부모는 항상 true인데 자식은 false를 반환할 수 있음!
        if (isExpired()) {
            return false  // 만료된 상품권은 환불 불가
        }
        return true
    }

    private fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiryTimestamp
    }
}

/**
 * ❌ LSP 위반: 무이자 할부 카드
 * 부모와 다른 방식으로 동작
 */
class InterestFreeCard(
    name: String,
    private val installmentMonths: Int
) : PaymentMethod(name) {

    /**
     * ❌ LSP 위반: 반환값의 의미가 다름
     * 부모의 calculateFee()는 "수수료"를 반환하지만,
     * 이 클래스는 "할부 수수료 할인액"을 반환 (음수 가능)
     */
    override fun calculateFee(amount: Int): Int {
        // ❌ 부모는 양수만 반환하는데 자식은 음수 반환!
        // 무이자 할부 혜택으로 수수료 할인
        return -(amount * 0.02).toInt()  // 2% 할인
    }
}

sealed class PaymentResult {
    data class Success(val transactionId: String) : PaymentResult()
    data class Failed(val reason: String) : PaymentResult()
}

// ========================================
// 예제 2: 저장소 계층 구조
// ========================================

/**
 * 부모 인터페이스: 읽기/쓰기 저장소
 */
interface Repository<T> {
    fun save(item: T): Boolean
    fun findById(id: String): T?
    fun findAll(): List<T>
    fun delete(id: String): Boolean
}

data class User(val id: String, val name: String)

/**
 * 기본 구현: 메모리 저장소
 */
open class InMemoryUserRepository : Repository<User> {
    protected val storage = mutableMapOf<String, User>()

    override fun save(item: User): Boolean {
        storage[item.id] = item
        return true
    }

    override fun findById(id: String): User? = storage[id]

    override fun findAll(): List<User> = storage.values.toList()

    override fun delete(id: String): Boolean {
        return storage.remove(id) != null
    }
}

/**
 * ❌ LSP 위반: 읽기 전용 저장소
 * Repository 인터페이스를 구현하지만 쓰기 작업을 거부
 */
class ReadOnlyUserRepository(
    initialData: List<User>
) : InMemoryUserRepository() {

    init {
        initialData.forEach { storage[it.id] = it }
    }

    /**
     * ❌ LSP 위반: 부모가 지원하는 기능을 자식이 거부
     */
    override fun save(item: User): Boolean {
        // ❌ 부모는 true를 반환하는데 자식은 예외를 던짐!
        throw UnsupportedOperationException("읽기 전용 저장소입니다")
    }

    /**
     * ❌ LSP 위반: 부모가 지원하는 기능을 자식이 거부
     */
    override fun delete(id: String): Boolean {
        // ❌ 부모는 삭제 후 결과를 반환하는데 자식은 예외를 던짐!
        throw UnsupportedOperationException("읽기 전용 저장소입니다")
    }
}

// ========================================
// LSP 위반이 문제가 되는 상황 시연
// ========================================

object LspViolationDemo {

    /**
     * PaymentMethod 타입으로 작성된 코드
     * LSP가 지켜졌다면 어떤 자식 타입이 들어와도 잘 동작해야 함
     */
    fun processPayment(method: PaymentMethod, amount: Int): String {
        // 부모의 계약을 믿고 작성된 코드
        return try {
            val fee = method.calculateFee(amount)  // ❌ GiftCardPayment는 여기서 예외!
            if (method.canProcess(amount)) {
                val result = method.process(amount)
                when (result) {
                    is PaymentResult.Success -> "결제 성공: ${result.transactionId}, 수수료: $fee"
                    is PaymentResult.Failed -> "결제 실패: ${result.reason}"
                }
            } else {
                "결제 불가"
            }
        } catch (e: Exception) {
            // ❌ 부모 타입은 예외를 던지지 않는데, 자식 때문에 try-catch 필요!
            "예상치 못한 오류: ${e.message}"
        }
    }

    /**
     * Repository 타입으로 작성된 코드
     */
    fun syncUsers(repository: Repository<User>, users: List<User>) {
        // 부모의 계약을 믿고 작성된 코드
        users.forEach { user ->
            repository.save(user)  // ❌ ReadOnlyRepository는 여기서 예외!
        }
    }

    fun demonstrate() {
        // 정상적인 결제 수단
        val normalCard = PaymentMethod("일반 카드")
        println(processPayment(normalCard, 10000))  // 정상 동작

        // LSP 위반 결제 수단
        val expiredGiftCard = GiftCardPayment(
            name = "만료된 상품권",
            balance = 50000,
            expiryTimestamp = System.currentTimeMillis() - 86400000  // 어제 만료
        )
        println(processPayment(expiredGiftCard, 10000))  // ❌ 예외 발생!

        // 정상적인 저장소
        val normalRepo = InMemoryUserRepository()
        syncUsers(normalRepo, listOf(User("1", "홍길동")))  // 정상 동작

        // LSP 위반 저장소
        val readOnlyRepo = ReadOnlyUserRepository(emptyList())
        // syncUsers(readOnlyRepo, listOf(User("1", "홍길동")))  // ❌ 예외 발생!
    }
}
