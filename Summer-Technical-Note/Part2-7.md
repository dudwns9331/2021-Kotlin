# Part2 - 7

## Summber Kotlin part2 2021-08-09 간단한 웹뷰

<br>

## **_레이아웃 xml 요소_**

- [SwipeRefreshLayout](#SwipeRefreshLayout)
- [WebView](#WebView)
- [ContentLoadingProgressBar](#ContentLoadingProgressBar)

---

## **Widget**

### SwipeRefreshLayout

**activity_main.xml** - SwipeRefreshLayout

```xml
<androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    android:id="@+id/refreshLayout"
    android:layout_width="0dp"
    android:layout_height="0dp"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toBottomOf="@+id/toolbar">
```

스와이프하여 새로고침 UI 패턴을 구현한다.

```gradle
dependencies {
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}
```

다음과 같이 앱 또는 모듈의 `build.gradle` 파일에 필요한
아티팩트의 종속 항목을 추가한다.

새로고침시에 모든 위젯을 새로받아오는 역할을 한다.

<br>

### WebView

**activity_main.xml** - WebView

```xml
<WebView
    android:id="@+id/webView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

대부분의 경우 `Chrome` 과 같은 표준 웹 브라우저를 사용하여 사용자에게 콘텐츠를 제공하는 것이 좋다. 활동 레잉아웃의 일부로 웹 콘텐츠를 표시할 수 있지만 완전히 개발된 브라우저 보다는 기능이 떨어진다. 웹 페이지에 대해서 주소를 받아 화면에 띄워주는 정도로 이해하면 편하다.

<br>

### ContentLoadingProgressBar

**activity_main.xml** - ContentLoadingProgressBar

```xml
<androidx.core.widget.ContentLoadingProgressBar
    android:id="@+id/progressBar"
    style="@style/Widget.AppCompat.ProgressBar.Horizontal"
    android:layout_width="0dp"
    android:layout_height="2dp"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toBottomOf="@+id/toolbar" />
```

`ContentLoadingProgressBar` 는 표시되기 전에 해제될 최소 시간을 기다리는 `ProgressBar` 를 구현한다. 진행률 표시줄이 표시되면 이벤트를 완료하는 데 시간이 많이 걸릴 수 있는 경우(없음에서 사용자가 인지할 수 있는 양까지) UI에서 "깜빡임" 을 방지하기 위해서 최소 시간 도안 진행률 표시줄이 표시된다.

<br>

### **_Activity - Kotlin_**

**MainActivity.kt** - property

```kt
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
```

<br>

**MainActivity.kt** - onCreate(savedInstanceState: Bundle?)

```kt
    /* onCreate 메인 스레드 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()        // 웹 뷰 초기화
        bindViews()        // 주소창 초기화

    }
```

<br>

**MainActivity.kt** - initViews()

```kt
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
```

<br>

**MainActivity.kt** - bindViews()

```kt
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
```

<br>

**MainActivity.kt** - inner class WebViewClient : android.webkit.WebViewClient()

```kt

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
```

<br>

**MainActivity.kt** - inner class WebChromeClient : android.webkit.WebChromeClient()

```kt
    /* 웹 페이지의 로딩 정로를 나타내는 progressBar 를 핸들링 하기 위한 클래스 (상속) */
    inner class WebChromeClient : android.webkit.WebChromeClient() {

        /* 진행 상태를 새롭게 업데이트 해준다. */
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }
```

<br>

**MainActivity.kt** - companion object

```kt
    /* DEFAULT URL 지정 */
    companion object {
        private const val DEFAULT_URL = "https://dudwns9331.github.io/"
    }
```

> `<uses-permission android:name="android.permission.INTERNET" />`

위 코드를 `AndroidManifest.xml` 에 추가하여 인터넷에 대한 권한을 받아온다.

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
