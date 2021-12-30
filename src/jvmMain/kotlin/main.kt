fun bank(
    fv: Expr,
    pv: Expr,
    pmt: Expr,
    rate: Double,
    m: Int,
    n: Int
): Double {
    val i = (rate / 100.00 / m).asExpr()
    val mn = m * n

    return (
        fv - pv * (1 + i).pow(mn) - pmt * ((1 + i).pow(mn) - 1) / i
    ).solve()
}

fun deposit() {
    val fv = bank(
        fv = x,
        pv = 1000.00.asExpr(),
        pmt = 0.asExpr(),
        rate = 10.00,
        m = 12,
        n = 2
    )
    println(
        "Deposit FV = %.2f".format(fv)
    )
}

fun credit() {
    val pv = 1000.00
    val i = 5.00
    val n = 2

    val fv = bank(
        fv = x,
        pv = pv.asExpr(),
        pmt = 0.asExpr(),
        rate = i,
        m = 1,
        n = n
    )

    val pmt = bank(
        fv = fv.asExpr(),
        pv = 0.asExpr(),
        pmt = x,
        rate = i,
        m = 12,
        n = n
    )

    "Credit PMT = %.2f".format(pmt).let(::println)
}

fun main() {
    deposit()
    credit()
}