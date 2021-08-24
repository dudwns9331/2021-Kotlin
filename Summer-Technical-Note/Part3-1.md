# Part3 - 1

## Summber Kotlin part3 2021-08-24

<br>

### **_레이아웃 xml_**

<br>

Part3 부터는 기본적인 XML 레이아웃에 대한 설명은 생략한다.

<br>

---

### **_Activity - Kotlin_**

**MainActivity.kt** - property

```kt
    private val resultTextView: TextView by lazy {
        findViewById(R.id.resultTextView)
    }

    private val firebaseToken: TextView by lazy {
        findViewById(R.id.firebaseTokenTextView)
    }
```

`resultTextView` 는 어떤 알림을 수신했는지에 대한 결과값을 바인딩한다. `firebaseToken` 은 Firebase 에서 받아오는 토큰에 대한 값을 바인딩한다.

<br>

**MainActivity.kt** - onCreate(savedInstanceState: Bundle?)

```kt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFirebase()
        updateResult()
    }
```

`onCreate` 함수에서 파이어베이스에 대한 초기화와 결과값에 대한 업데이트를 해준다.

<br>

**MainActivity.kt** - initFirebase()

```kt
    private fun initFirebase() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    firebaseToken.text = token
                }
            }
    }
```

`initFirebase` 는 `FirebaseMessaging` 에 대한 초기화를 진행한다. Firebase의 인스턴스를 가져오고 토큰을 가져와서 만약에 그 작업이 성공이라면 토큰에 대한 정보를 `firebaseToken` 에 담아준다.

<br>

**MainActivity.kt** - initFirebase()

```kt
    @SuppressLint("SetTextI18n")
    private fun updateResult(isNewIntent: Boolean = false) {
        resultTextView.text = (intent.getStringExtra("notificationType") ?: "앱 런처") +
                if (isNewIntent) {
                    "(으)로 갱신했습니다."
                } else {
                    "(으)로 실행했습니다."
                }
    }
```

**MyFirebaseMessagingService.kt** - onMessageReceived(remoteMessage: RemoteMessage)

```kt
 /* 메세지를 수신할 때마다 동작한다. 메세지에 대한 처리를 한다.*/
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        createNotificationChannel()

        val type = remoteMessage.data["type"]
            ?.let { NotificationType.valueOf(it) }
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        type ?: return

        NotificationManagerCompat.from(this)
            .notify(type.id, createNotification(type, title, message))

    }
```

`onMessageReceived()` 는 메세지를 수실할 때마다 동작한다. 메세지에 대한 처리를 한다.

`createNotificationChannel` 을 통해서 새로운 `NotificationChannel` 을 생성해준다.

remoteMessage 를 통해서 페이지에서 받아오는 메시지에 대해 짜여진 json 파일 형식대로 type, title, message 값을 저장한다. 만약에 type 이 null 값이라면 그대로 함수를 종료한다.

`NotificationManagerCompat` 을 통해서 createNotification() 함수에 정의된 알림메시지를 생성하도록 한다.

<br>

**MyFirebaseMessagingService.kt** - createNotificationChannel()

```kt
private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            channel.description = CHANNEL_DESCRIPTION
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }
```

안드로이드 8.0 이상의 버전부터 Channel 에 대한 생성이 없으면 알림을 보낼 수 없으므로 SDK 버전에 대한 확인을 조건문을 통해서 수행한다.

NotificationChannel() 을 통해서 채널을 생성하도록 하고 부가적으로 채널에 대한 설명을 넣는다.

`(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)` 코드를 통해서 정의한 채널에 대해서 NotificationManager 가 채널을 생성하도록 한다.

<br>

```kt
private fun createNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ): Notification {

        val intent = Intent(this, MainActivity::class.java)
            .apply {
                putExtra("notificationType", "${type.title} 타입")
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)


        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when (type) {
            NotificationType.NORMAL -> Unit
            NotificationType.EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "😀😁😂🤣😃😄😅😆" +
                                    "😉😊😋😎😍😘🥰😗" +
                                    "😙😚☺🙂🤗🤩🤔🤨" +
                                    "😐😑😶🙄😏😣😥😮"
                        )
                )
            }
            NotificationType.CUSTOM -> {
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.view_custom_notification
                        ).apply {
                            setTextViewText(R.id.title, title)
                            setTextViewText(R.id.message, message)
                        }
                    )
            }
        }

        return notificationBuilder.build()
    }
```

`Notification` 을 생성하는데 필요한 정보를 파라미터로 받아온다. 리턴 타입은 `Notification` 이다.

intent 에 `MainActivity` 에서 필요한 정보 (어떤 메시지 타입인지?) 를 담는다.

`addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)` 로 설정하는 경우 액티비티가 스택의 맨 위에서 이미 실행중인 경우 액티비티가 실행되지 않는다. 즉 중복으로 새로운 액티비티를 생성해서 사용하는 것이 아닌 원래 있던 액티비티를 실행하게 된다.

`val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)`

`FLAG_UPDATE_CURRENT` 는 설명된 PendingIntent가 이미 존재하는 경우 이를 유지하되 추가 데이터를 이 새 Intent에 있는 것으로 대체함을 나타내는 플래그이다.

---

<br>

### PendingIntent ?

Intent 및 대상 작업에 대한 설명이다. 이클래스의 인스턴스는 getActivity, getActivities, getBroadcast, getService 를 가질 수 있고 반환된 객체는 나중에 다른 응용프로그램에서 사용자를 대신하여 설명한 작업을 수행할 수 있도록 다른 응용프로그램에 전달할 수 있다.

다시 정리하자면, `PendingIntent` 는 Intent를 가지고 있는 클래스로, 기본 목적은 다른 애플리케이션(다른 프로세스)의 권한을 허가하여 가지고 있는 Intent를 마치 본인 앱의 프로세스에서 실행하는 것처럼 사용하게 하는 것이다.

`PendingIntent` 의 용도는 `Notification` 으로 작업을 수행할 때 인텐트가 실행되도록 한다. `Notification` 은 안드로이드 시스템의 `NotificationManager` 가 Intent를 실행한다. 즉 다른 프로세스에서 수행하기 때문에 `Notification` 으로 Inetent 수행시 `PendingIntent` 의 사용은 필수이다.

<br>

---

알림을 만드는 방법은 다음 공식 문서를 참고하였다. ==> [알림 만들기 | Android 개발자](https://developer.android.com/training/notify-user/build-notification?hl=ko)

when 조건문을 사용해서 NORMAL, EXPANDABLE, CUSTOM 인 경우 NotificationCompatBuilder(this, CHANNEL_ID) 객체가 담긴 notificationBuilder 를 수정해서 각기 다른 알림 형태를 보여주도록 한다.

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap1-result" width="360px" height="640px"/>

</p>

<br>

---

## 작업 및 백 스택 이해

### 백스택

작업 = 태스크(task), 활동 = 액티비티(Activity)

작업은 사용자가 특정 작업을 할 때 상호작용하는 액티비티의 컬렉션이다. 액티비티는 스택(백스택)에 각 액티비티가 열린 순서대로 정렬된다. 예를 들어 이메일 앱에는 새 메시지 목록을 표시하는 액티비티가 하나 있을 수 있다. 사용자가 메시지를 선택하면 메시지를 볼 수 있도록 새 액티비티가 열립니다. 새로운 액티비티는 백 스택에 추가된다. 사용자가 뒤로 버튼을 누르면 새로운 액티비티가 완료되고 스택에서 팝 된다.

기기의 홈 화면은 대부분의 작업이 시작되는 위치이다. 사용자가 앱 런처의 아이콘(또는 홈 화면의 바로가기)을 터치하면 앱의 작업이 포그라운드로 나온다. 앱의 작업이 없으면(앱이 최근에 사용된 적이 없으면) 새 작업이 생성되고 이 앱의 '기본' 액티비티가 스택의 루트 액티비티으로 열린다.

현재 액티비티가 또 다른 액티비티을 시작하면 새 액티비티가 스택의 맨 위에 푸시되고 포커스를 갖게 됩니다. 이전 액티비티는 스택에 남아있지만 중지됩니다. 액티비티가 중지되면 시스템은 액티비티의 사용자 인터페이스가 갖고 있는 현재 상태를 보존합니다. 사용자가 뒤로 버튼을 누르면 현재 액티비티가 스택의 맨 위에서 팝되고(액티비티가 제거되고) 이전 액티비티가 다시 시작됩니다(UI의 이전 상태가 복원됨).

백 스택은 'LIFO(후입선출)' 객체 구조로 작동한다. 아래 그림 참조

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/diagram_backstack.PNG" width="600px" height="190px"/>

</p>

<br>

정리하자면 액티비티는 새로 생성될 때마다 위로 겹쳐서 쌓이는 형식이고 이는 뒤로가기(종료) 를 통해서 하나씩 내보낼 수 있다. 그리고 밑에 쌓이는(현재 보여지지 않는) 액티비티들은 정지 상태를 가지게 된다. 또한, 같은 액티비티가 다른 프로세스에서 호출되어 중복될 수 있다. 이는 작업 관리를 통해서 조절 가능하다. 태스크는 백그라운드로 내려갈 수 있는 액티비티들의 묶음을 뜻한다.

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/diagram_multiple_instances.PNG" width="400px" height="380px"/>

</p>

<br>

---

### 작업 관리

**1. manifest 파일 사용**

manifest 파일에서 활동을 선언할 때 `<activity>` 요소의 `launchMode` 속성을 사용하여 활동이 작업과 연결되어야 하는 방식을 지정할 수 있다.

- standard (기본모드)

  기본값이다. 시스템은 활동이 시작된 작업에 활동의 새 인스턴스를 생성하고 인텐트를 인스턴스로 라우팅한다. 활동은 여러 번 인스턴스화될 수 있고 각 인스턴스는 서로 다른 작업에 속할 수 있으며 한 작업에는 여러 인스턴스가 있을 수 있다.

- singleTop

  활동의 인스턴스가 이미 현재 작업의 맨 위에 있으면 시스템은 활동의 새 인스턴스를 생성하지 않고 `onNewIntent()` 메서드를 호출하여 인텐트를 기존 인스턴스로 라우팅한다. 활동은 여러 번 인스턴스화될 수 있고 각 인스턴스는 서로 다른 작업에 속할 수 있으며 한 작업에는 여러 인스턴스가 있을 수 있다(단, 백 스택의 맨 위에 있는 활동이 활동의 기존 인스턴스가 아닐 때에만).

- singleTask

  시스템이 새 작업을 생성하고 새 작업의 루트에 있는 활동을 인스턴스화한다. 그러나 활동의 인스턴스가 이미 별도의 작업에 있다면 시스템은 새 인스턴스를 생성하지 않고 `onNewIntent()` 메서드를 호출하여 인텐트를 기존 인스턴스로 라우팅한다. 활동의 인스턴스가 한 번에 하나만 존재할 수 있다.

- singleInstance : 이해하기 어렵다. 자세한 설명은 안드로이드 공식 개발자 페이지를 참고하자.

<br>

**2. 인텐트 플래그 사용**

액티비티를 시작할 때 `startActivity()`에 전달하는 인텐트에 플래그를 포함함으로써 액티비티와 작업의 기본 연결을 수정할 수 있습니다. 기본 동작을 수정하는 데 사용할 수 있는 플래그는 다음과 같다.

- FLAG_ACTIVITY_NEW_TASK

  활동을 새 작업에서 시작한다. 지금 시작하고 있는 활동에 대해 이미 실행 중인 작업이 있으면 그 작업이 마지막 상태가 복원되어 포그라운드로 이동하고 활동은 `onNewIntent()`의 새 인텐트를 수신한다.

  이 플래그를 사용하면 이전 섹션에서 설명한 `"singleTask"` launchMode 값과 동일한 동작이 발생한다.

- FLAG_ACTIVITY_SINGLE_TOP

  시작 중인 활동이 현재 활동(백 스택의 맨 위에 있는)이면 활동의 새 인스턴스가 생성되는 대신 기존 인스턴스가 `onNewIntent()` 호출을 수신한다.

  이 플래그를 사용하면 이전 섹션에서 설명한 `"singleTop"` launchMode 값과 동일한 동작이 발생한다.

- FLAG_ACTIVITY_CLEAR_TOP

  시작 중인 활동이 현재 작업에서 이미 실행 중이면 활동의 새 인스턴스가 실행되는 대신 작업의 맨 위에 있는 다른 모든 활동이 제거되고 이 인텐트가 `onNewIntent()`를 통해 활동(이제 맨 위에 있음)의 다시 시작된 인스턴스로 전달된다.

  이 동작을 발생시키는 launchMode 속성 값은 없다.`FLAG_ACTIVITY_CLEAR_TOP`은 `FLAG_ACTIVITY_NEW_TASK`와 함께 가장 자주 사용된다.

  이러한 플래그를 함께 사용하면 또 다른 작업에 있는 기존 활동을 찾아 인텐트에 응답할 수 있는 위치에 활동을 넣을 수 있다.

<br>

**3. 백 스택 삭제**

사용자가 오랜 시간 동안 작업을 떠나있으면 시스템은 루트 액티비티를 제외한 모든 액티비티를 작업에서 삭제한다. 사용자가 작업으로 다시 돌아오면 루트 액티비티만 복원된다. 시스템이 이런 방식으로 동작하는 이유는 오랜 시간이 지나면 사용자가 이전에 하고 있던 일을 그만두고 작업으로 돌아왔을 때 새로운 일을 시작할 가능성이 크기 때문이다.

- alwaysRetainTaskState

  작업의 루트 액티비티에서 이 속성을 "true"로 설정하면 방금 설명한 기본 동작이 일어나지 않는다. 작업은 오랜 시간이 지난 후에도 스택의 모든 액티비티를 유지한다.

- clearTaskOnLaunch

  작업의 루트 활동에서 이 속성을 "true"로 설정하면 사용자가 작업을 떠났다가 작업으로 다시 돌아올 때마다 스택이 루트 액티비티까지 삭제된다. 다시 말해서 `alwaysRetainTaskState`와 정반대이다. 사용자는 항상 작업의 초기 상태로 돌아오게 되며 이는 아주 잠깐만 작업을 떠났다가 돌아와도 마찬가지이다.

- finishOnTaskLaunch

  이 속성은 `clearTaskOnLaunch`와 비슷하지만 작업 전체가 아니라 단일 액티비티에서 작동합니다. 또한 이 속성을 사용하면 루트 액티비티를 포함하여 어떤 액티비티라도 지울 수 있다. 이 속성을 "true"로 설정하면 액티비티는 현재 세션에 대해서만 작업의 일부로 유지된다. 사용자가 작업을 떠났다가 다시 돌아오면 이 작업은 더 이상 존재하지 않는다.

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
