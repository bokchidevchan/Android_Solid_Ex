package io.github.bokchidevchan.solid_ex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.bokchidevchan.solid_ex.ui.theme.Solid_ExTheme
import io.github.bokchidevchan.solid_ex.violation.dip.DipViolationScreen
import io.github.bokchidevchan.solid_ex.violation.dip.DipViolationViewModel
import io.github.bokchidevchan.solid_ex.violation.srp.SrpViolationScreen
import io.github.bokchidevchan.solid_ex.violation.srp.SrpViolationViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Solid_ExTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable("srp") {
            val viewModel: SrpViolationViewModel = hiltViewModel()
            DetailScaffold(
                title = "SRP 위반 예제",
                navController = navController
            ) {
                SrpViolationScreen(viewModel = viewModel)
            }
        }
        composable("ocp") {
            DetailScaffold(
                title = "OCP 위반 예제",
                navController = navController
            ) {
                OcpInfoContent()
            }
        }
        composable("lsp") {
            DetailScaffold(
                title = "LSP 위반 예제",
                navController = navController
            ) {
                LspInfoContent()
            }
        }
        composable("isp") {
            DetailScaffold(
                title = "ISP 위반 예제",
                navController = navController
            ) {
                IspInfoContent()
            }
        }
        composable("dip") {
            val viewModel = DipViolationViewModel()
            DetailScaffold(
                title = "DIP 위반 예제",
                navController = navController
            ) {
                DipViolationScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScaffold(
    title: String,
    navController: NavController,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit
) {
    val principles = listOf(
        PrincipleItem(
            id = "srp",
            title = "1. SRP - 단일 책임 원칙",
            description = "클래스는 단 하나의 이유로만 변경되어야 한다",
            violation = "ViewModel이 네트워크, 캐싱, 포맷팅 등 모든 책임을 담당"
        ),
        PrincipleItem(
            id = "ocp",
            title = "2. OCP - 개방-폐쇄 원칙",
            description = "기존 코드를 수정하지 않고 기능을 확장할 수 있어야 한다",
            violation = "새 결제 타입 추가 시 모든 when 분기를 수정해야 함"
        ),
        PrincipleItem(
            id = "lsp",
            title = "3. LSP - 리스코프 치환 원칙",
            description = "자식 클래스는 부모 타입으로 대체 가능해야 한다",
            violation = "GiftCardPayment가 부모의 계약을 위반 (예외 던짐)"
        ),
        PrincipleItem(
            id = "isp",
            title = "4. ISP - 인터페이스 분리 원칙",
            description = "클라이언트가 사용하지 않는 인터페이스를 강제하지 말라",
            violation = "PaymentProcessor 인터페이스가 모든 결제 수단의 메서드를 강제"
        ),
        PrincipleItem(
            id = "dip",
            title = "5. DIP - 의존 역전 원칙",
            description = "구현이 아니라 추상화(인터페이스)에 의존해야 한다",
            violation = "ViewModel이 구체 클래스(ApiClient, Cache)를 직접 생성"
        )
    )

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("SOLID 원칙 위반 예제") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "각 원칙을 클릭하여 위반 예제를 확인하세요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(principles) { principle ->
                    PrincipleCard(
                        principle = principle,
                        onClick = { onNavigate(principle.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PrincipleCard(
    principle: PrincipleItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = principle.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = principle.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "위반: ${principle.violation}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

data class PrincipleItem(
    val id: String,
    val title: String,
    val description: String,
    val violation: String
)

@Composable
fun OcpInfoContent() {
    InfoContent(
        description = "개방-폐쇄 원칙 위반",
        details = listOf(
            "FeeCalculator: 새 결제 타입 추가 시 when 분기 수정 필요",
            "PaymentValidator: 새 결제 타입 추가 시 검증 로직 수정 필요",
            "PaymentIconProvider: 새 결제 타입 추가 시 아이콘/색상 수정 필요",
            "PaymentReportGenerator: 새 결제 타입 추가 시 리포트 로직 수정 필요"
        ),
        codePath = "violation/ocp/OcpViolationExample.kt"
    )
}

@Composable
fun LspInfoContent() {
    InfoContent(
        description = "리스코프 치환 원칙 위반",
        details = listOf(
            "GiftCardPayment.calculateFee(): 부모는 예외를 던지지 않지만 자식이 예외를 던짐",
            "GiftCardPayment.canProcess(): 부모보다 더 엄격한 전제조건 추가",
            "GiftCardPayment.process(): 부모에 없는 부수효과(잔액 차감) 추가",
            "GiftCardPayment.refund(): 부모와 다른 반환 의미",
            "ReadOnlyUserRepository: 부모가 지원하는 save/delete를 예외로 거부"
        ),
        codePath = "violation/lsp/LspViolationExample.kt"
    )
}

@Composable
fun IspInfoContent() {
    InfoContent(
        description = "인터페이스 분리 원칙 위반",
        details = listOf(
            "PaymentProcessor: 카드/계좌/현금/포인트의 모든 메서드를 하나의 인터페이스에",
            "CardPaymentProcessor: 계좌이체/포인트 메서드도 구현 강제",
            "CashPaymentProcessor: 카드/계좌/포인트 메서드도 구현 강제",
            "PointPaymentProcessor: 카드/계좌/현금영수증 메서드도 구현 강제"
        ),
        codePath = "violation/isp/IspViolationExample.kt"
    )
}

@Composable
fun InfoContent(
    description: String,
    details: List<String>,
    codePath: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "위반 사례:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                details.forEach { detail ->
                    Text(
                        text = "• $detail",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "코드 위치:",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = codePath,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
