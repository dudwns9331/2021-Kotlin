# Part2 - 3

## Summber Kotlin part2 2021-08-04 계산기

<br>

## **_레이아웃 xml 요소_**

- [View](#View)
- [AppCompatButton](#AppCompatButton)
- [TableLayout](#TableLayout)
- [ImageButton](#ImageButton)
- [ScrollView](#ScrollView)

---

## **Widget**

### View

**activity_main.xml** - View

```xml
<View
    android:id="@+id/topLayout"
    android:layout_width="0dp"
    android:layout_height="0dp"
    app:layout_constraintBottom_toTopOf="@+id/keypadTableLayout"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintVertical_weight="1" />
```

View는 뷰와 관련된 데이터를 별도의 구조에 넣지 않고 뷰 자체에 저장하기 위해 편의상 많이 사용된다. 이 프로젝트에서는 사용하지 않았음. 어떻게 사용하는지에 대해서는 다시 한번 살펴보아야 한다.

<br>

### AppCompatButton

**activity_main.xml** - AppCompatButton

```xml
  <androidx.appcompat.widget.AppCompatButton
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
    android:layout_margin="7dp"
    android:background="@drawable/button_background"
    android:clickable="false"
    android:enabled="false"
    android:onClick="buttonClicked"
    android:stateListAnimator="@null"
    android:text="@string/bracket"
    android:textColor="@color/green"
    android:textSize="24sp" />
```

여기서 일반 `Button` 위젯을 사용하지 않고 `AppCompatButton` 을 사용하는 이유는 일반 `Button` 위젯은 프로젝트를 생성했을 때 적용되는 기본 테마에 의해서 색이 고정되기 때문이다. 이는 2장 내용과 동일하다. `android:clickable="false"` 와 `android:enabled="false"` 을 통해서 버튼을 활성화 시키지 않았다. 그리고 `android:stateListAnimator="@null"` 을 통해서 뷰에 드러어블 상태에 따라 연결된 뷰에서 실행할 애니메이터를 기본값으로 초기화 시켜주었다. `android:onClick="buttonClicked"` 을 통해서 계산기 버튼에 대해서 클릭 리스너를 하나씩 만들지 말고 한꺼번에 컨트롤 하기 위해서 onClick 함수를 사용하도록 했다.

<br>

### TableLayout

**activity_main.xml** - TableLayout

```xml
<TableLayout
    android:id="@+id/keypadTableLayout"
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:paddingStart="15dp"
    android:paddingTop="21dp"
    android:paddingEnd="15dp"
    android:paddingBottom="21dp"
    android:shrinkColumns="*"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toBottomOf="@+id/topLayout"
    app:layout_constraintVertical_weight="1.5">
```

`TableLayout`은 행과 열로 하위 `View` 요소를 표시하는 `ViewGroup` 이다. `TableLayout`은 자신의 하위 요소를 행과 열에 배치한다. `TableLayout`의 컨테이너는 행, 열 또는 셀의 테두리 선을 표시하지 않는다. 표에는 셀이 가장 많은 행과 같은 수의 열이 있다. 테이블은 셍르 비워둘 수도 있고, 셀은 HTML에서와 마찬가지로 여러 열게 걸쳐 있을 수 있다. `TableRow.LayoutParams` 클래스의 `span` 필드를 사용하여 열을 확장할 수 있다.

`android:shrinkColumns="*"` 는 `TableLayout` 의 내용이 전체 너비를 넘어버리는 경우 특정 열의 너비를 자동으로 줄여서 전체 너비를 넘어서지 않도록 만드는 것이다.

`TableLayout`은 `TableRow` 라는 태그와 함께 사용하는데 `TableRow`의 `layout_weight` 를 조정하여 전체 높이에 대해서 얼마만큼의 행을 나눌 수 있을지 비율 조정이 가능하다. 또한, `TableRow` 안에 들어가는 요소들은 그 행에 적용되어 오른쪽으로 열 값에 채워지게 된다.

<br>

### ImageView

**activity_main.xml** - ImageButton

```xml
<ImageButton
    android:id="@+id/historyButton"
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
    android:layout_margin="7dp"
    android:background="@drawable/button_background"
    android:contentDescription="@string/history"
    android:onClick="historyButtonClicked"
    android:src="@drawable/ic_baseline_access_time_24"
    android:stateListAnimator="@null"
    android:textColor="@color/textColor"
    android:textSize="24sp" />
```

사용자가 누르너가 클릭할 수 있는 이미지(텍스트 대신)가 있는 버튼을 표시한다. 기본적으로 ImageButton은 Button 상태 동안 색상이 변경되는 표준 버튼 배경을 사용하여 일반 버튼처럼 보인다. `android:src="@drawable/ic_baseline_access_time_24"` 와 같이 Button 이미지 리소스를 지정할 수 있다.

<br>

### ScrollView

**activity_main.xml** - ScrollView

```xml
<ScrollView
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:layout_margin="10dp"
    app:layout_constraintBottom_toTopOf="@+id/historyClearButton"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toBottomOf="@id/closeButton">
```

스크롤뷰 태그 안에 배치된 요소들을 스크롤 할 수 있도록 하는 `ViewGroup`이다. 가로 스크롤의 경우는 `HorizontalScrollView`를 사용해야 한다.

---

### **_Activity - Kotlin_**

**MainActivity.kt** - property

```kt

    private val expressionTextView: TextView by lazy {
        findViewById(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById(R.id.resultTextView)
    }

    private var isOperator = false
    private var hasOperator = false
    private var calculatedValue = false

    private val historyLayout: View by lazy {
        findViewById(R.id.historyLayout)
    }

    private val historyLinearLayout: LinearLayout by lazy {
        findViewById(R.id.historyLinearLayout)
    }

    lateinit var db: AppDatabase

```

`lateinit var db : AppDataBase` lateinit 을 통해서 선언했고 초기값을 지정하지 않았다. 이 변수는 nullable 이 아니기 때문에 초기값을 할당하지 않으면 에러가 발생하지만, lateinit으로 인해서 컴파일 에러가 발생하지 않는다.

`lateinit` 은 `var` 에만 사용할 수 있다. 또한, `primitive type`에 적용할 수 없다. `primitive type`은 `Int, Boolean, Double` 등 코틀린에서 제공하는 기본적인 타입을 말한다. 따라서, `val`을 사용하거나 Int를 사용한 코드는 컴파일 에러가 발생한다.

<br>

**MainActivity.kt** - onCreate()

```kt
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 데이터베이스 생성
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
    }

```

Room은 jetpack 라이브러리 중 하나로 SQLite에 추상화 레이어를 제공하여 SQLite를 완벽히 활용하면서 더 견고한 데이터베이스 엑세스를 가능하게 한다.

`build.gradle` 파일에 필요한 아티팩트 종속 항목을 추가한다.

build.gradle

```gradle
    implementation "androidx.room:room-runtime:2.3.0"
    kapt "androidx.room:room-compiler:2.3.0"
```

<br>

**MainActivity.kt** - onButtonClicked(v : View)

```kt
fun buttonClicked(v: View) {
    when (v.id) {

        // 숫자
        R.id.button0 -> numberButtonClicked("0")
        R.id.button1 -> numberButtonClicked("1")
        R.id.button2 -> numberButtonClicked("2")
        R.id.button3 -> numberButtonClicked("3")
        R.id.button4 -> numberButtonClicked("4")
        R.id.button5 -> numberButtonClicked("5")
        R.id.button6 -> numberButtonClicked("6")
        R.id.button7 -> numberButtonClicked("7")
        R.id.button8 -> numberButtonClicked("8")
        R.id.button9 -> numberButtonClicked("9")

        // 수식
        R.id.buttonPlus -> operatorButtonClicked("+")
        R.id.buttonMinus -> operatorButtonClicked("-")
        R.id.buttonMulti -> operatorButtonClicked("x")
        R.id.buttonDivider -> operatorButtonClicked("÷")
        R.id.buttonModulo -> operatorButtonClicked("%")
    }
}
```

`buttonClicked` 는 xml 파일의 위젯에 지정된 `onClick` 함수를 통해서 만든다. 해당 `View` 에 대해서 파라미터로 받고 `View` 의 `id` 를 `when` 문에 넣어 `id 에 따라서 어떤 버튼이 눌렸는지 구분한다.

<br>

**MainActivity.kt** - numberButtonClicked(number : String)

```kt
/* 버튼이 눌릴 때마다 숫자를 입력하고 실시간으로 계산 결과를 보여준다. */
    private fun numberButtonClicked(number: String) {

        if (calculatedValue) {
            clearValue()
        }

        if (isOperator) {
            expressionTextView.append(" ")
        }
        isOperator = false

        val expressionText = expressionTextView.text.split(" ")

        // 15 자리까지 입력 가능하도록 예외처리
        if (expressionText.isNotEmpty() && expressionText.last().length >= 15) {
            Toast.makeText(this, "15자리 까지만 사용할 수 있습니다. ", Toast.LENGTH_LONG).show()
            return
            // 비어 있거나, 제일 앞에 0이 오는 경우 예외처리
        } else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_LONG).show()
            return
        }

        expressionTextView.append(number)
        // TODO resultTextView 실시간으로 계산 결과를 넣여야 하는 기능
        resultTextView.text = calculateExpression()
    }
```

만약 `calculatedValue` 가 `true` 라면 이미 계산된 값이므로 다음 값을 입력받기 위해서 초기화한다.

`isOperator` 가 `true` 라면 버튼에서 입력된 값이 수식이므로 `expressionTextView` 에 공백 문자를 넣어 수식을 구분하도록 한다.

`expressionText` 숫자에 대한 값들이 들어있고 15자리까지만 사용할 수 있도록 예외처리 해준다.

`expressionTextView` 에 입력된 수식의 값을 표현하도록 한다.

<br>

**MainActivity.kt** - operatorButtonClicked(operator : String)

```kt
private fun operatorButtonClicked(operator: String) {
        if (expressionTextView.text.isEmpty()) {
            return
        }

        // 연산자가 이미 눌렸을 떄 한번 더 누르면 자동으로 수정하기 위해서
        when {
            isOperator -> {
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1) + operator
            }
            hasOperator -> {
                Toast.makeText(this, "연산자는 한 번만 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
                return
            }
            else -> {
                expressionTextView.append(" $operator")
            }
        }

        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        expressionTextView.text = ssb

        isOperator = true
        hasOperator = true
        calculatedValue = false
    }
```

수식 입력 값이 `String` 형태로 들어오게 되고 만약에 `expressionTextView` 에 숫자가 들어가 있지 않으면 그대로 종료한다.

`when` 문을 이용해서 `operator` 가 이미 눌렸을 때 한번 더 누르면 자동으로 수정하기 위해 만약 연산자일 때, 마지막 연산자를 지우고 다시 넣던가, 이미 연산자가 입력되어 있을 때 한번만 사용 가능하도록 예외처리한다. 이외의 경우는 연산자를 그대로 붙인다.

`SpannableStringBuilder` 를 통해서 만약에 입력도니 값이 연산자일 경우에 연산자에 대한 글자를 초록색으로 바꿔준다.

<br>

**MainActivity.kt** - resultButtonClicked(v : View)

```kt
/* 연산 결과를 TextView2(resultText)에 따로 보여준다. */
    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")

        // 비어 있거나 앞에 숫자만 입력된 경우
        if (expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }

        // 숫자와 연산자 까지만 입력된 경우
        if (expressionTexts.size != 3 && hasOperator) {
            Toast.makeText(this, "아직 완성되지 않은 수식입니다.", Toast.LENGTH_LONG).show()
            return
        }

        // 중간에 코드가 잘못된 경우. 혹시 모를 상황에 대비한다. => 첫번재 숫자와 두번째 숫자가 제대로 치환되지 않은 경우
        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            return
        }

        // 계산 수식과 결과 값을 history 에서 보여주기 위해 DB에 저장할 값을 지정한다.
        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        // TODO DB에 넣어주는 부분

        Thread(Runnable {
            db.historyDao().insertHistory(History(null, expressionText, resultText))
        }).start()

        // 계산 결과가 나오면 결과 값이 다시 수식으로 올라가는 기능을 구현하기 위함.
        resultTextView.text = ""
        expressionTextView.text = resultText

        isOperator = false
        hasOperator = false
        calculatedValue = true

    }
```

expressionTexts 에 수식으로 입력된 값들을 split을 통해서 공백으로 구분짓는다.

각각의 경우에 대해서 예외처리를 해준다.

1. 비어 있거나 앞에 숫자만 입력된 경우
2. 숫자와 연산자까지만 입력된 경우
3. 중간에 코드가 잘못된 경우, 혹시 모를 상황에 대비 -> 첫번째 숫자와 두번째 숫자가 제대로 치환되지 않은 경우

`Thread`를 이용해서 DB에 계산 결과 값을 저장하는 작업을 한다. Dao에 저장된 방식으로 `Histroy` 를 기록한다.

계산겨로가가 나오면 결과 값이 다시 수식으로 올라가는 기능을 구현하기 위해서 `resultTextView` 의 값은 초기화 해주고, `expressionTextView` 의 text 값은 `resultText` 의 값을 넣어준다.

<br>

**MainActivity.kt** - calculateExpression() : String

```kt
 private fun calculateExpression(): String {
        val expressionTexts = expressionTextView.text.split(" ")

        if (hasOperator.not() || expressionTexts.size != 3) {
            return ""
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            return ""
        }

        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "x" -> (exp1 * exp2).toString()
            "÷" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }
    }
```

수식에 대한 계산을 하는 함수이다. 반환 값으로 결과 값을 `String` 으로 리턴하도록 한다.

수식이 올바르게 입력되지 않은 경우, 연산자가 없거나 2개의 피연산자 1개의 연산자가 입력되지 않은 경우에 대해서 예외처리를 해준다.

`exp1` 은 첫번째 입력된 숫자, `exp2` 는 두번째 입력된 숫자, `op` 는 첫번째와 두번째 입력된 숫자 사이에 입력된 수식이다.

`when` 문을 사용하여 각각의 경우 대한 계산 결과 값을 `String` 으로 반환한다.

<br>

**MainActivity.kt** - clearButtonClicked(v: View), clearValue()

```kt
  /* 입력된 수식, 오퍼레이터 상태 값 초기화 */
    fun clearButtonClicked(v: View) {
        clearValue()
    }

    private fun clearValue() {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
        calculatedValue = false
    }

```

입력된 수식, 연산자 상태 값을 초기화 한다.

<br>

**MainActivity.kt** - historyButtonClicked(v: View)

```kt

/* 스레드 실행 */
    @SuppressLint("SetTextI18n")
    fun historyButtonClicked(v: View) {
        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao().getAll().reversed().forEach {
                runOnUiThread {
                    val historyView =
                        LayoutInflater.from(this).inflate(R.layout.history_row, null, false)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"
                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()

        // TODO 디비에서 모든 기록 가져오기
        // TODO 뷰에 모든 기록 가져오기
    }
```

계산 기록 버튼을 통해서 이전에 계산했던 내역을 보여준다.

`historyLinearLayout.removeAllViews()` 함수를 통해서 `historyLinearLayout` 에 연결된 모든 뷰를 지워서 초기화 시켜준다.

Thread 를 통해서 DB에 기록된 계산 수식, 결과 값을 역순으로 해서 가장 최근의 값부터 불러온다. runOnUiThread 를 통해서 Thread 에서 진행중인 작업을 Ui를 작업하는 thread에서 작업하도록 한다.

`val historyView = LayoutInflater.from(this).inflate(R.layout history_row, null, false)` 를 통해서 동적으로 `View` 를 `Inflate` 하도록 한다. `historyView` 는 스크롤뷰 안에 생기는 `xml` 파일이다. `xml` 형식은 `layout` 폴더에 있는 `history_row` 를 이용한다.

- LayoutInflater?

  - 레이아웃 XML 파일을 해당 View 개체로 인스턴스화 한다. 직접 사용한느 것이 아니다.

  - 이 클래스는 스레드로부터 안전하지 않으며 지정된 단일 스레드에서만 액세스해야 한다.

LayoutInflater 주석 설명

```s
/**
 * Instantiates a layout XML file into its corresponding {@link android.view.View}
 * objects. It is never used directly. Instead, use
 * {@link android.app.Activity#getLayoutInflater()} or
 * {@link Context#getSystemService} to retrieve a standard LayoutInflater instance
 * that is already hooked up to the current context and correctly configured
 * for the device you are running on.
 * <p>
 * To create a new LayoutInflater with an additional {@link Factory} for your
 * own views, you can use {@link #cloneInContext} to clone an existing
 * ViewFactory, and then call {@link #setFactory} on it to include your
 * Factory.
 * <p>
 * For performance reasons, view inflation relies heavily on pre-processing of
 * XML files that is done at build time. Therefore, it is not currently possible
 * to use LayoutInflater with an XmlPullParser over a plain XML file at runtime;
 * it only works with an XmlPullParser returned from a compiled resource
 * (R.<em>something</em> file.)
 * <p>
 * <strong>Note:</strong> This class is <strong>not</strong> thread-safe and a given
 * instance should only be accessed by a single thread.
 */
```

<br>

**MainActivity.kt** - closeHistoryButtonClicked(v: View), historyClearButtonClicked(v: View)

```kt

 fun closeHistoryButtonClicked(v: View) {
        historyLayout.isVisible = false
    }

    fun historyClearButtonClicked(v: View) {
        historyLinearLayout.removeAllViews()
        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()

        // TODO DB에서 모든 기록 삭제
        // TODO 뷰에서 모든 기록 삭제
    }

```

계산 기록 보기를 닫으면 레이아웃이 보이지 않도록 isVisible 값을 false로 한다.

계산 기록에 대해서 지우기 버튼을 통해서 모든 기록을 삭제한다.

<br>

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
