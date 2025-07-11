package eu.tutorials.gemverse

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Date

class ChatViewModel : ViewModel() {
    val messageList = mutableStateListOf<MessageModel>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro-latest",
        apiKey = Constants.apiKey,
        generationConfig = GenerationConfig.builder().build()
    )

    init {
        loadMessages()
    }

    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {

                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) {
                            text(it.message)
                        }
                    }
                )
                messageList.add(MessageModel(question, "user"))
                saveMessageToFirestore(question, "user")

                messageList.add(MessageModel("Typing", "model"))
                val response = chat.sendMessage(question)
                if (messageList.isNotEmpty()) messageList.removeAt(messageList.size - 1)

                val reply = response.text.toString()
                messageList.add(MessageModel(reply, "model"))
                saveMessageToFirestore(reply, "model")

            } catch (e: Exception) {
                if (messageList.isNotEmpty()) messageList.removeAt(messageList.size - 1)
                val errorMsg = "Error : ${e.message}"
                messageList.add(MessageModel(errorMsg, "model"))
                saveMessageToFirestore(errorMsg, "model")
            }
        }
    }

    private fun saveMessageToFirestore(msg: String, role: String) {
        val email = auth.currentUser?.email ?: return
        val messageMap = hashMapOf(
            "message" to msg,
            "role" to role,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        db.collection("chats")
            .document(email)
            .collection("messages")
            .add(messageMap)
    }

    private fun loadMessages() {
        val email = auth.currentUser?.email ?: return
        val cutoffTime = com.google.firebase.Timestamp.now()
            .toDate().time - 5 * 24 * 60 * 60 * 1000 // 5 days in ms
        val cutoffDate = Date(cutoffTime)

        db.collection("chats")
            .document(email)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                messageList.clear()
                for (doc in querySnapshot) {
                    val timestamp = doc.getTimestamp("timestamp")?.toDate()
                    if (timestamp != null && timestamp.after(cutoffDate)) {
                        val msg = doc.getString("message") ?: ""
                        val role = doc.getString("role") ?: "user"
                        messageList.add(MessageModel(msg, role))
                    } else {
                        // Delete old message
                        doc.reference.delete()
                    }
                }
            }
    }
}
//class ChatViewModel : ViewModel(){
//
//    val messageList by lazy{
//        mutableStateListOf<MessageModel>()
//    }
//    val generativeModel: GenerativeModel= GenerativeModel(
//        modelName="gemini-1.5-pro-latest",
//        apiKey= Constants.apiKey,
//        generationConfig = GenerationConfig.builder().build()
//    )
//    fun sendMessage(question : String){
//        viewModelScope.launch {
//            try{
//                val chat=generativeModel.startChat(
//                    history = messageList.map{
//                        content(it.role){text(it.message)}
//                    }.toList()
//                )
//
//                messageList.add(MessageModel(question,"user"))
//                messageList.add(MessageModel("Typing...", "model"))
//
//                val response = chat.sendMessage(question)
//                messageList.removeAt(messageList.size - 1)
//                messageList.add(MessageModel(response.text.toString(),"model"))
//            }catch(e : Exception){
//                messageList.removeAt(messageList.size - 1)
//                messageList.add(MessageModel("Error : "+e.message.toString(),"model"))
//            }
//        }
//    }
//}