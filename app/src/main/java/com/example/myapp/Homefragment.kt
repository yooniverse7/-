package com.example.myapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class Homefragment : Fragment(){
    companion object{
        const val TAG : String = "로그"

        fun newInstance() : Homefragment {

            return Homefragment()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Homefragment - onCreate() called")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "Homefragment - onAttach() called")
    }


    //프레그먼트와 레이아웃 연결
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "Homefragment - onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }
}