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
    // Создаем объект класса ElGamal с 128-битным ключом
    val elGamal = ElGamal(128)
    // Задаем сообщение для шифрования
    val message = encode("One")
    // Отправитель шифрует сообщение
    val ciphertext = ElGamal.encrypt(message, elGamal.publicKey, elGamal.p, elGamal.g)
    // Расшифровываем сообщение
    val decryptedMessage = elGamal.decrypt(ciphertext)
    // Выводим результаты в консоль
    println("Original message: $message")
    println("Encrypted message: $ciphertext")
    println("Decrypted message: $decryptedMessage")

    println(decode(message))
    println(decode(decryptedMessage))

    ////////////

    val receiver = Receiver()
    val sender = Sender()

    sender.setReceiversKeys(receiver.getPublicKeys())
    println(receiver.decryptMessage(sender.encryptMessage()))
    //////////

    // Старт программы - генерация ключей шифрования
    val process = ExchangeProcess()
    with(process) {
        // Передача публичного ключа шифрования
        sendReceiversKeysToSender()

        // Цифровая подпись
        createSendersSignature()

        // Отправка публичного ключа ЭЦП
        sendSendersPublicSignatureKeyToReceiver()

        // Подпись и отправка сообщения
        sendMessageToReceiver("Three")

        println(decryptReceivedMessage())
        println(verifySendersSignature())
    }

}