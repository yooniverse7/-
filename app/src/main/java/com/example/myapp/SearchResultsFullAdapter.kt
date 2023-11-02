package com.example.myapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SearchResultsFullAdapter(
    private val context: Context,
    private val imageUrlList: List<String>,
    private val nameList: List<String>,
    private val textList: List<String>,
    private val toInfoUrlList: List<String>
) : RecyclerView.Adapter<SearchResultsFullAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_item_image, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

        // toInfoUrl 설정
        holder.toInfoUrlTextView.text = toInfoUrl

        // 아이템 클릭 처리
        holder.itemView.setOnClickListener {
            val profileUrl = "https://m.kinolights.com$toInfoUrl"
            println(profileUrl)
            val intent = Intent(context, MovieInfoActivity::class.java)
            intent.putExtra("url", profileUrl)
            context.startActivity(intent)
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
    }
}
