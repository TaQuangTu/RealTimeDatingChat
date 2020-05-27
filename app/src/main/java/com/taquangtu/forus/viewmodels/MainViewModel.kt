package com.taquangtu.forus.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.taquangtu.forus.base.BaseViewModel
import com.taquangtu.forus.contexts.AppContext
import com.taquangtu.forus.helpers.TimeHelper
import com.taquangtu.forus.models.Message
import com.taquangtu.forus.models.Message.Companion.IMAGE_TAG
import com.taquangtu.forus.models.Message.Companion.REACTION_NONE

class MainViewModel : BaseViewModel() {
    var mFirebaseDataBase = FirebaseDatabase.getInstance()
    var mChatRef = mFirebaseDataBase.getReference("chats")
    var mLiveMessList: MutableLiveData<ArrayList<Message>> = MutableLiveData()
    var mLiveNewMess = MutableLiveData<Message>()
    var mLiveItemChange = MutableLiveData<Message>()
    fun loadMessages(loadMore: Boolean) {
        var numMore = 0
        if (loadMore) {
            numMore += 50
        }
        mChatRef.child(AppContext.roomId).limitToLast(mLiveMessList.value?.size ?: 50 + numMore)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    mLiveMessage.value = p0.message
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val messagesList = ArrayList<Message>()
                    p0.children.forEach {
                        var message = it.getValue(Message::class.java)
                        messagesList.add(message!!)
                    }
                    mLiveMessList.value = messagesList
                }
            })
    }

    fun listenNewMess() {
        mChatRef.child(AppContext.roomId).limitToLast(50)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    mLiveMessage.value = p0.message
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    val mess = p0.getValue(Message::class.java)
                    mLiveItemChange.value = mess
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val mess = p0.getValue(Message::class.java)
                    mLiveNewMess.value = mess
                    if (mLiveMessList.value == null) {
                        mLiveMessList.value = ArrayList()
                    }
                    for (i in mLiveMessList.value!!.size - 1 downTo 0) {
                        if (mLiveMessList.value!![i].id.equals(mess!!.id)) {
                            return
                        }
                    }
                    mLiveMessList.value!!.add(mess!!)
                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }

            })
    }

    fun pushMessage(userId: String, room: Int, content: String, reply: String?) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = mChatRef.child(AppContext.roomId).push().key
        if (key == null) {
            mLiveMessage.value = "No key to send message"
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

        mChatRef.child(AppContext.roomId).updateChildren(childUpdates).addOnSuccessListener {

        }
            .addOnFailureListener {
                mLiveMessage.value = it.localizedMessage
            }
    }

    fun changeReaction(mSelectedMessage: Message, reaction: Int) {
        mSelectedMessage.reaction = reaction
        val messageValues = mSelectedMessage.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["${mSelectedMessage.id}"] = messageValues
        mChatRef.child(AppContext.roomId).updateChildren(childUpdates).addOnSuccessListener {

        }
            .addOnFailureListener {
                mLiveMessage.value = it.localizedMessage
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
                mLiveMessage.value = it.localizedMessage
            }
    }

    fun deleteOldMessages() {
        mChatRef.child(AppContext.roomId).orderByChild("time").limitToFirst(200)
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
                mLiveMessage.value = it.localizedMessage
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
                    mLiveMessage.value = "Wait for sending"
                } else {
                    mLiveMessage.value = "Fail when send image"
                }
            }
    }
}
