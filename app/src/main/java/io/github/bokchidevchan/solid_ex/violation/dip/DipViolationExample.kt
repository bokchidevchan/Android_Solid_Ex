package io.github.bokchidevchan.solid_ex.violation.dip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * ❌ DIP (Dependency Inversion Principle) 위반 예제
 *
 * DIP 원칙: 고수준 모듈은 저수준 모듈에 의존하면 안 된다. 둘 다 추상화에 의존해야 한다.
 * 즉, 구현이 아니라 인터페이스(추상화)에 의존해야 한다.
 *
 * 이 예제들은 고수준 모듈(ViewModel)이 저수준 모듈(구체 클래스)에 직접 의존합니다:
 * 1. 테스트할 때 의존성을 교체할 수 없음
 * 2. 구현 변경 시 고수준 모듈도 수정해야 함
 * 3. 모듈 간 결합도가 높아짐
 */

// ========================================
// ❌ DIP 위반: 구체 클래스들 (저수준 모듈)
// ========================================

/**
 * ❌ DIP 위반: 인터페이스 없이 직접 구현된 API 클래스
 */
class PaymentApiClient {
    suspend fun fetchPayments(): List<PaymentData> {
        delay(500)  // 네트워크 시뮬레이션
        return listOf(
            PaymentData("1", "스타벅스", 5500, "CARD"),
            PaymentData("2", "편의점", 3200, "CASH"),
            PaymentData("3", "배달의민족", 28000, "CARD"),
        )
    }

    suspend fun savePayment(payment: PaymentData): Boolean {
        delay(300)
        return true
    }
}

/**
 * ❌ DIP 위반: 인터페이스 없이 직접 구현된 캐시 클래스
 */
class PaymentCache {
    private val cache = mutableListOf<PaymentData>()

    fun save(payments: List<PaymentData>) {
        cache.clear()
        cache.addAll(payments)
    }

    fun load(): List<PaymentData> = cache.toList()

    fun clear() = cache.clear()
}

/**
 * ❌ DIP 위반: 인터페이스 없이 직접 구현된 분석 클래스
 */
class AnalyticsTracker {
    fun trackEvent(eventName: String, params: Map<String, Any>) {
        println("Analytics: $eventName - $params")
    }

    fun trackPaymentLoaded(count: Int) {
        trackEvent("payment_loaded", mapOf("count" to count))
    }

    fun trackError(error: String) {
        trackEvent("error", mapOf("message" to error))
    }
}

/**
 * ❌ DIP 위반: 인터페이스 없이 직접 구현된 로거 클래스
 */
class PaymentLogger {
    fun debug(message: String) {
        println("[DEBUG] $message")
    }

    fun error(message: String, throwable: Throwable? = null) {
        println("[ERROR] $message")
        throwable?.printStackTrace()
    }

    fun info(message: String) {
        println("[INFO] $message")
    }
}

data class PaymentData(
    val id: String,
    val title: String,
    val amount: Int,
    val type: String
)

// ========================================
// ❌ DIP 위반: ViewModel (고수준 모듈)
// ========================================

/**
 * ❌ DIP 위반: 구체 클래스에 직접 의존하는 ViewModel
 *
 * 문제점:
 * 1. PaymentApiClient를 MockApi로 교체할 수 없음 → 테스트 불가
 * 2. PaymentCache를 다른 캐시(Room, SharedPrefs)로 교체할 수 없음
 * 3. 프로덕션에서 실제 API를 사용하고 테스트에서 Fake를 사용할 수 없음
 * 4. 의존성을 외부에서 주입할 수 없음 (Hilt 사용 불가)
 */
class DipViolationViewModel : ViewModel() {

    // ❌ DIP 위반: 구체 클래스를 직접 생성
    // 이 의존성들을 테스트에서 교체할 방법이 없음!
    private val apiClient = PaymentApiClient()      // ❌ 구체 클래스 직접 생성
    private val cache = PaymentCache()              // ❌ 구체 클래스 직접 생성
    private val analytics = AnalyticsTracker()      // ❌ 구체 클래스 직접 생성
    private val logger = PaymentLogger()            // ❌ 구체 클래스 직접 생성

    var uiState by mutableStateOf<DipUiState>(DipUiState.Loading)
        private set

    init {
        loadPayments()
    }

    private fun loadPayments() {
        viewModelScope.launch {
            uiState = DipUiState.Loading
            logger.debug("결제 내역 로딩 시작")  // ❌ 구체 클래스 사용

            try {
                // ❌ 구체 클래스 메서드 직접 호출
                val payments = apiClient.fetchPayments()
                cache.save(payments)
                analytics.trackPaymentLoaded(payments.size)
                logger.info("결제 내역 ${payments.size}건 로드 완료")

                uiState = DipUiState.Success(payments)

            } catch (e: Exception) {
                logger.error("결제 내역 로드 실패", e)
                analytics.trackError(e.message ?: "Unknown error")

                // 캐시에서 복구 시도
                val cached = cache.load()
                if (cached.isNotEmpty()) {
                    uiState = DipUiState.Success(cached)
                } else {
                    uiState = DipUiState.Error("데이터를 불러올 수 없습니다")
                }
            }
        }
    }

    fun refresh() {
        loadPayments()
    }
}

sealed class DipUiState {
    data object Loading : DipUiState()
    data class Success(val payments: List<PaymentData>) : DipUiState()
    data class Error(val message: String) : DipUiState()
}

// ========================================
// ❌ DIP 위반으로 인한 테스트 불가능 시연
// ========================================

/**
 * ❌ 테스트 코드 작성 시도 (불가능!)
 */
object DipViolationTestDemo {

    /**
     * 테스트를 작성하고 싶지만...
     * DipViolationViewModel은 구체 클래스에 직접 의존하므로
     * Mock이나 Fake를 주입할 수 없음!
     */
    fun cannotWriteTest() {
        // ❌ 이렇게 테스트하고 싶지만 불가능!
        // val fakeApi = FakePaymentApiClient()
        // val mockCache = MockPaymentCache()
        // val viewModel = DipViolationViewModel(fakeApi, mockCache)  // 생성자 없음!

        // ❌ ViewModel 내부에서 직접 생성하므로 교체 불가!
        val viewModel = DipViolationViewModel()
        // 실제 API가 호출됨 → 테스트가 느리고 불안정
        // 네트워크 상태에 따라 테스트 결과가 달라짐
    }

    /**
     * 만약 DIP를 지켰다면 이렇게 테스트할 수 있었을 것:
     *
     * interface PaymentRepository {
     *     suspend fun getPayments(): List<PaymentData>
     * }
     *
     * class PaymentViewModel(
     *     private val repository: PaymentRepository  // 인터페이스 의존
     * ) : ViewModel() { ... }
     *
     * // 테스트
     * val fakeRepository = FakePaymentRepository()
     * val viewModel = PaymentViewModel(fakeRepository)
     * // 빠르고 안정적인 테스트 가능!
     */
}

// ========================================
// UI (Composable)
// ========================================

@Composable
fun DipViolationScreen(
    viewModel: DipViolationViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.uiState

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "DIP 위반 예제",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "ViewModel이 구체 클래스에 직접 의존",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 문제점 설명
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("문제점:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                Text("• PaymentApiClient를 직접 생성")
                Text("• PaymentCache를 직접 생성")
                Text("• 테스트 시 Mock으로 교체 불가")
                Text("• Hilt 의존성 주입 불가")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            is DipUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DipUiState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.payments) { payment ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(payment.title)
                                Text(
                                    NumberFormat.getNumberInstance(Locale.KOREA)
                                        .format(payment.amount) + "원",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            is DipUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// ========================================
// DIP 위반의 문제점 요약
// ========================================

/**
 * ❌ DIP 위반의 문제점:
 *
 * 1. 테스트 불가능
 *    - Mock/Fake 객체를 주입할 수 없음
 *    - 실제 네트워크 호출이 발생하여 테스트가 느리고 불안정
 *    - 단위 테스트가 아닌 통합 테스트가 됨
 *
 * 2. 유연성 없음
 *    - API 구현체 변경 시 ViewModel도 수정 필요
 *    - 다른 캐시 전략(Room, DataStore)으로 교체 불가
 *    - 환경(개발/스테이징/프로덕션)별 설정 불가
 *
 * 3. Hilt 사용 불가
 *    - 의존성을 생성자로 받지 않으므로 DI 프레임워크 사용 불가
 *    - @Inject constructor() 패턴 적용 불가
 *
 * 4. 결합도 증가
 *    - ViewModel이 저수준 구현 세부사항을 알아야 함
 *    - 변경의 파급 효과가 큼
 *
 * 5. 재사용성 저하
 *    - 다른 프로젝트에서 ViewModel 재사용 불가
 *    - 의존성이 하드코딩되어 있음
 */
