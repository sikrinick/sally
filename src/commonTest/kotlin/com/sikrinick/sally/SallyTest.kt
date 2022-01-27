package com.sikrinick.sally

import com.sikrinick.sally.Expr
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

abstract class SallyTest {
    protected fun assert(
        given: Expr,
        then: Number,
        message: (expected: Number, actual: Double) -> String = { _, _ -> "" }
    ) {
        // when
        val value = given.solve()

        // then
        assertEquals(
            expected = then.toDouble(),
            actual = value,
            absoluteTolerance = 0.01,
            message = message(then, value)
        )
    }

    protected fun assertEquals(lhs: Expr, rhs: Expr) = assert(lhs, rhs, true)
    protected fun assertNotEquals(lhs: Expr, rhs: Expr) = assert(lhs, rhs, false)

    private fun assert(
        lhs: Expr, rhs: Expr, isEqual: Boolean
    ) {
        // when
        val lr = lhs.solve()
        val rr = rhs.solve()
        val assertion: (Double, Double, Double, String?) -> Unit = when (isEqual) {
            true -> ::assertEquals
            false -> ::assertNotEquals
        }

        // then
        assertion(lr, rr, 0.01, null)
    }
}
