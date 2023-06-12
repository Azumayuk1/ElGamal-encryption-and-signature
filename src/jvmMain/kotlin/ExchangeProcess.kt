class ExchangeProcess {
    // 1: Создание отправителя и получателя
    val receiver = Receiver()
    val sender = Sender()

    // 2
    fun checkIfNumberIsPrime(): Pair<Boolean, Boolean> {
        val p = receiver.elGamal.p
        return Pair(isPrimeFerma(p), isPrimeMillerRabin(p, 10))

    }

    // 3
    fun sendReceiversKeysToSender() {
        sender.setReceiversKeys(receiver.getPublicKeys())
    }

    // 4
    fun createSendersSignature() {
        sender.generateElGamalSignature()
    }

    // 5
    fun sendSendersPublicSignatureKeyToReceiver() {
        receiver.setSendersPublicSignatureKey(sender.getPublicSignatureKey())
    }

    // 6
    fun sendMessageToReceiver(message: String) {
        val signedMessage = sender.encryptAndSignMessage()
        val encryptedMessage = signedMessage.first
        val messageSignature = signedMessage.second
        receiver.receivedMessage = encryptedMessage
        receiver.receivedSignature = messageSignature
    }

    // 7
    fun decryptReceivedMessage(): String {
        return receiver.decryptMessage(receiver.receivedMessage)
    }

    // 8
    fun verifySendersSignature(): Boolean {
        return receiver.verifyMessage()
    }

}