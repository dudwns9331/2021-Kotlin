package com.example.summer_part2_chapter08

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    /* 홈 버튼 바인딩 */
    private val goHomeButton: ImageButton by lazy {
        findViewById(R.id.goHomeButton)
    }

    /* 주소창 바인딩 */
    private val addressBar: EditText by lazy {
        findViewById(R.id.addressBar)
    }

    /* 뒤로가기 버튼 바인딩 */
    private val goBackButton: ImageButton by lazy {
        findViewById(R.id.goBackButton)
    }

    /* 앞으로가기 버튼 바인딩 */
    private val goForwardButton: ImageButton by lazy {
        findViewById(R.id.goForwardButton)
    }

    /* 스와이프 리프레쉬 레이아웃 바인딩 */
    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout)
    }

    /* 웹 뷰 아이디 바인딩 */
    private val webView: WebView by lazy {
        findViewById(R.id.webView)
    }

    /* 프로그래스 바 (진행도) 바인딩 */
    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

    /* onCreate 메인 스레드 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()        // 웹 뷰 초기화
        bindViews()        // 주소창 초기화

    }

    /* 뒤로가기 버튼을 눌러서 웹의 뒤로가기 실행 */
    override fun onBackPressed() {
        // 만약 뒤로갈 수 있다면
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            // 뒤로갈 수 없으면 종료
            super.onBackPressed()
        }
    }

    /* 웹 뷰 초기화 */
    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            // 보안 문제 발생 가능
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }

    /* 버튼, 주소창 바인트 초기화, 이벤트 설정 */
    private fun bindViews() {

        // 홈 버튼 클릭 했을 때, DEFAULT_URL 로 이동하도록 이벤트 지정
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }


        // 주소창에서 동작이 발생했을 때
        addressBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val loadingUrl = v.text.toString()      // 로딩되는 URL 저장

                // 만약 URL 에 http 가 지정되어 있으면
                if (URLUtil.isNetworkUrl(loadingUrl)) {
                    webView.loadUrl(loadingUrl)
                } else {    // http 가 붙어 있지 않으면
                    // 자동으로 http 를 https 로 리다이렉팅 해줌
                    webView.loadUrl("http://$loadingUrl")
                }
            }

            // 키보드 내리기 false
            return@setOnEditorActionListener false
        }

        /* 뒤로가기 버튼 이벤트 */
        goBackButton.setOnClickListener {
            webView.goBack()
        }

        /* 앞으로가기 버튼 이벤트 */
        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    /* 스와이프 새로고침 시 작동하는 이벤트를 핸들링하기 위한 클래스 (상속) */
    inner class WebViewClient :
        android.webkit.WebViewClient() { //  inner 를 붙여줌으로써 상위에 있는 클래스에 접근하도록 한다.

        /* 페이지가 시작될 때 */
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()      // progressBar 보여주기
        }

        /* 페이지 refresh 가 끝났을 때 */
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            // isRefreshing = false 로 지정해서 refresh 가 끝났음을 설정함
            refreshLayout.isRefreshing = false

            progressBar.hide()      // progressBar 숨기기

            goBackButton.isEnabled = webView.canGoBack()        // 뒤로가기 진행이 불가능 할 때 비활성화
            goForwardButton.isEnabled = webView.canGoBack()     // 앞으로가기 진행이 불가능 할 때 비활성화

            addressBar.setText(url)     // 최종적으로 보여지는 url 업데이트
        }
    }

    /* 웹 페이지의 로딩 정로를 나타내는 progressBar 를 핸들링 하기 위한 클래스 (상속) */
    inner class WebChromeClient : android.webkit.WebChromeClient() {

        /* 진행 상태를 새롭게 업데이트 해준다. */
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }

    /* DEFAULT URL 지정 */
    companion object {
        private const val DEFAULT_URL = "https://dudwns9331.github.io/"
    }
}