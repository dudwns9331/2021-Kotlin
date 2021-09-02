package com.example.summer_part3_chapter06.chatdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.summer_part3_chapter06.DBKey.Companion.DB_CHATS
import com.example.summer_part3_chapter06.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 사용자 매칭이 되어 판매자와 구매자가 채팅하는 액티비티
 */
class ChatRoomActivity : AppCompatActivity() {

    // FirebaseAuth 인스턴스 초기화
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    // 채팅 데이터를 저장하기 위한 리스트
    private val chatList = mutableListOf<ChatItem>()

    // 채팅 주고받은 내역을 보여주는 리스트 데이터 처리 어댑터
    private val adapter = ChatItemAdapter()

    // 채팅 내역 저장 데이터베이스 레퍼런스 초기화
    private var chatDB: DatabaseReference? = null

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        // 채팅 키를 가져온다.
        val chatKey = intent.getLongExtra("chatKey", -1)

        // 채팅 내역 저장 데이터베이스 레퍼런스에서 chatKey 레퍼런스를 가져온다.
        chatDB = Firebase.database.reference.child(DB_CHATS).child("$chatKey")
        chatDB?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                // 채팅 데이터 클래스를 그대로 넣어준다.
                val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem ?: return

                // 채팅 리스트에 추가
                chatList.add(chatItem)
                // 채팅 리스트 데이터 관리 어댑터에 추가
                adapter.submitList(chatList)
                // 어댑터 업데이트
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        // 채팅 주고받은 내역을 보여주는 리스트 데이터 처리 어댑터 바인딩
        findViewById<RecyclerView>(R.id.chatRecyclerView).adapter = adapter
        findViewById<RecyclerView>(R.id.chatRecyclerView).layoutManager = LinearLayoutManager(this)

        // 전송 버튼 클릭 이벤트 처리
        findViewById<Button>(R.id.sendButton).setOnClickListener {
            // chatItem 에 전송자의 ID 와 message 를 messageEditText 에서 받아온다.
            val chatItem = auth.currentUser?.uid?.let { it1 ->
                ChatItem(
                    senderId = it1,
                    message = findViewById<EditText>(R.id.messageEditText).text.toString()
                )
            }
            // 데이터베이스에 데이터 추가
            chatDB?.push()?.setValue(chatItem)
        }

    }
}