package org.esiea.humblot.benoit.KotlinTP

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient

//Tp Kotlin : Benoit Humblot / Guillaume Danguin / Tran Xuan Tien
class MainActivity : AppCompatActivity()
{
    var histo: ArrayList<Pair<String, String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?)
    {
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
    fun showHistorique(view: View)
    {
        val wb = findViewById<WebView>(R.id.wikiWebView)

        val builder = AlertDialog.Builder(this)

        //histo.map({ pair -> pair.second}).toTypedArray<String>()

        builder.setTitle("Historique")
        builder.setItems(histo.map({ pair -> pair.second}).toTypedArray<String>(), { dialog, which ->
            wb.loadUrl(histo[which].first)
        })
        builder.show()
    }

    /**
     * Navigate to Random wikipedia page
     */
    fun goToRandomWiki( view : View)
    {
        val wb = findViewById<WebView>(R.id.wikiWebView)
        wb.loadUrl("https://en.m.wikipedia.org/wiki/Special:Random")
    }

    /**
     * Cancel Navigation to new article
     */
    fun cancelLoading( view: View)
    {
        val wb = findViewById<WebView>(R.id.wikiWebView)
        wb.stopLoading()
    }

}
