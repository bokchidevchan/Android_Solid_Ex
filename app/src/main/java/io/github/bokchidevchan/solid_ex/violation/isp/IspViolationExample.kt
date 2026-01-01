package io.github.bokchidevchan.solid_ex.violation.isp

/**
 * ❌ ISP (Interface Segregation Principle) 위반 예제
 *
 * ISP 원칙: 클라이언트가 사용하지 않는 인터페이스를 강제하지 말라.
 * 즉, 인터페이스는 클라이언트가 필요로 하는 메서드만 포함해야 함.
 *
 * 이 예제들은 하나의 거대한 인터페이스가 모든 기능을 강제합니다:
 * 1. 구현 클래스가 사용하지 않는 메서드도 구현해야 함
 * 2. 무의미한 구현이나 예외를 던지는 코드가 생김
 * 3. 인터페이스 변경 시 모든 구현 클래스에 영향
 */

// ========================================
// ❌ ISP 위반: 거대한 결제 처리기 인터페이스
// ========================================

/**
 * ❌ ISP 위반: 너무 많은 책임을 가진 인터페이스
 * 모든 결제 처리기가 이 모든 메서드를 구현해야 함
 */
interface PaymentProcessor {
    // === 기본 결제 기능 (모든 결제 수단에 필요) ===
    fun processPayment(amount: Int): Boolean
    fun refund(transactionId: String): Boolean
    fun getTransactionHistory(): List<String>

    // === 카드 전용 기능 (현금/계좌이체에는 필요 없음!) ===
    fun getCardNumber(): String
    fun getCardHolderName(): String
    fun validateCvv(cvv: String): Boolean
    fun setInstallmentPlan(months: Int)
    fun getAvailableInstallmentOptions(): List<Int>

    // === 계좌이체 전용 기능 (카드/현금에는 필요 없음!) ===
    fun getBankCode(): String
    fun getAccountNumber(): String
    fun validateAccountHolder(name: String): Boolean

    // === 포인트 전용 기능 (다른 결제에는 필요 없음!) ===
    fun getPointBalance(): Int
    fun convertCashToPoints(amount: Int): Int
    fun getPointExpiryDate(): Long

    // === 현금영수증 기능 (현금에만 필요!) ===
    fun issueReceipt(type: ReceiptType): String
    fun getReceiptNumber(): String

    // === 리포트/분석 기능 ===
    fun generateMonthlyReport(): String
    fun exportToExcel(): ByteArray
    fun sendEmailReceipt(email: String): Boolean
}

enum class ReceiptType { PERSONAL, BUSINESS }

/**
 * ❌ ISP 위반: 카드 결제 구현
 * 카드와 관련 없는 메서드도 모두 구현해야 함
 */
class CardPaymentProcessor(
    private val cardNumber: String,
    private val holderName: String
) : PaymentProcessor {

    private val transactions = mutableListOf<String>()

    // ✅ 의미 있는 구현
    override fun processPayment(amount: Int): Boolean {
        val txId = "CARD_${System.currentTimeMillis()}"
        transactions.add(txId)
        return true
    }

    override fun refund(transactionId: String): Boolean = true
    override fun getTransactionHistory(): List<String> = transactions

    // ✅ 카드 전용 - 의미 있는 구현
    override fun getCardNumber(): String = cardNumber
    override fun getCardHolderName(): String = holderName
    override fun validateCvv(cvv: String): Boolean = cvv.length == 3
    override fun setInstallmentPlan(months: Int) { /* 할부 설정 */ }
    override fun getAvailableInstallmentOptions(): List<Int> = listOf(1, 3, 6, 12)

    // ❌ 계좌이체 전용인데 구현 강제됨 - 의미 없는 구현
    override fun getBankCode(): String =
        throw UnsupportedOperationException("카드는 은행코드가 없습니다")
    override fun getAccountNumber(): String =
        throw UnsupportedOperationException("카드는 계좌번호가 없습니다")
    override fun validateAccountHolder(name: String): Boolean =
        throw UnsupportedOperationException("카드는 계좌주 확인이 없습니다")

    // ❌ 포인트 전용인데 구현 강제됨 - 의미 없는 구현
    override fun getPointBalance(): Int = 0
    override fun convertCashToPoints(amount: Int): Int = 0
    override fun getPointExpiryDate(): Long = 0L

    // ❌ 현금영수증 전용인데 구현 강제됨 - 의미 없는 구현
    override fun issueReceipt(type: ReceiptType): String =
        throw UnsupportedOperationException("카드는 현금영수증이 없습니다")
    override fun getReceiptNumber(): String =
        throw UnsupportedOperationException("카드는 현금영수증이 없습니다")

    // 리포트 기능
    override fun generateMonthlyReport(): String = "카드 결제 리포트"
    override fun exportToExcel(): ByteArray = byteArrayOf()
    override fun sendEmailReceipt(email: String): Boolean = true
}

/**
 * ❌ ISP 위반: 현금 결제 구현
 * 현금과 관련 없는 메서드도 모두 구현해야 함
 */
class CashPaymentProcessor : PaymentProcessor {

    private val transactions = mutableListOf<String>()
    private var lastReceiptNumber: String = ""

    // ✅ 의미 있는 구현
    override fun processPayment(amount: Int): Boolean {
        val txId = "CASH_${System.currentTimeMillis()}"
        transactions.add(txId)
        return true
    }

    override fun refund(transactionId: String): Boolean = true
    override fun getTransactionHistory(): List<String> = transactions

    // ❌ 카드 전용인데 구현 강제됨 - 의미 없는 구현
    override fun getCardNumber(): String =
        throw UnsupportedOperationException("현금은 카드번호가 없습니다")
    override fun getCardHolderName(): String =
        throw UnsupportedOperationException("현금은 카드소유자가 없습니다")
    override fun validateCvv(cvv: String): Boolean =
        throw UnsupportedOperationException("현금은 CVV가 없습니다")
    override fun setInstallmentPlan(months: Int) =
        throw UnsupportedOperationException("현금은 할부가 없습니다")
    override fun getAvailableInstallmentOptions(): List<Int> = emptyList()

    // ❌ 계좌이체 전용인데 구현 강제됨 - 의미 없는 구현
    override fun getBankCode(): String =
        throw UnsupportedOperationException("현금은 은행코드가 없습니다")
    override fun getAccountNumber(): String =
        throw UnsupportedOperationException("현금은 계좌번호가 없습니다")
    override fun validateAccountHolder(name: String): Boolean =
        throw UnsupportedOperationException("현금은 계좌주 확인이 없습니다")

    // ❌ 포인트 전용인데 구현 강제됨 - 의미 없는 구현
    override fun getPointBalance(): Int = 0
    override fun convertCashToPoints(amount: Int): Int = 0
    override fun getPointExpiryDate(): Long = 0L

    // ✅ 현금영수증 - 의미 있는 구현
    override fun issueReceipt(type: ReceiptType): String {
        lastReceiptNumber = "RCP_${System.currentTimeMillis()}"
        return lastReceiptNumber
    }
    override fun getReceiptNumber(): String = lastReceiptNumber

    // 리포트 기능
    override fun generateMonthlyReport(): String = "현금 결제 리포트"
    override fun exportToExcel(): ByteArray = byteArrayOf()
    override fun sendEmailReceipt(email: String): Boolean = true
}

/**
 * ❌ ISP 위반: 포인트 결제 구현
 * 포인트와 관련 없는 메서드도 모두 구현해야 함
 */
class PointPaymentProcessor(
    private var pointBalance: Int
) : PaymentProcessor {

    private val transactions = mutableListOf<String>()

    // ✅ 의미 있는 구현
    override fun processPayment(amount: Int): Boolean {
        if (amount > pointBalance) return false
        pointBalance -= amount
        transactions.add("POINT_${System.currentTimeMillis()}")
        return true
    }

    override fun refund(transactionId: String): Boolean {
        pointBalance += 1000  // 환불 시 포인트 복구
        return true
    }

    override fun getTransactionHistory(): List<String> = transactions

    // ❌ 카드 전용인데 구현 강제됨
    override fun getCardNumber(): String =
        throw UnsupportedOperationException("포인트는 카드번호가 없습니다")
    override fun getCardHolderName(): String =
        throw UnsupportedOperationException("포인트는 카드소유자가 없습니다")
    override fun validateCvv(cvv: String): Boolean =
        throw UnsupportedOperationException("포인트는 CVV가 없습니다")
    override fun setInstallmentPlan(months: Int) =
        throw UnsupportedOperationException("포인트는 할부가 없습니다")
    override fun getAvailableInstallmentOptions(): List<Int> = emptyList()

    // ❌ 계좌이체 전용인데 구현 강제됨
    override fun getBankCode(): String =
        throw UnsupportedOperationException("포인트는 은행코드가 없습니다")
    override fun getAccountNumber(): String =
        throw UnsupportedOperationException("포인트는 계좌번호가 없습니다")
    override fun validateAccountHolder(name: String): Boolean =
        throw UnsupportedOperationException("포인트는 계좌주 확인이 없습니다")

    // ✅ 포인트 전용 - 의미 있는 구현
    override fun getPointBalance(): Int = pointBalance
    override fun convertCashToPoints(amount: Int): Int = amount / 100
    override fun getPointExpiryDate(): Long = System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000

    // ❌ 현금영수증 전용인데 구현 강제됨
    override fun issueReceipt(type: ReceiptType): String =
        throw UnsupportedOperationException("포인트는 현금영수증이 없습니다")
    override fun getReceiptNumber(): String =
        throw UnsupportedOperationException("포인트는 현금영수증이 없습니다")

    // 리포트 기능
    override fun generateMonthlyReport(): String = "포인트 결제 리포트"
    override fun exportToExcel(): ByteArray = byteArrayOf()
    override fun sendEmailReceipt(email: String): Boolean = true
}

// ========================================
// ISP 위반이 문제가 되는 상황 시연
// ========================================

object IspViolationDemo {

    /**
     * PaymentProcessor 인터페이스를 사용하는 클라이언트
     * 이 클라이언트는 할부 기능만 필요함
     */
    class InstallmentService(private val processor: PaymentProcessor) {
        fun setupInstallment(months: Int) {
            // ❌ ISP 위반으로 인한 문제:
            // CashPaymentProcessor나 PointPaymentProcessor가 들어오면 예외 발생!
            processor.setInstallmentPlan(months)
        }
    }

    /**
     * 포인트 잔액만 조회하는 클라이언트
     * 이 클라이언트는 getPointBalance()만 필요함
     */
    class PointBalanceChecker(private val processor: PaymentProcessor) {
        fun checkBalance(): Int {
            // ❌ ISP 위반으로 인한 문제:
            // CardPaymentProcessor나 CashPaymentProcessor는 의미 없는 0 반환
            return processor.getPointBalance()
        }
    }

    fun demonstrate() {
        val cardProcessor = CardPaymentProcessor("1234-5678", "홍길동")
        val cashProcessor = CashPaymentProcessor()
        val pointProcessor = PointPaymentProcessor(10000)

        // 할부 서비스
        val installmentService = InstallmentService(cardProcessor)
        installmentService.setupInstallment(3)  // ✅ 카드는 OK

        // val cashInstallment = InstallmentService(cashProcessor)
        // cashInstallment.setupInstallment(3)  // ❌ 예외 발생!

        // 포인트 잔액 조회
        val pointChecker = PointBalanceChecker(pointProcessor)
        println("포인트 잔액: ${pointChecker.checkBalance()}")  // ✅ 포인트는 OK

        val cardChecker = PointBalanceChecker(cardProcessor)
        println("카드 포인트(?): ${cardChecker.checkBalance()}")  // ❌ 의미 없는 0 반환
    }
}

// ========================================
// ISP 위반의 문제점 요약
// ========================================

/**
 * ❌ ISP 위반의 문제점:
 *
 * 1. 불필요한 의존성
 *    - CashPaymentProcessor가 카드 관련 메서드에 의존
 *    - 카드 로직이 변경되면 현금 결제 클래스도 재컴파일 필요
 *
 * 2. 무의미한 구현 코드
 *    - throw UnsupportedOperationException() 으로 가득한 코드
 *    - 코드가 지저분해지고 유지보수 어려움
 *
 * 3. 런타임 에러 위험
 *    - 컴파일 타임에 잡을 수 없는 에러 발생
 *    - 사용자가 잘못된 메서드를 호출할 가능성
 *
 * 4. 테스트 복잡도 증가
 *    - 사용하지 않는 메서드도 테스트해야 함
 *    - Mock 객체 생성이 복잡해짐
 *
 * 5. 인터페이스 변경의 파급 효과
 *    - 카드에 새 기능 추가 시 현금, 포인트 클래스도 수정 필요
 */
