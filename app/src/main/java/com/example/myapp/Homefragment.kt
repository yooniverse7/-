package com.example.myapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class Homefragment : Fragment(){


    var firestore: FirebaseFirestore? = null
    var uid : String? = null
    var userId : String? = null
    lateinit var myfragment: Myfragment

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        userId = FirebaseAuth.getInstance().currentUser?.email



        val rv_upload = view.findViewById<RecyclerView>(R.id.rv_upload)

        rv_upload.adapter = DetailViewRecyclerViewAdapter()
        rv_upload.layoutManager = LinearLayoutManager(activity)

        val heartImageButton = view.findViewById<ImageButton>(R.id.alarm_button)

        heartImageButton.setOnClickListener {
            val intent = Intent(activity, AlarmActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {

            firestore?.collection("post")?.orderBy("date", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreExec ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewholder = holder as CustomViewHolder
            //userId
            viewholder.tv_profile_nickname.text = contentDTOs!![position].userId

            //Image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl)
                .into(viewholder.iv_post_image)

            //Explain of content
            viewholder.tv_explain_post.text = contentDTOs!![position].title

            //likes
            viewholder.tv_favoritecounter_post.text =
                "Likes " + contentDTOs!![position].favoriteCount


            viewholder.tv_profile_nickname.setOnClickListener{
                val intent = Intent(context,OtherprofileActivity::class.java)
                val otherUser_email = contentDTOs!![position].userId
                val otherUser_uid = contentDTOs!![position].uid
                val bundle = Bundle()
                myfragment = Myfragment()
                if(otherUser_email == userId) {

                    bundle.putString("destinationUid", uid)
                    myfragment.arguments = bundle

                    val transaction = requireActivity().supportFragmentManager.beginTransaction()

                    transaction.replace(R.id.fragments_frame, myfragment)
                    transaction.addToBackStack(null)
                    transaction.commit()


                }else {

                    intent.putExtra("other_uid", otherUser_uid)
                    startActivity(intent)

                    println(otherUser_uid)



                }

            }


            //ProfileImage
//            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl)
//                .into(viewholder.iv_profile_image)

            //This code is when the button is clicked
            viewholder.iv_favorite_post.setOnClickListener {
                favoriteEvent(position)
            }
            //This code is when the page is loaded
            if (contentDTOs!![position].favorites.containsKey(uid)) {
                viewholder.iv_favorite_post.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                viewholder.iv_favorite_post.setImageResource(R.drawable.baseline_favorite_border_24)
            }
            viewholder.iv_comment_post.setOnClickListener { v ->
                val intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid",contentDTOs[position].uid)
                startActivity(intent)
            }
            viewholder.iv_post_image.setOnClickListener {
                val intent = Intent(activity, PostDetailActivity::class.java)
                intent.putExtra("postId", contentUidList[position])
                intent.putExtra("destinationUid",contentDTOs[position].uid)
                startActivity(intent)
            }

        }

        fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("post")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)) { // 좋아요가 이미 눌러져 있는 경우
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                    contentDTO?.favorites?.remove(uid)
                } else {
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                    contentDTO?.favorites?.set(uid!!, true)
                    favoriteAlarm(contentDTOs[position].uid!!)
                }
                transaction.set(tsDoc, contentDTO)
            }
        }

        fun favoriteAlarm(destinationUid : String){
            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.kind = 0
            alarmDTO.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
        }


        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tv_profile_nickname: TextView = view.findViewById(R.id.tv_profile_nickname)
            val iv_post_image: ImageView = view.findViewById(R.id.iv_post_image)
            val tv_explain_post: TextView = view.findViewById(R.id.tv_explain_post)
            val iv_comment_post: ImageView = view.findViewById(R.id.iv_comment_post)
            val tv_favoritecounter_post: TextView = view.findViewById(R.id.tv_favoritecounter_post)
            val iv_profile_image: ImageView = view.findViewById(R.id.detailviewitem_profile_image)
            val iv_favorite_post: ImageView = view.findViewById(R.id.iv_favorite_post)


        }

    }









//    //프레그먼트와 레이아웃 연결
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        Log.d(TAG, "Homefragment - onCreateView() called")
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//
//        return view
//    }
}