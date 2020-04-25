package com.taquangtu.forus.fragments

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.taquangtu.forus.contexts.AppContext
import com.taquangtu.forus.contexts.ForUsApplication
import com.taquangtu.forus.R
import com.taquangtu.forus.activities.LoginActivity
import com.taquangtu.forus.adapters.MessageAdapter
import com.taquangtu.forus.base.BaseFragment
import com.taquangtu.forus.dialogs.BottomOptionsDialog
import com.taquangtu.forus.interfaces.ItemSelectionListener
import com.taquangtu.forus.models.Message
import com.taquangtu.forus.services.ListenMessageService
import com.taquangtu.forus.viewmodels.LoginViewModel
import com.taquangtu.forus.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment : BaseFragment(), View.OnClickListener, ItemSelectionListener<Message>,
    BottomOptionsDialog.OnOptionSelection {
    lateinit var mBtnSend: Button
    lateinit var mEdtMessage: EditText
    lateinit var mRcvMessages: RecyclerView
    lateinit var mSrlContainer: SwipeRefreshLayout
    var mSelectedMessage: Message? = null
    lateinit var mCstReply: ConstraintLayout
    lateinit var mTvReply: TextView
    lateinit var mImvCloseReply: ImageView
    val optionsDialog = BottomOptionsDialog()

    companion object {
        fun newInstance() = MainFragment()
        val PICK_IMAGE_MULTIPLE = 1
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mapViews()
        optionsDialog.listener = this
    }

    override fun onResume() {
        super.onResume()
        AppContext.appIsVisible = true
    }

    override fun onStop() {
        super.onStop()
        AppContext.appIsVisible = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemReload) {
            // mSrlContainer.isRefreshing = true
            (mRcvMessages.adapter as MessageAdapter).setData(ArrayList())
            viewModel.loadMessages(false)
            removeTempInstances()
        } else if (item.itemId == R.id.itemCam) {
            val intent = Intent()
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(
                Intent.createChooser(intent, "Select multiple picture"),
                PICK_IMAGE_MULTIPLE
            )
        } else if (item.itemId == R.id.itemDeleteImages) {
            viewModel.deleteOldImages()
            (rcvMessages.adapter as MessageAdapter).setData(ArrayList())
        } else if (item.itemId == R.id.itemDeleteMessages) {
            viewModel.deleteOldMessages()
            (rcvMessages.adapter as MessageAdapter).setData(ArrayList())
        } else if (item.itemId == R.id.itemLogout) {
            LoginViewModel.logout()
            startActivity(Intent(context, LoginActivity::class.java))
            activity!!.finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            toast("Wait for sending")
            if (requestCode == PICK_IMAGE_MULTIPLE) {

                if (data!!.data != null) {
                    val mImageUri: Uri? = data.data
                    viewModel.sendImage(mImageUri)
                }
            } else {
                if (data!!.clipData != null) {
                    val mClipData = data!!.clipData;
                    val mArrayUri = ArrayList<Uri>();
                    for (i in 0 until mClipData!!.itemCount) {

                        val item = mClipData.getItemAt(i)
                        val uri = item.uri
                        viewModel.sendImage(uri)
                    }
                }
            }

        }
    }

    fun removeTempInstances() {
        //close reply box if need
        cstReply.visibility = GONE
        mSelectedMessage = null
    }

    private fun mapViews() {
        mSrlContainer = view!!.findViewById(R.id.srlContainer)
        mSrlContainer.isEnabled = true;
        mSrlContainer.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                viewModel.loadMessages(true)
                removeTempInstances()
            }
        })
        mCstReply = view!!.findViewById(R.id.cstReply)
        mTvReply = view!!.findViewById(R.id.tvReply)
        mImvCloseReply = view!!.findViewById(R.id.imvClose)
        mImvCloseReply.setOnClickListener(this)
        mBtnSend = view!!.findViewById(R.id.btnSend)
        mEdtMessage = view!!.findViewById(R.id.edtTextBox);
        mBtnSend.setOnClickListener(this)
        mRcvMessages = view!!.findViewById(R.id.rcvMessages)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setUpViewModels()
        setUpRecyclerView()
        ForUsApplication.context.startService(Intent(context, ListenMessageService::class.java))
    }

    private fun setUpRecyclerView() {
        mRcvMessages.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        var adapter = MessageAdapter()
        adapter.listener = this
        mRcvMessages.adapter = adapter
    }

    private fun setUpViewModels() {
        viewModel.loadMessages(false)
        viewModel.listenNewMess()
        viewModel.listenItemChange()
        viewModel.liveData.observe(viewLifecycleOwner, Observer { t ->
            mSrlContainer.isRefreshing = false
            if (t == null) {
                toast("No messages")
            } else {
                (mRcvMessages.adapter as MessageAdapter).setData(t)
                mRcvMessages.scrollToPosition((mRcvMessages.adapter as MessageAdapter).itemCount - 1)
            }
        })
        viewModel.liveNewMess.observe(viewLifecycleOwner, Observer { message ->
            mSrlContainer.isRefreshing = false
            newMessCome(message)
        })
        viewModel.liveItemChange.observe(viewLifecycleOwner, Observer { message ->
            val adapter = (mRcvMessages.adapter as MessageAdapter)
            val messages = adapter.messages
            messages.forEachIndexed { index, mess ->
                if (mess.id == message.id) {
                    messages.set(index, message)
                    adapter.notifyItemChanged(index)
                }
            }
        })
        viewModel.liveError.observe(viewLifecycleOwner, Observer { t ->
            mSrlContainer.isRefreshing = false
            toast(t)
        })
    }

    private fun newMessCome(message: Message?) {
        if (message != null) {
            (mRcvMessages.adapter as MessageAdapter).newMessage(message)
            mRcvMessages.scrollToPosition((mRcvMessages.adapter as MessageAdapter).itemCount - 1)
        }
    }

    override fun onClick(v: View?) {
        if (v == mBtnSend) {
            val text = mEdtMessage.text.toString().trim()
            var userId = AppContext.userId
            var room = AppContext.roomId.toInt()
            var reply: String? = null
            if (mSelectedMessage != null && mSelectedMessage!!.content != null && mCstReply.visibility == VISIBLE) {
                reply = mSelectedMessage!!.content
            }
            viewModel.pushMessage(userId, room, text, reply)
            mEdtMessage.setText("")
        }
        //close reply box if need
        cstReply.visibility = GONE
    }

    override fun onItemSelected(item: Message, position: Int) {
        mSelectedMessage = item
        optionsDialog.show(activity!!.supportFragmentManager, "dialog")
    }

    override fun onReactionSelected(reaction: Int) {
        if (mSelectedMessage != null) {
            if (mSelectedMessage!!.userId != AppContext.userId) {
                viewModel.changeReaction(mSelectedMessage!!, reaction)

            }
        }
    }

    override fun onOptionSelected(v: View) {
        if (v.id == R.id.tvCopy) {
            if (mSelectedMessage != null) {
                val cManager =
                    context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val cData = ClipData.newPlainText("text", mSelectedMessage?.content ?: "")
                cManager.setPrimaryClip(cData)
                toast("The content has been copied")
            }
        } else if (v.id == R.id.tvReply) {
            if (mSelectedMessage != null) {
                mCstReply.visibility = VISIBLE
                mTvReply.text = mSelectedMessage!!.content
            }
        }
    }
}
