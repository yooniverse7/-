package com.example.myapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment


class Myfragment : Fragment(){
    companion object{
        const val TAG : String = "로그"

        fun newInstance() : Myfragment {

            return Myfragment()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Myfragment - onCreate() called")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "Myfragment - onAttach() called")
    }


    //프레그먼트와 레이아웃 연결
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "Myfragment - onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_my, container, false)

        val go_to_make_playlist = view.findViewById<ImageButton>(R.id.go_to_make_playlist)
        go_to_make_playlist.setOnClickListener {
            val intent = Intent(requireContext(), PostingActivity::class.java)
            startActivity(intent)
        }




        return view
    }
}