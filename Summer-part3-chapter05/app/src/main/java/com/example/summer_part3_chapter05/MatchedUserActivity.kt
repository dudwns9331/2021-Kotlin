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