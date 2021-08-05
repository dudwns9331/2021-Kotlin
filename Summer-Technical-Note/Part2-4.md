# Part2 - 4

## Summber Kotlin part2 2021-08-05 전자액자

<br>

## **_레이아웃 xml 요소_**

- [LinearLayout](#LinearLayout)
- [ImageView](#ImageView)

---

## **Widget**

### LinearLayout

**activity_main.xml** - LinearLayout

```xml
<LinearLayout
    android:id="@+id/firstRowLinearLayout"
    android:layout_width="0dp"
    android:layout_height="0dp"
    app:layout_constraintDimensionRatio="H, 3:1"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent">
```

전체 부모의 레이아웃이 `ConstraintLayout` 으로 설정되어 있기 때문에 `ConstraintLayout` 에 따라서 `width` 와 `height` 가 조정되도록 `0dp` 를 주었다.

두 찬원이 모두 0dp 로 설정된 경우 비율을 사용할 수 있다. 이 경우 시스템은 모든 제약 조건을 충족하고 지정된 종횡비를 유지하는 가장 큰 치수를 설정한다. 다른 치수를 기반으로 한 특정 면을 제한하려면 W, H 를 미리 추가하여 너비 또는 높이를 제한할 수 있다. `0dp는 match_parent 와 같다`

`app:layout_constraintDimensionRatio="H, 3:1"` 을 통해서 위젯의 한 차원을 다른 차원의 비율로 정의할 수도 있다 현재 속성 값이 H = (Height), 3:1 이므로 높이를 제한하고 3:1의 비율에 맞춰서 높이를 설정하겠다는 뜻이다.

<br>

### ImageView

**activity_main.xml** - ImageView

```xml
<ImageView
    android:id="@+id/imageView11"
    android:layout_width="0dp"
    android:layout_height="match_parent"
    android:layout_weight="1"
    android:scaleType="centerCrop" />
```

`scaleType` 을 `centerCrop` 으로 설정함으로써 이미지의 두 치수(높이와 너비)가 보기의 해당 치수(패딩 빼기) 보다 크거나 같도록 이미지의 크기를 균일하게 조정한다(이미지의 종횡비 유지).

---

### **_Activity - Kotlin_**

**MainActivity.kt** - property

```kt
    private val addPhotoButton: Button by lazy {
        findViewById(R.id.addPhotoButton)
    }

    private val startPhotoFrameModeButton: Button by lazy {
        findViewById(R.id.startPhotoFrameModeButton)
    }

    private val imageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.imageView11))
            add(findViewById(R.id.imageView12))
            add(findViewById(R.id.imageView13))
            add(findViewById(R.id.imageView21))
            add(findViewById(R.id.imageView22))
            add(findViewById(R.id.imageView23))
        }
    }

    private val imageUriList: MutableList<Uri> = mutableListOf()
```

`addPhotoButton` 을 통해서 "사진 추가하기" 버튼을 눌렀을 때 추가할 수 있도록 버튼을 바인딩 해준다.

`startPhotoFrameModeButton` 은 "전자 액자 실행하기" 버튼을 눌렀을 때 전자 액자 모드로 추가된 사진들을 보여주기 위해 사용한다.

현재 프로젝트는 6개의 사진 칸을 가지고 있고 숫자가 많지 않기 떄문에 imageView 타입을 가진 List를 통해서 한꺼번에 바인딩 하도록 한다.

List 에 있는 기본 함수인 add 를 통해서 List에 버튼 하나씩 추가해 준다. 선언과 함께 값을 주기 위해서 `.apply` 를 사용했다.

"전자 액자 실행하기" 를 눌렀을 때 사진에 대한 정보를 가져오기 위해서 변경 가능한 List의 형태로 Uri 타입으로 빈 리스트를 생성한다.

<br>

**MainActivity.kt** - onCreate(savedInstanceState: Bundle?)

```kt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 사진 관련 버튼 초기화, 코드 추상화
        initAddPhotoButton()
        initStartPhotoFrameModeButton()
    }
```

`initAddPhotoButton()` 을 통해서 사진 관련 버튼을 초기화하고 `initStartPhotoFrameModeButton()` 을 통해서 전자 액자 모드로 들어가기 위한 로직을 초기화한다.

<br>

**MainActivity.kt** - initAddPhotoButton()

```kt
    private fun initAddPhotoButton() {
        addPhotoButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    navigatePhotos()
                    // TODO 권한이 잘 부여 되었을 때 갤러리에서 사진을 선택하는 기능
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                    // TODO 교육용 팝업 확인 후 권한 팝업을 띄우는 기능
                }
                else -> {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                }
            }
        }
    }
```

`addPhotoButton` 리스너를 통해서 버튼이 눌렸을 때 권한이 잘 부여 되어 있으면 갤러리에서 사진을 선택하는 기능을 넣는다. 따라서 권한이 부여되어 있으면 `navigatePhotos()` 함수를 통해서 사진을 선택기를 실행하도록 하고, 만약 권한이 부여되어있지 않으면 권한 부여를 하도록 안내하는 창을 띄운다. 권한을 부여하지 않았을 경우에는 다시 교육용 팝업을 띄우고 권한을 부여하도록 유도한다.

<br>

### **_앱 권한 요청 ?_**

모든 Android 앱은 엑세스가 제한된 샌드박스에서 실행된다. 앱이 자체 샌드박스 밖에 있는 리소스나 정보를 사용해야 하는 경우 권한을 선언하고 이 액세스를 제공하는 권한 요청을 설정할 수 있다. 이러한 단계는 [권한 사용 워크플로](https://developer.android.com/guide/topics/permissions/overview?hl=ko#workflow) 의 일부이다.

위험한 권한을 선언하고 앱이 Android 6.0(API 수준 23) 이상을 실행하는 기기에 설치된 경우 이 가이드의 단계에 따라 위험한 런타임 권한을 요청해야 한다.

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/work-flow.PNG" width="500px" height="275px"/>

</p>

<br>

### **_기본 원칙_**

런타임 권한을 요청하기 위한 기본 원칙은 다음과 같다.

1. 사용자가 권한이 필요한 기능과 상호작용하기 시작할 때 컨텍스트에 따라 권한을 요청한다.

2. 사용자를 차단하지 않는다. 항상 권한과 관련된 교육용 UI 흐름을 취소하는 옵션을 제공한다.

3. 사용자가 기능에 필요한 권한을 거부하거나 취소하면 권한이 필요한 기능을 사용 중지하는 등의 방법으로 앱의 성능을 단계적으로 저하시켜 사용자가 앱을 계속 사용할 수 있도록 한다.

4. 시스템 동작을 가정하지 않는다. 예를 들어 동일한 권한 그룹에 권한이 표시된다고 가정하지마라. 권한 그룹은 앱이 밀접하게 관련된 권한을 요청할 때 시스템에서 사용자에게 표시하는 시스템 대화상자의 수를 최소화하는 데만 도움이 된다.

<br>

> 출처 - [안드로이드 공식 개발자 홈페이지](https://developer.android.com/training/permissions/requesting?hl=ko#workflow_for_requesting_permissions)

```kt
when {
    ContextCompat.checkSelfPermission(
            CONTEXT,
            Manifest.permission.REQUESTED_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED -> {
        // You can use the API that requires the permission.
    }
    shouldShowRequestPermissionRationale(...) -> {
        // In an educational UI, explain to the user why your app requires this
        // permission for a specific feature to behave as expected. In this UI,
        // include a "cancel" or "no thanks" button that allows the user to
        // continue using your app without granting the permission.
        showInContextUI(...)
    }
    else -> {
        // You can directly ask for the permission.
        // The registered ActivityResultCallback gets the result of this request.
        requestPermissionLauncher.launch(
                Manifest.permission.REQUESTED_PERMISSION)
    }
}
```

위의 코드 스니펫은 권한을 확인하고 필요할 때 사용자에게 권한을 요청하는 권장 프로세스이다.

<br>

**MainActivity.kt** - initStartPhotoFrameModeButton()

```kt

    private fun initStartPhotoFrameModeButton() {
        startPhotoFrameModeButton.setOnClickListener {
            val intent = Intent(this, PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed { index, uri ->
                intent.putExtra("photo$index", uri.toString())
            }

            intent.putExtra("photoListSize", imageUriList.size)
            startActivity(intent)
        }
    }
```

전자 액자 모드로 들어가기 위해 버튼을 눌렀을 때 실행되는 코드이다. `intent` 를 통해서 `PhotoFrameActivity` 로 `imageUriList` 의 값들을 `index` 와 `uri` 를 붙여서 넣어주도록 한다. 또한, `imageUriList` 의 사이즈를 넘겨준다. 그리고 `intent` 를 넣어주며 액티비티를 실행시킨다.

<br>

**MainActivity.kt** - onRequestPermissionsResult(requestCode :Int, permissions: array`<out String>`, grantResults : IntArray)

```kt

 /* 권한이 부여 되었을 때 제대로 부여 되었는지 확인하는 함수 */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigatePhotos()
                    // TODO 권한이 부여 되었음.
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            }
        }
    }

```

권한을 부여했을 때 잘 부여 되었는지 확인하는 함수 `requestCode` 를 통해서 해당 권한이 부여 되었는지를 확인한다.

<br>

**MainActivity.kt** - initStartPhotoFrameModeButton()

```kt
/* 사진 추가하기 버튼을 눌렀을 때 SAF 기능을 통해서 사진을 가져온다.*/
    private fun navigatePhotos() {
        // SAF 기능을 활용하기 위함.
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }
```

`intent` 를 넘겨줄 때 Intent.`ACTION_GET_CONTENT` 를 넘겨주는데, 이는 사용자가 특정 종류의 데이터를 선택하고 반환하도록 하는 것을 허용하는 것이다. 그 intent의 type을 `"imgae/"` 라는 문자열을 포함하는 모든것을 지정했다. 또한, `startActivityForResult(intent, 2000)` 을 통해서 액티비티를 실행했고 그에 대한 Result code를 2000으로 지정해 주었다.

<br>

### **_intent ?_**

인텐트는 수행할 작업에 대한 추상적인 설명이다. startActivity를 통해서 액티비티를 시작할 수 있다.

Intent는 다른 응용 프로그램 코드 간에 런타임 바인딩을 수행하기 위한 기능을 제공한다. 가장 중요한 용도는 액티비티를 시작하는 데 있으며 활동 사이의 접착제로 생각할 수 있다. 기본적으로 수행할 작업에 대한 추상적인 설명을 담고 있는 수동적 데이터 구조이다.

<br>

**MainActivity.kt** - onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

```kt
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2000 -> {
                val selectedImageUri: Uri? = data?.data

                if (selectedImageUri != null) {

                    if (imageUriList.size == 6) {
                        Toast.makeText(this, "이미 사진이 꽉 찼습니다.", Toast.LENGTH_SHORT).show()
                        return
                    }

                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
```

`startActivityForResult(intent, 2000)` 로 시작한 액티비티에서 requestCode가 2000인 경우를 통해서 해당 액티비티를 시작했을 때 요청 코드에 따라 콜백 요청을 할 수 있다.

선택한 사진들에 대해서 예외처리를 해주었고 만약에 정상적인 사진 선택이라면 imageUriList에 선택된 Image의 Uri를 추가하는 방식이다.

<br>

**MainActivity.kt** - showPermissionContextPopup()

```kt

    /* 교육용 팝업 : 권한을 거절하고 사진 관련 버튼을 누른 경우에 작동 */
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("전자액자에 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }
```

권한 요청을 거절했을 경우 권한이 필요하다는 것을 교육하기 위한 팝업이다. 권한을 거절하고 사진 관련 버튼을 누를 경우에 작동한다. 동의하기 버튼을 누르면 권한에 대한 요청이 실행된다.

<br>

---

**PhotoFrameActivity.kt** - property

```kt
    private var photoList = mutableListOf<Uri>()
    private var currentPosition = 0

    private var timer: Timer? = null

    private val photoImageView: ImageView by lazy {
        findViewById(R.id.photoImageView)
    }

    private val backgroundPhotoImageView: ImageView by lazy {
        findViewById(R.id.backgroundPhotoImageView)
    }
```

- 사진을 담기 위한 photoList
- 화면에 보여지는 현재 사진 currentPosition
- 사진을 넘기기 위한 시간 측정 Timer
- 사진을 담을 View PhotoImageView
- 사진이 담겨질 곳의 배경

<br>

**PhotoFrameActivity.kt** - onCreate(savedInstanceState: Bundle?)

```kt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoframe)

        Log.d("PhotoFrame", "onCreate! timer start")
        getPhotoUriFromIntent()
    }
```

`getPhotoUriFromIntent()` 함수를 통해서 `MainActivity` 에서 넘어온 `intent` 를 받는다.

<br>

**PhotoFrameActivity.kt** - getPhotoUriFromIntent()

```kt
    private fun getPhotoUriFromIntent() {
        val size = intent.getIntExtra("photoListSize", 0)
        for (i in 0..size) {
            intent.getStringExtra("photo$i")?.let {
                photoList.add(Uri.parse(it))
            }
        }
    }
```

size에 photoListSize 를 저장하고 for 문을 통해서 `photo + index` 순으로 가져온 후 photoList에 해당 값을 parse 하여 넣는다.

<br>

**PhotoFrameActivity.kt** - startTimer()

```kt

    private fun startTimer() {
        timer = timer(period = 5 * 1000) {
            runOnUiThread {
                Log.d("PhotoFrame", "5초 경과!")
                val current = currentPosition
                val next = if (photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                backgroundPhotoImageView.setImageURI(photoList[current])

                // 투명도를 0으로 준다.
                photoImageView.alpha = 0f
                photoImageView.setImageURI(photoList[next])
                photoImageView.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next
            }
        }
    }
```

Timer를 통해서 5초에 한번씩 Ui를 업데이트 하는 스레드 함수를 통해서 List의 사이즈 만큼 한번씩 보여준 후에 마지막 이후에는 다시 처음으로 돌아가도록 설정한다. `photoImageView` 에 대한 애니메이션 효과도 투명도를 통해서 점점 사라지는 효과를 준다.

<br>

**PhotoFrameActivity.kt** - onStop(), onStart(), onDestroy()

```kt

    override fun onStop() {
        super.onStop()
        Log.d("PhotoFrame", "onStop! timer cancel")
        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()
        Log.d("PhotoFrame", "onStart! timer start")
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PhotoFrame", "onDestroy! timer cancel")
        timer?.cancel()
    }
}

```

해당 함수들은 액티비티 생명주기에 관련된 함수로 사진이 앱을 사용할 때 화면을 내리거나 종료가 된 경우에 대해서 타이머에 대한 설정을 다시 하기 위해서 사용된다.

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/activity_lifecycle.PNG" width="500px" height="650px"/>

</p>

---

### **_AndroidMenifest.xml_**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.summer_part2_chapter05">
    <!-- 권한 -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Summerpart2chapter05">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity
            android:name=".PhotoFrameActivity"
            android:screenOrientation="landscape" />

    </application>

</manifest>
```

`<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />` 와 같이 외부 권한에 대해서 사용하고 싶다면 `AndroidMenifest.xml` 에 사용하고자 하는 권한을 명시해야 한다.

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
