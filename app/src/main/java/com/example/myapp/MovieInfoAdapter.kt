package com.example.myapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieInfoAdapter(
    private val context: Context,
    private val nameList: List<String>,
    private val toInfoUrlList: List<String>
) : RecyclerView.Adapter<MovieInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_movie_info_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < toInfoUrlList.size) {
            val name = nameList[position]
            val toInfoUrl = toInfoUrlList[position] // 해당 위치의 toInfoUrl 가져오기

            // 이름과 텍스트 설정
            holder.ottname.text = name

            // toInfoUrl 설정
            // holder.otturl.text = toInfoUrl

            holder.itemView.setOnClickListener {
                val url = toInfoUrl
                val intent = if (url.startsWith("http://") || url.startsWith("https://")) {
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                } else {
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://$url"))
                }
                val requestCode = 1 // 원하는 코드로 변경 가능

                // startActivityForResult로 액티비티 시작
                (holder.itemView.context as Activity).startActivityForResult(intent, requestCode)
            }
        }
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ottname: TextView = itemView.findViewById(R.id.tv_ott_name)
        val otturl: TextView = itemView.findViewById(R.id.tv_ott_url) // 추가된 뷰 요소
    }
}