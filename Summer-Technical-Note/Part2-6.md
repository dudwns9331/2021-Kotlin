# Part2 - 6

## Summber Kotlin part2 2021-08-09 시각화 녹음기

<br>

## **_레이아웃 xml 요소_**

- [SoundVisualizerView](#SoundVisualizerView)
- [CountUpView](#CountUpView)
- [RecordButton](#RecordButton)

---

## **Widget**

### SoundVisualizerView

**activity_main.xml** - SoundVisualizerView

```xml
<com.example.summer_part2_chapter07.SoundVisualizerView
    android:id="@+id/soundVisualizer"
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:layout_marginBottom="10dp"
    app:layout_constraintBottom_toTopOf="@+id/recordTimeTextView"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent">
</com.example.summer_part2_chapter07.SoundVisualizerView>
```

`SoundVisualizerView` 는 녹음될 때 녹음되는 값들에 대해서 높낮이를 보여준다. `SoundVisualizerView` 라는 코틀린 클래스를 생성해서 따로 정의를 해주고 기능에 맞게 코딩한다.

<br>

### CountUpView

**activity_main.xml** - CountUpView

```xml
<com.example.summer_part2_chapter07.CountUpView
    android:id="@+id/recordTimeTextView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginBottom="20dp"
    android:text="00:00"
    android:textColor="@color/white"
    app:layout_constraintBottom_toTopOf="@+id/recordButton"
    app:layout_constraintLeft_toLeftOf="@+id/recordButton"
    app:layout_constraintRight_toRightOf="@+id/recordButton" />
```

`CountUpView` 는 녹음 될 때 녹음되는 시간을 체크하여 `TextView` 로 보여준다.

<br>

### RecordButton

**activity_main.xml** - RecordButton

```xml
<com.example.summer_part2_chapter07.RecordButton
    android:id="@+id/recordButton"
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:layout_marginBottom="50dp"
    android:padding="20dp"
    android:scaleType="fitCenter"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    tools:src="@drawable/ic_record">
</com.example.summer_part2_chapter07.RecordButton>
```

`RecordButton` 은 녹음을 시작하기 위해서 사용하는 버튼이다. 녹음을 시작 하기 전에는 빨간점을 표시하고 녹음이 되는 동안에는 정지 버튼을 띄우도록 되어있다. 녹음을 재생하는 버튼도 있고 `Reset` 을 누르면 초기화 된다.

<br>

### **_Activity - Kotlin_**

**MainActivity.kt** - property

```kt
    private val soundVisualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.soundVisualizer)
    }

    private val recordTimeTextView: CountUpView by lazy {
        findViewById(R.id.recordTimeTextView)
    }

    private val resetButton: Button by lazy {
        findViewById(R.id.resetButton)
    }

    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }

    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    /* 메모리 관리를 위해서 null 로 초기화 시켜준다. */
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var state = State.BEFORE_RECORDING
        set(value) {
            field = value
            resetButton.isEnabled = (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING)
            recordButton.updateIconWithState(value)
        }

```

1. soundVisualizerView : 녹음 상황을 시각화 해준다.
2. recordTimeTextView : 녹음 시간을 알려주는 텍스트뷰
3. resetButton : 녹음 초기화 버튼
4. recordButton : 녹음 시작 버튼
5. requiredPermissions : 녹음에 필요한 권한을 받도록 한다.
6. recordingFilePath : 녹음된 파일의 경로를 지정한다.
7. recorder, player : 녹음 기능을 사용하기 위한 변수
8. state : 버튼의 상태(녹음의 상태) 를 표시하기 위한 상수

<br>

#### **_*MediaRecorder?*_**

Android 멀티미디어 프레임워크에는 다양한 일반 오디오 및 동영상 포맷을 캡처하고 인코딩하는 지원 기능이 포함되어 있다. 기기 하드웨어에서 지원되는 경우 MediaReocrder API를 사용할 수 있다.

<br>

**_오디오 녹음 권한 요청_**

> `<uses-permission android:name="android.permission.RECORD_AUDIO" />`

녹음하려면 앱에서 사용자에게 기기의 오디오 입력에 대한 액세스할 것임을 알리는 메시지를 표시해야 한다. 앱의 메니페스트 파일에 다음 권한 태그를 포함해야 한다.

`private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)` 을 통해서 `RECORD_AUDIO` 에 대한 모든 권한 정보를 배열에 담아 필요한 권한을 받을 수 있도록 한다.

<br>

**MainActivity.kt** - onCreate(savedInstanceState: Bundle?)

```kt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAudioPermission()
        initView()
        bindViews()
        initVariables()
    }
```

`requestAudioPermission()` 함수를 통해서 Audio 에 대한 권한을 얻도록 한다. `initView()` 함수를 통해서 버튼 상태에 대한 초기화를 진행한다. `bindViews()` 함수를 통해서 표시되는 뷰들에 대해 이벤트를 정의한다. `initVariables()` 를 통해서 상태에 대한 값을 초기화 시켜준다.

<br>

**MainActivity.kt** - requestAudioPermission()

```kt
    /* 권한을 요청하는 함수 */
    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }
```

**MainActivity.kt** - initView()

```kt
    /* 상태 초기화 */
    private fun initView() {
        recordButton.updateIconWithState(state)
    }
```

<br>

**MainActivity.kt** - bindViews()

```kt
    /* 버튼 리스너 연결 */
    private fun bindViews() {

        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }

        resetButton.setOnClickListener {
            stopPlaying()
            soundVisualizerView.clearVisualization()
            recordTimeTextView.clearCountTime()
            state = State.BEFORE_RECORDING
        }

        recordButton.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }
```

`soundVisualizerView` 에 대한 초기화를 진행한다. `reset` 버튼이 눌렸을 때 모든 상태들을 녹음을 시작하기 전으로 돌린다. 녹음 버튼에 대해서 상태에 따라 녹음의 시작, 정지, 재생, 멈춤 이 동작하도록 `when` 문을 통해서 조건을 나눈다.

<br>

**MainActivity.kt** - initVariables()

```kt
    private fun initVariables() {
        state = State.BEFORE_RECORDING
    }
```

<br>

**MainActivity.kt** - startRecording(), stopRecording(), startPlaying(), stopPlaying()

```kt
    /* 녹음 시작 : 각 함수는 순서를 지켜야함 */
    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }
        // 녹음 시작
        recorder?.start()
        soundVisualizerView.startVisualizing(false)
        recordTimeTextView.startCountUp()
        state = State.ON_RECORDING
    }

    /* 녹음 종료 */
    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    /* 재생 시작 */
    private fun startPlaying() {
        player = MediaPlayer()
            .apply {
                setDataSource(recordingFilePath)
                prepare()
            }

        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECORDING
        }

        player?.start()
        soundVisualizerView.startVisualizing(true)
        recordTimeTextView.startCountUp()
        state = State.ON_PLAYING
    }

    /* 재생 종료 */
    private fun stopPlaying() {
        player?.release()
        player = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }
```

녹음 시작은 아래 다이어그램의 순서에 따라서 진행된다.

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/mediarecorder_state_diagram.PNG" width="650px" height="720px"/>

</p>

<br>

녹음이 시작되는 순간에 시각화를 시작하고 `startCountUp` 을 시작해서 시각화와 녹음 시간이 표시되도록 한다. 또한 상태를 `ON_RECORDING` 으로 설정하여 녹음되는 상태를 저장하도록 한다.

녹음이 종료 되었을 때도 stop() 과 release() 함수를 통해서 멈춰주고, recorder 를 null 처리하여 메모리를 절약하도록 한다. 또한 시각화와 녹음 시간 증가를 멈추고 상태를 바꿔주도록 한다.

재생이 시작될 때는 MediaPlayer 를 통해서 녹음이 저장된 경로로 이동해 재생을 시작한다. 또한, 시각화에 대해서 녹음된 자료가 재생된다는 것을 Boolean 값으로 알려주고 녹음 시간에 대해서 다시 증가시키도록 하며 상태 값을 바꿔주도록 한다.

재생이 종료되엇을 때 recorder 와 마찬가지로 player 를 null 로 초기화 하여 메모리를 절약하도록 한다. 시각화와 녹음 시간도 멈춰주고 상태를 녹음 후로 바꿔준다.

<br>

**MainActivity.kt** - companion object

```kt
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
```

#### **_companion object?_**

코틀린에서는 `static` 멤버 변수나 함수가 없다. 만약 클래스 인스턴스 없이 어떤 클래스 내부에 접근하고 싶다면 클래스 내부에 객체를 선언할 때 `companion` 식별자를 붙인 `object` 를 선언하면 된다. 동반자 객체(`companion object`) 는 어떤 클래스의 모든 인스턴스가 공유하는 객체를 만들고 싶을 때 사용하며 클래스당 한 개만 가질 수 있다. `companion object` 의 멤버가 `static` 으로 선언된 변수처럼 보이지만 `companion object` 는 프로그램 런타임에 실제 객체의 인스턴스로 실행된다.

<br>

**MainActivity.kt** - onRequestPermissionsResult(`requestCode: Int, permissions: Array<out String>, grantResults: IntArray`)

```kt
    /* permission 을 요청한것에 대한 처리 */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        /* 권한 거절 시 종료 */
        if (!audioRecordPermissionGranted) {
            finish()
        }
    }
```

권한요청에 대한 결과 처리 함수이다. 권한을 거절했을 때 종료하도록 했다.

---

### **_View, Enum - Kotlin_**

**SoundVisualizerView.kt**

```kt
package com.example.summer_part2_chapter07

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SoundVisualizerView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var onRequestCurrentAmplitude: (() -> Int)? = null

    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            color = context.getColor(R.color.teal_700)
            strokeWidth = LINE_WIDTH
            strokeCap = Paint.Cap.ROUND
        }

    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()
    private var isReplaying: Boolean = false
    private var replayingPosition: Int = 0

    private val visualizerRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if (!isReplaying) {

                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            } else {
                replayingPosition++
            }
            // 갱신
            invalidate()

            // Amplitude, Draw
            handler?.postDelayed(this, ACTION_INTERVAL)

        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2f
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes
            .let { amplitudes ->
                if (isReplaying) {
                    amplitudes.takeLast(replayingPosition)
                } else {
                    amplitudes
                }
            }
            .forEach { amplitude ->
                val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F

                offsetX -= LINE_SPACE
                if (offsetX < 0) return@forEach

                canvas.drawLine(
                    offsetX,
                    centerY - lineLength / 2F,
                    offsetX,
                    centerY + lineLength / 2F,
                    amplitudePaint
                )
            }
    }

    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying
        handler?.post(visualizerRepeatAction)
    }

    fun stopVisualizing() {
        replayingPosition = 0
        handler?.removeCallbacks(visualizerRepeatAction)
    }

    fun clearVisualization() {
        drawingAmplitudes = emptyList()
        invalidate()
    }

    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 20L
    }
}
```

<br>

**RecordButton.kt**

```kt
package com.example.summer_part2_chapter07

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

class RecordButton(
    context: Context, attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {
    /* AppCompat 은 이전버전의 호환성을 위해서 사용한다.*/
    init {
        setBackgroundResource(R.drawable.shape_oval_button)
    }


    /* 버튼의 아이콘 상태를 업데이트 하는 함수 */
    fun updateIconWithState(state: State) {
        when (state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop)
            }
        }
    }

}
```

<br>

**CountUpView.kt**

```kt
package com.example.summer_part2_chapter07

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var startTimeStamp: Long = 0L

    private val countUpAction: Runnable = object : Runnable {
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime()
            val countTimeSeconds = ((currentTimeStamp - startTimeStamp) / 1000L).toInt()
            updateCountTime(countTimeSeconds)

            handler?.postDelayed(this, 1000L)
        }
    }

    fun startCountUp() {
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }

    fun stopCountUp() {
        handler?.removeCallbacks(countUpAction)
    }

    fun clearCountTime() {
        updateCountTime(0)
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountTime(countTimeSeconds: Int) {
        val minutes = countTimeSeconds / 60
        val seconds = countTimeSeconds % 60

        text = "%02d:%02d".format(minutes, seconds)

    }
}
```

<br>

**State.kt**

```kt
package com.example.summer_part2_chapter07

enum class State {
    BEFORE_RECORDING,
    ON_RECORDING,
    AFTER_RECORDING,
    ON_PLAYING
}
```

<br>

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
