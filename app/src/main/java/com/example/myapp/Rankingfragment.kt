package com.example.myapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class Rankingfragment : Fragment(){
    companion object{
        const val TAG : String = "로그"

        fun newInstance() : Rankingfragment {

            return Rankingfragment()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Rankingfragment - onCreate() called")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "Rankingfragment - onAttach() called")
    }


    //프레그먼트와 레이아웃 연결
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "Rankingfragment - onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_ranking, container, false)

        return view
    }
}