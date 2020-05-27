package com.taquangtu.forus.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

open class BaseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
    }
    fun toast(content:String?){
        Toast.makeText(context,content, Toast.LENGTH_SHORT).show()
    }
    fun <T: View> findView(id:Int):T{
        return view!!.findViewById(id)
    }
}