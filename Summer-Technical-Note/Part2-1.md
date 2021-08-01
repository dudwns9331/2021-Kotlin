# Part2 - 1

## Summber Kotlin part2 2021-08-01 랜덤

<br>

## **_레이아웃 xml_**

- [NumberPicker](#NumberPicker)
- [Button](#LinearLayout)
- [ConstraintLayout](#ConstraintLayout)
- [TextView](#TextView)
- [LinearLayout](#LinearLayout)

---

## **Widget**

### NumberPicker

**activity_main.xml**

```xml
 <NumberPicker
        android:id="@+id/numberPicker"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="100dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>
```

`NumberPicker`는 사용자가 미리 정의된 범위에서 숫자를 선택할 수 있도록 하는 위젯이다. 이 위젯에는 두 가지 유형이 있으며 사용자에게 표시되는 유형은 현재 테마에 따라 다르다.

코틀린 코드를 통해서 `MinValue`, `MaxValue`를 설정할 수 있다. 이를 통해서 로또 번호 랜덤 추출기를 최소값과 최대값을 지정하고 그 사이의 범위의 숫자를 랜덤하게 추출하도록 한다.

id는 `numberPicker`로 설정하고, `wrap_content`를 통해서 콘텐츠의 사이즈에 맞게 위젯을 조절한다.

<br>

### Button

**activity_main.xml**

```xml
<Button
        android:id="@+id/addButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:layout_marginEnd="16dp"
        android:text="@string/addNumber"
        app:layout_constraintEnd_toStartOf="@+id/clearButton"
        app:layout_constraintHorizontal_chainStyle="packed"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/numberPicker" />
```

버튼은 총 3개로 `번호 추가하기, 초기화, 자동 생성 시작` 버튼으로 구성되어 있다. 각각 버튼들은 `ConstrainLayout` 으로 연결되어 있고 text는 `hardCoding`을 방지하기 위해서 `values/string.xml`에 버튼에 들어가는 text를 정의해 주었다.

<br>

### ConstraintLayout

ConstraintLayout 은 상대 위치에 따라 유연한 방식으로 위젯의 위치와 크기를 지정하는 레이아웃 방식이다. ConstraintLayout을 사용하면 플랫 뷰 계증 구조(중첨 뷰 그룹이 없음)로 크고 복잡한 레이아웃을 만들 수 있다.

ConstraintLayout에서 보기의 위치를 정의하려면 보기의 가로 및 세로 제약조건을 각각 하나 이상 추가해야 한다. 각 제약조건은 다른 보기, 상위 레이아웃 또는 표시되지 않는 안내선을 기준으로 한 정렬 또는 연결을 나타낸다.

ConstraintLayout은 Palette 창에서 드로그 앤 드롭으로 조절이 가능하지만 코드로 작성하는 것을 권장한다.

ConstraintLayout은 다음과 같은 제약조건을 추가하거나 삭제할 수 있다.

1. 상위 요소 포지셔닝

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/constraint01.PNG" width="460px" height="220px"/>

</p>

왼쪽은 상위 레이아웃의 왼쪽 가장자리에 연결되어 있다. 여백을 사용하여 가장자리로부터 거리를 정의할 수 있다.

<br>

2. 위치 순서 조정

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/constraint02.PNG" width="460px" height="220px"/>

</p>

가로 또는 세로로 두 보기가 표시되는 순서를 정의한다. B는 항상 A의 오른쪽에 있도록 제한되고 C는 A 아래 있도록 제한된다. 그러나 이 제약조건은 정렬을 의미하지 않으므로, B는 여전히 위아래로 이동할 수 있다.

<br>

3. 정렬

제약조건에서 안쪽으로 보기를 드래그라여 기준에서 벗어나 정렬할 수 있다.

<br>

4. 기준선 정렬

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/constraint03.PNG" width="460px" height="220px"/>

</p>

보기의 텍스트 기준선을 다른 보기의 텍스트 기준선에 맞춘다.

기준선 제약조건을 만들려면 제한할 텍스트 보기를 마우스 오른쪽 버튼으로 클릭한 다음 ShowBaseline을 선택한다. 그런 다음 텍스트 기준선을 클릭하고 선을 다른 기준선으로 드래그한다.

<br>

> 현재 프로젝트에서는 각 위젯의 위, 아래, 왼쪽, 오른쪽에 대한 제약조건 설정만 나타나 있으므로 자세한 설명은 생략한다.

<br>

### TextView

**activity_main.xml**

```xml
 <TextView
            android:id="@+id/textView1"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_margin="5dp"
            android:background="@drawable/circle_red"
            android:gravity="center"
            android:text="1"
            android:textColor="@color/white"
            android:textSize="16sp"
            android:textStyle="bold"
            android:visibility="gone"
            tools:visibility="visible" />
```

여기서 `android:background="@drawable/circle_red" ` 를 통해서 background에 drawable에 정의한 커스텀 background를 넣어주었다. `android:visibility="gone"` 을 통해서 초기에는 해당 텍스트뷰가 보이지 않도록 설정할 수 있다. (아예 생성되지 않는 것과 같음)

<br>

### LinearLayout

**activity_main**

```xml
  <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="30dp"
        android:gravity="center"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/addButton"
        tools:ignore="HardcodedText">
```

여기서 `LinearLayout`은 랜덤 번호들을 가리키는 `TextView`를 가로 일렬로 보여주기 위해 사용되었다. `android:layout_width="0dp"` 인 이유는 `constraintLayout`을 통해서 end와 start의 값을 `parent`로 맞췄기 때문에 해당 제약조건에 따라서 크기도 자동으로 조절되었기 때문이다.

<br>

---

### **_drawable_**

Button에 들어가는 background 값을 커스텀 해주기 위해서 drawable에 xml 파일을 따로 정의하도록 한다.

**circle_red.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>

<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">

    <solid android:color="#FF7272" />
    <size
        android:width="44dp"
        android:height="44dp" />

</shape>
```

`<Shape>` 태그안에 `android:shape="oval"` 코드를 통해서 전체 모양을 원으로 정의해주고 `<solid>` 를 통해서 채워지는 색을 지정해주도록 한다. `<size>` 를 통해서 전체 크기를 정의한다.

<br>

---

### **_Activity - Kotlin_**

**MainActivity.kt**

MainActivity.kt 바인딩 선언

```kt

// 선언부, 바인딩
 private val clearButton: Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }

    private val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    private val runButton: Button by lazy {
        findViewById<Button>(R.id.runButton)
    }

    private val numberPicker: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }

```

`lazy` 를 사용하여 초기화 하는 경우 `var` 을 사용할 수 없다. `val` 을 사용하는 경우만 가능하고 `val` 선언 뒤 `by lazy` 블록에서 초기화에 필요한 코드를 작성한다. 호출시점에서 최초 1회 초기화가 된다. `lateinit` 과 다르게 선언과 동시에 초기화 해야 한다. 변수 선언과 동시에 초기화를 선언하고 있지만 호출 시점에서 초기화가 이루어지므로 늦은 초기화라고 한다.

<br>

MainAcitivy.kt 바인딩 선언 리스트

```kt
private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.textView1),
            findViewById<TextView>(R.id.textView2),
            findViewById<TextView>(R.id.textView3),
            findViewById<TextView>(R.id.textView4),
            findViewById<TextView>(R.id.textView5),
            findViewById<TextView>(R.id.textView6)
        )
    }
```

`numberTextViewList` 를 통해서 `TextView`가 들어가는 List를 선언했다. `listOf` 함수를 통해서 초기값을 지정한다.

<br>

MainActivity.kt clearButton

```kt
private fun initClearButton() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            numberTextViewList.forEach {
                it.isVisible = false
            }
            didRun = false
        }
    }
```

`clearButton`에 `OnClickListener` 를 통해서 이벤트를 지정해주고 n`umberTextViewList`를 `forEach` 구문을 통해서 리스트에 담긴 `TextView`의 `isVisible` 값을 `false` 로 바꿔준다.

<br>

MainAcitivty.kt getRandomNumber

```kt
private fun getRandomNumber(): List<Int> {
    val numberList = mutableListOf<Int>()
        .apply {
            for (i in 1..45) {
                if (pickNumberSet.contains(i))
                    continue
                this.add(i)
            }
        }
    numberList.shuffle()
    return pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size).sorted()
    }
```

`getRandomNumber`에서 생성된 랜덤 숫자의 리스트를 리턴한다.

`.apply{}` 구문을 통해서 앞에서 활용되는 객체의 초기 값들을 지정할 수 있따. 여기서는 `pickNumberSet` 에 중복되는 값이 있는지 확인하고 리스트에 추가해 준다. `numberList.shuffle()` 을 통해서 랜덤하게 순서를 바꿔주고 `subList()` 함수를 통해서 앞에서 6자리 숫자만 랜덤하게 섞인 숫자를 추출한다.

<br>

MainActivity.kt - 전체 코드

```kt
package com.example.summer_part2_chapter02

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private val clearButton: Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }

    private val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    private val runButton: Button by lazy {
        findViewById<Button>(R.id.runButton)
    }

    private val numberPicker: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }

    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.textView1),
            findViewById<TextView>(R.id.textView2),
            findViewById<TextView>(R.id.textView3),
            findViewById<TextView>(R.id.textView4),
            findViewById<TextView>(R.id.textView5),
            findViewById<TextView>(R.id.textView6)
        )
    }

    private var didRun = false
    private val pickNumberSet = hashSetOf<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        initRunButton()
        initAddButton()
    }

    private fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNumber()

            didRun = true

            list.forEachIndexed { index, number ->
                val textView = numberTextViewList[index]

                textView.text = number.toString()
                textView.isVisible = true

                setNumberBackground(number, textView)
            }

            Log.d("MainActivity", list.toString())
        }
    }

    private fun initAddButton() {
        addButton.setOnClickListener {
            if (didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pickNumberSet.size >= 6) {
                Toast.makeText(this, "번호는 5개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }

            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /* TextView에 숫자를 넣어주는 작업 */
            val textView = numberTextViewList[pickNumberSet.size]
            textView.isVisible = true
            textView.text = numberPicker.value.toString()

            setNumberBackground(numberPicker.value, textView)

            pickNumberSet.add(numberPicker.value)
        }
    }

    private fun setNumberBackground(number: Int, textView: TextView) {
        when (number) {
            in 1..10 ->
                textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yello)
            in 11..20 ->
                textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 ->
                textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 ->
                textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
            else -> textView.background =
                ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }

    private fun initClearButton() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            numberTextViewList.forEach {
                it.isVisible = false
            }
            didRun = false
        }
    }

    private fun getRandomNumber(): List<Int> {
        val numberList = mutableListOf<Int>()
            .apply {
                for (i in 1..45) {
                    if (pickNumberSet.contains(i))
                        continue
                    this.add(i)
                }
            }
        numberList.shuffle()
        return pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size).sorted()
    }


}

```

### MainActivity.kt 전체 코드 Structure

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/randomNumberStructure.PNG" width="308px" height="452px"/>

</p>

<br>

안드로이드 스튜디오를 처음 자바로 배웠을 때랑 난이도가 비슷하다. 하지만 코들린에서 사용하는 문법은 확실히 자바보다 간결해진 것은 사실이다. 코틀린 문법이 아직 완벽하게 이해가지는 않지만 앞으로 프로젝트를 진행하면서 사용하는 여러가지 문법을 익히면서 진행한다면 충분히 내 것으로 만들 수 있을 것 같다.

layout 파일을 코딩하는 것은 어렵지만, 하나 하나씩 뜯어서 이해하면 정말로 쉽다. 하지만 여러 위젯에 들어가는 속성들의 값이 생소해서 어렵게 느껴지는 것 같다. layout에 들어가는 여러가지 라이브러리를 아직 사용해보지 못해서 잘 모르겠지만, layout을 코딩하는 것도 나중에는 어려워질 것 같은 느낌이다.

- 출처 : [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
