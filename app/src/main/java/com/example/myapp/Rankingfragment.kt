package com.example.myapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class Rankingfragment : Fragment() {

    var firestore: FirebaseFirestore? = null
    var uid: String? = null

    //    private lateinit var titleAdapter : RankingAdapter
    companion object {
        const val TAG: String = "로그"

        fun newInstance(): Rankingfragment {

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
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        val view = inflater.inflate(R.layout.fragment_ranking, container, false)
        firestore = FirebaseFirestore.getInstance()

        val ranking_recycler = view.findViewById<RecyclerView>(R.id.ranking)
        val adapter = RankingViewRecyclerViewAdapter()


        firestore?.collection("post")
            ?.orderBy("favoriteCount", Query.Direction.DESCENDING)
            ?.get()
            ?.addOnSuccessListener { querySnapshot ->
                val contentList = ArrayList<String>()
                val contentList2 = ArrayList<Int>()

                for (document in querySnapshot.documents) {
                    val title = document.getString("title")
                    title?.let {
                        contentList.add(it)
                    }

                    val favoriteCount = document.getLong("favoriteCount") // favoriteCount를 Int로 가져옴
                    favoriteCount?.let {
                        contentList2.add(it.toInt())
                    }
                }

                // RecyclerView 어댑터에 데이터 설정
                adapter.setData(contentList, contentList2)

                // RecyclerView에 어댑터 설정
                ranking_recycler.adapter = adapter
                ranking_recycler.layoutManager = LinearLayoutManager(activity)
            }
            ?.addOnFailureListener { e ->
                Log.e(TAG, "Error getting documents: $e")
            }

        return view

    }

    inner class RankingViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentList: ArrayList<String> = arrayListOf()
        var favoriteCountList: ArrayList<Int> = arrayListOf() // 추가
        private val numberList = (1..10).toList()

        // 데이터 설정 메서드
        fun setData(data: ArrayList<String>, favoriteCountData: ArrayList<Int>) { // 수정
            contentList = data
            favoriteCountList = favoriteCountData // 추가
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_ranking, parent, false)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return contentList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = holder as CustomViewHolder
            val title = contentList[position]
            val favoriteCount = favoriteCountList[position] // 수정
            val number = numberList[position]
            viewHolder.pl_title.text = title
            viewHolder.like_count.text = favoriteCount.toString() // 추가
            viewHolder.count.text = number.toString()

            // 아이템 클릭 리스너 설정
            holder.itemView.setOnClickListener {
                // Intent를 사용하여 postdetailActivity로 이동
                val intent = Intent(context, PostDetailActivity::class.java)
                // 클릭한 문자열을 intent에 추가
                intent.putExtra("title", title)
                // postdetailActivity로 이동
                startActivity(intent)
            }
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val pl_title: TextView = view.findViewById(R.id.pl_title)
            val like_count: TextView = view.findViewById(R.id.ranking_likecount)
            val count: TextView = view.findViewById(R.id.ranking_count)
        }
    }
}