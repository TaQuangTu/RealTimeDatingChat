package com.taquangtu.forus.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.taquangtu.forus.contexts.AppContext
import com.taquangtu.forus.base.BaseViewModel
import com.taquangtu.forus.helpers.TimeHelper
import com.taquangtu.forus.models.Message
import com.taquangtu.forus.models.Message.Companion.IMAGE_TAG
import com.taquangtu.forus.models.Message.Companion.REACTION_NONE

class MainViewModel : BaseViewModel() {
    var mLoadedMessages = 50
    var firebaseDataBase = FirebaseDatabase.getInstance()
    var chatRef = firebaseDataBase.getReference("chats")
    var liveData: MutableLiveData<ArrayList<Message>> = MutableLiveData()
    var liveNewMess = MutableLiveData<Message>()
    var liveItemChange = MutableLiveData<Message>()
    fun loadMessages(loadMore: Boolean) {
        chatRef.child(AppContext.roomId).limitToLast(mLoadedMessages)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    liveError.value = p0.message
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val messagesList = ArrayList<Message>()
                    p0.children.forEach {
                        var message = it.getValue(Message::class.java)
                        messagesList.add(message!!)
                    }
                    liveData.value = messagesList
                    if (loadMore)
                        mLoadedMessages += 50
                }
            })
    }

    fun listenNewMess() {
        chatRef.child(AppContext.roomId).limitToLast(1)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    liveError.value = p0.message
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val mess = p0.getValue(Message::class.java)
                    liveNewMess.value = mess
                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }

            })
    }

    fun listenItemChange() {
        var limitToListen = 200 //listen on change of 200 nearest items
        chatRef.child(AppContext.roomId).limitToLast(limitToListen)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    liveError.value = p0.message
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    val mess = p0.getValue(Message::class.java)
                    liveItemChange.value = mess
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }

            })
    }

    fun pushMessage(userId: String, room: Int, content: String, reply: String?) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = chatRef.child(AppContext.roomId).push().key
        if (key == null) {
            liveError.value = "No key to send message"
            return
        }
        val time = System.currentTimeMillis()
        val mess = Message(content, time, userId, room, REACTION_NONE)
        mess.id = key
        if (reply != null) {
            mess.reply = reply
        }
        val messageValues = mess.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["$key"] = messageValues

        chatRef.child(AppContext.roomId).updateChildren(childUpdates).addOnSuccessListener {

        }
            .addOnFailureListener {
                liveError.value = it.localizedMessage
            }
    }

    fun changeReaction(mSelectedMessage: Message, reaction: Int) {
        mSelectedMessage.reaction = reaction
        val messageValues = mSelectedMessage.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["${mSelectedMessage.id}"] = messageValues
        chatRef.child(AppContext.roomId).updateChildren(childUpdates).addOnSuccessListener {

        }
            .addOnFailureListener {
                liveError.value = it.localizedMessage
            }
    }

    fun deleteOldImages() {
        val storage = Firebase.storage
        val listRef = storage.reference.child("" + AppContext.roomId + "/images")

        listRef.listAll()
            .addOnSuccessListener { listResult ->
                listResult.prefixes.forEach { prefix ->
                    val a = 1
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.
                }

                listResult.items.forEach { item ->
                    // All the items under listRef.
                    if (TimeHelper.greaterThan3Days(
                            item.name.toLong(),
                            System.currentTimeMillis()
                        )
                    ) {
                        listRef.child(item.name).delete()
                    }
                }
            }
            .addOnFailureListener {
                liveError.value = it.localizedMessage
            }
    }

    fun deleteOldMessages() {
        chatRef.child(AppContext.roomId).orderByChild("time").limitToFirst(200)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.ref.removeValue();
                }
            })
    }

    fun sendImage(mImageUri: Uri?) {
        val storage = Firebase.storage
        val time = System.currentTimeMillis()
        storage.reference.child("" + AppContext.roomId + "/images/$time").putFile(mImageUri!!)
            .addOnFailureListener {
                liveError.value = it.localizedMessage
            }.addOnSuccessListener {

            }.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storage.reference.child("" + AppContext.roomId + "/images/$time").downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    pushMessage(
                        AppContext.userId,
                        AppContext.roomId.toInt(),
                        IMAGE_TAG + task.result.toString(),
                        null
                    )
                    liveError.value = "Wait for sending"
                } else {
                    liveError.value = "Fail when send image"
                }
            }
    }
}
