package io.github.bokchidevchan.solid_ex.violation.ocp

/**
 * ✅ OCP (Open-Closed Principle) 준수 예제
 *
 * OCP 원칙: 확장에는 열려있고, 수정에는 닫혀있어야 한다.
 * → 새 기능은 새 클래스 추가로 해결, 기존 코드는 수정 안 함
 */

// ========================================
// ✅ 준수 예제: 할인 정책
// ========================================

/**
 * ✅ 할인 정책 인터페이스
 * 각 등급이 이 인터페이스를 구현함
 */
interface DiscountPolicy {
    fun calculateDiscount(price: Int): Int
}

/**
 * ✅ 브론즈 등급: 할인 없음
 */
class BronzeDiscount : DiscountPolicy {
    override fun calculateDiscount(price: Int): Int = 0
}

/**
 * ✅ 실버 등급: 10% 할인
 */
class SilverDiscount : DiscountPolicy {
    override fun calculateDiscount(price: Int): Int = (price * 0.1).toInt()
}

/**
 * ✅ 골드 등급: 20% 할인
 */
class GoldDiscount : DiscountPolicy {
    override fun calculateDiscount(price: Int): Int = (price * 0.2).toInt()
}

/**
 * ✅ OCP 준수: 할인 계산기
 * 새 등급 추가해도 이 클래스는 수정 안 함!
 */
class OcpDiscountCalculator {

    fun calculateDiscount(price: Int, policy: DiscountPolicy): Int {
        // when 분기 없음! 그냥 policy한테 물어보기만 함
        return policy.calculateDiscount(price)
    }
}

// ========================================
// ✅ 새 등급 추가: 기존 코드 수정 없음!
// ========================================

/**
 * ✅ 플래티넘 등급 추가: 30% 할인
 * 이 클래스만 추가하면 됨!
 */
class PlatinumDiscount : DiscountPolicy {
    override fun calculateDiscount(price: Int): Int = (price * 0.3).toInt()
}

/**
 * ✅ VIP 등급 추가: 50% 할인
 * 이 클래스만 추가하면 됨!
 */
class VipDiscount : DiscountPolicy {
    override fun calculateDiscount(price: Int): Int = (price * 0.5).toInt()
}

// ========================================
// 사용 예시
// ========================================

fun main() {
    val calculator = OcpDiscountCalculator()
    val price = 10000

    // 각 등급별 할인 계산
    println("브론즈: ${calculator.calculateDiscount(price, BronzeDiscount())}원 할인")
    println("실버: ${calculator.calculateDiscount(price, SilverDiscount())}원 할인")
    println("골드: ${calculator.calculateDiscount(price, GoldDiscount())}원 할인")

    // ✅ 새 등급 추가해도 OcpDiscountCalculator는 수정 안 함!
    println("플래티넘: ${calculator.calculateDiscount(price, PlatinumDiscount())}원 할인")
    println("VIP: ${calculator.calculateDiscount(price, VipDiscount())}원 할인")
}

/**
 * ✅ 장점 정리:
 *
 * 새 등급(DIAMOND) 추가하려면?
 * 1. DiamondDiscount 클래스 생성 (DiscountPolicy 구현)
 * 2. 끝!
 *
 * OcpDiscountCalculator는 건드리지 않음!
 * → 기존 코드 안전, 버그 위험 없음
 */
