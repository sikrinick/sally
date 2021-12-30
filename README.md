# Sally
Sally is a simple solver for Simple Algebraic Linear equations.  
Platform-agnostic as a common module in a Kotlin/Multiplatform library.

## Why?
Occasionally, even in commercial programming you may run into a problem, which can be represented by a simple equation.  
Let's assume, we run a logistic business.  
For example, basic business requirement may be to calculate box volume:   

<img alt="boxVolume" src="https://render.githubusercontent.com/render/math?math=length \cdot width \cdot height = volume">

```kotlin
fun boxVolume(length: Double, width: Double, height: Double) = length * width * height
```

The next business requirement may be to calculate length, width or height based on any other 3 known parameters.

```kotlin
fun boxLength(volume: Double, width: Double, height: Double) = volume / width / height
fun boxWidth(volume: Double, length: Double, height: Double) = volume / length / height
fun boxHeight(volume: Double, length: Double, width: Double) = volume / length / width
```
It is obvious that for the same relationship, which can be represented by a single formula we need 4 methods and tests for each of those.

As an option, we may represent relationship as an expression and delegate methods to that expression.

```kotlin
private fun boxVolume(length: Expr, width: Expr, height: Expr, volume: Expr) = (
    volume - length * width * height
).solve()

fun boxVolume(length: Double, width: Double, height: Double) = boxVolume(
    length = length.asExpr(),
    width = width.asExpr(),
    height = height.asExpr(),
    volume = x
)
fun boxLength(volume: Double, width: Double, height: Double) = boxVolume(
    length = x,
    width = width.asExpr(),
    height = height.asExpr(),
    volume = volume.asExpr()
)
fun boxWidth(volume: Double, length: Double, height: Double) = boxVolume(
    length = length.asExpr(),
    width = x,
    height = height.asExpr(),
    volume = volume.asExpr()
)
fun boxHeight(volume: Double, length: Double, width: Double) = boxVolume(
    length = length.asExpr(),
    width = width.asExpr(),
    height = x,
    volume = volume.asExpr()
)
```


## How-to


Unknown part is marked with an `x`.
```kotlin
val expr = x - 1
println(expr.solve()) // 1
```

For explicit conversion of number to expressions use `asExpr()`
```kotlin
val expr = 10.asExpr() - x * 2.asExpr()
println(expr.solve()) // 5
```

If there is no `x` in expression, it will be solved as a simple expression.
```kotlin
val expr = 10.asExpr() - 3 * 2
println(expr.solve()) // 4
```

More complicated example is in `jvmMain/kotlin/main.kt`
```kotlin
fun bank(
    fv: Expr,
    pv: Expr, pmt: Expr,
    rate: Double,
    m: Int, n: Int
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
    "Deposit FV = %.2f".format(fv).let(::println)
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
```

## Reasoning
Consider common financial formula that represents relationship between Future Value (FV), Present Value (PV), and Periodic Payments (PMT).   

<img alt="fv - pv*(1+i/m)^mn - pmt*((1+i/m)^(mn)-1)/i/m " src="https://render.githubusercontent.com/render/math?math={FV - PV\cdot(1 %2b \dfrac{i}{m})^{mn} - PMT\dfrac{(1 %2b \dfrac{i}{m})^{mn}-1}{\dfrac{i}{m}}  = 0}">  

Where:  
<img src="https://render.githubusercontent.com/render/math?math=i"> is an annual rate.  
<img src="https://render.githubusercontent.com/render/math?math=n"> is a number of years.  
<img src="https://render.githubusercontent.com/render/math?math=m"> is a number of periods of capitalization of interest per year.

For a simple deposit for 2 years with a rate of 10% and an initial amount of 1000 we would need next formula:  

<img src="https://render.githubusercontent.com/render/math?math=FV - 1000\cdot(1 %2b 10 \%25)^2 = 0">

For a credit for 2 years with a rate of 10% and an initial amount of 1000 we may use a set of 2 formulas:  
This is not an optimal solution, but it allows us to use same relationship.  
Firstly, we have to find FV, the value of a loan in 2 years.  

<img src="https://render.githubusercontent.com/render/math?math=FV - 1000\cdot(1 %2b 10 \%25)^2 = 0">
<br/>
<img src="https://render.githubusercontent.com/render/math?math=FV = 1210">  

Then, we have to find out monthly payments:  

<img src="https://render.githubusercontent.com/render/math?math=1210 - PMT\dfrac{(1 %2b \dfrac{10 \%25}{12})^{12*2}-1}{\dfrac{10 \%25}{12}} = 0">  

As one can observe, same formula is used in all the examples, but in some cases FV, PV or PMT is equal to zero.  
However, as a code naive solution would be represented by 3 methods, one for each unknown variable FV, PV or PMT.  
This results in an unnecessary growth of codebase, which can be substituted with a simple algebraic solver for linear equations.

## Installation

Fork or use [Jitpack](https://jitpack.io/).  
Maven support will be done on request.

