package io.github.bokchidevchan.solid_ex.violation.isp

/**
 * ✅ ISP (Interface Segregation Principle) 준수 예제
 *
 * ISP 원칙: 클라이언트가 사용하지 않는 인터페이스를 강제하지 말라.
 *
 * 해결 방법: 인터페이스를 작게 분리
 * → 각 기기가 필요한 인터페이스만 구현
 */

// ========================================
// ✅ 준수 예제: 작게 분리된 인터페이스
// ========================================

/**
 * ✅ 출력 기능
 */
interface Printer {
    fun print(document: String)
}

/**
 * ✅ 스캔 기능
 */
interface Scanner {
    fun scan(): String
}

/**
 * ✅ 팩스 기능
 */
interface Fax {
    fun fax(document: String)
}

/**
 * ✅ 복사 기능
 */
interface Copier {
    fun copy(document: String)
}

// ========================================
// ✅ 각 기기가 필요한 것만 구현
// ========================================

/**
 * ✅ 단순 프린터: Printer만 구현
 */
class CorrectSimplePrinter : Printer {
    override fun print(document: String) = println("출력: $document")
}

/**
 * ✅ 단순 스캐너: Scanner만 구현
 */
class CorrectSimpleScanner : Scanner {
    override fun scan(): String = "스캔된 문서"
}

/**
 * ✅ 복합기: 필요한 인터페이스 모두 구현
 */
class CorrectMultiFunctionPrinter : Printer, Scanner, Fax, Copier {
    override fun print(document: String) = println("출력: $document")
    override fun scan(): String = "스캔된 문서"
    override fun fax(document: String) = println("팩스 전송: $document")
    override fun copy(document: String) = println("복사: $document")
}

/**
 * ✅ 프린터+스캐너 복합기
 */
class PrinterScanner : Printer, Scanner {
    override fun print(document: String) = println("출력: $document")
    override fun scan(): String = "스캔된 문서"
}

// ========================================
// ✅ 안전한 코드: 필요한 타입만 받음
// ========================================

/**
 * ✅ Printer만 받으므로 안전!
 */
fun printDocument(printer: Printer) {
    printer.print("문서")  // ✅ 항상 성공!
}

/**
 * ✅ Scanner만 받으므로 안전!
 */
fun scanDocument(scanner: Scanner): String {
    return scanner.scan()  // ✅ 항상 성공!
}

/**
 * ✅ 둘 다 필요하면 둘 다 받음
 */
fun <T> copyDocument(device: T) where T : Printer, T : Scanner {
    val scanned = device.scan()
    device.print(scanned)
}

fun correctMain() {
    val simplePrinter = CorrectSimplePrinter()
    val simpleScanner = CorrectSimpleScanner()
    val multiFunction = CorrectMultiFunctionPrinter()

    // ✅ 각 함수는 필요한 타입만 받음
    printDocument(simplePrinter)   // ✅ OK
    printDocument(multiFunction)   // ✅ OK

    scanDocument(simpleScanner)    // ✅ OK
    scanDocument(multiFunction)    // ✅ OK

    // ✅ 복사는 Printer + Scanner 둘 다 필요
    copyDocument(multiFunction)    // ✅ OK
    // copyDocument(simplePrinter) // ❌ 컴파일 에러! Scanner 없음

    // ✅ 컴파일 타임에 실수 방지!
    // printDocument(simpleScanner)  // ❌ 컴파일 에러! Printer 아님
}

/**
 * ✅ 장점 정리:
 *
 * 1. SimplePrinter는 Printer만 구현 (불필요한 메서드 없음)
 * 2. 예외를 던지는 빈 구현이 필요 없음
 * 3. 컴파일 타임에 타입 체크로 실수 방지
 * 4. 인터페이스 변경 시 영향 범위 최소화
 *
 * → 인터페이스를 작게 분리 = ISP 준수
 */
