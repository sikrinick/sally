package com.sikrinick.sally

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlin.math.pow

@State(Scope.Benchmark)
class PerformanceTest {

    @Benchmark
    fun expression_performance(): Double = fv(
        pv = 1000.00,
        pmt = 100.00,
        rate = 10.00,
        m = 12,
        n = 2
    )

    @Benchmark
    fun naive_double_performance(): Double {
        val pv = 1000.00
        val pmt = 100.00
        val m = 12
        val n = 2

        val i = 10.00 / 100.00 / m
        val mn = m * n

        return pv * (1.0 + i).pow(mn) - pmt * ((1.0 + i).pow(mn) - 1) / i
    }


    companion object {
        private fun fv(
            pv: Double,
            pmt: Double,
            rate: Double,
            m: Int,
            n: Int
        ) = expr(
            fv = X,
            pv = pv.asExpr(),
            pmt = pmt.asExpr(),
            rate = rate.asExpr(),
            m = m.asExpr(),
            n = n.asExpr()
        )

        private fun expr(
            fv: Expr,
            pv: Expr,
            pmt: Expr,
            rate: Expr,
            m: Expr,
            n: Expr
        ): Double {
            val mn = m * n
            val i = rate / 100.0 / m
            val expr = fv - pv * (1.0 + i).pow(mn) - pmt * ((1.0 + i).pow(mn) - 1) / i
            return expr.solve()
        }
    }
}
