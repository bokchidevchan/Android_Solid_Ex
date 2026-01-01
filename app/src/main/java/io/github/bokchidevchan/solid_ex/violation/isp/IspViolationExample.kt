package io.github.bokchidevchan.solid_ex.violation.isp

/**
 * ❌ ISP (Interface Segregation Principle) 위반 예제
 *
 * ISP 원칙: 클라이언트가 사용하지 않는 인터페이스를 강제하지 말라.
 * → 인터페이스는 작게 분리하라
 */

// ========================================
// ❌ 위반 예제: 뚱뚱한 인터페이스
// ========================================

/**
 * ❌ ISP 위반: 모든 기능을 강제하는 거대한 인터페이스
 */
interface Machine {
    fun print(document: String)
    fun scan(): String
    fun fax(document: String)
    fun copy(document: String)
}

/**
 * ✅ 복합기: 모든 기능 사용 가능
 */
class MultiFunctionPrinter : Machine {
    override fun print(document: String) = println("출력: $document")
    override fun scan(): String = "스캔된 문서"
    override fun fax(document: String) = println("팩스 전송: $document")
    override fun copy(document: String) = println("복사: $document")
}

/**
 * ❌ ISP 위반: 단순 프린터인데 모든 기능 구현 강제!
 */
class SimplePrinter : Machine {
    override fun print(document: String) = println("출력: $document")

    // ❌ 스캔 기능 없는데 구현 강제!
    override fun scan(): String {
        throw UnsupportedOperationException("스캔 기능이 없습니다")
    }

    // ❌ 팩스 기능 없는데 구현 강제!
    override fun fax(document: String) {
        throw UnsupportedOperationException("팩스 기능이 없습니다")
    }

    // ❌ 복사 기능 없는데 구현 강제!
    override fun copy(document: String) {
        throw UnsupportedOperationException("복사 기능이 없습니다")
    }
}

/**
 * ❌ ISP 위반: 스캐너인데 모든 기능 구현 강제!
 */
class SimpleScanner : Machine {
    override fun scan(): String = "스캔된 문서"

    // ❌ 출력 기능 없는데 구현 강제!
    override fun print(document: String) {
        throw UnsupportedOperationException("출력 기능이 없습니다")
    }

    // ❌ 팩스 기능 없는데 구현 강제!
    override fun fax(document: String) {
        throw UnsupportedOperationException("팩스 기능이 없습니다")
    }

    // ❌ 복사 기능 없는데 구현 강제!
    override fun copy(document: String) {
        throw UnsupportedOperationException("복사 기능이 없습니다")
    }
}

// ========================================
// ❌ 문제 상황
// ========================================

fun useMachine(machine: Machine) {
    machine.print("문서")  // ❌ SimpleScanner면 크래시!
    machine.scan()         // ❌ SimplePrinter면 크래시!
}

/**
 * ❌ 문제점 정리:
 *
 * 1. SimplePrinter는 print()만 필요한데 scan(), fax(), copy()도 구현해야 함
 * 2. 사용하지 않는 메서드에 예외를 던지는 코드가 생김
 * 3. Machine 타입을 믿고 작성한 코드가 런타임에 크래시!
 *
 * → 인터페이스가 너무 뚱뚱해서 문제 발생
 */
