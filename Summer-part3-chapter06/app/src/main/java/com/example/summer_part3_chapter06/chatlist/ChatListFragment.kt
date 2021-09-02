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