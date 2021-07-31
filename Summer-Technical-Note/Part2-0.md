# Part2 - 0

## Summber Kotlin part2 2021-07-31

<br>

### **_레이아웃 xml_**

<br>

- [EditText](#EditText)
- [LinearLayout](#LinearLayout)

<br>

#### **EditText**

```xml
 <EditText
     android:id="@+id/plain_text_input"
     android:layout_height="wrap_content"
     android:layout_width="match_parent"
     android:inputType="text"/>
```

텍스트를 입력하고 수정하기 위한 사용자 인터페이스 요소이다. `EditText` 위젯을 정의할 때, `inputType` 속성을 지정해야 한다. 속성값을 지정해주지 않으면 default 값으로 `text` 로 type이 지정된다.

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/EditText.PNG" width="600px" height="300px"/>

</p>

<br>

[안드로이드 공식 개발자 페이지 EditText 설명 바로가기](https://developer.android.com/reference/android/widget/EditText)

<br>

#### **LinearLayout**

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingLeft="16dp"
    android:paddingRight="16dp"
    android:orientation="vertical" >
</LinearLayout>

```

`LinearLayout`은 세로 또는 가로의 단일 방향으로 모든 하위 요소를 정렬하는 뷰 그룹이다. `android:orientation` 속성을 사용하여 레이아웃 방향을 지정할 수 있다.

<br>

> 참고: 더 나은 성능과 도구 지원을 위해 ConstraintLayout을 사용하여 레이아웃을 빌드해야 한다.

<br>

`LinearLayout`의 모든 하위 요소는 순차적을 스택된다. 따라서 세로 목록은 너비에 상관없이 행 하나에 하나의 하위 요소만 갖고, 가로 목록의 높이는 한 행과 같다. (가장 긴 하위 요소의 높이에 패딩을 더한 값.) `LinearLayout`은 하위 요소 사이의 여백 및 각 하위 요소의 `gravity`(오른쪽, 가운데, 왼쪽 정렬) 을 따른다.

<br>

**레이아웃 가중치**

`LinearLayout`을 사용해서도 `android:layout_weight` 속성을 개별 하위 요소에 가중치를 할당할 수 있다. 이 속성은 뷰가 화면에서 얼마만큼 공간을 차지해야 하느냐에 따라 해당 뷰에 `weight` 를 할당할 수 있다. 큰 가중치 값을 사용하면 상위 뷰의 남은 공간을 모두 채우도록 확장할 수 있다. 하위 뷰에서는 가중치 값을 지정할 수 있고, 그러면 뷰 그룹의 남은 모든 공간이 선언된 가중치 비율에 따라 하위 요소에 할당된다. 기본 가중치는 0이다.

<br>

> 위의 설명은 LinearLayout에 android:layout_weight = x 의 값을 설정하여 화면에 차지하는 비율을 설정할 수 있다는 말과 같다.

<br>

- 균등 분포 : 각 하위 요소가 화면에서 동일한 크기의 공간을 사용하는 선형 레이아웃을 생성하려면, 각 뷰의 `android:layout_(width or height)` 를 0dp 로 설정하여 weight 값을 1로 주면 된다.

---

### **_layout의 기본 속성_**

<br>

- _android:id="@+id/id" : 레이아웃 XML 파일에 할당된 고유한 아이디를 통해서 Activity와 바인딩하여 활용 가능하다._ -> 해당 위젯을 활용하기 위해 필요한 고유의 ID 라고 생각하면 편하다.

<br>

- _android:layout_width : View의 너비를 정의한다._

  - wrap_content : 콘텐츠에 필요한 치수대로 알아서 크기를 조절하라고 뷰에 지시한다.
    <br>
  - match_parent : 상위 뷰 그룹이 허용하는 한 최대한 커지도록 뷰에 지시한다.

  <br>

- _android:layout_height : View의 높이를 정의한다._

  - wrap_content : 콘텐츠에 필요한 치수대로 알아서 크기를 조절하라고 뷰에 지시한다.
    <br>
  - match_parent : 상위 뷰 그룹이 허용하는 한 최대한 커지도록 뷰에 지시한다.

  <br>

- _android:layout_gravity : 자신을 포함하고 있는 부모 위젯 레이아웃에서 옵션 값에 따라 정렬한다._ -> 해당 콘텐츠 자체를 부모 레이아웃에 맞추어 정렬함

<br>

- _android:gravity : 자신의 뷰에서 포함하고 있는 데이터 정렬_ -> 해당 콘텐츠 안에 있는 내용을 콘텐츠 레이아웃에 맞추어 정렬함

<br>

- _android:layout_margin : 위젯이 부모 레이아웃의 테두리로부터의 여백을 말한다._

<br>

- _android:padding : 위젯 테두리로부터 위젯 안에 내용 사이의 여백을 말한다._

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/PaddingMargin.PNG" width="600px" height="460px"/>

</p>

<br>

[출처 : 블로그 이미지, 내용 참조](https://sharp57dev.tistory.com/26)

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/ViewGroup.PNG" width="600px" height="260px"/>

</p>

[안드로이드 공식 개발자 페이지 Layout 설명 바로가기](https://developer.android.com/guide/topics/ui/declaring-layout?hl=ko)

<br>

---

### **_Activity - Kotlin_**

**MainActivity.kt**

```kt
val heightEditText: EditText = findViewById(R.id.heightEditText)
val weightEditText = findViewById<EditText>(R.id.weightEditText)
val resultButton = findViewById<Button>(R.id.resultButton)
```

kotlin 에서는 자바와 다르게 val 과 var로 변수와 상수를 지정하도록 한다. 또한, 해당 변수나 상수에 저장할 때, type에 대한 명시가 필요하다.

<br>

```kt

if (heightEditText.text.isEmpty() || weightEditText.text.isEmpty()) {
  Toast.makeText(this, "빈 값이 있습니다.", Toast.LENGTH_SHORT).show()
  return@setOnClickListener
}
```

위와같이 나머지 문법은 Java와 비슷하다. 하지만 `return@setOnClickListener` 와 같이 해당 이벤트 함수의 이름에 @ 를 붙여서 return 하게 되면 해당 함수를 그냥 종료한다.

<br>

```kt
val intent = Intent(this, ResultActivity::class.java)
```

`intent`를 설정할 때도 뒤에 `ResultActivity::class.java` 와 같이 파라미터가 Kotlin 문법으로 바뀌는 것을 볼 수 있다.

코틀린에서는 더블콜론이 참조로 사용된다. 리플렉션을 위해서 더블콜론을 사용하는데 리플렉션이란 코드를 작성하는 시점에서 런타임상 컴파일된 바이트 코드에서 내가 작성한 코드가 어디에 위치하는지 알 수 없기 때문에 바이트 코드를 이용해 내가 참조하려는 값을 찾기 위해서 사용한다. `ResultActivity::class` 를 하면 `KClass` 를 리턴한다. 따라서 `KClass` 를 `Class`로 바꾸어 주어야 하는데 이때 `.java` 를 이용하여 자바 클래스 값을 받는다. - [출처](https://woovictory.github.io/2019/08/08/Kotlin-Double-Ref/)

<br>

```java

/**
     * Create an intent for a specific component.  All other fields (action, data,
     * type, class) are null, though they can be modified later with explicit
     * calls.  This provides a convenient way to create an intent that is
     * intended to execute a hard-coded class name, rather than relying on the
     * system to find an appropriate class for you; see {@link #setComponent}
     * for more information on the repercussions of this.
     *
     * @param packageContext A Context of the application package implementing
     * this class.
     * @param cls The component class that is to be used for the intent.
     *
     * @see #setClass
     * @see #setComponent
     * @see #Intent(String, android.net.Uri , Context, Class)
     */
    public Intent(Context packageContext, Class<?> cls) {
        mComponent = new ComponentName(packageContext, cls);
    }

```

Intent에 관한 java 코드이다.

<br>

**ResultActivity**

```kt
val resultText = when {
  bmi >= 35.0 -> "고도 비만"
  bmi >= 30.0 -> "중정도 비만"
  bmi >= 25.0 -> "경도 비만"
  bmi >= 23.0 -> "과체중"
  bmi >= 18.5 -> "정상체중"
  else -> "저체중"
}
```

코틀린에서는 여러가지 조건이 들어가는 조건문에서 CASE를 사용하지 않고 When을 사용한다. `when { 조건 -> 결과 else 예외 }` 형식으로 사용한다.
