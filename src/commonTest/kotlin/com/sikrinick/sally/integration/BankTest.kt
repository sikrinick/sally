package com.sikrinick.sally.integration

import com.sikrinick.sally.*
import kotlin.math.pow
import kotlin.test.Test

class BankTest : SallyTest() {

    @Test
    fun fv_should_be_extractable_for_fv_pv_pmt() {
        // given
        val fv = x
        val pv = 1000
        val pmt = 100
        val rate = 10
        val m = 12
        val n = 2
        val mn = m * n
        val i = rate / 100.0 / m

        // when & then
        assert(
            given = fv - pv * (1.0 + i).pow(mn) - pmt * ((1.0 + i).pow(mn) - 1) / i,
            then = 3865.08,
            message = { expected, actual ->
                "FV should be equal to $expected, but is $actual"
            }
        )
    }

    @Test
    fun pv_should_be_extractable_for_fv_pv_pmt() {
        // given
        val fv = 3865.08
        val pv = x
        val pmt = 100
        val rate = 10
        val m = 12
        val n = 2
        val mn = m * n
        val i = rate / 100.0 / m

        // when & then
        assert(
            given = fv - pv * (1.0 + i).pow(mn) - pmt * ((1.0 + i).pow(mn) - 1) / i,
            then = 1000.00,
            message = { expected, actual ->
                "PV should be equal to $expected, but it $actual"
            }
        )
    }

    @Test
    fun pmt_should_be_extractable_for_fv_pv_pmt() {
        // given
        val fv = 3865.08
        val pv = 1000
        val pmt = x
        val rate = 10
        val m = 12
        val n = 2
        val mn = m * n
        val i = rate / 100.0 / m

        // when & then
        assert(
            given = fv - pv * (1.0 + i).pow(mn) - pmt * ((1.0 + i).pow(mn) - 1) / i,
            then = 100.00,
            message = { expected, actual ->
                "PMT should be equal to $expected, but is $actual"
            }
        )
    }
}