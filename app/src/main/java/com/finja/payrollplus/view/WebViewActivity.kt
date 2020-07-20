package com.finja.payrollplus.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.finja.payrollplus.BuildConfig
import com.finja.payrollplus.R
import com.finja.payrollplus.utilities.NetworkChangeReceiver
import com.finja.payrollplus.utilities.NetworkUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.general_custom_dialog_network_error.*
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.Charset
import kotlin.concurrent.thread


class WebViewActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val networkUtils = NetworkUtils()
    private val networkChangeReceiver = NetworkChangeReceiver()
    private var flag = false
    private var injected = false

    var refreshRate = arrayOf(
        "10 msec",
        "30 msec",
        "50 msec",
        "100 msec",
        "200 msec",
        "300 msec",
        "400 msec",
        "500 msec",
        "700 msec",
        "1000 msec",
        "1.5 sec",
        "2 sec",
        "3 sec",
        "4 sec",
        "5 sec",
        "8 sec",
        "10 sec",
        "15 sec",
        "20 sec",
        "30 sec"
    )


    override fun onStart() {
        super.onStart()
        fab.bringToFront()
        fab.setOnClickListener { Log.d("console", "pressed") }



        LocalBroadcastManager.getInstance(this).registerReceiver(
            mNotificationReceiverInternet,
            IntentFilter(getString(R.string.keySendInternetStatus))
        )

        if (Build.VERSION.SDK_INT >= 23) {
            // Above marshmallow Manifest Connectivity Changes not working.
            val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            this.registerReceiver(networkChangeReceiver, intentFilter)
        }


//        button.setOnClickListener {
//
//            clickRefresh()
////            Handler().postDelayed({
////                clickRefresh();
////            }, 2000)
//////            Timer("SettingUp", false).schedule(2000) {
//////
//////            }
//
//
//        }
//
//
//        button2.setOnClickListener {
//         //   checkIsFound()
//
//
//        }
//    }

//    private fun clickRefresh() {
//        webView.evaluateJavascript(
//            "click();",
//            ValueCallback<String?> { s ->
//                Log.d("LogName", s) // Prints 'this'
//            })
//    }

//        editText.setOnClickListener {
//            PopupMenu(this, editText).apply {
//                menuInflater.inflate(R.menu.menu_refresh_rate, menu)
//                setOnMenuItemClickListener { item ->
//                    editText.setText(item.title)
//                    true
//                }
//                show()
//            }
//        }


        val spin: Spinner = findViewById<View>(R.id.spinner1) as Spinner
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, refreshRate)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin.adapter = adapter
        spin.onItemSelectedListener = this

        nav_view.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.navigation_home -> {
                        Log.d("console", "home")
                        // viewFragment(HomeFragment(), FRAGMENT_HOME)
                        // item.isChecked = true
                        showHomeFragment()

                        return true
                    }
                    R.id.navigation_filter -> {
                        Log.d("console", "filter")
                        //  item.isChecked = true
                        //   viewFragment(OneFragment(), FRAGMENT_OTHER)
                        showFilterFragment()
                        return true
                    }

                }
                return false
            }
        })


    }

    private fun showFilterFragment() {
        filterLayout.bringToFront()
        filterLayout.visibility = View.VISIBLE
    }

    private fun showHomeFragment() {

        filterLayout.visibility = View.GONE
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(
        arg0: AdapterView<*>?,
        arg1: View?,
        position: Int,
        id: Long
    ) {
        Toast.makeText(
            applicationContext,
            "Selected User: " + refreshRate[position],
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun clickRefresh() {
        webView.evaluateJavascript(
            "isWorking = true;\n" +
                    "      isGotResponse = false;\n" +
                    "     confirmMethod = 1;" +
                    " console.log(\"start repeat\")\n" +
                    "      clickRefreshButton();\n" +
                    "      console.log(\"clicked\");",
            ValueCallback<String?> { s ->
                Log.d("LogName", s) // Prints 'this'
            })
    }

//
//    private fun checkIsFound() {
//        webView.evaluateJavascript(
//            "found=isFound();" +
//                    "android.isFound(found);" +
//                    "",
//            ValueCallback<String?> { s ->
//                Log.d("LogName", s) // Prints 'this'
//            })
//    }


    private fun injectJS() {
        try {
            Log.d("inject", "start")
            val inputStream = assets.open("book.js")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()

            // preserve non-english letters
            val uriEncoded: String =
                URLEncoder.encode(String(buffer, Charset.forName("UTF-8")), "UTF-8")
                    .replace("+", "%20")
            //  Log.d("inject", uriEncoded)
            val encoded: String =
                Base64.encodeToString(uriEncoded.toByteArray(), Base64.NO_WRAP)
            Log.d("console", "Start injecting from android")
            webView.loadUrl(
                "javascript:(function() {" +


                        //  "var parent = document.getElementsByTagName('head').item(0);" +
                        //      "const parent = (document.head || document.documentElement);" +

                        "console.log('start injecting'); " +
                        "var script = document.createElement('script');" +
                        "script.type = 'text/javascript';" +  // don't forget to use decodeURIComponent after base64 decoding

                        "script.innerHTML = decodeURIComponent(window.atob('" + encoded + "'));" +

                        "console.log(script);" +
                        "script.onchange= function () {\n" +
                        "    this.remove();\n" +
                        "};\n" +

                        "(document.head || document.documentElement).appendChild(script);" +
                        //      " parent.appendChild(script)" +

                        "})()"
            )

            Log.d("inject", "yes")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun injectScripts() {
        webView.evaluateJavascript(
            "function click() { \n" +
                    "                         const refreshBtn = document.querySelector('.loadboard-reload__refresh-icon--reload-icon');\n" +
                    "                         refreshBtn.click() \n" +
                    "                         };\n" +

                    " function isFound() {\n" +
                    "    summaryText = document.querySelector('.summary-text')\n" +
                    "    text = summaryText.textContent || summaryText.innerText;\n" +
                    "    //  console.log(text)\n" +
                    "    if (text[0] == '0')\n" +
                    "        return false\n" +
                    "    else return true\n" +
                    "       } " +
                    "let found = false",
            ValueCallback<String?> { s ->
                Log.d("LogName", s) // Prints 'this'
            })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (networkUtils.haveNetworkConnection(this@WebViewActivity)) {
            loadWeb(BuildConfig.URL)
        } else {
            imgv_network_error.setVisibility(View.GONE)
            webView.setVisibility(View.VISIBLE)
            overlayView.visibility = View.VISIBLE
            connectionLostAlert("Quit", BuildConfig.URL)
        }
    }

    /**
     */
    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface", "ClickableViewAccessibility")
    private fun loadWeb(url: String?) {
        val webSettings = webView.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webSettings.setBuiltInZoomControls(false)
        webSettings.setDomStorageEnabled(true)
        webView.setWebViewClient(myWebClient())
        webView.setWebChromeClient(MyWebChromeClient())
        webView.addJavascriptInterface(JavaScriptHandler(), "android")
        try {
            webView.loadData("", "text/html", null)
            webView.loadUrl(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        webView.setOnTouchListener { _, event ->
            if (!networkUtils.haveNetworkConnection(this)) {
                connectionLostAlert("Quit", webView.getUrl())
            }
            false
        }
    }

    /**
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    /**
     *
     */
    inner class myWebClient : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            flag = false
            if (networkUtils.haveNetworkConnection(this@WebViewActivity)) {
                imgv_network_error.setVisibility(View.GONE)
                webView.setVisibility(View.VISIBLE)
                overlayView.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            } else {
                webView.setVisibility(View.GONE)
                imgv_network_error.setVisibility(View.VISIBLE)
                overlayView.visibility = View.VISIBLE
                connectionLostAlert("Quit", url)
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            if (networkUtils.haveNetworkConnection(this@WebViewActivity)) {
                webView.setVisibility(View.VISIBLE)
                //   webView.setVisibility(View.GONE)
                overlayView.visibility = View.GONE
                //  injectScripts();
                Log.d("console", "finished")
                Log.d("console", "injected $injected")
                Log.d("console", "url $url")
                if (!injected && url == "https://relay.amazon.com/tours/loadboard?") {
                    Log.d("console", "injected!!!")
                    injectJS();
                    injected = true
                }
                flag = true
                super.onPageFinished(view, url)
            }
        }
//
//        override fun shouldOverrideUrlLoading(
//            view: WebView?,
//            request: WebResourceRequest?
//        ): Boolean {
//            Log.d("override", "start")
//            if (flag) {
//
//                val aURL = URL(BuildConfig.URL);
//
//                val conn = aURL.openConnection()
//                conn.connect()
//
//                val istream = conn.getInputStream();
//                Log.d("IS", istream.toString())
//                return true;
//            } else
//                return super.shouldOverrideUrlLoading(view, request)
//        }


        private fun handleRequestViaOkHttp(url: String) {

            val httpClient = OkHttpClient()

            thread {
                try {
                    val request = Request.Builder().url(url).build()
                    print("Request: $request")
                    //  val response = httpClient.newCall(request).execute()
                    //  println("Response: " + response.headers().size())

                    try {
                        val okResponse =
                            httpClient.newCall(request).execute()
                        if (okResponse != null) {
                            val statusCode: Int = okResponse.code()
                            val encoding = "UTF-8"
                            val mimeType = "application/json"
                            val reasonPhrase = "OK"
                            val responseHeaders: Map<String, String> =
                                HashMap()
                            if (okResponse.headers() != null) {
                                if (okResponse.headers().size() > 0) {
                                    for (i in 0 until okResponse.headers().size()) {
                                        val key: String = okResponse.headers().name(i)
                                        val value: String = okResponse.headers().value(i)
                                        println("key $key, value $value")
//                                        responseHeaders.put(key, value)
//                                        if (key.toLowerCase().contains("x-cart-itemcount")) {
//                                            Log.i(TAG, "setting cart item count")
//                                            app.setCartItemsCount(value.toInt())
//                                        }
                                    }
                                }
                            }
//                            val data: InputStream = ByteArrayInputStream(
//                                okResponse.body().string()
//                                    .getBytes(StandardCharsets.UTF_8)
//                            )
//                            Log.i(TAG, "okResponse code:" + okResponse.code())
//                            returnResponse = WebResourceResponse(
//                                mimeType,
//                                encoding,
//                                statusCode,
//                                reasonPhrase,
//                                responseHeaders,
//                                data
                            //   )
                        } else {
                            Log.w("sdsd", "okResponse fail")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }


//
//
//                    val data: InputStream = ByteArrayInputStream(
//                        response.body().bytes()
//                    )
//                    Log.i("ddd", "okResponse code:" + response.code())
//                    print("data" + data)
//                    val body = response.body()
//                    println("Response2: " + body)
                } catch (e: Exception) {
                }
            }
        }


        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {

            //   handleRequestViaOkHttp(BuildConfig.URL)
            return super.shouldInterceptRequest(view, request)
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            try {
                webView.setVisibility(View.GONE)
                imgv_network_error.setVisibility(View.VISIBLE)
                overlayView.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }


    /**
     *
     */
    internal inner class MyWebChromeClient : WebChromeClient() {

        override fun onJsConfirm(
            view: WebView,
            url: String,
            message: String,
            result: JsResult
        ): Boolean {
            return super.onJsConfirm(view, url, message, result)
        }

        override fun onJsPrompt(
            view: WebView,
            url: String,
            message: String,
            defaultValue: String,
            result: JsPromptResult
        ): Boolean {
            return super.onJsPrompt(view, url, message, defaultValue, result)
        }

        override fun onJsAlert(
            view: WebView,
            url: String,
            message: String,
            result: JsResult
        ): Boolean {
            result.confirm()
            if (message.equals("exit", ignoreCase = true)) {
                finish()
            } else {
                showToast(message)
            }
            return true
        }
    }


    class JavaScriptHandler internal constructor() {

        @JavascriptInterface
        fun setResult(value: String?, msg: String, status: String) {
            // You can control your flow by checking status
        }

        @JavascriptInterface
        fun isFound(value: String?) {
            Log.d("isFound", value)
            // You can control your flow by checking status
        }
    }


    /**
     * Back press callback onBackPressed
     */
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            generalDailog(getString(R.string.app_name), "Are you sure you want to quit?")
        }
    }


    /**
     * Back Press Alert Dialog
     */
    fun generalDailog(title: String, message: String) {
        try {
            val builder = AlertDialog.Builder(this@WebViewActivity)

            builder.setTitle(title)
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton("YES") { _, _ ->
                try {
                    webView.clearCache(true)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     */
    private val mNotificationReceiverInternet = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            if (intent != null && intent.extras != null && !intent.extras!!.isEmpty) {
                if (!intent.getBooleanExtra("isConnected", false)) {
                    var url = ""
                    if (webView.getUrl() == null) {
                        url = BuildConfig.URL
                    } else {
                        url = webView.getUrl();
                    }
                    connectionLostAlert("Quit", url)
                }
            }
        }
    }


    /***
     * @param noButtonText Button text
     * @param url Url
     */
    protected fun connectionLostAlert(noButtonText: String, url: String) {
        try {
            // custom dialog
            webView.visibility = View.GONE
            val customDialog = AppCompatDialog(this)
            customDialog.setContentView(R.layout.general_custom_dialog_network_error)
            customDialog.setCanceledOnTouchOutside(false)
            customDialog.setCancelable(false)
            customDialog.tvDialogTitle.text = getString(R.string.noInternetConnection)

            customDialog.tvDialogRetry.setOnClickListener { _ ->
                customDialog.cancel()
                if (networkUtils.haveNetworkConnection(this)) {
                    if (!isTextEmpty(url))
                        loadWeb(url)
                    customDialog.cancel()
                } else {
                    connectionLostAlert(noButtonText, url)
                }
            }
            customDialog.tvDialogCancel.text = noButtonText
            customDialog.tvDialogCancel.setOnClickListener { _ ->
                customDialog.cancel()
                finish()
            }

            if (!customDialog.isShowing()) {
                customDialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     */
    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    /**
     */
    protected fun isTextEmpty(text: String?): Boolean {
        var result = ""
        try {
            if (text != null) {
                result = text.trim { it <= ' ' }
                return result.isEmpty() || result.equals("null", ignoreCase = true)
            } else {
                return true
            }
        } catch (e: Exception) {
            return false
        }

    }

    /**
     *
     */
    override fun onDestroy() {
        try {
            LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mNotificationReceiverInternet)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(networkChangeReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

}
