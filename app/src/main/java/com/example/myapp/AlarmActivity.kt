package com.example.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AlarmActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        val alarm_recyclerview = findViewById<RecyclerView>(R.id.alarm_recyclerview)


        alarm_recyclerview.adapter = AlarmRecyclerviewAdapter()
        alarm_recyclerview.layoutManager = LinearLayoutManager(this)
    }

    inner class AlarmRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid",uid).addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                alarmDTOList.clear()
                if(querySnapshot == null) return@addSnapshotListener

                for (snapshot in querySnapshot.documents){
                    alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val alarmTextView: TextView = view.findViewById(R.id.alarmview_comment_textView)
//            val profileTextView: TextView = view.findViewById(R.id.alarmview_profile_textView)
        }


        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is CustomViewHolder) {
                val alarm = alarmDTOList[position]
                when (alarmDTOList[position].kind) {
                    0 -> {
                        val str_0 = alarmDTOList[position].userId + "님이 좋아요를 누르셨습니다."
                        holder.alarmTextView.text = str_0
                    }
                    1 -> {
                        val str_0 = alarmDTOList[position].userId + "님이 댓글을 다셨습니다." + " of " + alarmDTOList[position].message
                        holder.alarmTextView.text = str_0
                    }
                }

            }


        }
    }

}