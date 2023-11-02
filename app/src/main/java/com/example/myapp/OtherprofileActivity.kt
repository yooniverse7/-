package com.example.myapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class OtherprofileActivity : AppCompatActivity() {

    private var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
    var other_uid : String? = null
    var userEmail: String? = null
    var currentUserUid: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        other_uid = intent.getStringExtra("other_uid")
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid


        setContentView(R.layout.activity_otherprofile)

        val account_post_count: TextView = findViewById(R.id.account_post_count)
        val email: TextView = findViewById(R.id.account_email)

        val adapter = OtherUserRecylcerViewAdapter(account_post_count, email)
        val accountRecyclerView = findViewById<RecyclerView>(R.id.account_recyclerview)
        accountRecyclerView.adapter = adapter
        accountRecyclerView.layoutManager = GridLayoutManager(this, 3)

        getFollowerAndFollowing()

        val follow = findViewById<Button>(R.id.follow_btn)
        follow.setOnClickListener {
            requestFollow()
            println("클릭은 됐음")
        }

    }

    fun getFollowerAndFollowing(){
        firestore?.collection("users")?.document(other_uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot == null) return@addSnapshotListener
            var followDTO = documentSnapshot.toObject(UserInfo::class.java)
            if (followDTO?.followingCount != null){
                findViewById<TextView>(R.id.other_following)?.text = followDTO.followingCount?.toString()
            }
            if (followDTO?.followerCount != null){
                findViewById<TextView>(R.id.other_follow)?.text = followDTO.followerCount?.toString()
//                if (followDTO?.followers?.containsKey(currentUserUid!!) == true) {
//                    findViewById<Button>(R.id.follow_btn)?.text = getString(R.string.follow_cancel)
//                    findViewById<Button>(R.id.follow_btn)?.background?.setColorFilter(
//                        ContextCompat.getColor(
//                            R.color.colorLightGray
//                        )
//                    )
//
//                }
            }
        }
    }

    fun requestFollow() {
        val tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
        val tsDocFollower = firestore?.collection("users")?.document(other_uid!!)

        firestore?.runTransaction { transaction ->
            val followDTO = transaction.get(tsDocFollowing!!).toObject(UserInfo::class.java) ?: UserInfo()

            if (followDTO.followings.containsKey(other_uid)) {
                followDTO.followingCount -= 1
                followDTO.followers.remove(other_uid)
            } else {
                followDTO.followingCount += 1
                followDTO.followers[other_uid!!] = true
            }

            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }

        firestore?.runTransaction { transaction ->
            val followDTO = transaction.get(tsDocFollower!!).toObject(UserInfo::class.java) ?: UserInfo()

            if (followDTO.followers.containsKey(currentUserUid)) {
                followDTO.followerCount -= 1
                followDTO.followers.remove(currentUserUid)
            } else {
                followDTO.followerCount += 1
                followDTO.followers[currentUserUid!!] = true
            }

            getProfileImage()
            transaction.set(tsDocFollower, followDTO)
            return@runTransaction
        }

    }
    fun getProfileImage(){
        firestore?.collection("profileImages")?.document(other_uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot == null) return@addSnapshotListener
            if (documentSnapshot.data != null) {
                val account_iv_profile = findViewById<CircleImageView>(R.id.other_iv_profile)
                var url = documentSnapshot?.data!!["image"]
                if (account_iv_profile != null) {
                    Glide.with(this).load(url).apply(RequestOptions().circleCrop()).into(account_iv_profile)
                }
            }
        }
    }






    inner class OtherUserRecylcerViewAdapter(
        private val account_post_count: TextView,
        private val email: TextView
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("post")?.whereEqualTo("uid", other_uid)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                if (querySnapshot == null) return@addSnapshotListener

                contentDTOs.clear()

                for (snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }

                if (querySnapshot.documents.isNotEmpty()) {
                    val document = querySnapshot.documents[0]
                    val emailTest = document.getString("userId")
                    email.text = emailTest
                }

                account_post_count.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val width = resources.displayMetrics.widthPixels / 3
            val imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val imageView = (holder as CustomViewHolder).imageView
            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageView)

            holder.itemView.setOnClickListener {
                val intent = Intent(this@OtherprofileActivity, PostDetailActivity::class.java)
                intent.putExtra("imageUrl", contentDTOs[position].imageUrl)
                startActivity(intent)
            }
        }
    }
}