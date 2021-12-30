
operator fun Expr.plus(rhs: Expr) = Op.Binary(this, rhs, Op.Binary.BinaryOperation.Add).withPrecedence()
operator fun Expr.plus(number: Number) = plus(number.asExpr())
operator fun Number.plus(expr: Expr) = asExpr() + expr

operator fun Expr.minus(rhs: Expr) = Op.Binary(this, rhs, Op.Binary.BinaryOperation.Minus).withPrecedence()
operator fun Expr.minus(number: Number) = minus(number.asExpr())
operator fun Number.minus(expr: Expr) = asExpr() - expr

operator fun Expr.times(rhs: Expr) = Op.Binary(this, rhs, Op.Binary.BinaryOperation.Multiply).withPrecedence()
operator fun Expr.times(number: Number) = times(number.asExpr())
operator fun Number.times(expr: Expr) = asExpr() * expr

operator fun Expr.div(rhs: Expr) = Op.Binary(this, rhs, Op.Binary.BinaryOperation.Divide).withPrecedence()
operator fun Expr.div(number: Number) = div(number.asExpr())
operator fun Number.div(expr: Expr) = asExpr() / expr

fun Expr.pow(rhs: Expr) = Op.Binary(br(this), rhs, Op.Binary.BinaryOperation.Power).withPrecedence()
fun Expr.pow(number: Number) = pow(number.asExpr())
fun Number.pow(expr: Expr) = asExpr().pow(expr)

operator fun Expr.unaryMinus() = Op.Unary(this, Op.Unary.UnaryOperation.UnaryMinus).withPrecedence()

fun br(number: Number) = br(number.asExpr())
fun br(expr: Expr) = Op.Unary(
    expr = Op.Unary(
        expr = expr,
        operation = Op.Unary.UnaryOperation.RightBracket
    ),
    operation = Op.Unary.UnaryOperation.LeftBracket
).withPrecedence()

fun Double.asExpr() = Id(this)
fun Number.asExpr() = this.toDouble().asExpr()
