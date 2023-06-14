import java.math.BigInteger
import java.security.SecureRandom

// Проверка Миллера-Рабина
fun isPrimeMillerRabin(n: BigInteger, k: Int = 20): Boolean {
    if (n == BigInteger.TWO) return true
    if (n < BigInteger.TWO || n % BigInteger.TWO == BigInteger.ZERO) return false
    var (r, d) = n - BigInteger.ONE to n - BigInteger.ONE
    while (d % BigInteger.TWO == BigInteger.ZERO) {
        r /= BigInteger.TWO
        d /= BigInteger.TWO
    }
    val random = SecureRandom()
    repeat(k) {
        var a = BigInteger(n.bitLength(), random)
        while (a <= BigInteger.ONE || a >= n - BigInteger.ONE) {
            a = BigInteger(n.bitLength(), random)
        }
        var x = a.modPow(r, n)
        if (x == BigInteger.ONE || x == n - BigInteger.ONE) return@repeat
        repeat(d.bitLength() - 1) {
            x = x.modPow(BigInteger.TWO, n)
            if (x == n - BigInteger.ONE) return@repeat
        }
        return false
    }
    return true
}

// Проверка Ферма
fun isPrimeFermat(n: BigInteger): Boolean {
    if (n == BigInteger.TWO) return true
    if (n < BigInteger.TWO || n % BigInteger.TWO == BigInteger.ZERO) return false
    val random = SecureRandom()
    repeat(35) { // Количество проверок
        var a = BigInteger(n.bitLength(), random)
        while (a >= n) {
            a = BigInteger(n.bitLength(), random)
        }
        if (a.modPow(n - BigInteger.ONE, n) != BigInteger.ONE) return false
    }
    return true
}