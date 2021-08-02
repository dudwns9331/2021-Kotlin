# Part2 - 1

## Summber Kotlin part2 2021-08-02 다이어리

<br>

## **_레이아웃 xml_**

- [TextView](#TextView)
- [AppCompatButton](#AppCompatButton)
- [ConstraintLayout](#ConstraintLayout)
- [NoActionBar](#NoActionBar)

---

## **Widget**

### TextView

**activity_main.xml** - TextView

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginBottom="50dp"
    android:fontFamily="@font/dongdong_font"
    android:text="@string/title"
    android:textSize="30sp"
    android:textStyle="bold"
    app:layout_constraintBottom_toTopOf="@+id/passwordLayout"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
/>
```

`android:fontFamily="@font/dongdong_font"` 를 통해서 font에 대한 지정을 할 수 있다. res에 font라는 폴더를 생성한 후에 font 파일을 먼저 저장하도록 한다. font는 보통 검색하여 완성된 폰트를 사용한다. 폰트는 `fontFamily` 속성을 이용해서 지정한다.

<br>

### AppCompatButton

**activity_main.xml** - AppCompatButton

```xml
 <androidx.appcompat.widget.AppCompatButton
    android:id="@+id/openButton"
    android:layout_width="50dp"
    android:layout_height="60dp"
    android:layout_marginEnd="10dp"
    app:layout_constraintBottom_toBottomOf="@+id/numberPicker1"
    app:layout_constraintEnd_toStartOf="@+id/numberPicker1"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="@+id/numberPicker1" />
```

여기서 일반 `Button` 위젯을 사용하지 않고 `AppCompatButton` 을 사용하는 이유는 일반 `Button` 위젯은 프로젝트를 생성했을 때 적용되는 기본 테마에 의해서 색이 고정되기 때문이다.

<br>

### ConstraintLayout

**activity_main.xml** - ConstraintLayout

NumberPicker

```xml
app:layout_constraintHorizontal_chainStyle="packed"
```

여기서 chain은 단일 축에서 그룹과 같은 동작을 제공하도록 한다. 다른 축은 구속될 수 있다.

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/chains.png" width="500px" height="140px"/>

</p>

<br>

체인은 체인의 첫 번째 요소에(헤드)에 설정된 속성에 의해 제어된다. 헤드는 수평 체인인 경우 맨 왼쪽 위젯이고 수직 체인인 경우는 맨 위 위젯이다.

속성을 설정을 `layout_constraintHorizontal_chainStyle` 으로 수평으로 정하거나 `layout_constraintVertical_chainStyle` 으로 수직으로 설정할 수 있다. 첫 번째 요소에서 체인의 동작은 지정된 스타일에 따라 변경된다.(기본 속성값은 `CHAIN_SPREAD` 이다.)

<br>

- CHAIN_SPREAD : 위젯의 크기에 맞게 공간을 일정하게 분할된다.
- CHAIN_SPREAD_INSIDE : 처음 요소와 마지막 요소는 끝에 붙고 나머지 공간은 일정하게 분할된다.
- CHAIN_PACKED : 체인의 요소가 함께 포장된다. 말 그대로 체인으로 묶인 위젯들이 하나의 팩 처럼 붙고 나머지 공간을 일정하게 분할된다.
- 가중치 체인 : 가중치 값에 따라서 위젯의 크기가 늘어남. (여백 x)

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/chains-styles.png" width="600px" height="270px"/>

</p>

<br>

### NoActionBar

**themes.xml**

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.Summerpart2chapter03" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
        <!-- Customize your theme here. -->
    </style>

    <style name="Theme.Summerpart2chapter03.NoActionBar" parent="Theme.MaterialComponents.DayNight.NoActionBar" />

</resources>
```

`res/themes/themes.xml` 파일에 `<style name="Theme.Summerpart2chapter03" parent="Theme.MaterialComponents.DayNight.DarkActionBar">` 를 통해서 기본 테마를 변경할 수 있다. 현재는 기본 테마가 적용되어 ActionBar가 상단에 표시되지만 ` <style name="Theme.Summerpart2chapter03.NoActionBar" parent="Theme.MaterialComponents.DayNight.NoActionBar" />` 를 추가하므로써 커스텀한 테마를 덮어 사용할 수 있다. 여기서 마지막 부분에 적은 `NoActionBar`를 통해서 상단에 액션바를 없애주었다.

---

### **_Activity - Kotlin_**

**MainActivity.kt**

```kt
package com.example.summer_part2_chapter03

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    private val openButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.openButton)
    }

    private val changePasswordButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.changePasswordButton)
    }

    private var changePasswordMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker1
        numberPicker2
        numberPicker3

        openButton.setOnClickListener {

            if (changePasswordMode) {
                Toast.makeText(this, "비밀번호 변경 중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser =
                "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            if (passwordPreference.getString("password", "000").equals(passwordFromUser)) {
                // 패스워드 성공
                startActivity(Intent(this, DiaryActivity::class.java))

            } else {
                // 실패
                showErrorAlertDialog()
            }
        }

        changePasswordButton.setOnClickListener {
            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser =
                "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            if (changePasswordMode) {

                passwordPreferences.edit(true) {
                    putString("password", passwordFromUser)
                }

                changePasswordMode = false
                changePasswordButton.setBackgroundColor(Color.BLACK)

            } else {
                // changePasswordMode 가 활성화 :: 비밀번호가 맞는지를 체크
                if (passwordPreferences.getString("password", "000").equals(passwordFromUser)) {

                    changePasswordMode = true
                    Toast.makeText(this, "변경할 패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()

                    changePasswordButton.setBackgroundColor(Color.RED)

                } else {
                    // 실패
                    showErrorAlertDialog()
                }
            }

        }
    }

    private fun showErrorAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("실패")
            .setMessage("비밀번호가 잘못되었습니다.")
            .setPositiveButton("확인") { _, _ -> }
            .create()
            .show()
    }
}

```

- numberPicker1, 2, 3 은 메인 화면에서 설정하는 비밀번호를 지정하기 위한 위젯을 바인딩 시킨 것이다.

- openButton 은 개인 다이어리를 열기 위해서 사용하는 버튼이다.

- changePasswordButton 은 비밀번호를 바꾸기 위해서 사용하는 버튼이다.

<br>

## **_openButton.setonClickListener_**

changePasswordMode(Boolean) 을 통해서 비밀번호 변경 모드인지 아닌지를 먼저 판단한다.

그 다음 val `passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)` 를 통해서 password 라는 이름으로 공유 환경설정 파일에 저장하도록 한다.

<br>

## **_changePasswordButton.setOnClickListener_**

`passwordPreferences` 를 통해서 공유 환경설정 파일에 있는 `password` 를 불러오도록 한다. passwordFromUser 를 통해서 numberPicker에 저장된 값들을 불러온다.

만약 changePasswordMode가 true이면 `SharedPreference` 가 저장되어 있는 `passwordPreferences` 값을 수정하도록 한다.

만약 changePasswordMode가 false이면 `SharedPreference`에 저장된 공유 환경설정 값인 password가 일치하는지 검사한다. 실패시 showErrorAlertDialog를 통해서 오류 다이얼로그를 보여줌.

**SharedPreferences?**

저장하려는 키-값 컬렉션이 비교적 작은 경우 SharedPreferences API를 사용한다. SharedPreferences 객체는 키-값 상이 포함된 파일을 가리키며 키-값 쌍을 일곡 쓸 수 있는 간단한 메서드를 제공한다. SharedPreferences 파일은 프레임워크에서 관리하며 비공개이거나 공유일 수 있다. (Context.MODE_PRIVATE)

SharedPreference 에서 제공하는 함수인 edit 을 통해서 공유 환경설정에 대한 값을 바꿔줄 수 있다. `putString("password", passwordFromuser)` 를 통해서 유저로부터 입력된 숫자 값을 바꾸도록 한다.

<br>

## **_showErrorAlertDialog_**

AlertDialog 는 `Builder(this)` 함수를 통해서 빌드하도록 한다.

- setTitle : 타이틀
- setMessage : 본문메시지
- setPositiveButton : 긍정 버튼(확인)
- setNegativeButton : 부정 버튼(취소)
- create() : 생성 함수
- shwo() : 화면에 보여주는 함수

---

**DiaryActivity.kt**

```kt
package com.example.summer_part2_chapter03

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity : AppCompatActivity() {

    // 메인 쓰레드에 연결된 루퍼가 만들어 진다.
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val diaryEditText = findViewById<EditText>(R.id.diaryEditText)
        val detailPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)

        diaryEditText.setText(detailPreferences.getString("detail", ""))

        val runnable = Runnable {
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit {
                putString("detail", diaryEditText.text.toString())

                Log.d("DiaryActivity", "Save! ${diaryEditText.text.toString()}")
            }
        }

        diaryEditText.addTextChangedListener {

            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 500)
        }
    }
}
```

`private val handler = Handler(Looper.getMainLooper())` 메인스레드에서 동작하는 루퍼를 핸들러에 넣어 저장한다. 이는 일기장에 기록되는 Text 값들을 저장하기 위함이다.

`val detailPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)` 를 통해서 diary에 공유 환경변수 값을 저장하도록 한다. 여기서는 diary에 적혀지는 Text값들이 저장된다.

<br>

## **_Runnable_**

`Runnable` 인터페이스는 상속 관계에 있는 클래스도 구현할 수 있도록 지원하는 모델이다. 일기를 저장하는 작업을 백그라운드에서 실행하기 때문에 `Runnable` 인터페이스를 통해서 처리한다. 해당 코드의 `Runnable`은 detail이라는 이름으로 `EditText`에 적히는 값들을 `SharedPreference`를 통해서 저장한다.

<br>

## **_diaryEditText.addTextChangedListener_**

`EditText`의 값이 변경될 때마다 `runnable`에 등록된 코드들을 실행하도록 한다. `removeCallbacks`를 통해서 이전에 불러왔던 콜백들을 지워주고 `postDelayed`를 통해서 스레드에 무리가 가지 않도록 5초의 텀으로 `runnable`을 실행하도록 한다.

<br>

**handler? Looper?**

- 루퍼(Looper) : Message Queue에서 메시지, Runnable 객체를 차례로 꺼내 핸들러가 처리하도록 전달한다.

- 핸들러(handler) : 루퍼로부터 받은 메시지, Runnable 객체를 처리하거나 메시지를 받아서 Message Queue에 넣는 스레드 간의 통신장치이다.

루퍼는 MainActivity가 실행됨과 동시에 for문 하나가 무한루프 돌고 있는 서브 스레드라고 생각하면 된다. 이 무한루프는 대기하고 있다가 자긴의 큐에 쌓인 메시지를 핸들러에 전달한다.
핸들러는 루퍼를 통해 전달되는 메시지를 받아서 처리하는 일종의 명령어 처리기로 사용된다. 앱이 실행되면 자동으로 생성되어 무한루프를 도는 루퍼와 달리 핸들러는 개발자가 직접 생성해서 사용해야 한다.
메시지는 루퍼의 큐에 값을 전달하기 위해 사용되는 클래스로 Message 객체에 미리 정해둔 코드를 입력하고, 큐에 담아두면 루퍼가 핸들러에 전달한다. - [출처](https://velog.io/@taeyang/Kotlin-%EC%8A%A4%EB%A0%88%EB%93%9C%EC%99%80-%EB%A3%A8%ED%8D%BC)

---

<br>

- 사진 출처 : [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
