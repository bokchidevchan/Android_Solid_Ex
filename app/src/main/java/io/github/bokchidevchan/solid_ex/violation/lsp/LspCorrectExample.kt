package io.github.bokchidevchan.solid_ex.violation.lsp

/**
 * ✅ LSP (Liskov Substitution Principle) 준수 예제
 *
 * LSP 원칙: 자식 클래스는 부모 클래스를 대체할 수 있어야 한다.
 *
 * 해결 방법: 계층 구조를 올바르게 설계
 * → "날 수 있는 새"와 "날 수 없는 새"를 분리
 */

// ========================================
// ✅ 준수 예제: 올바른 계층 구조
// ========================================

/**
 * ✅ 최상위: 새의 공통 동작만 정의
 */
interface Animal {
    val name: String
    fun move(): String
}

/**
 * ✅ 날 수 있는 새 인터페이스
 */
interface Flyable {
    fun fly(): String
}

/**
 * ✅ 헤엄칠 수 있는 동물 인터페이스
 */
interface Swimmable {
    fun swim(): String
}

// ========================================
// ✅ 각 새가 자기 능력에 맞게 구현
// ========================================

/**
 * ✅ 참새: 날 수 있음
 */
class CorrectSparrow : Animal, Flyable {
    override val name = "참새"

    override fun move(): String = "참새가 이동합니다"

    override fun fly(): String = "참새가 하늘을 날아갑니다"
}

/**
 * ✅ 독수리: 날 수 있음
 */
class CorrectEagle : Animal, Flyable {
    override val name = "독수리"

    override fun move(): String = "독수리가 이동합니다"

    override fun fly(): String = "독수리가 높이 날아오릅니다"
}

/**
 * ✅ 펭귄: 날 수 없지만, 헤엄칠 수 있음
 * Flyable을 구현하지 않으므로 LSP 위반 없음!
 */
class CorrectPenguin : Animal, Swimmable {
    override val name = "펭귄"

    override fun move(): String = "펭귄이 뒤뚱뒤뚱 걸어갑니다"

    override fun swim(): String = "펭귄이 빠르게 헤엄칩니다"
}

/**
 * ✅ 타조: 날 수 없지만, 빠르게 달릴 수 있음
 */
class CorrectOstrich : Animal {
    override val name = "타조"

    override fun move(): String = "타조가 빠르게 달립니다"

    fun run(): String = "타조가 시속 70km로 달립니다"
}

// ========================================
// ✅ 안전한 코드: 타입에 맞게 동작
// ========================================

/**
 * ✅ Flyable 타입만 받으므로 안전!
 */
fun makeFly(flyable: Flyable): String {
    return flyable.fly()  // ✅ 항상 성공!
}

/**
 * ✅ Swimmable 타입만 받으므로 안전!
 */
fun makeSwim(swimmable: Swimmable): String {
    return swimmable.swim()  // ✅ 항상 성공!
}

/**
 * ✅ 모든 Animal에 공통 동작
 */
fun makeMove(animal: Animal): String {
    return animal.move()  // ✅ 모든 동물이 이동 가능!
}

fun correctMain() {
    // ✅ 날 수 있는 새들만 fly() 호출
    val flyingBirds: List<Flyable> = listOf(
        CorrectSparrow(),
        CorrectEagle()
    )

    flyingBirds.forEach { bird ->
        println(makeFly(bird))  // ✅ 안전!
    }

    // ✅ 펭귄은 Flyable이 아니므로 fly() 호출 불가 (컴파일 에러)
    val penguin = CorrectPenguin()
    // makeFly(penguin)  // ❌ 컴파일 에러! 실수 방지!
    println(makeSwim(penguin))  // ✅ 헤엄은 가능!

    // ✅ 모든 동물은 move() 가능
    val animals: List<Animal> = listOf(
        CorrectSparrow(),
        CorrectPenguin(),
        CorrectOstrich()
    )

    animals.forEach { animal ->
        println(makeMove(animal))  // ✅ 모두 안전!
    }
}

/**
 * ✅ 장점 정리:
 *
 * 1. 펭귄은 Flyable이 아니므로 fly()를 구현하지 않음
 * 2. makeFly(penguin) 호출 시 컴파일 에러 발생
 * 3. 런타임 크래시 대신 컴파일 타임에 실수를 잡아줌!
 *
 * → 모든 자식이 부모를 대체 가능 = LSP 준수
 */
