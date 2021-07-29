package com.example.summer_part2_chapter07

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAudioPermission()
        initView()
        bindViews()
        initVariables()
    }

    /* 권한을 요청하는 함수 */
    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    /* 상태 초기화 */
    private fun initView() {
        recordButton.updateIconWithState(state)
    }

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

    private fun initVariables() {
        state = State.BEFORE_RECORDING
    }

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


    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }

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
}