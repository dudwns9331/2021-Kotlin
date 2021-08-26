# Part3 - 3

## Summber Kotlin part3 2021-08-26

<br>

### **_레이아웃 xml_**

<br>

Part3 부터는 기본적인 XML 레이아웃에 대한 설명은 생략한다.

<br>

---

### **_Activity - Kotlin_**

**MainActivity.kt**

```kotlin
package com.example.summer_part3_chpater03

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // OnOffButton 뷰에 대한 초기화
        initOnOffButton()
        // ChangeAlarmTimeButton 뷰에 대한 초기화
        initChangeAlarmTimeButton()

        // model 에 fetchDataFromSharedPreferences() 함수를 통해서 모델값을 넣어준다.
        val model = fetchDataFromSharedPreferences()
        renderView(model)
    }

    /**
     * OnOffButton 에 대한 설명
     * 1. 데이터를 확인한다.
     * 2. 온오프에 따라 작업을 처리한다.
     * 3. 오프 -> 알람을 제거, 온 -> 알람을 등록
     * 4. 데이터를 저장한다.
     */
    private fun initOnOffButton() {
        // onOffButton 버튼 바인딩
        val onOffButton = findViewById<Button>(R.id.onOffButton)
        // 버튼이 눌렸을 떄,
        onOffButton.setOnClickListener {

            // model 에 버튼을 클릭했을 때의 뷰(버튼) 의 태그 값을 AlarmDisplayModel 로 캐스팅한다.
            // 엘비스 연산자를 통해서 만약에 그 값이 null 이라면 클릭리스너 자체를 리턴한다.(아무 동작 x)
            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener

            // 새로운 모델은 saveAlarmModel 에서 시간, 분, on 상태를넣어주어 만들어준다.
            val newModel = saveAlarmModel(model.hour, model.minute, model.onOff.not())

            // 새로운 모델을 보여줌
            renderView(newModel)

            if (newModel.onOff) {
                // ** 켜진 경우 -> 알람을 등록 **
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE, 1)
                    }
                }

                // alarmManager 에 알람 서비스 AlarmManager 로 캐스팅하여 저장
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                // 알람 서비스 를 받았을 때 인텐트를 넘겨줌
                val intent = Intent(this, AlarmReceiver::class.java)
                // pendingIntent 를 통해서 BroadCastReceiver 를 실행한다.
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    ALARM_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                // setInexactRepeating 은

                // 반복 알람 설정
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                // ** 꺼진 경우 -> 알람을 제거 **
                cancelAlarm()
            }
        }
    }

    /**
     * 알람 변경 버튼
     */
    private fun initChangeAlarmTimeButton() {
        // changeAlarmTimeButton 버튼 바인딩
        val changeAlarmTimeButton = findViewById<Button>(R.id.changeAlarmTimeButton)

        // changeAlarmTimeButton 을 눌렀을 때,
        changeAlarmTimeButton.setOnClickListener {

            // 현재 시간을 가져온다.
            val calendar = Calendar.getInstance()

            /**
             * 1. TimePickDialog 띄워줘서 시간을 설정을 하도록 하게끔 하고,
             * 2. 그 시간을 가져와서 데이터를 저장한다.
             * 3. 뷰를 업데이트 한다.
             * 4. 기존에 있던 알람을 삭제한다.
             */
            TimePickerDialog(this, { picker, hour, minute ->
                // 알람의 시간, 분 on/off 정보를 보여주는 텍스트 뷰를 업데이트 시켜준다.
                val model = saveAlarmModel(hour, minute, false)
                renderView(model)
                // 알람 서비스를 종료함
                cancelAlarm()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }
    }

    /**
     * 입력한 알람 모델을 저장한다.
     * 시간 ,분 , on/off 여부를 파라미터로 받음
     * AlarmDisplayModel 을 반환한다.
     */
    private fun saveAlarmModel(
        hour: Int,
        minute: Int,
        onOff: Boolean
    ): AlarmDisplayModel {

        // 모델은 시간, 분, on/off AlarmDisplayModel 로 여부를 저장한다.
        val model = AlarmDisplayModel(hour = hour, minute = minute, onOff = onOff)

        // sharedPreferences 에 키값 time 을 가진 값들을 불러온다.
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        // sharedPreference 를 공유 환경 설정 에디터를 통해서 수정한다.
        with(sharedPreferences.edit()) {
            putString(ALARM_KEY, model.makeDataForDB())
            putBoolean(ONOFF_KEY, model.onOff)
            commit()
        }
        return model
    }

    /**
     * sharedPreference 에 저장되어 있는 값을 불러온다.
     * AlarmDisplayModel 객체를 반환한다.
     */
    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {

        // sharedPreference 값 가져오기
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        // sharedPreference 에 저장된 시간 가져옴, null 값이거나 초기 실행이면 9:30 을 저장함
        val timeDBValue = sharedPreferences.getString(ALARM_KEY, "9:30") ?: "9:30"

        // sharedPreference 에 저장된 on/Off 값을 가져옴, 초기 값은 false
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)

        // timeDBValue 에서 ":" 을 스플릿 하여 시간 값을 4자리 배열로 받아온다.
        val alarmData = timeDBValue.split(":")

        // alarmData 에서 받아온 배열을 나눠서 각각 저장하고 alarmModel을 만든다.
        val alarmModel = AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onOff = onOffDBValue
        )

        // 보정 예외처리
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )

        if ((pendingIntent == null) and alarmModel.onOff) {
            // 알람은 꺼져있는데, 데이터는 켜져있는 경우
            alarmModel.onOff = false
        } else if ((pendingIntent != null) and alarmModel.onOff.not()) {
            // 알람은 켜져있는데, 데이터는 꺼져있는 경우
            // 알람을 취소함
            pendingIntent.cancel()
        }
        return alarmModel
    }

    /**
     * 알람을 설정했을 떄, 보여지는 텍스트뷰의 업데이트를 한다.
     */
    private fun renderView(model: AlarmDisplayModel) {
        // 오전 오후 AM/PM 텍스트뷰 바인딩
        findViewById<TextView>(R.id.ampmTextView).apply {
            text = model.ampmText
        }
        // 시간 텍스트뷰 바인딩
        findViewById<TextView>(R.id.timeTextView).apply {
            text = model.timeText
        }
        // on/off 텍스트뷰 바인딩
        findViewById<Button>(R.id.onOffButton).apply {
            text = model.onOffText
            tag = model
        }
    }

    /**
     * 알람 서비스 취소, 버튼 눌렀을 때 작동하도록 함
     */
    private fun cancelAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.cancel()
    }

    // 상수 값 정의
    companion object {
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val ALARM_REQUEST_CODE = 1000
    }
}
```

as? 연산자는 어떤 값을 지정한 타입으로 캐스트를 하고, 만약 대상 타입으로 캐스트 할 수 없으면 null을 반환한다.

`setInexactRepeating()` 을 사용하면 Android는 여러 앱의 반복 알람을 동기화하고 동시에 실행한다. Android 4.4(API 레벨 19)부터 모든 반복 알람의 시간이 부정확하다. `setInexactRepeating()`은 `setRepeating()` 을 개선한 것이지만 비슷한 시기에 앱의 모든 인스턴스가 서버에 도달하면 여전히 서버에 부담이 될 수 있다.

저장하려는 키-값 컬렉션이 비교적 작은 경우 `SharedPreferences` API를 사용해야 한다. `SharedPreferences` 객체는 키-값 쌍이 포함된 파일을 가리키며 키-값 쌍을 읽고 쓸 수 있는 간단한 메서드를 제공한다. 각 `SharedPreferences` 파일은 프레임워크에서 관리하며 비공개이거나 공유일 수 있다.

<br/>

### **_Data Class - Kotlin_**

**AlarmDisplayModel.kt**

```kotlin
package com.example.summer_part3_chpater03

/**
 * 알람에 대한 정보
 * TextView 에 보여지는 데이터 정보들
 * 시, 분, on/off 에 대한 정보를 저장한다.
 */
data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
) {
    val timeText: String
        get() {
            val h = "%02d".format(if (hour < 12) hour else hour - 12)
            val m = "%02d".format(minute)

            return "$h:$m"
        }
    val ampmText: String
        get() {
            return if (hour < 12) "AM" else "PM"
        }

    val onOffText: String
        get() {
            return if (onOff) "알람 끄기" else "알람 켜기"
        }

    fun makeDataForDB(): String? {
        return "$hour:$minute"
    }
}
```

<br/>

### **_BroadcastReceiver - Kotlin_**

**AlarmReceiver.kt**

```kotlin
package com.example.summer_part3_chpater03

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * 알람에 대한 처리
 * BroadcastReceiver 반환값을 가진다.
 */
class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }

    /**
     * 브로드캐스트를 받았을 때,
     * createNotificationChannel 을 통해서 Notification 채널을 만들어준다.
     * notifyNotification 을 통해서 알람을 보여준다.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        createNotificationChannel(context)
        notifyNotification(context)
    }

    /**
     * 알람 채널 설정
     */
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "기상 알람",
                NotificationManager.IMPORTANCE_HIGH
            )
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    /**
     * 알람을 보여준다.
     * NotificationManagerCompat 을 통해서 알람을 설정한다.
     */
    private fun notifyNotification(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("알람")
                .setContentText("일어날 시간입니다.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notify(NOTIFICATION_ID, build.build())
        }
    }
}
```

`with()` 은 어떤 객체의 이름을 반복하지 않고 객체에 대해 다양한 연산을 수행하려고 할때 사용한다. `with()` 함수는 첫 번째 인자로 받은 객체를 두 번째 인자로 받은 람다의 수신 객체로 전달하고, 람다의 실행 결과(마지막 식의 값) 를 리턴한다.

<br/>

### 결과화면

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap3-result.PNG" width="360px" height="640px"/>

</p>

<br>

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
