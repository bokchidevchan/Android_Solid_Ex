package io.github.bokchidevchan.solid_ex.violation.ocp

/**
 * ❌ OCP (Open-Closed Principle) 위반 예제
 *
 * OCP 원칙: 확장에는 열려있고, 수정에는 닫혀있어야 한다.
 * → 새 기능 추가 시 기존 코드를 수정하지 않아야 함
 */

// ========================================
// ❌ 위반 예제: 할인 계산기
// ========================================

enum class MemberGrade {
    BRONZE, SILVER, GOLD
}

/**
 * ❌ OCP 위반: 새 등급 추가 시 이 클래스를 수정해야 함
 */
class DiscountCalculator {

    fun calculateDiscount(price: Int, grade: MemberGrade): Int {
        // ❌ 새 등급(PLATINUM, VIP) 추가하면 여기를 수정해야 함!
        return when (grade) {
            MemberGrade.BRONZE -> 0                          // 할인 없음
            MemberGrade.SILVER -> (price * 0.1).toInt()      // 10% 할인
            MemberGrade.GOLD -> (price * 0.2).toInt()        // 20% 할인
            // MemberGrade.PLATINUM -> ???  ← 추가하려면 여기 수정!
            // MemberGrade.VIP -> ???       ← 추가하려면 여기 수정!
        }
    }
}

/**
 * ❌ 문제점:
 *
 * PLATINUM 등급을 추가하려면?
 * 1. MemberGrade enum에 PLATINUM 추가
 * 2. DiscountCalculator.calculateDiscount() 수정  ← 기존 코드 수정!
 * 3. 다른 곳에도 when(grade) 있으면 전부 수정!
 *
 * → 기존에 잘 동작하던 코드를 건드려야 함 = 버그 위험!
 */
