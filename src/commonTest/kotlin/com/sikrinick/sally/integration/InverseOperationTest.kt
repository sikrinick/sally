package com.sikrinick.sally.integration

import com.sikrinick.sally.SallyTest
import com.sikrinick.sally.*
import kotlin.test.Test

class InverseOperationTest : SallyTest() {

    @Test
    fun plus_inverse_lhs() = assert(
        given = x + 1,
        then = -1,
    )

    @Test
    fun plus_inverse_rhs() = assert(
        given = 1 + x,
        then = -1
    )

    @Test
    fun minus_inverse_lhs() = assert(
        given = x - 1,
        then = 1
    )

    @Test
    fun minus_inverse_rhs() = assert(
        given = 1 - x,
        then = 1
    )

    @Test
    fun multiply_inverse_lhs() = assert(
        given = (2 * x) + 4,
        then = -2
    )

    @Test
    fun multiply_inverse_rhs() = assert(
        given = (x * 3) - 9,
        then = 3
    )

    @Test
    fun divide_inverse_lhs() = assert(
        given = (x / 3) - 4,
        then = 12
    )

    @Test
    fun divide_inverse_rhs() = assert(
        given = (27 / x) - 3,
        then = 9
    )

    @Test
    fun power_inverse_lhs() = assert(
        given = x.pow(4) - 81,
        then = 3
    )

    @Test
    fun power_inverse_rhs() = assert(
        given = 4.pow(x) - 1024,
        then = 5
    )
}