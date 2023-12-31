class ExchangeProcess {
    // 1: Создание отправителя и получателя
    val receiver = Receiver()
    val sender = Sender()

    // 2
    fun checkIfNumberIsPrime(): Pair<Boolean, Boolean> {
        val p = receiver.elGamal.p
        return Pair(isPrimeFermat(p), isPrimeMillerRabin(p, 20))
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
        receiver.setNewSendersPublicSignatureKey(sender.getPublicSignatureKey())
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

    // Attacks
    // Злоумышленник подделывает сообщение
    fun fakeMessage() {
        val signedMessage = sender.encryptAndSignMessage()
        val encryptedMessage = signedMessage.first
        val messageSignature = signedMessage.second

        // Faking
        val fakeSender = Sender()
        fakeSender.setReceiversKeys(receiver.getPublicKeys())

        fakeSender.generateElGamalSignature()
        fakeSender.setNewMessage("Fake")
        val fakeSignedMessage = fakeSender.encryptAndSignMessage()

        receiver.receivedMessage = fakeSignedMessage.first
        receiver.receivedSignature = messageSignature
    }

    // Злоумышленник подделывает подпись
    fun fakeSignature() {
        val signedMessage = sender.encryptAndSignMessage()
        val encryptedMessage = signedMessage.first
        val messageSignature = signedMessage.second

        // Faking
        val fakeSender = Sender()
        fakeSender.setReceiversKeys(receiver.getPublicKeys())
        fakeSender.generateElGamalSignature()
        fakeSender.setNewMessage(sender.message)
        val fakeSignedMessage = fakeSender.encryptAndSignMessage()

        receiver.receivedMessage = encryptedMessage
        receiver.receivedSignature = fakeSignedMessage.second
    }

}