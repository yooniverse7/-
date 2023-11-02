package com.example.myapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.jsoup.Jsoup

class MovieInfoActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieInfoAdapter
    private lateinit var textView: TextView

    private val nameList = mutableListOf<String>()
    private val urlList = mutableListOf<String>()

    private var hasJavascriptRun = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_info)

        webView = findViewById(R.id.webView)
        recyclerView = findViewById(R.id.rv_ott_platform)
        textView = findViewById(R.id.textView2)

        webView.settings.javaScriptEnabled = true

        val url = intent.getStringExtra("url")!!
        //val textV = findViewById<TextView>(R.id.tv_story)

        // URL을 TextView에 설정
        //textV.text = url
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if (!hasJavascriptRun && url == webView.url) { // Ensure JavaScript is run only once
                    hasJavascriptRun = true // Mark as already executed
                    if (!view!!.settings.domStorageEnabled) {
                        view.settings.domStorageEnabled = true
                    }
                    view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")
                }
            }
        }

        webView.addJavascriptInterface(MyJavascriptInterface(), "Android")
        webView.loadUrl(url)

        // MainActivity에서 ImageAdapter 초기화
        movieAdapter = MovieInfoAdapter(this@MovieInfoActivity, nameList, urlList)
        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@MovieInfoActivity)
    }

    inner class MyJavascriptInterface {
        @JavascriptInterface
        fun getHtml(html: String) {
            val handler = Handler(Looper.getMainLooper())
            var source = html
            val doc = Jsoup.parse(source)

//            val newNameList = mutableListOf<String>()
            val newUrlList = mutableListOf<String>()
            val ottName = mutableListOf<String>()

// a 태그를 선택
            doc.select("a").forEach { aTag ->
                aTag.select("div span.name").firstOrNull()?.let { spanTag ->
                    val name = spanTag.text()
                    ottName.add(name)
                    // name 변수에 이름 데이터가 포함됩니다.
                    println(name + "!!!!!!!!!@@") //OTT 플랫폼 하나씩 출력
                }
            }

            println(ottName+"제발되세요") // OTT 플랫폼 있을 시[넷플릭스, 티빙, 왓챠, U+모바일tv, 제발되세요] 상영영화의 경우 [제발되세요]만 출력

            if (ottName.isEmpty()) {
                // If span.name is not found, try selecting theater-name
                val theaterNameElements = doc.select("span.theater-name")
                for (theaterNameElement in theaterNameElements) {
                    val name = theaterNameElement.text()
                    ottName.add(name)
                }
            }
            println(ottName+"!!!!!!!") // [넷플릭스, 티빙, 왓챠, U+모바일tv, !!!!!!!]


            var toInfoUrl: MutableList<String> = doc.select(".movie-price-link-wrap > a").eachAttr("href")
            if (toInfoUrl.isEmpty()) {
                // If .movie-price-link-wrap > a is not found, try selecting theater elements and get their href
                val theaterUrls = doc.select(".theater")
                for (theater in theaterUrls) {
                    val url = theater.attr("href")
                    toInfoUrl.add(url)

                    println(url + "얜 url")
                    println(toInfoUrl + "얜 newUrlList")
                }
            }

            println(toInfoUrl+"얜 3개 출력?") // OTT 플랫폼 출력되면 [얜 3개만 출력?]만 출력함


            runOnUiThread {
                val newNameList = mutableListOf<String>()
                val newUrlList = mutableListOf<String>()

                // 이미 있는 데이터는 추가하지 않도록 필터링
                for (nameElement in ottName) {
                    val name = nameElement
                    if (!nameList.contains(name) && !newNameList.contains(name)) {
                        newNameList.add(name)
                        println(newNameList+"이건 이름 잘 가져오나요?")
                    }
                }

                // 해당 영화 URL 가져오는 코드
                for (url in toInfoUrl) {
                    newUrlList.add(url)
                    println(newUrlList+"이건 url 잘 들고 옴?")
                }

                // 이미 추가된 데이터를 제거
                nameList.clear()
                nameList.addAll(newNameList)

                println(nameList+"nameList 출력")


                urlList.clear()
                urlList.addAll(newUrlList)
                println(urlList+"urlList 출력")


                movieAdapter.notifyDataSetChanged()
            }



            handler.post {

                val doc = Jsoup.parse(html)
                val backImg = findViewById<ImageView>(R.id.iv_backgroud)
                val backImgUrl = doc.select("div.backdrop source").attr("srcset")

                Glide.with(this@MovieInfoActivity)
                    .load(backImgUrl)
                    .into(backImg)

                val posterImg = this@MovieInfoActivity.findViewById<ImageView>(R.id.iv_info_poster)
                val posterUrl = doc.select("div.poster source").attr("srcset")

                Glide.with(this@MovieInfoActivity)
                    .load(posterUrl)
                    .into(posterImg)
            }

            val title = doc.select(".title-kr").first()?.text()
            val tv_name = findViewById<TextView>(R.id.tv_name)
            tv_name.text = title

            val movieAdditionalInformation = doc.select(".metadata").first()?.text()
            val tv_movie_info = findViewById<TextView>(R.id.tv_movie_info)
            tv_movie_info.text = movieAdditionalInformation

            val story = doc.select(".synopsis").first()?.text()
            val tv_story = findViewById<TextView>(R.id.tv_story)
            tv_story.text = story
        }
    }
}