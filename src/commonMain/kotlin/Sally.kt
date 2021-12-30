import kotlin.math.log
import kotlin.math.pow

sealed class Expr {

    private fun result(): Result<*> = when(this) {
        is Op.Binary -> {
            val lhs = lhs.result()
            val rhs = rhs.result()
            if (lhs is Answer && rhs is Answer) {
                Answer(operation.native(lhs.value, rhs.value))
            } else if (lhs is Unknown && rhs is Answer) {
                Unknown.Binary.LeftX(lhs, rhs, operation)
            } else if (lhs is Answer && rhs is Unknown) {
                Unknown.Binary.RightX(lhs, rhs, operation)
            } else {
                throw RuntimeException("Multiple X not supported: $this")
            }
        }
        is Op.Unary  -> when(val expr = expr.result()) {
            is Answer  -> Answer(operation.native(expr.value))
            is Unknown -> Unknown.Unary(expr, operation)
        }
        is Id        -> Answer(value)
        is X         -> Unknown.None
    }

    fun solve() = result().findX()
}

internal fun Op.withPrecedence(): Op = when(this) {
    is Op.Binary -> when(lhs) {
        is Op -> when(this.precedence < lhs.precedence) {
            true -> when(lhs) {
                is Op.Binary -> lhs.copy(rhs = this.copy(lhs = lhs.rhs))
                is Op.Unary  -> lhs.copy(expr = this.copy(lhs = lhs.expr))
            }
            else -> this
        }
        else -> this
    }
    is Op.Unary -> when(expr) {
        is Op -> when(this.precedence < expr.precedence) {
            true -> when(expr) {
                is Op.Binary -> expr.copy(rhs = this.copy(expr = expr.rhs))
                is Op.Unary  -> expr.copy(expr = this.copy(expr = expr.expr))
            }
            else -> this
        }
        else -> this
    }
}

data class Id(val value: Double) : Expr()
object X : Expr()
val x = X

sealed class Op: Expr() {

    internal abstract val precedence: Int

    interface Operation {
        val precedence: Int
    }

    data class Binary(
        val lhs: Expr,
        val rhs: Expr,
        val operation: BinaryOperation,
    ) : Op(), Operation by operation {

        override fun toString() = "Binary(operation = $operation, lhs = $lhs, rhs = $rhs)"

        enum class BinaryOperation(
            override val precedence: Int,
            val commutative: Boolean,
            val native: (Double, Double) -> Double,
            val inverseForLhs: (acc: Double, rhs: Double) -> Double,
            val inverseForRhs: (acc: Double, lhs: Double) -> Double,

            ) : Operation {
            Add(6, true,
                Double::plus,
                inverseForLhs = { acc, rhs -> acc - rhs },
                inverseForRhs = { acc, lhs -> acc - lhs }
            ),
            Minus(6, false,
                Double::minus,
                inverseForLhs = { acc, rhs -> acc + rhs },
                inverseForRhs = { acc, lhs -> lhs - acc }
            ),
            Multiply(5, true,
                Double::times,
                inverseForLhs = { acc, rhs -> acc / rhs },
                inverseForRhs = { acc, lhs -> acc / lhs }
            ),
            Divide(5, false,
                Double::div,
                inverseForLhs = { acc, rhs -> acc * rhs },
                inverseForRhs = { acc, lhs -> lhs / acc }
            ),
            Power(2, false,
                Double::pow,
                inverseForLhs = { acc, rhs -> acc.pow(1 / rhs) },
                inverseForRhs = { acc, lhs -> log(acc, lhs) }
            )
        }
    }
    data class Unary(
        val expr: Expr,
        val operation: UnaryOperation
    ) : Op(), Operation by operation {

        override fun toString() = "Unary(operation = $operation, expr = $expr)"

        enum class UnaryOperation(
            override val precedence: Int,
            val native: (Double) -> Double,
            val inverse: (Double) -> Double

        ) : Operation {
            UnaryMinus(3, Double::unaryMinus, Double::unaryMinus),
            LeftBracket(1, { it }, { it }),
            RightBracket(1, { it }, { it }),
        }
    }
}
