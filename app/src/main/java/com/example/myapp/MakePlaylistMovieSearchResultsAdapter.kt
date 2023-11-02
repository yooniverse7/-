package com.example.myapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapp.Homefragment.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MakePlaylistMovieSearchResultsAdapter(
    private val context: Context,
    private val imageUrlList: List<String>,
    private val nameList: List<String>,
    private val textList: List<String>,
    private val toInfoUrlList: List<String>
) : RecyclerView.Adapter<MakePlaylistMovieSearchResultsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.adapter_make_playlist_movie_search_results, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Firestore 초기화
        val firestore = FirebaseFirestore.getInstance()

        val imageUrl = imageUrlList[position]
        val name = nameList[position]
        val text = textList[position]
        val toInfoUrl = toInfoUrlList[position] // 해당 위치의 toInfoUrl 가져오기

        // 이미지 로딩 및 설정
        Glide.with(context)
            .load(imageUrl)
            .into(holder.imageView)

        // 이름과 텍스트 설정
        holder.nameTextView.text = name
        holder.textTextView.text = text
        holder.toInfoUrlTextView.text = toInfoUrl
        // "추가" 버튼 클릭 처리
        holder.addButton.setOnClickListener {
            val curPos: Int = holder.adapterPosition
            val imageUrl = imageUrlList[curPos]
            val name = nameList[curPos]
            val text = textList[curPos]
            val url = toInfoUrlList[curPos]

            // 현재 사용자의 uid 가져오기
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            // 영화 정보를 HashMap에 저장
            val data = hashMapOf(
                "imageUrl" to imageUrl,
                "nameTextView" to name,
                "textTextView" to text,
                "url" to url
            )

            val postCollection = firestore.collection("post")

            postCollection.orderBy("date", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val latestPostId = documents.documents[0].id
                        println(latestPostId + "출력되시오!!!!!!!!!!!!!!!!!!!!")
                        // latestPostId 변수에 최근에 생성된 문서의 ID가 저장됩니다.
                        if (userId != null) {
                            firestore.collection("post").document(latestPostId)
                                .collection("movies")
                                .add(data)
                                .addOnSuccessListener { documentReference ->
                                    println(latestPostId+"println(latestPostId)래요")
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot added with ID: ${documentReference.id}"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }


    override fun getItemCount(): Int {
        return imageUrlList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_poster)
        val nameTextView: TextView = itemView.findViewById(R.id.tv_movie_name)
        val textTextView: TextView = itemView.findViewById(R.id.tv_movie_genre_year)
        val toInfoUrlTextView: TextView = itemView.findViewById(R.id.tv_to_info_url) // 추가된 뷰 요소
        val addButton: CheckBox = itemView.findViewById<CheckBox>(R.id.bt_add_playlist) // 추가 버튼

    }
}