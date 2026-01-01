package io.github.bokchidevchan.solid_ex.violation.lsp

/**
 * ❌ LSP (Liskov Substitution Principle) 위반 예제
 *
 * LSP 원칙: 자식 클래스는 부모 클래스를 대체할 수 있어야 한다.
 * → 부모 타입을 받는 함수에 자식을 넣어도 동일하게 동작해야 함
 */

// ========================================
// ❌ 위반 예제: 새 (Bird)
// ========================================

/**
 * 부모 클래스: 새
 * 계약: 모든 새는 날 수 있다
 */
open class Bird(val name: String) {

    open fun fly(): String {
        return "${name}가 하늘을 날아갑니다"
    }
}

/**
 * ✅ 정상: 참새는 날 수 있음
 */
class Sparrow : Bird("참새")

/**
 * ✅ 정상: 독수리는 날 수 있음
 */
class Eagle : Bird("독수리")

/**
 * ❌ LSP 위반: 펭귄은 새지만 날 수 없음!
 */
class Penguin : Bird("펭귄") {

    override fun fly(): String {
        // ❌ 부모는 날 수 있는데, 자식이 예외를 던짐!
        throw UnsupportedOperationException("펭귄은 날 수 없습니다")
    }
}

/**
 * ❌ LSP 위반: 타조도 새지만 날 수 없음!
 */
class Ostrich : Bird("타조") {

    override fun fly(): String {
        // ❌ 부모의 계약을 위반!
        throw UnsupportedOperationException("타조는 날 수 없습니다")
    }
}

// ========================================
// ❌ 문제 상황: 부모 타입을 믿고 작성한 코드
// ========================================

fun makeBirdFly(bird: Bird): String {
    // Bird 타입은 fly()가 가능하다고 "약속"했음
    return bird.fly()  // ❌ Penguin이 오면 크래시!
}

fun main() {
    val birds = listOf(
        Sparrow(),
        Eagle(),
        Penguin()  // ❌ 여기서 문제 발생!
    )

    birds.forEach { bird ->
        try {
            println(makeBirdFly(bird))
        } catch (e: Exception) {
            // ❌ 부모 타입은 예외를 안 던지는데, try-catch가 필요함!
            println("오류: ${e.message}")
        }
    }
}

/**
 * ❌ 문제점 정리:
 *
 * 1. Bird 클래스는 "새는 날 수 있다"고 약속함
 * 2. Penguin은 Bird를 상속하지만 날 수 없음
 * 3. Bird 타입을 받는 함수에 Penguin을 넣으면 크래시!
 *
 * → 자식(Penguin)이 부모(Bird)를 대체할 수 없음 = LSP 위반
 */
