import java.math.BigInteger

class Receiver {
    // Шифрование
    val elGamal = ElGamal(128)

    lateinit var receivedMessage: Pair<BigInteger, BigInteger>
    lateinit var receivedSignature: Pair<BigInteger, BigInteger>
    private lateinit var sendersPublicSignatureKey: ElGamalDigitalSignature.PublicKey

    fun setSendersPublicSignatureKey(publicKey: ElGamalDigitalSignature.PublicKey) {
        sendersPublicSignatureKey = publicKey
    }

    fun getPublicKeys(): Triple<BigInteger, BigInteger, BigInteger> {
        return Triple(elGamal.publicKey, elGamal.p, elGamal.g)
    }

    fun decryptMessage(message: Pair<BigInteger, BigInteger>): String {
        val decryptedMessage = elGamal.decrypt(message)
        return decode(decryptedMessage)
    }

    fun verifyMessage(): Boolean {
        return ElGamalDigitalSignature.verify(
            elGamal.decrypt(receivedMessage).toByteArray(),
            receivedSignature,
            sendersPublicSignatureKey
        )
    }

}