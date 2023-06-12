import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom


class ElGamalDigitalSignature(private val keySize: Int) {
    data class PublicKey(val p: BigInteger, val g: BigInteger, val y: BigInteger)
    data class PrivateKey(val x: BigInteger)

    private val secureRandom = SecureRandom()

    val publicKey: PublicKey
    private val privateKey: PrivateKey

    init {
        // Создание ключей
        val keys = generateKeys()
        publicKey = keys.first
        privateKey = keys.second
    }

    fun accessPrivateKey(): PrivateKey {
        return privateKey
    }

    fun generateKeys(): Pair<PublicKey, PrivateKey> {
        val p = BigInteger.probablePrime(keySize, secureRandom) // Выбираем случайное большое простое число p
        val g = findGenerator(p) // Поиск генератора эллиптической кривой
        val x = BigInteger(p.bitLength() - 1, secureRandom) // Выбираем случайное число x в диапазоне [0, p-2]
        val y = g.modPow(x, p) // Вычисляем открытый ключ y = g^x mod p
        val publicKey = PublicKey(p, g, y)
        val privateKey = PrivateKey(x)
        return Pair(publicKey, privateKey)
    }

    fun sign(message: ByteArray): Pair<BigInteger, BigInteger> {
        val pMinusOne = publicKey.p - BigInteger.ONE
        var k: BigInteger
        do {
            k = BigInteger(publicKey.p.bitLength() - 1, secureRandom)
        } while (k.gcd(pMinusOne) != BigInteger.ONE) // Генерировать k до тех пор, пока он удовлетворяет условию взаимной простоты с p-1
        val r = publicKey.g.modPow(k, publicKey.p)
        val h = message.hash()
        val s = k.modInverse(pMinusOne).multiply(h.subtract(privateKey.x.multiply(r))).mod(pMinusOne)
        return Pair(r, s)
    }

    companion object {
        fun verify(message: ByteArray, signature: Pair<BigInteger, BigInteger>, publicKey: PublicKey): Boolean {
            val r = signature.first
            val s = signature.second
            val h = message.hash() // Вычисляем хеш сообщения
            val v1 = publicKey.g.modPow(h, publicKey.p) // Вычисляем g^h mod p
            val v2 = publicKey.y.modPow(r, publicKey.p).multiply(r.modPow(s, publicKey.p)).mod(publicKey.p) // Вычисляем y^r * r^s mod p
            return v1 == v2 // Проверяем равенство v1 и v2
        }

        private fun ByteArray.hash(): BigInteger {
            // Хеш-функция SHA-256:
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val hashBytes = messageDigest.digest(this)
            return BigInteger(1, hashBytes)
        }
    }

    private fun findGenerator(p: BigInteger): BigInteger {
        var g = BigInteger("2")
        while (g < p) {
            if (p.isProbablePrime(80) && g.modPow(p.subtract(BigInteger.ONE).divide(BigInteger.TWO), p) == p.subtract(BigInteger.ONE)) {
                return g
            }
            g++
        }
        throw Exception("Failed to find generator for $p")
    }


}
