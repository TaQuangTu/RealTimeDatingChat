package com.taquangtu.forus.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taquangtu.forus.contexts.AppContext
import com.taquangtu.forus.contexts.ForUsApplication
import com.taquangtu.forus.R
import com.taquangtu.forus.interfaces.ItemSelectionListener
import com.taquangtu.forus.models.Message
import com.taquangtu.forus.models.Message.Companion.REACTION_ANGRY
import com.taquangtu.forus.models.Message.Companion.REACTION_DIS_LIKE
import com.taquangtu.forus.models.Message.Companion.REACTION_HAHA
import com.taquangtu.forus.models.Message.Companion.REACTION_HEART
import com.taquangtu.forus.models.Message.Companion.REACTION_LIKE
import com.taquangtu.forus.models.Message.Companion.REACTION_SAD
import com.taquangtu.forus.models.Message.Companion.REACTION_WOW
import java.text.SimpleDateFormat


class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    var messages = ArrayList<Message>()
    lateinit var listener: ItemSelectionListener<Message>

    class ViewHolder : RecyclerView.ViewHolder {
        val imvImage: ImageView
        val tvReply: TextView
        val imvReaction: ImageView
        val tvContent: TextView
        val tvTimeStamp: TextView
        val lnMessageContainer: LinearLayout

        constructor(itemView: View) : super(itemView) {
            imvImage = itemView.findViewById(R.id.imvImage)
            tvContent = itemView.findViewById(R.id.tvContent)
            tvTimeStamp = itemView.findViewById(R.id.tvTime)
            lnMessageContainer = itemView.findViewById(R.id.lnMessageContener)
            tvReply = itemView.findViewById(R.id.tvReply)
            imvReaction = itemView.findViewById(R.id.imvReaction)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mess = messages.get(position)
        holder.tvContent.text = mess.content
        val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
        val time = formatter.format(mess.time)
        holder.tvTimeStamp.text = time
        if (mess.reaction == 0) holder.imvReaction.visibility = GONE
        else {
            holder.imvReaction.visibility = VISIBLE
            val context = ForUsApplication.context
            if (mess.reaction == REACTION_HEART) holder.imvReaction.background =
                ContextCompat.getDrawable(context, R.drawable.ic_heart)
            if (mess.reaction == REACTION_HAHA) holder.imvReaction.background =
                ContextCompat.getDrawable(context, R.drawable.ic_haha)
            if (mess.reaction == REACTION_WOW) holder.imvReaction.background =
                ContextCompat.getDrawable(context, R.drawable.ic_wow)
            if (mess.reaction == REACTION_SAD) holder.imvReaction.background =
                ContextCompat.getDrawable(context, R.drawable.ic_sad)
            if (mess.reaction == REACTION_ANGRY) holder.imvReaction.background =
                ContextCompat.getDrawable(context, R.drawable.ic_angry)
            if (mess.reaction == REACTION_LIKE) holder.imvReaction.background =
                ContextCompat.getDrawable(context, R.drawable.ic_like)
            if (mess.reaction == REACTION_DIS_LIKE) holder.imvReaction.background =
                ContextCompat.getDrawable(context, R.drawable.ic_dislike)
        }
        if (mess.reply != null && !mess.reply.equals("")) {
            holder.tvReply.visibility = VISIBLE
            holder.tvReply.text = "'" + mess.reply + "'"
        } else {
            holder.tvReply.visibility = GONE
        }
        if (mess.content.contains(Message.IMAGE_TAG)) {
            holder.imvImage.visibility = VISIBLE
            holder.imvImage.setOnLongClickListener {
                val browse = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(mess.content.substring(Message.IMAGE_TAG.length))
                )
                startActivity(ForUsApplication.context, browse, null)
                true
            }
            holder.tvContent.visibility = GONE
            val imageUrl = mess.content.substring(Message.IMAGE_TAG.length)
            Glide.with(holder.itemView.context).load(imageUrl).into(holder.imvImage)
        } else {
            holder.lnMessageContainer.layoutParams.width = WRAP_CONTENT
            holder.imvImage.visibility = GONE
            holder.tvContent.visibility = VISIBLE
        }
        if (mess.userId.equals(AppContext.userId)) { //my own message
            val layoutParams = ConstraintLayout.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            )
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            holder.lnMessageContainer.layoutParams = layoutParams
            val drawable =
                ContextCompat.getDrawable(ForUsApplication.context, R.drawable.shape_solid_white)
            holder.tvContent.background = drawable
        } else { // income messages
            val layoutParams = ConstraintLayout.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            )
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            holder.lnMessageContainer.layoutParams = layoutParams
            val drawable =
                ContextCompat.getDrawable(ForUsApplication.context, R.drawable.shape_solid_color)
            holder.tvContent.background = drawable
        }
        holder.itemView.setOnLongClickListener {
            listener.onItemSelected(mess, position)
            true
        }
    }

    fun setData(messages: ArrayList<Message>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    fun newMessage(message: Message) {
        this.messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}