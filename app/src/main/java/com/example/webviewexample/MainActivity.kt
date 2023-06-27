package com.example.webviewexample

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.webviewexample.ui.theme.WebViewExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    //WebViewPage("file:///android_asset/index.html") //OFFLINE
                    WebViewPage("https://www.boltuix.com/")
                }
            }
        }
    }
}


@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun WebViewPage(url: String){
    var facebookTrigger by remember { mutableStateOf(false) }
    val infoDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    //Проверяет ориентацию экрана
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Toast.makeText(context, "landscape", Toast.LENGTH_SHORT).show()
        }
        else -> {
            Toast.makeText(context, "portrait", Toast.LENGTH_SHORT).show()
        }
    }

    // Добавление WebView внутрь AndroidView с макетом во весь экран
    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            webViewClient = WebViewClient()

            // включаем JS (просмотр видео,
            settings.javaScriptEnabled = true

            //Связываем JS код с кодом Андроида, передаем интерфейс
            addJavascriptInterface(WebAppInterface(context, infoDialog), "Android")

            webViewClient = object : WebViewClient(){

                //Если ссылка, куда мы пытаемся перейти содержит "facebook.com", то мы ее подменяем путем переключения триггера
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    if(url.contains("facebook.com")){
                        facebookTrigger = true
                        Toast.makeText(context, "Тут какое-то действие", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    return false
                }

                //WebView, часть 7 | Скрыть элементы из веб-представления
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    removeElement(view!!)
                }

            }

            // чтобы убедиться, что клиент, запрашивающий вашу веб-страницу, на самом деле является вашим Android-приложением.
            settings.userAgentString = System.getProperty("http.agent") //Dalvik/2.1.0 (Linux; U; Android 11; M2012K11I Build/RKQ1.201112.002)

            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })

    if (infoDialog.value) {
        InfoDialog(
            title = "Отработал JS код",
            desc = "Тут может быть сообщение",
            onDismiss = {
                infoDialog.value = false
            }
        )
    }

    //Переход подменяется путем открытия нового WebView
    if(facebookTrigger){
        //WebViewPage(url = "http://www.instagram.com/boltuix/")
        WebViewPage(url = "https://mnogotovarov.ru/")
    }
}

fun removeElement(webView: WebView) {

    // скрыть элемент по id
    webView.loadUrl("javascript:(function() { document.getElementById('blog-pager').style.display='none';})()")

    // мы также можем скрыть имя класса
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[0].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[1].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[2].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[3].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[4].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[5].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[6].style.display='none';})()")
}

//Создайте интерфейс и установите контекст
class WebAppInterface (private val mContext: Context, private var infoDialog: MutableState<Boolean>){
    //Показать toast с веб страницы
    @JavascriptInterface
    fun showToast(toast: String){
        //Тут ваши действия
        infoDialog.value = true
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }
}
