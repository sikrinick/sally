import kotlin.test.Test

class PrecedenceTest : SallyTest() {

    @Test
    fun power_before_divide() = assert(
        given = 81.asExpr() / 3.pow(3.asExpr()),
        then = 3
    )

    @Test
    fun power_before_multiply() = assert(
        given = 4.asExpr() * 3.pow(3.asExpr()),
        then = 108
    )

    @Test
    fun power_before_minus() = assert(
        given = 5.asExpr() - 2.pow(2.asExpr()),
        then = 1
    )

    @Test
    fun power_before_plus() = assert(
        given = 5.asExpr() + 2.pow(2.asExpr()),
        then = 9
    )

    @Test
    fun divide_as_multiply() = assertEquals(
        lhs = 5.asExpr() * 4 / 2,
        rhs = 4.asExpr() / 2 * 5
    )

    @Test
    fun divide_before_minus() = assert(
        given = 5.asExpr() - 4 / 2.asExpr(),
        then = 3
    )

    @Test
    fun divide_before_plus() = assert(
        given = 5.asExpr() + 4 / 2.asExpr(),
        then = 7
    )

    @Test
    fun multiply_before_minus() = assert(
        given = 5.asExpr() - 4 * 2.asExpr(),
        then = -3
    )

    @Test
    fun multiply_before_plus() = assert(
        given = 5.asExpr() + 4 * 2.asExpr(),
        then = 13
    )

    @Test
    fun minus_as_plus() = assertEquals(
        lhs = 3.asExpr() + 4.asExpr() - 5.asExpr(),
        rhs = 4.asExpr() - 5.asExpr() + 3.asExpr()
    )
}
