package com.example.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jsoup.Jsoup
import org.jsoup.nodes.Element


class SearchResultsFullActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: SearchResultsFullAdapter
    private lateinit var textView: TextView

    private val imageUrlList = mutableListOf<String>()
    private val nameList = mutableListOf<String>()
    private val textList = mutableListOf<String>()
    private val urlList = mutableListOf<String>()

    private var hasJavascriptRun = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results_full)

        webView = findViewById(R.id.wv_add_movie_list)
        recyclerView = findViewById(R.id.recyclerView)
        textView = findViewById(R.id.textView)

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

        // MainActivity에서 ImageAdapter 초기화
        imageAdapter = SearchResultsFullAdapter(this@SearchResultsFullActivity, imageUrlList, nameList, textList, urlList)
        recyclerView.adapter = imageAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@SearchResultsFullActivity)
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
            val toInfoUrl: Iterable<String> = doc.select(".movie-list-item-wrap > a").eachAttr("href")


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

            runOnUiThread {
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

                // 이미지 URL 목록을 출력하는 코드 // 이거 무조건 필요한 듯?
                imageAdapter.notifyDataSetChanged()
            }
        }
    }
}