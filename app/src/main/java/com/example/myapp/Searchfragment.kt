package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore


class Searchfragment : Fragment() {
    var firestore: FirebaseFirestore? = null


    companion object {
        const val TAG: String = "로그"

        fun newInstance(): Searchfragment {
            return Searchfragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_searh, container, false)

        val searchEditText = view.findViewById<EditText>(R.id.input)
        val searchButton = view.findViewById<Button>(R.id.seach_click)
        firestore = FirebaseFirestore.getInstance()

        searchButton.setOnClickListener {
            val searchString = searchEditText.text.toString()
            println("Search string is $searchString") // 변수에 사용f자가 입력한 값 들어 왔는지 확인

//             searchString을 MainActivity로 보내기 위한 코드
            val intent = Intent(context, SearchResultsFullActivity::class.java)
            val bundle = Bundle()
            bundle.putString("searchString", searchString)
            intent.putExtras(bundle)
            startActivity(intent)

            // Firestore 쿼리를 생성하여 "post" 컬렉션에서 title 필드와 일치하는 문서 검색

            // Firestore Collection Group Query를 사용하여 부분 일치 문자열 검색
//            firestore?.collection("post")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                for
//            }
        }
    return view
    }
}
