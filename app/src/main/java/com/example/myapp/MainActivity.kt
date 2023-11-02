package com.example.myapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class MainActivity : AppCompatActivity() {

    private lateinit var homefragment: Homefragment
    private lateinit var searchfragment: Searchfragment
    private lateinit var rankingfragment: Rankingfragment
    private lateinit var myfragment: Myfragment

    companion object{
        const val TAG : String = "로그"
        
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //레이아웃과 연결
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: MainActivity")

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNavigationView.setOnItemSelectedListener(onBottomNavItemSelectedListner)

        homefragment = Homefragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragments_frame, homefragment).commit()
    }


    private val onBottomNavItemSelectedListner = NavigationBarView.OnItemSelectedListener {

        when (it.itemId) {
            R.id.home -> {
                Log.d(TAG, "onNavigationItemSelected: 홈버튼 클릭")
                homefragment = Homefragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, homefragment).commit()
            }

            R.id.search -> {
                Log.d(TAG, "onNavigationItemSelected: 서치버튼 클릭")
                searchfragment = Searchfragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, searchfragment).commit()
            }

            R.id.ranking -> {
                Log.d(TAG, "onNavigationItemSelected: 랭킹버튼 클릭")
                rankingfragment = Rankingfragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, rankingfragment).commit()
            }

            R.id.my -> {
                Log.d(TAG, "onNavigationItemSelected: 마이버튼 클릭")
                var myfragment = Myfragment()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                bundle.putString("destinationUid", uid)
                myfragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, myfragment).commit()

            }
        }


        true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Myfragment.PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            var imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
            storageRef.putFile(imageUri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                var map = HashMap<String,Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)
            }
        }
    }

}