package org.esiea.humblot.benoit.KotlinTP

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.net.ssl.HttpsURLConnection

//Tp Kotlin : Benoit Humblot / Guillaume Danguin / Tran Xuan Tien
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    var histo: ArrayList<Pair<String, String>> = ArrayList()
    var webPage: String = ""
    var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val wb = findViewById<WebView>(R.id.wikiWebView)
        wb.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(wb, url)
                histo.add(Pair(url, wb.title))
            }
        }
        wb.webChromeClient = WebChromeClient()
    }

    /**
     * Show Historique pop-up
     */
    fun showHistorique(view: View) {
        val wb = findViewById<WebView>(R.id.wikiWebView)

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Historique")
        builder.setItems(histo.map({ pair -> pair.second}).toTypedArray<String>(), { dialog, which ->
            wb.loadUrl(histo[which].first)
        })
        builder.show()
    }

    /**
     * Navigate to Random wikipedia page
     */
    fun goToRandomWiki( view : View) {
        val wb = findViewById<WebView>(R.id.wikiWebView)
        wb.loadUrl("https://en.m.wikipedia.org/wiki/Special:Random")
        //this.getHtmlFromUrl("https://en.m.wikipedia.org/wiki/Special:Random")
        launch {
            job = getPageContentFromUrl(wb.url, this@MainActivity.wikiWebView)
            delay(1000)
            if (job!!.isCompleted)
                Toast.makeText(this@MainActivity, "La page complètement chargée", Toast.LENGTH_LONG).show()

        }
    }

    /**
     * Cancel Navigation to new article
     */
    fun cancelLoading( view: View) {
        val wb = findViewById<WebView>(R.id.wikiWebView)
        wb.stopLoading()
        Toast.makeText(this@MainActivity, "La page est annulée", Toast.LENGTH_LONG).show()
    }

    fun search(view: View){
        launch{
            val subStr = searchInput.editableText.toString()
            if (subStr.length != 0) {
                val occurrences = async{searchOccurences(webPage, subStr)}.await()
                view.searchInput.text!!.clear()
                Toast.makeText(this@MainActivity, "$occurrences instance(s) de $subStr trouvées", Toast.LENGTH_LONG).show()
            }
        }


    }

    fun getPageContentFromUrl(url: String, wb: WebView): Job = launch(Dispatchers.Main){
        val rawPage = async { read(url) }.await()
        webPage =  Jsoup.parse(rawPage).text()
    }

    suspend fun read(url: String) = withContext(Dispatchers.Default) {

        var text = ""

        val openUrl = URL(url)
        val urlConnection = openUrl.openConnection() as HttpsURLConnection
        urlConnection.instanceFollowRedirects = true
        urlConnection.requestMethod = "GET"
        try {
            text = urlConnection.inputStream.bufferedReader().readText()
        } finally {
            urlConnection.disconnect()
        }
        text
    }

    fun searchOccurences(str: String, substr: String): Int {

        val regex = Regex(substr)
        val matches = regex.findAll(str)
        println()
        return matches.count()
    }
}