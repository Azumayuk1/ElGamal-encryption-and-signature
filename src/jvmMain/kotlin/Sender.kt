import java.math.BigInteger

class Sender {
    // Публичный ключ получателя
    lateinit var receiversPublicKey: BigInteger
    lateinit var receiversP: BigInteger
    lateinit var receiversG: BigInteger

    lateinit var elGamalSignature: ElGamalDigitalSignature
    //private lateinit var message: String
    var message: String = "Three"

    fun generateElGamalSignature() {
        elGamalSignature = ElGamalDigitalSignature(128)
    }

    fun setNewMessage(message: String) {
        this.message = message
    }

    fun setReceiversKeys(keysTriple: Triple<BigInteger, BigInteger, BigInteger>) {
        this.receiversPublicKey = keysTriple.first
        this.receiversP = keysTriple.second
        this.receiversG = keysTriple.third
    }

    fun encryptMessage(): Pair<BigInteger, BigInteger> {
        val encodedMessage = encode(message)
        return ElGamal.encrypt(encodedMessage, receiversPublicKey, receiversP, receiversG)
    }

    fun encryptAndSignMessage(): Pair<Pair<BigInteger, BigInteger>, Pair<BigInteger, BigInteger>> {
        val encodedMessage = encode(message)

        val encryptedMessage = ElGamal.encrypt(encodedMessage, receiversPublicKey, receiversP, receiversG)
        val messageSignature = elGamalSignature.sign(encodedMessage.toByteArray())

        return Pair(encryptedMessage, messageSignature)
    }

    fun getPublicSignatureKey(): ElGamalDigitalSignature.PublicKey {
        return elGamalSignature.publicKey
    }
}