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