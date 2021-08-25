# Part3 - 2

## Summber Kotlin part3 2021-08-25

<br>

### **_레이아웃 xml_**

<br>

Part3 부터는 기본적인 XML 레이아웃에 대한 설명은 생략한다.

- [androidx.viewpager2.widget.ViewPager2](https://developer.android.com/reference/kotlin/androidx/viewpager2/widget/ViewPager2)

  `Viewpager2`는 `androidx.viewpager.widget.ViewPager` 를 오른쪽에서 왼쪽 레이아웃 지원, 수직 방향, 수정 가능한 Fragment 컬렉션 등을 포함하여 이전 버전의 대부분의 문제점을 해결하는 새로운 버전이다.

<br>

---

### **_Activity - Kotlin_**

**MainActivity.kt** - property

```kotlin
private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.viewPager)
}

private val progressBar: ProgressBar by lazy {
        findViewById(R.id.progressBar)
}
```

viewPager 는 액티비티 위에 있는 Fragement 를 페이지 형식으로 넘기기 위해서 사용하는 viewPager2 를 바인딩한다.

progressBar는 명언을 불러오기 전에 진행되는 상태를 알려주기 위해서 바인딩한다.

<br>

**MainActivity.kt** - onCreate(savedInstanceState: Bundle?)

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initData()
    }
```

`initViews()` 는 보여지는 View 에 대한 초기화다. `initData() `는 `Firebase RemoteConfig` 에 대한 설정을 한다.

<br>

**MainActivity.kt** - initViews()

```kotlin
viewPager.setPageTransformer { page, position ->
            when {
                position.absoluteValue >= 1F -> {
                    page.alpha = 0F
                }
                position == 0F -> {
                    page.alpha = 1F
                }
                else -> {
                    page.alpha = 1F - 2 * position.absoluteValue
                }
            }
        }
    }
```

`pageTransformer` 를 통해서 애니메이션에 대한 설정을 할 수 있다. `page` 와 `position` 에 대한 값을 인자로 받아 `absoluteValue` 를 통해서 `page` 의 `alpah(투명도)` 에 대한 값을 조절한다.

<br/>

**MainActivity.kt** - initData()

```kotlin
private fun initData() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            progressBar.visibility = View.GONE
            if (it.isSuccessful) {
                val quotes = parseQuotesJson(remoteConfig.getString("quotes"))
                val isNameRevealed = remoteConfig.getBoolean("is_name_revealed")

                displayQuotesPager(quotes, isNameRevealed)

            }
        }
    }
```

initData() 함수는 remoteConfig 에 Firebase의 remoteConfig 에 대한 값을 넣어준다.

<br>

### Firebase RemoteConfig?

Firebase RemoteConfig 는 앱 업데이트를 게시하지 않아도 하루 활설 사용자 수 제한 없이 무료로 앱의 동작과 모양을 변경할 수 있다.

`Remote Config backend API`를 사용하여 모든 앱 사용자 또는 사용자층의 특정 세그먼트에 대한 인앱 기본값을 재정의할 수 있다. 업데이트를 적용할 시점을 앱에서 제어할 수 있으며 성능에 거의 영향을 주지 않고 업데이트를 자주 확인하여 적용할 수 있다.

<br>

**MainActivity.kt** - parseQuotesJson(json: String): List<Quote>

```kotlin
private fun parseQuotesJson(json: String): List<Quote> {
        val jsonArray = JSONArray(json)
        var jsonList = emptyList<JSONObject>()

        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)
            jsonObject?.let {
                jsonList = jsonList + it
            }
        }

        return jsonList.map {
            Quote(
                quote = it.getString("quote"),
                name = it.getString("name")
            )
        }
    }
```

`parseQuotesJson` 은 `remoteConfig` 에서 불러오는 `Quotes` 값들을 JSON 값으로 불러와서 `jsonList` 의 `Quote` 데이터 객체에 넣어주는 역할을 한다.

`jsonArray` 는 `JSONArray` 값을 통해서 가져온 JSON 전체의 값들이 배열로 들어가게 된다.

<br>

**MainActivity.kt** - displayQuotesPager(quotes: List<Quote>, isNameRevealed: Boolean)

```kotlin
private fun displayQuotesPager(quotes: List<Quote>, isNameRevealed: Boolean) {
        val adapter = QuotesPagerAdapter(
            quotes,
            isNameRevealed = isNameRevealed
        )

        viewPager.adapter = adapter
        viewPager.setCurrentItem(adapter.itemCount / 2, false)
    }
```

`displayQuotesPager()` 는 `Firebase RemoteConfig` 를 통해서 추출한 JSON 값들을 pager 에 보여주는 역할을 한다. `adpater` 를 통해서 quotes 값과 이름을 숨기는지에 대한 Boolean 값을 넘겨준다. 그리고 `viewPager` 의 어댑터에 연결시켜준다. 무한 페이징에서 처음 시작 페이지를 지정하기 위해서 어댑터의 아이템 개수의 절반 값을 시작 포지션으로 정해준다.

<br/>

### **_Data Class - Kotlin_**

**Quote.kt**

```kotlin
data class Quote(
    val quote: String,
    val name: String
)
```

`Quote` 데이터에는 명언을 표시하는 `quote` 값이 String 으로 지정되어 있고 그 명언을 말한 사람의 이름인 `name` 값도 String 으로 지정되어 있다.

### Data class ?

코틀린에서 데이터를 보관하는 것이 주 목적인 클래스를 만들 때 data class 를 만든다.

> data class User(val name : String, val age: Int)

<br/>

코틀린의 data class 는 생성자부터 getter 와 setter 그리고 Canonical Methods(equals, hashCode, toString) 까지 생성해준다.

데이터 클래스는 아래와 같은 제한 사항을 가진다.

- 기본 생성자에는 최소 하나의 파라미터가 있어야 한다.

- 기본 생성자의 파라미터는 val 이나 var 이어야 한다.

- 데이터 클래스틑 abstract, open, sealed, inner 가 되면 안된다.

<br/>

### **_Adapter - Kotlin_**

**QuotesPagerAdapter.kt**

```kotlin
package com.example.summer_part3_chapter02

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 받아오는 파라미터 : quotes, isNameRevealed
 * Adapter Type : RecyclerView.Adapter<QuotesPagerAdapter.QuoteViewHolder>
 */
class QuotesPagerAdapter(
    // quotes : 명언들이 담긴 리스트, 리스트의 데이터 타입은  Quote(data class) 이다.
    private val quotes: List<Quote>,
    // isNameRevealed : 명언을 말한 사람의 이름을 가릴것인지 결정하는 함수
    private val isNameRevealed: Boolean
) : RecyclerView.Adapter<QuotesPagerAdapter.QuoteViewHolder>() {

    /**
     * onCreateViewHolder() : RecyclerView 는 ViewHolder 를 새로 만들어야 할 때마다 이 메서드를 호출한다.
     * 이 메서드는 ViewHolder 와 그에 연결된 View 를 생성하고 초기화하지만 뷰의 콘텐츠를 채우지는 않는다.
     * ViewHolder 아직 특정데이터에 바인딩된 상태가 아니기 때문이다.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder =
        QuoteViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quote, parent, false)
        )

    /**
     * onBindViewHolder() : RecyclerView 는 ViewHolder 를 데이터와 연결할 때 이 메서드를 호출한다.
     * 이 메서드는 적절한데이터를 가져와서 그 데이터를 사용하여 뷰 홀더의 레이아웃을 채운다. 예를 들어
     * RecyclerView 가 이름 목록을 표시하는 경우 메서드는 목록에서 적절한 이름을 찾아 뷰 홀더의 TextView 위젯을 채울 수 있다.
     *
     * 여기서는 holder 를 받아 bind 함수에서 목록을 표시하는 메서드를 연결시켰다.
     * quotes(명언이 담긴 리스트) 에 actualPosition(인덱스) 를 넣어서 bind 함수를 호출하였다.
     */
    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        // 실제 위치는 현재위치에서 quotes 리스트의 size 만큼을 나눈 나머지를 넣어준다.
        val actualPosition = position % quotes.size
        holder.bind(quotes[actualPosition], isNameRevealed)
    }

    /**
     * getItemCount() : RecyclerView 는 데이터 세트 크기를 가져올 때 이 메서드를 호출한다.
     * 예를 들어 주소록 앱에서는 총 주소 개수가 여기에 해당할 수 있다.
     * RecyclerView 는 이 메서드를 사용하여, 항목을 추가로 표시할 수 없는 상황을 확인한다.
     */
    override fun getItemCount() = Int.MAX_VALUE         // 무한으로 넘어가는 것을 사용자가 느끼도록 충분히 큰 수


    /**
     *
     * ViewHolder 는 목록에 있는 개별 항목의 레이아웃을 포함하는 View 의 래퍼이다.
     * Adapter 는 필요에 따라 ViewHolder 객체를 만들고 이러한 뷰에 데이터를 설정하기도 한다.
     * 뷰를 데이터에 연결하는 프로세스를 바인딩이라고 한다.
     *
     * 커스텀 ViewHolder
     */
    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // 명언을 보여주는 TextView 바인딩
        private val quoteTextView: TextView = itemView.findViewById(R.id.quoteTextView)

        // 이름을 보여주는 TextView 바인딩
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)


        /**
         *  TextView 에 Firebase remoteConfig 를 통해서 가져온 Quote 값을 넣어준다.
         *  isNameRevealed 에 따라서 nameTextView 를 보여줄지 숨길지 결정한다.
         */
        @SuppressLint("SetTextI18n")
        fun bind(quote: Quote, isNameRevealed: Boolean) {
            quoteTextView.text = "\"${quote.quote}\""

            if (isNameRevealed) {
                nameTextView.text = "- ${quote.name}"
                nameTextView.visibility = View.VISIBLE
            } else {
                nameTextView.visibility = View.GONE
            }
        }

    }
}
```

[RecyclerView 로 목록 만들기](https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=ko)

### 결과화면

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap2-result.PNG" width="360px" height="640px"/>

</p>

<br>

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
