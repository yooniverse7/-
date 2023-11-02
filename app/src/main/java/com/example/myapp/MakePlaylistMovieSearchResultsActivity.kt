package com.example.myapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
class MakePlaylistMovieSearchResultsActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: MakePlaylistMovieSearchResultsAdapter
    private lateinit var textView: TextView
    private lateinit var movieList: MutableList<String>

    private val imageUrlList = mutableListOf<String>()
    private val nameList = mutableListOf<String>()
    private val textList = mutableListOf<String>()
    private val urlList = mutableListOf<String>()

    private var hasJavascriptRun = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_playlist_movie_search)

        webView = findViewById(R.id.wv_add_movie_list)
        recyclerView = findViewById(R.id.rv_add_movie_list)

        val SearchButton = findViewById<Button>(R.id.seach_click)
        val searchEditText = findViewById<EditText>(R.id.input)
        SearchButton.setOnClickListener {
            val searchStr = searchEditText.text.toString()
            println("Search string is $searchStr") // 변수에 사용자가 입력한 값 들어 왔는지 확인

            val intent = Intent(this, MakePlaylistMovieSearchActivity::class.java)
            intent.putExtra("searchString", searchStr)

            startActivity(intent)
        }
        // MainActivity에서 ImageAdapter 초기화
        imageAdapter = MakePlaylistMovieSearchResultsAdapter(
            this@MakePlaylistMovieSearchResultsActivity,
            imageUrlList,
            nameList,
            textList,
            urlList
        )
        recyclerView.adapter = imageAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(this@MakePlaylistMovieSearchResultsActivity)

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if (!hasJavascriptRun) {
                    hasJavascriptRun = true  // 이미 호출되었음을 표시
                    if (!view!!.settings.domStorageEnabled) {
                        view.settings.domStorageEnabled = true
                    }
                    view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")
                }
            }
        }

        val searchString = intent.getStringExtra("searchString")

        webView.addJavascriptInterface(MyJavascriptInterface(), "Android")
        webView.loadUrl("https://m.kinolights.com/search/movies?keyword=$searchString")

        val addImageButton = findViewById<ImageButton>(R.id.confirmButton)

        addImageButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }


    }


    inner class MyJavascriptInterface {
        @JavascriptInterface
        fun getHtml(html: String) {
            var source = html
            val doc = Jsoup.parse(source)

            val newImageUrlList = mutableListOf<String>()
            val newNameList = mutableListOf<String>()
            val newTextList = mutableListOf<String>()
            val newUrlList = mutableListOf<String>()

            val imageBoxElements: List<Element> = doc.select("div.image-box")
            val movieName: List<Element> = doc.select(".name")
            val movieGenreAndYear: List<Element> = doc.select(".text")
            val toInfoUrl: Iterable<String> =
                doc.select(".movie-list-item-wrap > a").eachAttr("href")


            // 이미지 url 가져오는 코드
            for (imageBoxElement in imageBoxElements) {
                val sourceElements = imageBoxElement.select("source")
                for (sourceElement in sourceElements) {
                    val imageUrl = sourceElement.attr("srcset")
                    newImageUrlList.add(imageUrl)
                }
            }

            // 영화 제목 가져오는 코드
            for (nameElement in movieName) {
                val name = nameElement.text()
                newNameList.add(name)
            }

            // 영화 장르와 년도 가져오는 코드
            for (textElement in movieGenreAndYear) {
                val text = textElement.text()
                newTextList.add(text)
            }

            // 해당 영화 url 가져오는 코드
            for (url in toInfoUrl) {
                newUrlList.add(url)
            }

            if (newTextList.size >= 1) {
                // newTextList의 내용을 첫 번째 인덱스부터 추가 //0은 데이터가 없기에
                textList.addAll(newTextList.subList(1, newTextList.size))
            } else {
                // newTextList에 내용이 없는 경우
                textList.add("No text data available")
            }

            imageUrlList.addAll(newImageUrlList)
            nameList.addAll(newNameList)
            urlList.addAll(newUrlList)

            runOnUiThread {
                // 이미지 URL 목록을 출력하는 코드 // 이거 무조건 필요한 듯?
                imageAdapter.notifyDataSetChanged()
            }
        }
    }
}
