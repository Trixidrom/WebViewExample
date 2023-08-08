package com.example.webviewexample

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.webviewexample.ui.theme.WebViewExampleTheme
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder

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

    //триггер для загрузчика
    val loaderDialogScreen = remember { mutableStateOf(false) }
    var facebookTrigger by remember { mutableStateOf(false) }
    val infoDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if(loaderDialogScreen.value){
        Loader(loaderDialogScreen)
    }

    //Удаляем рекламу
    //извлекаем данные и добавляем в stringBuilder
    val adServers = StringBuilder()
    var line: String? = ""
    val inputStream = context.resources.openRawResource(R.raw.adblockserverlist)
    val br = BufferedReader(InputStreamReader(inputStream))
    try {
        while (br.readLine().also { line = it } != null){
            adServers.append(line)
            adServers.append("\n")
        }
    }catch (e: IOException){
        e.printStackTrace()
    }

    //Триггер для навигации назад
    var backEnabled by remember{ mutableStateOf(false) }
    var webView: WebView? = null

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

                    loaderDialogScreen.value = false //по завершению загрузки страницы прячем лоадер
                }

                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                    //при старте проверяем, есть ли возможность вернуться назад
                    backEnabled = view.canGoBack()

                    loaderDialogScreen.value = true //показываем лоадер
                }

                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest): WebResourceResponse? {
                    val empty = ByteArrayInputStream("".toByteArray())
                    val kk5 = adServers.toString()
                    if (kk5.contains(":::::" + request.url.host))
                        return WebResourceResponse( "text/plain", "utf-8", empty)
                    return super.shouldInterceptRequest(view, request)
                }

            }

            // чтобы убедиться, что клиент, запрашивающий вашу веб-страницу, на самом деле является вашим Android-приложением.
            settings.userAgentString = System.getProperty("http.agent") //Dalvik/2.1.0 (Linux; U; Android 11; M2012K11I Build/RKQ1.201112.002)

            loadUrl(url)
            webView = this
        }
    }, update = {
        webView = it
        //it.loadUrl(url)
    })

    //открывается диалог при отрабатывании js
    if (infoDialog.value) {
        InfoDialog(
            title = "Отработал JS код",
            desc = "Тут может быть сообщение",
            onDismiss = {
                infoDialog.value = false
            }
        )
    }

    //Переход на facebook подменяется путем открытия нового WebView
    if(facebookTrigger){
        //WebViewPage(url = "http://www.instagram.com/boltuix/")
        WebViewPage(url = "https://mnogotovarov.ru/")
    }

    //если есть куда возвращаться, срабатывает backHandler, если нет, приложение закроется (поведение по умолчанию)
    BackHandler(enabled = backEnabled) {
        //removeElement(webView!!)
        webView?.goBack()
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Loader(loaderDialogScreen: MutableState<Boolean>) {
    // Dialog function
    Dialog(
        onDismissRequest = {
            loaderDialogScreen.value = false
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false // experimental
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {



                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),

                    )

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Loading...",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Please wait",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    letterSpacing = 3.sp,
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.primary,
                )
                Spacer(modifier = Modifier.height(24.dp))

            }

        }
    }
}