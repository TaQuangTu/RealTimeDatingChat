package com.taquangtu.forus.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.taquangtu.forus.R
import com.taquangtu.forus.models.Message.Companion.REACTION_ANGRY
import com.taquangtu.forus.models.Message.Companion.REACTION_DIS_LIKE
import com.taquangtu.forus.models.Message.Companion.REACTION_HAHA
import com.taquangtu.forus.models.Message.Companion.REACTION_HEART
import com.taquangtu.forus.models.Message.Companion.REACTION_LIKE
import com.taquangtu.forus.models.Message.Companion.REACTION_SAD
import com.taquangtu.forus.models.Message.Companion.REACTION_WOW

class BottomOptionsDialog : BottomSheetDialogFragment(), View.OnClickListener {
    lateinit var listener: OnOptionSelection
    lateinit var tvCopy: TextView
    lateinit var tvReply: TextView
    lateinit var imvHeart: ImageView
    lateinit var imvHaha: ImageView
    lateinit var imvWow: ImageView
    lateinit var imvSad: ImageView
    lateinit var imvAngry: ImageView
    lateinit var imvLike: ImageView
    lateinit var imvDisLike: ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViews()
    }

    interface OnOptionSelection {
        fun onReactionSelected(reaction: Int)
        fun onOptionSelected(view: View)
    }

    fun mapViews() {

        if (view != null) {
            tvCopy = view!!.findViewById(R.id.tvCopy)
            tvCopy.setOnClickListener(this)

            tvReply = view!!.findViewById(R.id.tvReply)
            tvReply.setOnClickListener(this)

            imvHeart = view!!.findViewById(R.id.imvHeart)
            imvHeart.setOnClickListener(this)

            imvHaha = view!!.findViewById(R.id.imvHaha)
            imvHaha.setOnClickListener(this)

            imvWow = view!!.findViewById(R.id.imvWow)
            imvWow.setOnClickListener(this)

            imvAngry = view!!.findViewById(R.id.imvAngry)
            imvAngry.setOnClickListener(this)

            imvSad = view!!.findViewById(R.id.imvSad)
            imvSad.setOnClickListener(this)

            imvLike = view!!.findViewById(R.id.imvLike)
            imvLike.setOnClickListener(this)

            imvDisLike = view!!.findViewById(R.id.imvDisLike)
            imvDisLike.setOnClickListener(this)

        }

    }

    override fun onClick(v: View?) {
        if (v == tvCopy) {
            listener.onOptionSelected(v)
        } else if (v == tvReply) {
            listener.onOptionSelected(v)
        } else if (v == imvHeart) {
            listener.onReactionSelected(REACTION_HEART)
        } else if (v == imvHaha) {
            listener.onReactionSelected(REACTION_HAHA)
        } else if (v == imvWow) {
            listener.onReactionSelected(REACTION_WOW)
        } else if (v == imvSad) {
            listener.onReactionSelected(REACTION_SAD)
        } else if (v == imvAngry) {
            listener.onReactionSelected(REACTION_ANGRY)
        } else if (v == imvLike) {
            listener.onReactionSelected(REACTION_LIKE)
        } else if (v == imvDisLike) {
            listener.onReactionSelected(REACTION_DIS_LIKE)
        }
        dismiss()
    }
}