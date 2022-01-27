package com.sikrinick.sally.integration

import com.sikrinick.sally.SallyTest
import com.sikrinick.sally.asExpr
import com.sikrinick.sally.minus
import com.sikrinick.sally.x
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneralTest : SallyTest() {

    @Test
    fun simple_solver() {
        // given
        val expr = 1.asExpr()

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }

    @Test
    fun find_simple_x() {
        // given
        val expr = x - 1
        // 1

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }


    @Test
    fun find_simple_x_2() {
        // given
        val expr = x - 1
        // 1

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }


    @Test
    fun find_simple_x_3() {
        // given
        val expr = x - 1
        // 1

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }

    @Test
    fun find_simple_4() {
        // given
        val expr = x - 1
        // 1

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }

    @Test
    fun find_simple_x_5() {
        // given
        val expr = x - 1
        // 1

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }

    @Test
    fun find_simple_x_6() {
        // given
        val expr = x - 1
        // 1

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }

    @Test
    fun find_simple_x_7() {
        // given
        val expr = x - 1
        // 1

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }

    @Test
    fun find_simple_x_8() {
        // given
        val expr = x - 1
        // 1

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }

    @Test
    fun find_simple_x_9() {
        // given
        val expr = x - 1
        // 1

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }

    @Test
    fun find_simple_x_10() {
        // given
        val expr = x - 1
        // 1

        // when
        val value = expr.solve()

        // then
        assertEquals(1.00, value, "Should be expr 1 should be equal to 1")
    }
}
