import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    var messageText by remember { mutableStateOf("Example") }

    var receiverPrivateKey by remember { mutableStateOf("") }
    var receiverPublicKey by remember { mutableStateOf("") }

    var receiverPrimeNumber by remember { mutableStateOf("") }
    var primeTestFermaResult by remember { mutableStateOf(false) }
    var primeTestMillerRabinResult by remember { mutableStateOf(false) }

    var senderReceivedPublicKey by remember { mutableStateOf("") }

    var senderSignaturePublicKey by remember { mutableStateOf("") }
    var senderSignaturePrivateKey by remember { mutableStateOf("") }

    var receiverSendersSignaturePublicKey by remember { mutableStateOf("")}

    var encryptedMessage by remember { mutableStateOf("") }
    var encryptedMessageSignature by remember { mutableStateOf("") }

    var receiverDecryptedMessage by remember { mutableStateOf("") }

    var receiverIsMessageVerified by remember { mutableStateOf(false) }


    lateinit var exchangeProcess: ExchangeProcess


    MaterialTheme {

        Row(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.padding(8.dp).fillMaxWidth(0.5f)) {
                Text("Получатель", fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                Button(onClick = {
                    exchangeProcess = ExchangeProcess()
                    receiverPublicKey = exchangeProcess.receiver.getPublicKeys().toString()
                    receiverPrivateKey = exchangeProcess.receiver.getPrivateKey().toString()
                    receiverPrimeNumber = exchangeProcess.receiver.elGamal.p.toString()
                }) {
                    Text("1: Сгенерировать ключи")
                }

                Text("Приватный ключ:", fontWeight = FontWeight.Bold)
                Text(receiverPrivateKey)


                Text("Публичный ключ:", fontWeight = FontWeight.Bold)
                Text(receiverPublicKey)

                Text("Простое число:", fontWeight = FontWeight.Bold)
                Text(receiverPrimeNumber)

                Button(onClick = {
                    val results = exchangeProcess.checkIfNumberIsPrime()
                    primeTestFermaResult = results.first
                    primeTestMillerRabinResult = results.second
                }) {
                    Text("2: Проверить простоту")
                }

                Text("Результат проверки Ферма:", fontWeight = FontWeight.Bold)
                Text(primeTestFermaResult.toString())

                Text("Результат проверки Миллера-Рабина:", fontWeight = FontWeight.Bold)
                Text(primeTestMillerRabinResult.toString())

                Button(onClick = {
                    exchangeProcess.sendReceiversKeysToSender()
                    senderReceivedPublicKey = exchangeProcess.sender.receiversPublicKey.toString()
                }) {
                    Text("3: Отправить публичный ключ")
                }

                Button(onClick = {
                    receiverDecryptedMessage = exchangeProcess.decryptReceivedMessage()
                }) {
                    Text("7: Расшифровать сообщение")
                }

                Text("Расшифрованное сообщение:", fontWeight = FontWeight.Bold)
                Text(receiverDecryptedMessage)

                Text("Публичный ключ подписи отправителя:", fontWeight = FontWeight.Bold)
                Text(receiverSendersSignaturePublicKey)

                Button(onClick = {
                    receiverIsMessageVerified = exchangeProcess.verifySendersSignature()
                }) {
                    Text("8: Проверить подпись")
                }

                Text("Результат проверки подписи:", fontWeight = FontWeight.Bold)
                Text(receiverIsMessageVerified.toString())
            }

            Column(modifier = Modifier.padding(8.dp).fillMaxWidth(0.5f)) {
                Text("Отправитель", fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)

                Button(onClick = {
                    exchangeProcess.createSendersSignature()
                    senderSignaturePrivateKey = exchangeProcess.sender.getPrivateSignatureKey().toString()
                    senderSignaturePublicKey = exchangeProcess.sender.getPublicSignatureKey().toString()
                }) {
                    Text("4: Сгенерировать ключи подписи")
                }

                Text("Приватный ключ подписи:", fontWeight = FontWeight.Bold)
                Text(senderSignaturePrivateKey)

                Text("Публичный ключ подписи:", fontWeight = FontWeight.Bold)
                Text(senderSignaturePublicKey)

                Button(onClick = {
                    exchangeProcess.sendSendersPublicSignatureKeyToReceiver()
                    receiverSendersSignaturePublicKey = exchangeProcess.receiver.sendersPublicSignatureKey.toString()
                }) {
                    Text("5: Отправить публичный ключ")
                }

                Text("Полученный публичный ключ шифрования:", fontWeight = FontWeight.Bold)
                Text(senderReceivedPublicKey)

                Button(onClick = {
                    exchangeProcess.sender.setNewMessage(messageText)
                    exchangeProcess.sendMessageToReceiver(messageText)

                    encryptedMessage = exchangeProcess.receiver.receivedMessage.toString()
                    encryptedMessageSignature = exchangeProcess.receiver.receivedSignature.toString()
                }) {
                    Text("6: Подписать и отправить сообщение")
                }

                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = { Text("Введите сообщение") }
                )

                Text("Зашифрованный текст:", fontWeight = FontWeight.Bold)
                Text(encryptedMessage)


                Text("Подпись:", fontWeight = FontWeight.Bold)
                Text(encryptedMessageSignature)

            }

        }
        }

}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Криптографический алгоритм") {
        App()
    }
}
