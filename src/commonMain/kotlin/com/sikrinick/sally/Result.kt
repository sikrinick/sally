package com.sikrinick.sally

sealed class Result<out T : Result<T>> {

    fun findX(acc: Double = 0.0): Double {
        val result = recursiveSimplify()
        return with(result) {
            when (this) {
                is Answer -> value
                is Unknown.None -> acc
                is Unknown.Unary -> expr.findX(operation.inverse(acc))
                is Unknown.Binary.LeftX -> lhs.findX(operation.inverseForLhs(acc, rhs.value))
                is Unknown.Binary.RightX -> rhs.findX(operation.inverseForRhs(acc, lhs.value))
            }
        }
    }

    private fun recursiveSimplify(): Result<T> {
        var result = this
        var oldResult: Result<*>
        do {
            oldResult = result
            result = result.simplify()
        } while (result != oldResult)
        return result
    }

    abstract fun simplify(): T
}

data class Answer(val value: Double) : Result<Answer>() {
    override fun simplify() = this
}

sealed class Unknown<out T : Unknown<T>> : Result<T>() {

    object None : Unknown<None>() {
        override fun simplify() = this
    }

    data class Unary(
        val expr: Result<*>,
        val operation: Op.Unary.UnaryOperation
    ) : Unknown<Unary>() {
        override fun simplify() = this
    }

    sealed class Binary : Unknown<Binary>() {
        data class LeftX(
            val lhs: Unknown<*>,
            val rhs: Answer,
            val operation: Op.Binary.BinaryOperation
        ) : Binary() {
            override fun simplify() = when(lhs) {
                is LeftX -> if (operation == lhs.operation && operation.commutative) {
                    LeftX(
                        lhs = lhs.lhs.simplify(),
                        rhs = Answer(operation.native(lhs.rhs.value, rhs.value)),
                        operation = lhs.operation
                    )
                } else this
                is RightX -> if (operation == lhs.operation && operation.commutative) {
                    RightX(
                        lhs = Answer(operation.native(lhs.lhs.value, rhs.value)),
                        rhs = lhs.rhs.simplify(),
                        operation = lhs.operation
                    )
                } else this
                is Unary -> this
                else -> this
            }
        }

        data class RightX(
            val lhs: Answer,
            val rhs: Unknown<*>,
            val operation: Op.Binary.BinaryOperation
        ) : Binary() {
            override fun simplify() = when(rhs) {
                is LeftX -> if (operation == rhs.operation && operation.commutative) {
                    LeftX(
                        lhs = rhs.lhs.simplify(),
                        rhs = Answer(operation.native(rhs.rhs.value, lhs.value)),
                        operation = rhs.operation
                    )
                } else this
                is RightX -> if (operation == rhs.operation && operation.commutative) {
                    RightX(
                        lhs = Answer(operation.native(rhs.lhs.value, lhs.value)),
                        rhs = rhs.rhs.simplify(),
                        operation = rhs.operation
                    )
                } else this
                is Unary -> this
                else -> this
            }
        }
    }
}
