import java.math.BigInteger

fun encode(text: String): BigInteger {
    var result = BigInteger.ZERO
    for (char in text) {
        result = result.multiply(BigInteger.valueOf(256))
        result = result.add(BigInteger.valueOf(char.code.toLong()))
    }
    return result
}

fun decode(number: BigInteger): String {
    var result = ""
    var num = number
    while (num > BigInteger.ZERO) {
        val charCode = num.mod(BigInteger.valueOf(256)).toInt()
        result = "${charCode.toChar()}$result"
        num = num.divide(BigInteger.valueOf(256))
    }
    return result
}