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
    val sessionList = mutableStateListOf<ChatSessionModel>()

    // ✅ NEW → track karega kaunsi session active hai
    var currentSessionId: String? = null
    var lastMessageTime: Long = System.currentTimeMillis()
    val SESSION_TIMEOUT_MILLIS = 20 * 60 * 1000L // 20 minutes

    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro-latest",
        apiKey = Constants.apiKey,
        generationConfig = GenerationConfig.builder().build()
    )

    init {
        loadSessions()
        loadMessages()
    }

    fun shouldStartNewSession(): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentSessionId == null) ||
                (currentTime - lastMessageTime > SESSION_TIMEOUT_MILLIS)
    }

    fun loadSessions() {
//        val email = auth.currentUser?.email ?: return
//        db.collection("chats")
//            .document(email)
//            .collection("sessions")
//            .orderBy("timestamp")
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                sessionList.clear()
//                for (doc in querySnapshot) {
//                    val title = doc.getString("title") ?: "Untitled Chat"
//                    val timestamp = doc.getTimestamp("timestamp")?.toDate() ?: Date()
//                    sessionList.add(ChatSessionModel(doc.id, title, timestamp))
//                }
//            }
        val email = auth.currentUser?.email ?: return
        db.collection("chats")
            .document(email)
            .collection("sessions")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING) // 👈 sabse latest pehle
            .limit(5) // 👈 sirf 5 latest
            .get()
            .addOnSuccessListener { querySnapshot ->
                sessionList.clear()
                for (doc in querySnapshot) {
                    val title = doc.getString("title") ?: "Untitled Chat"
                    val timestamp = doc.getTimestamp("timestamp")?.toDate() ?: Date()
                    sessionList.add(ChatSessionModel(doc.id, title, timestamp))
                }
            }

    }

    // ✅ NAYA METHOD → messages load karega kisi ek session se
//    fun loadMessagesFromSession(sessionId: String) {
//        val email = auth.currentUser?.email ?: return
//        currentSessionId = sessionId
//        db.collection("chats")
//            .document(email)
//            .collection("sessions")
//            .document(sessionId)
//            .collection("messages")
//            .orderBy("timestamp")
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                messageList.clear()
//                for (doc in querySnapshot) {
//                    val msg = doc.getString("message") ?: ""
//                    val role = doc.getString("role") ?: "user"
//                    messageList.add(MessageModel(msg, role))
//                }
//            }
//    }
    fun loadMessagesFromSession(sessionId: String) {
        val email = auth.currentUser?.email ?: return
        currentSessionId = sessionId
        db.collection("chats")
            .document(email)
            .collection("sessions")
            .document(sessionId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                messageList.clear()
                for (doc in querySnapshot) {
                    val msg = doc.getString("message") ?: ""
                    val role = doc.getString("role") ?: "user"
                    messageList.add(MessageModel(msg, role))
                }
                lastMessageTime = System.currentTimeMillis() // ✅ NEW
            }
    }


    //    // ✅ NAYA METHOD → session banata hai aur pehla message bhejta hai
//    fun createSessionAndSendMessage(question: String) {
//        val email = auth.currentUser?.email ?: return
//        val newSession = hashMapOf(
//            "title" to question.take(25),
//            "timestamp" to com.google.firebase.Timestamp.now()
//        )
//        val sessionRef = db.collection("chats")
//            .document(email)
//            .collection("sessions")
//            .document()
//        currentSessionId = sessionRef.id
//        sessionRef.set(newSession).addOnSuccessListener {
//            sendMessage(question)
//            loadSessions()
//        }
//    }
fun createSessionAndSendMessage(question: String) {
    lastMessageTime = System.currentTimeMillis()

    val email = auth.currentUser?.email ?: return
    val newSession = hashMapOf(
        "title" to question.take(25),
        "timestamp" to com.google.firebase.Timestamp.now()
    )
    val sessionRef = db.collection("chats")
        .document(email)
        .collection("sessions")
        .document()

    currentSessionId = sessionRef.id

    sessionRef.set(newSession).addOnSuccessListener {
        sendMessage(question)
        loadSessions()
    }
}


    fun sendMessage(question: String) {

        if (shouldStartNewSession()) {
            createSessionAndSendMessage(question)
            return
        }

        lastMessageTime = System.currentTimeMillis()
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

//    private fun saveMessageToFirestore(msg: String, role: String) {
//        val email = auth.currentUser?.email ?: return
//        val messageMap = hashMapOf(
//            "message" to msg,
//            "role" to role,
//            "timestamp" to com.google.firebase.Timestamp.now()
//        )
//        db.collection("chats")
//            .document(email)
//            .collection("messages")
//            .add(messageMap)
//    }
private fun saveMessageToFirestore(msg: String, role: String) {
    val email = auth.currentUser?.email ?: return
    val sessionId = currentSessionId ?: return
    val messageMap = hashMapOf(
        "message" to msg,
        "role" to role,
        "timestamp" to com.google.firebase.Timestamp.now()
    )
    db.collection("chats")
        .document(email)
        .collection("sessions")
        .document(sessionId)
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
