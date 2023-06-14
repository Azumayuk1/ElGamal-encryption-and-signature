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
                Text("Receiver", fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                Button(onClick = {
                    exchangeProcess = ExchangeProcess()
                    receiverPublicKey = exchangeProcess.receiver.getPublicKeys().toString()
                    receiverPrivateKey = exchangeProcess.receiver.getPrivateKey().toString()
                    receiverPrimeNumber = exchangeProcess.receiver.elGamal.p.toString()
                }) {
                    Text("1: Generate keys")
                }

                Text("Private key:", fontWeight = FontWeight.Bold)
                Text(receiverPrivateKey)


                Text("Public key:", fontWeight = FontWeight.Bold)
                Text(receiverPublicKey)

                Text("Used prime number:", fontWeight = FontWeight.Bold)
                Text(receiverPrimeNumber)

                Button(onClick = {
                    val results = exchangeProcess.checkIfNumberIsPrime()
                    primeTestFermaResult = results.first
                    primeTestMillerRabinResult = results.second
                }) {
                    Text("2: Check primality")
                }

                Text("Fermat primality test result:", fontWeight = FontWeight.Bold)
                Text(
                    when(primeTestFermaResult) {
                    true -> "OK: Number is prime"
                    false -> "Error: Number is NOT prime"
                })

                Text("Miller-Rabin primality test result:", fontWeight = FontWeight.Bold)
                Text(
                    when(primeTestMillerRabinResult) {
                    true -> "OK: Number is prime"
                    false -> "Error: Number is NOT prime"
                })

                Button(onClick = {
                    exchangeProcess.sendReceiversKeysToSender()
                    senderReceivedPublicKey = exchangeProcess.sender.receiversPublicKey.toString()
                }) {
                    Text("3: Send public key")
                }

                Button(onClick = {
                    receiverDecryptedMessage = exchangeProcess.decryptReceivedMessage()
                }) {
                    Text("7: Decrypt the message")
                }

                Text("Decrypted message:", fontWeight = FontWeight.Bold)
                Text(receiverDecryptedMessage)

                Text("Public signature key:", fontWeight = FontWeight.Bold)
                Text(receiverSendersSignaturePublicKey)

                Button(onClick = {
                    receiverIsMessageVerified = exchangeProcess.verifySendersSignature()
                }) {
                    Text("8: Verify signature")
                }

                Text("Verifying signature result:", fontWeight = FontWeight.Bold)
                Text(
                    when(receiverIsMessageVerified) {
                        true -> "Signature verified"
                        false -> "Error: signature not verified"
                    })
            }

            Column(modifier = Modifier.padding(8.dp).fillMaxWidth(0.5f)) {
                Text("Sender", fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)

                Button(onClick = {
                    exchangeProcess.createSendersSignature()
                    senderSignaturePrivateKey = exchangeProcess.sender.getPrivateSignatureKey().toString()
                    senderSignaturePublicKey = exchangeProcess.sender.getPublicSignatureKey().toString()
                }) {
                    Text("4: Generate signature keys")
                }

                Text("Private signature key:", fontWeight = FontWeight.Bold)
                Text(senderSignaturePrivateKey)

                Text("Public signature key:", fontWeight = FontWeight.Bold)
                Text(senderSignaturePublicKey)

                Button(onClick = {
                    exchangeProcess.sendSendersPublicSignatureKeyToReceiver()
                    receiverSendersSignaturePublicKey = exchangeProcess.receiver.sendersPublicSignatureKey.toString()
                }) {
                    Text("5: Send public key")
                }

                Text("Received public key:", fontWeight = FontWeight.Bold)
                Text(senderReceivedPublicKey)

                Button(onClick = {
                    exchangeProcess.sender.setNewMessage(messageText)
                    exchangeProcess.sendMessageToReceiver(messageText)

                    encryptedMessage = exchangeProcess.receiver.receivedMessage.toString()
                    encryptedMessageSignature = exchangeProcess.receiver.receivedSignature.toString()
                }) {
                    Text("6: Sign and send the message")
                }

                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = { Text("Message input") }
                )

                Text("Encrypted text:", fontWeight = FontWeight.Bold)
                Text(encryptedMessage)


                Text("Signature:", fontWeight = FontWeight.Bold)
                Text(encryptedMessageSignature)

                Text("Intruder's possibilities:", fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                Button(onClick = {
                    exchangeProcess.fakeMessage()
                }) {
                    Text("Fake message")
                }

                Button(onClick = {
                    exchangeProcess.fakeSignature()
                }) {
                    Text("Fake signature")
                }

            }

        }
        }

}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "ElGamal encryption") {
        App()
    }
}
