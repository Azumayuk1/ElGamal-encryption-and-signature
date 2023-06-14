import java.math.BigInteger
import java.security.SecureRandom

class ElGamal {
    var p: BigInteger  // Большое простое число (модуль)
    var g: BigInteger  // Примитивный корень по модулю p
    var publicKey: BigInteger  // Открытый ключ
    private var secretKey: BigInteger  // Секретный ключ

    // Конструктор для генерации новых ключей
    constructor(bitLength: Int) {
        val random = SecureRandom()
        // Генерируем случайное большое простое число p заданной длины
        p = BigInteger.probablePrime(bitLength, random)
        // Генерируем случайный примитивный корень g по модулю p
        g = BigInteger.probablePrime(bitLength - 1, random)
        // Генерируем случайное число x, которое является секретным ключом
        secretKey = BigInteger(bitLength - 1, random).add(BigInteger.ONE)
        // Вычисляем открытый ключ y = g^x mod p
        publicKey = g.modPow(secretKey, p)
    }

    // Конструктор для использования заданных ключей
    constructor(p: BigInteger, g: BigInteger, x: BigInteger) {
        this.p = p
        this.g = g
        this.secretKey = x
        publicKey = g.modPow(x, p)
    }

    fun getPublicKeys(): Triple<BigInteger, BigInteger, BigInteger> {
        return Triple(publicKey, p, g)
    }

    fun getPrivateKey(): BigInteger {
        return secretKey
    }

    companion object {
        // Метод для шифрования сообщения
        fun encrypt(m: BigInteger, publicKey: BigInteger, p: BigInteger, g: BigInteger): Pair<BigInteger, BigInteger> {
            val random = SecureRandom()
            // Генерируем случайный параметр k
            val k = BigInteger(p.bitLength() - 1, random)
            // Вычисляем a = g^k mod p
            val a = g.modPow(k, p)
            // Вычисляем b = m * y^k mod p
            val b = m.multiply(publicKey.modPow(k, p)).mod(p)
            // Возвращаем пару значений (a, b)
            return Pair(a, b)
        }
    }

    // Метод для расшифровки сообщения
    fun decrypt(c: Pair<BigInteger, BigInteger>): BigInteger {
        val a = c.first
        val b = c.second
        // Вычисляем s = a^x mod p
        val s = a.modPow(secretKey, p)
        // Вычисляем si = s^-1 mod p
        val si = s.modInverse(p)
        // Вычисляем и возвращаем m = b * si mod p
        return b.multiply(si).mod(p)
    }
}

fun main() {
    // Старт программы - генерация ключей шифрования
    val process = ExchangeProcess()
    println("Receiver's public keys: " + process.receiver.getPublicKeys())
    println("Receiver's private key: " + process.receiver.getPrivateKey())
    println("-----")

    with(process) {
        // Проверка на простоту
        println("Test if prime:")
        println("Test Ferma result: " + checkIfNumberIsPrime().first)
        println("Test Miller-Rabin result: " + checkIfNumberIsPrime().second)
        println("-----")

        // Передача публичного ключа шифрования
        sendReceiversKeysToSender()
        println("Receiver's public key sent.")

        // Цифровая подпись
        createSendersSignature()
        println("Signature created.")

        println("Sender's public signature key: " + sender.getPublicSignatureKey())
        println("Sender's private signature key: " + sender.getPrivateSignatureKey())

        // Отправка публичного ключа ЭЦП
        sendSendersPublicSignatureKeyToReceiver()
        println("Signature public key sent.")

        // Подпись и отправка сообщения
        sendMessageToReceiver("Moscow and Saint-Petersburg")
        println("Message signed and sent.")
        println("-----")

        println("Message decryption:")
        println(decryptReceivedMessage())

        println("Verified: " + verifySendersSignature())
    }

}