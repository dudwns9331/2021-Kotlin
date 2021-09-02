# Part3 - 6 (chat)

## Summber Kotlin part3 2021-09-02

<br>

### **_레이아웃 xml_**

<br>

Part3 부터는 기본적인 XML 레이아웃에 대한 설명은 생략한다.

<br>

---

### **_chatdetail - Kotlin_**

**ChatRoomActivity.kt**

```kotlin
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
```

**ChatItemAdapter.kt**

```kotlin
package com.example.summer_part3_chapter06.chatdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.summer_part3_chapter06.databinding.ItemChatBinding

class ChatItemAdapter : ListAdapter<ChatItem, ChatItemAdapter.ViewHolder>(diffUtil) {

    /**
     * ViewHolder RecyclerView : 뷰홀더 내부 클래스로 작성
     */
    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * bind 함수를 통해서 senderTextView 에 senderId 를 가져오고
         * messageTextView 에 message 를 가져온다.
         */
        fun bind(chatItem: ChatItem) {
            binding.senderTextView.text = chatItem.senderId
            binding.messageTextView.text = chatItem.message
        }
    }

    /**
     * 뷰 홀더가 생성되었을 때,
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
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
        val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
```

<br/>

**chatItem.kt**

```kotlin
package com.example.summer_part3_chapter06.chatdetail

/**
 * 채팅에 들어가는 Item data
 */
data class ChatItem(
    val senderId: String,   // 보낸사람 ID
    val message: String,    // 메시지
) {
    // 파이어베이스에서 바로 데이터 클래스를 가져오려면 초기값에 대한 설정이 필요하다.
    constructor() : this("", "")
}
```

### **_chatlist - Kotlin_**

**ChatListFragment.kt**

```kotlin
package com.example.summer_part3_chapter06.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summer_part3_chapter06.DBKey.Companion.CHILD_CHAT
import com.example.summer_part3_chapter06.DBKey.Companion.DB_USERS
import com.example.summer_part3_chapter06.R
import com.example.summer_part3_chapter06.chatdetail.ChatRoomActivity
import com.example.summer_part3_chapter06.databinding.FragmentChatlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 채팅방 List 를 보여주는 Fragment
 * fragment_chatlist.xml 을 보여준다.
 */
class ChatListFragment : Fragment(R.layout.fragment_chatlist) {

    // fragment_chatlist.xml 파일을 바인딩한다.
    private var binding: FragmentChatlistBinding? = null

    // chatList 를 관리하는 어댑터
    private lateinit var chatListAdapter: ChatListAdapter

    // 채팅방 리스트 를 저장하는 List : ChatListItem
    private val chatRoomList = mutableListOf<ChatListItem>()

    // FirebaseAuth 인스턴스 초기화
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    /**
     * Fragment 생명주기 중 View 가 생성되었을 때,
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fragment_chatlist.xml 파일 바인딩
        val fragmentChatlistBinding = FragmentChatlistBinding.bind(view)
        binding = fragmentChatlistBinding

        // chatList RecyclerView 에 표시해줄 데이터를 관리하는 어댑터,
        // onItemClicked 를 통해서 해당 아이템을 눌렀을 때 intent 로 chatKey 를 넘겨주는 처리를 해준다.
        chatListAdapter = ChatListAdapter(onItemClicked = { chatRoom ->
            // 채팅방으로 이동 하는 코드
            context?.let {
                val intent = Intent(it, ChatRoomActivity::class.java)
                intent.putExtra("chatKey", chatRoom.key)
                startActivity(intent)
            }
        })

        // 리스트 초기화
        chatRoomList.clear()

        // 어댑터를 통해서 RecyclerView 를 그려준다.
        fragmentChatlistBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatlistBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        // 로그인 하지 않은 경우 return
        if (auth.currentUser == null) {
            return
        }

        // Users 레퍼런스에서 자식값 chat 을 가져온다.
        val chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser!!.uid)
            .child(CHILD_CHAT)

        // 데이터베이스에서 한번 값을 가져온다.
        chatDB.addListenerForSingleValueEvent(object : ValueEventListener {
            // 데이터가 바뀌었을 때
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    // 모델 값을 가져온다.
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    // 채팅방 리스트에 추가한다.
                    chatRoomList.add(model)
                }
                // 채팅방 리스트를 어댑터 리스트에 넣어준다.
                chatListAdapter.submitList(chatRoomList)
                // 변경된 사항을 업데이트 한다.
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // "멈춤" 상태일 때, 어댑터 값을 업데이트 한다.
    override fun onResume() {
        super.onResume()
        chatListAdapter.notifyDataSetChanged()
    }
}
```

<br/>

**ChatListAdapter**

```kotlin
package com.example.summer_part3_chapter06.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.summer_part3_chapter06.databinding.ItemChatListBinding

/**
 * onItemClicked 를 통해서 ChatListItem 이 클릭 당했을 때의 기능을 포함하게 인자로 받는다.
 * ListAdapter : RecyclerView 로 구성되어 있음.
 */
class ChatListAdapter(val onItemClicked: (ChatListItem) -> Unit) :
    ListAdapter<ChatListItem, ChatListAdapter.ViewHolder>(diffUtil) {

    /**
     * ViewHolder RecyclerView : 뷰홀더 내부 클래스로 작성
     */
    inner class ViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * bind 에서 chatListItem 이 클릭당했을 때의 처리를 한다.
         * 채팅방 이름에 대한 설정을 한다.
         */
        fun bind(chatListItem: ChatListItem) {
            binding.root.setOnClickListener {
                onItemClicked(chatListItem)
            }
            binding.chatRoomTitleTextView.text = chatListItem.itemTitle
        }
    }

    /**
     * 뷰 홀더가 생성되었을 때,
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
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
        val diffUtil = object : DiffUtil.ItemCallback<ChatListItem>() {
            override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
```

<br/>

**ChatListItem.kt**

```kotlin
package com.example.summer_part3_chapter06.chatlist

/**
 * 채팅방 목록을 보여주는 곳에 들어가는 ListItem data
 */
data class ChatListItem(
    val buyerId: String,    // 구매자 ID
    val sellerId: String,   // 판매자 ID
    val itemTitle: String,  // 아이템 제목
    val key: Long           // key 값
) {
    // 파이어베이스에서 바로 데이터 클래스를 가져오려면 초기값에 대한 설정이 필요하다.
    constructor() : this("", "", "", 0)
}
```

<br/>

---

## 참고 문서

#### Fragment, RecyclerView

- 프래그먼트 | Android 개발자 가이드

  https://developer.android.com/guide/fragments

- 프래그먼트 컴포넌트 제트팩 설명

  https://developer.android.com/guide/components/fragments?hl=ko

- RecyclerView 로 동적 목록 만들기

  https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=ko

<br/>

#### Firebase 관련 리스트

- FirebaseAuth for Android : Android 에서 Firebase 인증 시작하기

  https://firebase.google.com/docs/auth/android/start?hl=ko

- DataSnapShot 을 이용한 데이터베이스 처리

  https://firebase.google.com/docs/reference/android/com/
  google/firebase/database/DataSnapshot

- Android에서 데이터 읽기 및 쓰기

  https://firebase.google.com/docs/database/android/read-and-write?hl=ko

- Android에서 데이터 목록 다루기

  https://firebase.google.com/docs/database/android/lists-of-data?hl=ko

---

### 결과화면

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap6-result1.PNG" width="360px" height="640px"/>

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap6-result2.PNG" width="360px" height="640px"/>

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap6-result3.PNG" width="360px" height="640px"/>

</p>

<br>

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
