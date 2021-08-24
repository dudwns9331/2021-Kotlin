# Part2 - 5

## Summber Kotlin part2 2021-08-06 뽀모도로 타이머

<br>

## **_레이아웃 xml 요소_**

- [SeekBar](#SeekBar)

---

## **Widget**

### SeekBar

**activity_main.xml** - SeekBar

```xml
    <SeekBar
    android:id="@+id/seekBar"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_marginHorizontal="20dp"
    android:max="60"
    android:progressDrawable="@color/transparent"
    android:thumb="@drawable/ic_baseline_directions_run_24"
    android:tickMark="@drawable/drawable_tick_mark"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toBottomOf="@id/remainMinutesTextView"
    tools:ignore="UnusedAttribute" />
```

`SeekBar` 는 드래그 가능한 `Thumb` 를 추가하는 `ProgressBar` 의 확장이다. 사용자는 `Thumb` 을 터치하여 왼쪽이나 오른쪽으로 드래그하여 현재 진행 수준을 설정하거나 화살표 키를 사용할 수 있다. 포커스 가능한 위젯을 `SeekBar` 왼쪽이나 오른쪽에 배치하는 것은 권장하지 않는다.

`SeekBar` 는 `Seekbar.OnSeekBarChangeListener` 을 통해서 시크바가 바뀌었을 때에 대한 이벤트를 설정할 수 있다.

`max` 속성을 통해서 `SeekBar` 의 최대 값을 지정할 수 있다. 현재 타이머에서는 60분이 설정 가능한 최대의 시간이기 때문에 60이 최대 값으로 지정되었다.

`progressDrawable` 을 통해서 `progress` 가 진행되는 표시줄을 지정할 수 있다.

`thumb`를 통해서 `progress` 가 진행되는 표시(컨트롤 가능)를 지정한다.

`tickMark` 를 통해서 `progress` 에 표시되는 표시줄을 지정할 수 있다.

---

### **_Activity - Kotlin_**

**MainActivity.kt** - property

```kt
    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null
```

- remainMinutesTextView : 남은 분을 표시해주는 TextView 이다.

- remainSecondsTextView : 남은 초를 표시해주는 TextView 이다.

- seekBar : 시간을 설정할 수 있는 seekBar 를 표시해준다.

- soundPool : 알림에 대한 사운드를 가져오기 위해 사용한다.

- currentCountDownTimer : 최근 시간에 대한 정보를 가져오기 위해 사용된다.

- tickingSoundId : 시간이 가는 사운드의 ID 를 담기 위해서 사용한다.

- bellSoundId : 시간이 다 되었을 때 벨소리 ID 를 담기 위해서 사용한다.

<br>

**MainActivity.kt** - onCreate(savedInstanceState: Bundle?)

```kt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }
```

bindViews() 는 표시되는 뷰에 대한 초기화를 한다. initSounds()는 효과음에 대한 처리를 위해 초기화하는 부분이다.

<br>

**MainActivity.kt** - bindViews()

```kt
    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        updateRemainTime(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return

                    if (seekBar.progress == 0) {
                        stopCountDown()
                    } else {
                        startCountDown()
                    }

                }
            }
        )
    }
```

`seekBar` 는 `setOnSeekBarChangeListener` 를 통해서 `seekBar` 에 대해서 thumb 를 조작했을 때 이벤트에 대한 처리를 한다.

`onProgressChanged` 는 progress가 thumb의 위치에 따라서 변경되었을 때의 이벤트를 처리한다. 만약에 `fromUser` : 유저의 이동 이라면 `updateRemainTime(reaminMillis)` 를 통해서 남은 시간을 조정하도록 한다.

`onStartTrackingTouch` 사용자가 터치 제스처를 시작했음을 알려준다. `Thumb` 의 위치를 옮기는 순간에 이전에 기록되었던 시간에 대해서 초기화를 해준다.

`onStopTrackingTouch` 사용자가 터치 제스터를 완료했음을 알려준다. `Thumb` 의 조작을 마쳤을 때 `startCountDown()` 함수를 통해서 카운트의 시작을 알린다.

<br>

**MainActivity.kt** - initSounds()

```kt
    private fun initSounds() {
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }
```

tickingSound 를 soundPool에서 load 하도록 한다.
bellSound 를 soundPool 에서 load 하도록 한다.

### **_soundPool ?_**

SoundPool 클래스는 응용 프로그램의 오디오 리소스를 관리하고 재생한다.

SoundPool은 APK 내부의 리소스 또는 파일 시스템의 파일에서 메모리를 로드할 수 있는 사운드 샘플 모음이다. SoundPool 라이브러리는 MediaCodec 서비스를 사용하여 오디오를 원시 16비트 PCM으로 디코딩한다. 이를 통해 응용 프로그램은 재생 중 압축 해제의 지연 시간과 CPU 부하를 겪지 않고도 압축된 스트림과 함께 제공될 수 있다.

`load(Context context, int resId, int priority)` 지정된 APK 리소스에서 사운드를 로드한다.

<br>

**MainActivity.kt** - createCountDownTimer(initialMillis: Long)

```kt
    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }
```

`CountDownTimer` 를 생성한다. 초기화해주고 텀은 1초 간격으로 해준다. `onTick()` 함수를 통해서 1초가 지나가면 남은 시간을 업데이트 하고 `SeekBar` 를 업데이트 해준다. `CountDown` 이 끝났을 때 `completeCountDown()` 을 통해서 초기화 시켜준다.

<br>

**MainActivity.kt** - startCountDown(), stopCountDown(), completeCountDown()

```kt
    private fun startCountDown() {
        currentCountDownTimer =
            createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        tickingSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun completeCountDown() {
        updateRemainTime(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }
```

1. startCountDown() 함수는 CountDown 을 시작한다. 최대값은 60분으로 설정하고 1초간격으로 실행하기 때문에 60과 1000을 곱해서 설정해준다. tickSound를 불러오고 실행하도록 한다.

2. stopCountDown() 함수는 currentCountDownTimer 를 취소하고 초기화 시켜주며 모든 soundPool에 담긴 소리에 대해서 멈추게 한다.

3. completeCountDown() 은 남은 시간과 SeekBar의 위치를 0으로 초기화 한다. 그리고 bellSound가 재생되도록 설정한다.

<br>

**MainActivity.kt** - updateRemainTime(remainMillis: Long), updateSeekBar(remainMillis: Long)

```kt
    /* 남은 시간을 텍스트 뷰에 갱신하는 함수 */
    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000
        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    /* SeekBar 를 초에 따라서 진행도를 업데이트 시키는 함수 */
    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
```

남은 시간에 대해서 계속적으로 TextView 를 업데이트 하도록 한다. SeekBar도 마찬가지로 progress 남은 시간에 대해서 업데이트 하도록 한다.

<br>

**MainActivity.kt** - onResume(), onPause(), onDestroy()

```kt
    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    /* 앱이 화면에서 보이지 않을 때 */
    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
```

액티비티 생명주기에 따라서 앱 화면을 보이지 않고 있을때, 멈췄을 경우, 종료되는 경우에 대해서 처리 해주도록 한다.

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)

2021-08-09 이름 수정
