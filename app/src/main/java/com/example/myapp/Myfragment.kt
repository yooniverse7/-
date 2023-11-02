package com.example.myapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView


@Suppress("DEPRECATION")
class Myfragment : Fragment() {

    private var fragmentView: View? = null
    private var firestore: FirebaseFirestore? = null
    private var uid: String? = null
    private var auth: FirebaseAuth? = null
    private val user = Firebase.auth.currentUser
    var currentUserUid: String? = null
    var currentUserEmail: String? = null

    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_my, container, false)
        fragmentView = view  // fragmentView를 초기화
        var account_profile = view?.findViewById<CircleImageView>(R.id.account_iv_profile)

        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid
        currentUserEmail = auth?.currentUser?.email

        currentUserUid = auth?.currentUser?.uid
        if (currentUserUid != null) {
            // Firestore에서 "post" 컬렉션에서 uid 필드가 현재 사용자의 uid와 일치하는 문서를 쿼리하고, 그 결과를 RecyclerView 어댑터에 설정
            FirebaseFirestore.getInstance().collection("post")
                .whereEqualTo("uid", currentUserUid)
                .get()
                .addOnSuccessListener { documents ->
                    val contents = documents.toObjects(ContentDTO::class.java)
                    contents.forEach { contentDTO ->
                        contentDTO.postId =
                            documents.documents.firstOrNull { it["imageUrl"] == contentDTO.imageUrl }?.id
                    }
                }
        }
        if (currentUserUid != null) {
            // Firestore에서 "users" 컬렉션에서 현재 사용자의 UID와 일치하는 문서를 쿼리하고, 그 결과를 사용하여 followCount 및 followingCount 값을 가져옴
            FirebaseFirestore.getInstance().collection("users")
                .document(currentUserUid!!)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val followerCount = documentSnapshot.getLong("followerCount")
                        val followingCount = documentSnapshot.getLong("followingCount")

                        if (followerCount != null && followingCount != null) {
                            // followerCount 값을 followcount TextView에 설정
                            val followcountTextView = view.findViewById<TextView>(R.id.account_follow)
                            followcountTextView.text = followerCount.toString()

                            // followingCount 값을 followingcount TextView에 설정
                            val followingcountTextView = view.findViewById<TextView>(R.id.account_following)
                            followingcountTextView.text = followingCount.toString()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // 실패 시 처리
                }
        }





        val go_to_make_playlist = view.findViewById<ImageButton>(R.id.go_to_make_playlist)
        go_to_make_playlist?.setOnClickListener {
            val intent = Intent(requireContext(), PostingActivity::class.java)
            startActivity(intent)
        }

        val logOut = view.findViewById<Button>(R.id.logout_btn)

        logOut?.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
        }

        val accountRecyclerView = view.findViewById<RecyclerView>(R.id.account_recyclerview)
        accountRecyclerView?.adapter = UserFragmentRecyclerViewAdapter()
        accountRecyclerView?.layoutManager = GridLayoutManager(requireActivity(), 3)

        val goToMakePlaylist = view.findViewById<ImageButton>(R.id.go_to_make_playlist)
        goToMakePlaylist?.setOnClickListener {
            val intent = Intent(requireContext(), PostingActivity::class.java)
            startActivity(intent)
        }

        fragmentView?.findViewById<CircleImageView>(R.id.account_iv_profile)?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)

        }
//        getFollowerAndFollowing()
        getProfileImage()
        return view
    }

    fun getProfileImage(){
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot == null) return@addSnapshotListener
            if (documentSnapshot.data != null) {
                val account_iv_profile = fragmentView?.findViewById<CircleImageView>(R.id.account_iv_profile)
                var url = documentSnapshot?.data!!["image"]
                if (account_iv_profile != null) {
                    Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop()).into(account_iv_profile)
                }
            }
        }
    }


    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        val account_post_count: TextView = fragmentView!!.findViewById(R.id.account_post_count)
        val account_email: TextView = fragmentView!!.findViewById(R.id.account_email)

        init {
            firestore?.collection("post")?.whereEqualTo("uid", uid)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                if (querySnapshot == null || querySnapshot.isEmpty) {
                    // 조건에 맞는 문서가 없거나 비어 있는 경우
                    // emailTest를 초기화하거나 기본값을 설정할 수 있습니다.
                    // 또는 다른 원하는 값 설정
                    account_email.text = currentUserEmail
                    contentDTOs.clear() // 기존 데이터 초기화
                } else {
                    // 조건에 맞는 문서가 있는 경우
                    val document = querySnapshot.documents[0]
                    val emailTest = document.getString("userId")

                    contentDTOs.clear() // 기존 데이터 초기화

                    for (snapshot in querySnapshot.documents) {
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }

                    account_email.text = emailTest
                }

                account_post_count.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val width = resources.displayMetrics.widthPixels / 3
            val imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
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
                val intent = Intent(activity, PostDetailActivity::class.java)
                intent.putExtra("imageUrl", contentDTOs[position].imageUrl)

                println(contentDTOs[position].imageUrl + "!!!!!!!!!!!!!!!!!")

                startActivity(intent)


            }


        }
    }




}
