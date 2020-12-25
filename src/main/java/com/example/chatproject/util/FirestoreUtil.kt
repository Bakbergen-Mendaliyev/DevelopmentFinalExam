package com.example.chatproject.util

import android.content.Context
import android.util.Log
import com.example.chatproject.RecycleView.item.PersoonItem
import com.example.chatproject.RecycleView.item.TextMessageItem
import com.example.chatproject.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.kotlinandroidextensions.Item


object FirestoreUtil {
    private val firestoreInstanse: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstanse.document(
            "users/${FirebaseAuth.getInstance().uid
                ?: throw NullPointerException("UID is null")}"
        )

    private val chatChannelIsCollectionRef = firestoreInstanse.collection("chatChannels")

    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {

        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = User(
                    FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    "", null
                )
                currentUserDocRef.set(newUser).addOnSuccessListener { onComplete() }
            } else
                onComplete()
        }
    }

    fun updateCurrentUser(name: String = "", bio: String = "", profilePicturePath: String? = null){
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (bio.isNotBlank()) userFieldMap["bio"] = bio
        if (profilePicturePath != null)
            userFieldMap["profilePicturePath"] = profilePicturePath
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit){
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration{
        return firestoreInstanse.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if(firebaseFirestoreException != null){
                Log.e("FIRESTORE", "Users listener error", firebaseFirestoreException)
                return@addSnapshotListener
            }

                val items = mutableListOf<Item>()
                if (querySnapshot != null) {
                    querySnapshot.documents.forEach{
                        if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                            items.add(PersoonItem(it.toObject(User::class.java)!!,it.id, context))

                    }
                }
                onListen(items)
            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()


    fun getOrCreateChatChannel(otherUserId: String,
    onComplete: (channelId:String) -> Unit){
        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
                if(it.exists()){
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelIsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserDocRef
                    .collection("engagedChatChannels")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstanse.collection("users").document(otherUserId)
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    fun addChatMessageListener(channelId: String, context: Context,
    onListen: (List<Item>) -> Unit):ListenerRegistration {
        return chatChannelIsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "ChatMessageListener error", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                if (querySnapshot != null) {
                    querySnapshot.documents.forEach {
                        if (it["type"] == MessageType.TEXT)
                            items.add(
                                TextMessageItem(
                                    it.toObject(TextMessage::class.java)!!,
                                    context
                                )
                            )
                        else
                            TODO("Add image section")



                    }

                }
                onListen(items)
            }
    }

    fun sendMessage(message: TextMessage, channelId: String){
        chatChannelIsCollectionRef.document(channelId)
            .collection("messages")
            .add(message)
    }
}
