# Sally
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.sikrinick/sally/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.reactivex.rxjava3/rxjava)
Sally is a simple solver for Simple Algebraic Linear equations.  
Platform-agnostic as a common module in a Kotlin/Multiplatform library.

## Installation 

### Kotlin Multiplatform
```kotlin
// Add Maven Central if needed
repositories {
    maven { url = java.net.URI("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    // mavenCentral() // not released yet
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(group = "io.github.sikrinick", name = "sally", version = "0.0.1-SNAPSHOT")
            }
        }
    }
}
```

### Kotlin JVM
```kotlin
// Add Maven Central if needed
repositories {
    maven { url = java.net.URI("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    // mavenCentral() // not released yet
}

implementation(group = "io.github.sikrinick", name = "sally-jvm", version = "0.0.1-SNAPSHOT")
```

### Kotlin JS
```kotlin
// Add Maven Central if needed
repositories {
    maven { url = java.net.URI("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    // mavenCentral() // not released yet
}

implementation(group = "io.github.sikrinick", name = "sally-js", version = "0.0.1-SNAPSHOT")
```

### Kotlin Native
```kotlin
// Add Maven Central if needed
repositories {
    maven { url = java.net.URI("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    // mavenCentral() // not released yet
}

implementation(group = "io.github.sikrinick", name = "sally-native", version = "0.0.1-SNAPSHOT")
```

## Why?
Occasionally, even in commercial programming you may run into a problem, which can be represented by a simple equation.  
Let's assume, we run a logistic business.  
For example, basic business requirement may be to calculate box volume:   

<img alt="boxVolume" src="https://render.githubusercontent.com/render/math?math=\color{gray}length \cdot width \cdot height = volume">

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

## Reasoning
Consider common financial formula that represents relationship between Future Value (FV), Present Value (PV), and Periodic Payments (PMT).   

<img alt="fv - pv*(1+i/m)^mn - pmt*((1+i/m)^(mn)-1)/i/m " src="https://render.githubusercontent.com/render/math?math={\color{gray}FV - PV\cdot(1 %2b \dfrac{i}{m})^{mn} - PMT\dfrac{(1 %2b \dfrac{i}{m})^{mn}-1}{\dfrac{i}{m}}  = 0}">  

Where:  
`i` is an annual rate.  
`n` is a number of years.  
`m` is a number of periods of capitalization of interest per year.

For a simple deposit for 2 years with a rate of 10% and an initial amount of 1000 we would need next formula:  

<img src="https://render.githubusercontent.com/render/math?math=\color{gray}FV - 1000\cdot(1 %2b 10 \%25)^2 = 0">

For a credit for 2 years with a rate of 10% and an initial amount of 1000 we may use a set of 2 formulas.  
This is not an optimal solution, but it allows us to use same relationship.  
Firstly, we have to find FV, the value of a loan in 2 years.  

<img src="https://render.githubusercontent.com/render/math?math=\color{gray}FV - 1000\cdot(1 %2b 10 \%25)^2 = 0">
<br/>
<img src="https://render.githubusercontent.com/render/math?math=\color{gray}FV = 1210">  

Then, we have to find out monthly payments:  

<img src="https://render.githubusercontent.com/render/math?math=\color{gray}1210 - PMT\dfrac{(1 %2b \dfrac{10 \%25}{12})^{12*2}-1}{\dfrac{10 \%25}{12}} = 0">

As one can observe, same formula is used in all the examples, but in some cases FV, PV or PMT is equal to zero.  
However, as a code naive solution would be represented by 3 methods, one for each unknown variable FV, PV or PMT.  
This results in an unnecessary growth of codebase, which can be substituted with a simple algebraic solver for linear equations.

Sally allows to rewrite it as a single function representing logic with a couple of decorating functions.
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
```

## Performance

Obviously, performance would be lower than a simple version based on Doubles.  
Consider next benchmarks, one is using Doubles:
```kotlin
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
```

Another one is using this library:
```kotlin
@Benchmark
fun expression_performance(): Double = fv(
    pv = 1000.00,
    pmt = 100.00,
    rate = 10.00,
    m = 12,
    n = 2
)

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
```

On a Macbook Pro 2019 it shows next results (the higher the better):

|        | Double-based    | Sally          |
|--------|-----------------|----------------|
| JVM    | 26981.54 ops/ms | 1601.96 ops/ms |
| JS     | 926430 ops/ms   | 254.29 ops/ms  |
| Native | 5966 ops/ms     | 40.67 ops/ms   |

Therefore, I suggest using it on a JVM platform, and, probably, Kotlin/Native, not on a JS platform.  

