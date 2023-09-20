package com.example.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

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
                myfragment = Myfragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, myfragment).commit()
            }
        }


        true

    }

}