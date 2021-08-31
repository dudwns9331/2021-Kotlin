# Part3 - 5

## Summber Kotlin part3 2021-08-30

<br>

### **_레이아웃 xml_**

<br>

Part3 부터는 기본적인 XML 레이아웃에 대한 설명은 생략한다.

<br>

---

### **_Activity - Kotlin_**

**MainActivity.kt**

```kotlin
package com.example.summer_part3_chapter05

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    /**
     * FirebaseAuth 의 인스턴스를 선언하고 가져온다.
     */
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    /**
     * auth 의 최근 유저가 null 값이라면, LoginActivity 를 시작한다.
     * 그렇지 않다면, 유저가 이미 등록되어 있으므로 LikeActivity 를 시작한다.
     */
    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, LikeActivity::class.java))
            finish()
        }
    }
}
```

**_참고문서_**

- `Firebase Android` 연동
  https://firebase.google.com/docs/android/setup?hl=ko

- `FirebaseAuth`
  https://firebase.google.com/docs/auth/android/start?hl=ko

`finish()` 함수는 해당 액티비티를 종료한다. (백스택에서 사라짐)

<br/>

**LoginActivity.kt**

```kt
package com.example.summer_part3_chapter05

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    // FirebaseAuth 의인스턴스를 선언한다.
    private lateinit var auth: FirebaseAuth

    // callbackManager 를 선언한다.
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //FirebaseAuth 인스턴스를 초기화 시킨다.
        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()

        // 로그인 버튼 초기화
        initLoginButton()
        // 회원가입 버튼 초기화
        initSignUpButton()
        // 이메일과 패스워드 EditText 초기화
        initEmailAndPasswordEditText()
        // 페이스북 로그인 초기화
        initFacebookLoginButton()
    }


    /**
     * 로그인 버튼
     * 로그인 버튼을 눌렀을 때, 이메일과 패스워드 값이 FirebaseAuth 에 있는 값인지 확인한다.
     * 만약에 email, password 값이 제대로 넘어왔다면 handleSuccessLogin 을 통해서 처리하도록 함.
     */
    private fun initLoginButton() {
        // 로그인 버튼 바인딩
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener { // 로그인 버튼이 눌렸을 때,
            // 이메일 입력창에서 값을 가져옴
            val email = getInputEmail()
            // 패스워드 입력창에서 값을 가져옴
            val password = getInputPassword()

            // FirebaseAuth 의 signInWithEmailAndPassword() 함수를 통해 로그인을 한다.
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // email, password 값이 제대로 넘어온 경우
                    if (task.isSuccessful) {
                        // 로그인 처리 함수
                        handleSuccessLogin()
                    } else {
                        // 실패 시,
                        Toast.makeText(this, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요", Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }
    }

    /**
     * 회원가입 버튼
     * 이메일과 패스워드 값을 가져와서 Firebase auth 에 등록하도록 한다.
     *
     */
    private fun initSignUpButton() {
        // 회원가입 버튼 바인딩
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener { // 회원가입 버튼이 눌렸을 때,
            // 이메일 입력창에서 값을 가져옴
            val email = getInputEmail()
            // 패스워드 입력창에서 값을 가져옴
            val password = getInputPassword()

            // FirebaseAuth 의 createUserWithEmailAndPassword() 함수를 통해 Firebase Auth 에 등록한다.
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 성공한 경우
                        Toast.makeText(this, "회원가입에 성공했습니다. 로그인 버튼을 눌러 로그인해주세요.", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        // 실패한 경우
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_LONG)
                            .show()
                    }

                }
        }
    }

    /**
     * 이메일과 패스워드 EditText 에 대한 활성화 처리를 해준다.
     */
    private fun initEmailAndPasswordEditText() {
        // 이메일 EditText 바인딩
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        // 패스워드 EditText 바인딩
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        // 로그인 Button 바인딩
        val loginButton = findViewById<Button>(R.id.loginButton)
        // 회원가입 Button 바인딩
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        // 이메일 EditText 가 변할때마다 enable 값을 체킹한다.
        emailEditText.addTextChangedListener {
            // 만약 emailEditText 가 비어있지 않고, 패스워드가 비어있지 않으면.
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            // 로그인 버튼 활성화
            loginButton.isEnabled = enable
            // 회원가입 버튼 활성화
            signUpButton.isEnabled = enable

        }

        // 이메일 EditText 가 변할때마다 enable 값을 체킹한다.
        passwordEditText.addTextChangedListener {
            // 만약 emailEditText 가 비어있지 않고, 패스워드가 비어있지 않으면.
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            // 로그인 버튼 활성화
            loginButton.isEnabled = enable
            // 회원가입 버튼 활성화
            signUpButton.isEnabled = enable
        }
    }

    /**
     * 페이스북 로그인 버튼
     * 페이스북 로그인 버튼을 활성화 한다. LoginButton Type 으로 정의된 facebookLoginButton
     */
    private fun initFacebookLoginButton() {
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)

        // 이메일과 public_profile 을 받아오도록 한다.
        facebookLoginButton.setPermissions("email", "public_profile")

        // callbackManager 를 통해서 로그인 하는 과정을 통제한다.
        facebookLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                // 로그인이 성공적일때,
                override fun onSuccess(result: LoginResult) {
                    // credential 값을 FacebookAuthProvider 를 통해서 토큰 값으로 받아온다.
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    // FirebaseAuth 에서 해당 credential 값을 통해서 회원가입 한다.
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            // 로그인에 성공한 경우
                            if (task.isSuccessful) {
                                handleSuccessLogin()
                            } else {
                                // 로그인에 실패한 경우
                                Toast.makeText(
                                    this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

                override fun onCancel() {}

                // 페이스북 로그인에서 오류가 발생한 경우
                override fun onError(error: FacebookException?) {
                    Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    /**
     * emailEditText 에 들어있는 값을 가져온다.
     */
    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }

    /**
     * passwordEditText 에 들어있는 값을 가져온다.
     */
    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

    /**
     * 페이스북 로그인 버튼으로 로그인한 경우?
     * 결과를 담는다.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 페이스북 로그인 결과를 콜백매니저에 담는다.
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 로그인에 성공한 경우, 처리 함수
     */
    private fun handleSuccessLogin() {
        // 만약에 로그인한 유저가 기록이 없다면
        if (auth.currentUser == null) {
            // 로그인에 실패
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 유저 아이디 저장.
        val userId = auth.currentUser?.uid.orEmpty()
        // 최근 유저 아이디 데이터베이스에 "Users" 로 저장한다.
        val currentUserDB = Firebase.database.reference.child("Users").child(userId)
        // user Map 생성
        val user = mutableMapOf<String, Any>()
        // "userId 에 최근에 로그인한 유저를 넣어준다.
        user["userId"] = userId
        // currentUserDB 에 해당 값을 업데이트 해준다.
        currentUserDB.updateChildren(user)

        finish()
    }
}

```

**_참고 문서_**

- `CallbackManager` : FaceBook SDK
  https://developers.facebook.com/docs/reference/androidsdk/current/facebook/com/facebook/callbackmanager.html/

`CallbackManager` 는 액티비티 또는 프래그먼트의 `onActivityResult()` 메서드에서 FacebookSdk 로의 콜백을 관리한다.

<br/>

**LikeActivity.kt**

```kotlin
package com.example.summer_part3_chapter05

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.summer_part3_chapter05.DBKey.Companion.DIS_LIKE
import com.example.summer_part3_chapter05.DBKey.Companion.LIKE
import com.example.summer_part3_chapter05.DBKey.Companion.LIKED_BY
import com.example.summer_part3_chapter05.DBKey.Companion.NAME
import com.example.summer_part3_chapter05.DBKey.Companion.USERS
import com.example.summer_part3_chapter05.DBKey.Companion.USER_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

/**
 * CardStackListener 를 이용해 카드 스택 뷰에 대한 이벤트 리스너를 정의해준다.
 */
class LikeActivity : AppCompatActivity(), CardStackListener {


    // FirebaseAuth 선언 및 인스턴스 가져오기.
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    // userDB 로 데이터베이스 레퍼런스를 불러온다.
    private lateinit var userDB: DatabaseReference

    // CardItem 에 대해서 어댑터를 초기화 해준다.
    private val adapter = CardItemAdapter()

    // cardItems 배열에 CardItem Type 으로 배열을 초기화 해준다.
    private val cardItems = mutableListOf<CardItem>()

    // CardStackLayoutManager 를 통해서 레이아웃을 컨트롤 한다.
    private val manager by lazy {
        CardStackLayoutManager(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        // userDB 에 Firebase RealTime Database 를 연결해준다. (유저 정보 저장)
        userDB = Firebase.database.reference.child(USERS)

        // 최근 유저를 저장한다.
        val currentUserDB = userDB.child(getCurrentUserID())

        // addListenerForSingleValueEvent 는 한번 데이터를 읽는다.
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 만약 NAME 값의 value 가 비어있는 경우
                if (snapshot.child(NAME).value == null) {
                    // 이름을 입력하는 팝업을 띄워준다.
                    showNameInputPopup()
                    return
                }
                // 선택되지 않은 유저를 가져온다.
                getUnSelectedUsers()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // CardStackView 초기화
        initCardStackView()
        // 로그아웃 버튼 초기화
        initSignOutButton()
        // 매치 리스트 버튼 초기화
        intiMatchedListButton()
    }


    /**
     * CardStackView 를 초기화한다.
     */
    private fun initCardStackView() {
        // cardStackView 바인딩
        val stackView = findViewById<CardStackView>(R.id.cardStackView)

        // CardStackLayoutManager 를 넣어준다.
        stackView.layoutManager = manager
        // CardItemAdapter 를 넣어준다.
        stackView.adapter = adapter
    }

    /**
     * 로그아웃 버튼 초기화
     */
    private fun initSignOutButton() {
        // signOutButton 버튼 바인딩
        val signOutButton = findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener { // 로그아웃 버튼 눌렸을 때
            // FirebaseAuth 에서 로그아웃 한다.
            auth.signOut()
            // 로그아웃 시 MainActivity 를 시작함으로써 Login 화면을 띄워준다.
            startActivity(Intent(this, MainActivity::class.java))
            // 종료
            finish()
        }
    }

    /**
     * 매치 버튼 초기화
     * CardStackView 에서 스와이프를 통해서 Like 한 유저끼리
     * 매칭시켜주는 액티비티를 시작한다.
     */
    private fun intiMatchedListButton() {
        // matchListButton 바인딩
        val matchedListButton = findViewById<Button>(R.id.matchListButton)
        matchedListButton.setOnClickListener { // 버튼이 눌렸을 때,
            // MatchedUserActivity 를 시작한다.
            startActivity(Intent(this, MatchedUserActivity::class.java))
        }
    }

    /**
     * getUnSelectedUsers 는 선택되지 않은 유저 목록을 불러온다.
     */
    private fun getUnSelectedUsers() {
        userDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) { // 추가되는 경우
                // 만약 최근 유저의 값이 아니고, 좋아요 혹은 싫어요를 하지 않은경우
                if (snapshot.child(USER_ID).value != getCurrentUserID()
                    && snapshot.child(LIKED_BY).child(LIKE).hasChild(getCurrentUserID()).not()
                    && snapshot.child(LIKED_BY).child(DIS_LIKE).hasChild(getCurrentUserID()).not()
                ) {

                    // 유저 아이디를 가졍노다.
                    val userId = snapshot.child(USER_ID).value.toString()
                    // name 에 대한 초기화
                    var name = "undecided"

                    // 만약에 이름이 null 값이 아니라면
                    if (snapshot.child(NAME).value != null) {
                        // 이름 값을 데이터베이스에서 가져온다.
                        name = snapshot.child(NAME).value.toString()
                    }

                    // cardItems 에 CardItem 을 추가한다.
                    cardItems.add(CardItem(userId, name))
                    // 어댑터에 해당 리스트를 추가한다.
                    adapter.submitList(cardItems)
                    // 어댑터 업데이트
                    adapter.notifyDataSetChanged()
                }
            }

            /**
             * 데이터 값이 변경되는 경우
             */
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                // carItem 에서 해당 값을 찾는다. snapshot 의 key 값과 userId 가 같다면
                // 해당 값으로 이름을 업데이트 해준다.
                cardItems.find { it.userId == snapshot.key }?.let {
                    it.name = snapshot.child(NAME).value.toString()
                }

                // 어댑터에 추가한다.
                adapter.submitList(cardItems)
                // 어댑터 업데이트
                adapter.notifyDataSetChanged()
            }

            // 사용하지 않음
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })
    }

    /**
     * 이름을 정하는 팝업을 띄운다.
     */
    private fun showNameInputPopup() {

        // 이름을 입력하는 EditText
        val editText = EditText(this)

        // AlertDialog 를 띄워준다.
        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
            .setView(editText)
            .setPositiveButton("저장") { _, _ ->
                // 저장 버튼이 눌렸을 때,
                if (editText.text.isEmpty()) { // 만약 editText 가 비었을 때
                    // 다시 팝업을 띄운다.
                    showNameInputPopup()
                } else {
                    // 비어있지 않는 경우 해당 값을 저장한다.
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false) // 취소 버튼 비활성화
            .show()
    }

    /**
     * saveUserName 은 로그인 후 사용자의 이름을 저장한다.
     */
    private fun saveUserName(name: String) {
        // 데이터베이스에서 userID 값을 가져온다.
        val userId = getCurrentUserID()
        // 데이터베이스의 최근 유저 key 값을 불러온다.
        val currentUserDB = userDB.child(userId)
        // 유저 리스트를 초기화한다.
        val user = mutableMapOf<String, Any>()
        // 데이터베이스에서 가져온 USER_ID 값에 useId 저장
        user[USER_ID] = userId
        // 이름도 저장함
        user[NAME] = name
        currentUserDB.updateChildren(user)
        getUnSelectedUsers()
    }

    /**
     * auth 의 값의 uid 값을 리턴함
     */
    private fun getCurrentUserID(): String {

        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        return auth.currentUser?.uid.orEmpty()
    }

    /**
     * 좋아요 스와이프 처리
     */
    private fun like() {
        // cardItem 의 제일 위에서 하나 빠진 값을 담아둠
        val card = cardItems[manager.topPosition - 1]
        // cardItems 배열의 제일 위에 있는 값을 없앤다.
        cardItems.removeFirst()

        // 데이터베이스 업데이트
        userDB.child(card.userId)
            .child(LIKED_BY)
            .child(LIKE)
            .child(getCurrentUserID())
            .setValue(true)

        // 다른 유저가 나를 좋아요 했을 때,
        saveMatchIfOtherUserLikeMe(card.userId)

        Toast.makeText(this, "${card.name} 님을 Like 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    /**
     * 싫어요 스와이프 처리
     */
    private fun dislike() {
        // cardItem 의 제일 위에서 하나 빠진 값을 담아둠
        val card = cardItems[manager.topPosition - 1]
        // cardItems 배열의 제일 위에 있는 값을 없앤다.
        cardItems.removeFirst()

        // 데이터베이스 업데이트
        userDB.child(card.userId)
            .child(LIKED_BY)
            .child(DIS_LIKE)
            .child(getCurrentUserID())
            .setValue(true)

        Toast.makeText(this, "${card.name} 님을 disLike 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    /**
     * 다른 사람이 나를 좋아요 했을 경우 처리.
     */
    private fun saveMatchIfOtherUserLikeMe(otherUserId: String) {
        // 다른 사람 데이터베이스 정보, LIKED_BY -> LIKE 에 저장된 otherUserId 를 불러온다.
        val otherUserDB =
            userDB.child(getCurrentUserID()).child(LIKED_BY).child(LIKE).child(otherUserId)
        otherUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 만약에 값이 있다면, 내 데이터베이스에
                if (snapshot.value == true) {
                    userDB.child(getCurrentUserID())
                        .child(LIKED_BY)
                        .child("match")     // LIKED_BY -> match 에 데이터 생성
                        .child(otherUserId)         // 다른 사람 아이디를 넣는다.
                        .setValue(true)
                    // 만약에 값이 있다면, 나를 좋아요한 사람 데이터베이스에
                    userDB.child(otherUserId)
                        .child(LIKED_BY)
                        .child("match")     // LIKED_BY -> match 에 데이터 생성
                        .child(getCurrentUserID())  // 내 아이디를 넣는다.
                        .setValue(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    /**
     * 카드를 스와이프 했을 경우
     */
    override fun onCardSwiped(direction: Direction?) {
        when (direction) {
            // 오른쪽 방향이면 좋아요
            Direction.Right -> like()
            // 왼쪽 방향이면 싫어요
            Direction.Left -> dislike()
            else -> {
            }
        }
    }

    override fun onCardRewound() {}
    override fun onCardCanceled() {}
    override fun onCardAppeared(view: View?, position: Int) {}
    override fun onCardDisappeared(view: View?, position: Int) {}
}
```

**_참고 문서_**

- `FirebaseAuth`
  https://firebase.google.com/docs/auth/android/start?hl=ko

- Firebase Android 에서 데이터 목록 다루기
  https://firebase.google.com/docs/database/android/lists-of-data?hl=ko

- `CardStackView` Github open Source 사용하기
  https://github.com/yuyakaido/CardStackView

<br/>

**MatchedUserActivity.kt**

```kotlin
package com.example.summer_part3_chapter05

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.summer_part3_chapter05.DBKey.Companion.LIKED_BY
import com.example.summer_part3_chapter05.DBKey.Companion.NAME
import com.example.summer_part3_chapter05.DBKey.Companion.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MatchedUserActivity : AppCompatActivity() {

    // FirebaseAuth 의 인스턴스를 선언하고 가져온다.
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 데이터베이스 레퍼런스를 가져온다.
    private lateinit var userDB: DatabaseReference

    // MatchedUser RecyclerView 에 대한 어댑터
    private val adapter = MatchedUserAdapter()

    // cardItem 들을 담을 배열
    private val cardItems = mutableListOf<CardItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        userDB = Firebase.database.reference.child(USERS)

        // MatchedUserRecyclerView 에 대한 초기화
        initMatchedUserRecyclerView()

        // 매칭되는 유저들을 가져온다.
        getMatchUsers()
    }

    private fun initMatchedUserRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.matchedUserRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    /**
     * 서로 맞는 유저에 대해서 getUserByKey 에 해당 유저의 key 값을 넘겨준다.
     */
    private fun getMatchUsers() {
        // likedBy -> match 에 해당하는 정보를 가져온다.
        val matchedDB = userDB.child(getCurrentUserID()).child(LIKED_BY).child("match")

        matchedDB.addChildEventListener(object : ChildEventListener {

            // 해당 데이터베이스에 값이 추가될 대마다 getUserByKey 함수를 호출한다.
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key?.isNotEmpty() == true) {
                    getUserByKey(snapshot.key.orEmpty())
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

    }

    /**
     * Key 값으로 유저에 대한 정보를 가져와서 유저 ID, 유저 이름을 넣어 cardItems 리스트에 넣어준다.
     * 리스트를 표시해주는 어댑터에 해당 값을 추가한다.
     */
    private fun getUserByKey(userId: String) {
        userDB.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cardItems.add(CardItem(userId, snapshot.child(NAME).value.toString()))
                adapter.submitList(cardItems)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * 최근의 유저 ID 를 가져온다.
     */
    private fun getCurrentUserID(): String {
        // 예외처리, 로그인 되어 있지 않은 경우
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }
}
```

`Firebase RealTime database` 를 통해서 좋아요를 한 유저들끼리 매칭시킬 수 있도록 `database` 에 저장하도록 하는 코드가 메인이다.

<br/>

### **_데이터 파일 - Kotlin_**

**DBKey.kt**

```kotlin
package com.example.summer_part3_chapter05

/**
 * Firebase database 쿼리에 사용되는 Keyword
 */
class DBKey {
    companion object {
        // 유저 정보
        const val USERS = "Users"

        // 좋아요 정보
        const val LIKED_BY = "likedBy"

        // 종아요
        const val LIKE = "like"

        // 싫어요
        const val DIS_LIKE = "disLike"

        // 유저 아이디
        const val USER_ID = "userId"

        // 유저 이름
        const val NAME = "name"
    }
}
```

database Query 코드에 들어가는 상수값들이다. 해당 값은 database에 직접 들어가는 값들로 오타 및 오류를 제거하기 위해 `companion object`로 따로 만들어서 참조하도록 했다.

<br/>

**CardItem.kt**

```kotlin
package com.example.summer_part3_chapter05

/**
 * CardItem 에 들어가는 data
 */
data class CardItem(
    val userId: String,
    var name: String
)

```

`LikeActivity`에서 RecyclerView CardView 를 담는 리스트 사용되는 `CardItem` 데이터 객체에 대한 데이터 클래스

<br/>

### **_adapter - Kotlin_**

**CardItemAdapter**

```kotlin
package com.example.summer_part3_chapter05

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * CardView 가 들어가는 어댑터
 */
class CardItemAdapter : ListAdapter<CardItem, CardItemAdapter.ViewHolder>(diffUtil) {

    /**
     * ViewHolder RecyclerView : 뷰홀더 내부 클래스로 작성
     */
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        /**
         * bind 함수를 통해서 nameTextView 에 cardItem 의 name 을 넣는다.
         */
        fun bind(cardItem: CardItem) {
            view.findViewById<TextView>(R.id.nameTextView).text = cardItem.name
        }
    }

    /**
     * 뷰 홀더가 생성되었을 때,
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflater 에 Layout 을 불러온다.
        val inflater = LayoutInflater.from(parent.context)
        // 뷰홀더를 리턴함. item_card.xml 파일을 inflate 한다.
        return ViewHolder(inflater.inflate(R.layout.item_card, parent, false))

    }

    /**
     * ViewHolder 를 bind 한다.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    /**
     * 아이템에 대한 처리, DiffUtil 을 통해서
     * 이전의 아이템이 새로운 아이템과 일치하는지 검사한다.
     */
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CardItem>() {
            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
```

**_참고 목록_**

- RecyclerView 로 동적 목록 만들기
  https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=ko

<br/>

**MatchedUserAdapter.kt**

```kotlin
package com.example.summer_part3_chapter05

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * 매칭된 유저의 목록을 보여주는 리스트를 처리하는 어댑터
 */
class MatchedUserAdapter : ListAdapter<CardItem, MatchedUserAdapter.ViewHolder>(diffUtil) {

    /**
     * ViewHolder RecyclerView : 뷰홀더 내부 클래스로 작성
     */
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        /**
         * bind 함수를 통해서 userNameTextView 에 cardItem 의 name 을 넣는다.
         */
        fun bind(cardItem: CardItem) {
            view.findViewById<TextView>(R.id.userNameTextView).text = cardItem.name
        }
    }

    /**
     * 뷰 홀더가 생성되었을 때,
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflater 에 Layout 을 불러온다.
        val inflater = LayoutInflater.from(parent.context)
        // 뷰홀더를 리턴함. item_matched_user.xml 파일을 inflate 한다.
        return ViewHolder(inflater.inflate(R.layout.item_matched_user, parent, false))

    }

    /**
     * ViewHolder 를 bind 한다.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    /**
     * 아이템에 대한 처리, DiffUtil 을 통해서
     * 이전의 아이템이 새로운 아이템과 일치하는지 검사한다.
     */
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CardItem>() {
            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
```

**_참고 목록_**

- RecyclerView 로 동적 목록 만들기
  https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=ko

<br/>

### 결과화면

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap5-result1.PNG" width="360px" height="640px"/>

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap5-result2.PNG" width="360px" height="640px"/>

</p>

<br>

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
