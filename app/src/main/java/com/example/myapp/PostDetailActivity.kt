package com.example.myapp

import android.content.Intent
import android.media.Image
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapp.Homefragment.Companion.TAG

import com.google.firebase.firestore.FirebaseFirestore

class PostDetailActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private var postId: String? = null
    private var imageUrl: String? = null
    private var title: String? = null
    private lateinit var recyclerView: RecyclerView
    private val moviesList = ArrayList<MovieProfiles>()  // 영화 데이터 리스트
    private lateinit var imageAdapter: SearchResultsFullAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)
        val imageUrlList = mutableListOf<String>()
        val nameList = mutableListOf<String>()
        val textList = mutableListOf<String>()
        val urlList = mutableListOf<String>()

        firestore = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.rv_account_movie)

        val iv_account_comment = findViewById<ImageView>(R.id.iv_account_comment)
        iv_account_comment.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java)
            startActivity(intent)
        }

        postId = intent.getStringExtra("postId")
        imageUrl = intent.getStringExtra("imageUrl")
        title = intent.getStringExtra("title")

        if (postId != null) {
            // Firestore에서 해당 postId에 해당하는 문서를 쿼리하고, 그 결과를 UI에 설정
            firestore.collection("post").document(postId!!)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val contentDTO = document.toObject(ContentDTO::class.java)
                        findViewById<TextView>(R.id.tv_account_nickname).text = contentDTO?.userId
                        findViewById<TextView>(R.id.tv_account_title).text = contentDTO?.title
                        findViewById<TextView>(R.id.tv_account_explain).text = contentDTO?.story
                        findViewById<TextView>(R.id.tv_tag1).text = contentDTO?.tag1
                        findViewById<TextView>(R.id.tv_tag2).text = contentDTO?.tag2
                        findViewById<TextView>(R.id.tv_tag3).text = contentDTO?.tag3
                        if (!contentDTO?.imageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(contentDTO?.imageUrl)
                                .into(findViewById(R.id.iv_account_image))
                        }

                        // postId를 사용하여 해당 post 문서 참조
                        firestore.collection("post").document(postId!!)
                            .get()
                            .addOnSuccessListener { postDocument ->
                                if (postDocument != null) {
                                    // 이미 contentDTO에서 UI 설정 코드가 있는 것 같으므로 유지

                                    // "movies" 컬렉션 참조
                                    val moviesCollection = firestore.collection("post").document(postId!!)
                                        .collection("movies")

                                    moviesCollection.get().addOnSuccessListener { moviesQuerySnapshot ->
                                        val imageUrlList = mutableListOf<String>()
                                        val nameList = mutableListOf<String>()
                                        val textList = mutableListOf<String>()
                                        val urlList = mutableListOf<String>()

                                        for (movieDocument in moviesQuerySnapshot.documents) {
                                            // 각 문서에서 필드 값을 가져와서 출력
                                            val imageUrl = movieDocument.getString("imageUrl")
                                            val nameTextView = movieDocument.getString("nameTextView")
                                            val textTextView = movieDocument.getString("textTextView")
                                            val url = movieDocument.getString("url")

                                            // 데이터를 리스트에 추가
                                            imageUrlList.add(imageUrl ?: "") // 빈 문자열로 처리하거나 에러 처리 방식을 선택
                                            nameList.add(nameTextView ?: "")
                                            textList.add(textTextView ?: "")
                                            urlList.add(url ?: "")
                                        }

                                        // RecyclerView에 데이터를 설정
                                        imageAdapter = SearchResultsFullAdapter(this@PostDetailActivity, imageUrlList, nameList, textList, urlList)
                                        recyclerView.adapter = imageAdapter
                                        recyclerView.layoutManager = LinearLayoutManager(this@PostDetailActivity)

                                        // 어댑터에 변경 사항을 알림
                                        imageAdapter.notifyDataSetChanged()
                                    }.addOnFailureListener { e ->
                                        Log.w(TAG, "Error getting movies collection: ", e)
                                    }
                                }
                            }
                    }
                }
        }else if (imageUrl != null) {
            // Firestore에서 해당 imageUrl과 일치하는 문서를 쿼리하고, 그 결과를 UI에 설정
            firestore.collection("post")
                .whereEqualTo("imageUrl", imageUrl)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val contentDTO = document.toObject(ContentDTO::class.java)
                        findViewById<TextView>(R.id.tv_account_nickname).text = contentDTO.userId
                        findViewById<TextView>(R.id.tv_account_title).text = contentDTO.title
                        findViewById<TextView>(R.id.tv_account_explain).text = contentDTO.story
                        findViewById<TextView>(R.id.tv_tag1).text = contentDTO.tag1
                        findViewById<TextView>(R.id.tv_tag2).text = contentDTO.tag2
                        findViewById<TextView>(R.id.tv_tag3).text = contentDTO.tag3

                        if (!contentDTO.imageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(contentDTO.imageUrl)
                                .into(findViewById(R.id.iv_account_image))
                        }
                    }

                    // postId를 사용하여 해당 post 문서 참조
                    firestore.collection("post").document(postId!!)
                        .get()
                        .addOnSuccessListener { postDocument ->
                            if (postDocument != null) {
                                // 이미 contentDTO에서 UI 설정 코드가 있는 것 같으므로 유지

                                // "movies" 컬렉션 참조
                                val moviesCollection = firestore.collection("post").document(postId!!)
                                    .collection("movies")

                                moviesCollection.get().addOnSuccessListener { moviesQuerySnapshot ->
                                    val imageUrlList = mutableListOf<String>()
                                    val nameList = mutableListOf<String>()
                                    val textList = mutableListOf<String>()
                                    val urlList = mutableListOf<String>()

                                    for (movieDocument in moviesQuerySnapshot.documents) {
                                        // 각 문서에서 필드 값을 가져와서 출력
                                        val imageUrl = movieDocument.getString("imageUrl")
                                        val nameTextView = movieDocument.getString("nameTextView")
                                        val textTextView = movieDocument.getString("textTextView")
                                        val url = movieDocument.getString("url")

                                        // 데이터를 리스트에 추가
                                        imageUrlList.add(imageUrl ?: "") // 빈 문자열로 처리하거나 에러 처리 방식을 선택
                                        nameList.add(nameTextView ?: "")
                                        textList.add(textTextView ?: "")
                                        urlList.add(url ?: "")
                                    }

                                    // RecyclerView에 데이터를 설정
                                    imageAdapter = SearchResultsFullAdapter(this@PostDetailActivity, imageUrlList, nameList, textList, urlList)
                                    recyclerView.adapter = imageAdapter
                                    recyclerView.layoutManager = LinearLayoutManager(this@PostDetailActivity)

                                    // 어댑터에 변경 사항을 알림
                                    imageAdapter.notifyDataSetChanged()
                                }.addOnFailureListener { e ->
                                    Log.w(TAG, "Error getting movies collection: ", e)
                                }
                            }
                        }
                }
        }else if(title != null) {
                // Firestore에서 해당 imageUrl과 일치하는 문서를 쿼리하고, 그 결과를 UI에 설정
                firestore.collection("post")
                    .whereEqualTo("title", title)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val contentDTO = document.toObject(ContentDTO::class.java)
                            findViewById<TextView>(R.id.tv_account_nickname).text = contentDTO.userId
                            findViewById<TextView>(R.id.tv_account_title).text = contentDTO.title
                            findViewById<TextView>(R.id.tv_account_explain).text = contentDTO.story
                            findViewById<TextView>(R.id.tv_tag1).text = contentDTO.tag1
                            findViewById<TextView>(R.id.tv_tag2).text = contentDTO.tag2
                            findViewById<TextView>(R.id.tv_tag3).text = contentDTO.tag3

                            if (!contentDTO.imageUrl.isNullOrEmpty()) {
                                Glide.with(this)
                                    .load(contentDTO.imageUrl)
                                    .into(findViewById(R.id.iv_account_image))
                            }
                        }

                        // postId를 사용하여 해당 post 문서 참조
                        firestore.collection("post").document(postId!!)
                            .get()
                            .addOnSuccessListener { postDocument ->
                                if (postDocument != null) {
                                    // 이미 contentDTO에서 UI 설정 코드가 있는 것 같으므로 유지

                                    // "movies" 컬렉션 참조
                                    val moviesCollection = firestore.collection("post").document(postId!!)
                                        .collection("movies")

                                    moviesCollection.get().addOnSuccessListener { moviesQuerySnapshot ->
                                        val imageUrlList = mutableListOf<String>()
                                        val nameList = mutableListOf<String>()
                                        val textList = mutableListOf<String>()
                                        val urlList = mutableListOf<String>()

                                        for (movieDocument in moviesQuerySnapshot.documents) {
                                            // 각 문서에서 필드 값을 가져와서 출력
                                            val imageUrl = movieDocument.getString("imageUrl")
                                            val nameTextView = movieDocument.getString("nameTextView")
                                            val textTextView = movieDocument.getString("textTextView")
                                            val url = movieDocument.getString("url")

                                            // 데이터를 리스트에 추가
                                            imageUrlList.add(imageUrl ?: "") // 빈 문자열로 처리하거나 에러 처리 방식을 선택
                                            nameList.add(nameTextView ?: "")
                                            textList.add(textTextView ?: "")
                                            urlList.add(url ?: "")
                                        }

                                        // RecyclerView에 데이터를 설정
                                        imageAdapter = SearchResultsFullAdapter(this@PostDetailActivity, imageUrlList, nameList, textList, urlList)
                                        recyclerView.adapter = imageAdapter
                                        recyclerView.layoutManager = LinearLayoutManager(this@PostDetailActivity)

                                        // 어댑터에 변경 사항을 알림
                                        imageAdapter.notifyDataSetChanged()
                                    }.addOnFailureListener { e ->
                                        Log.w(TAG, "Error getting movies collection: ", e)
                                    }
                                }
                            }
                    }
            }

        }


    }



