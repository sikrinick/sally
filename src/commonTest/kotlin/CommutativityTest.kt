import kotlin.test.Test

class CommutativityTest : SallyTest() {

    @Test
    fun plus() = assertEquals(
        2.asExpr() + 3,
        3.asExpr() + 2
    )

    @Test
    fun minus() = assertNotEquals(
        3.asExpr() - 2,
        2.asExpr() - 3
    )

    @Test
    fun multiply() = assertEquals(
        2.asExpr() * 3,
        3.asExpr() * 2
    )

    @Test
    fun divide() = assertNotEquals(
    2.asExpr() / 3,
    3.asExpr() / 2
    )

    @Test
    fun power() = assertNotEquals(
        2.pow(3.asExpr()),
        3.pow(2.asExpr())
    )
}
