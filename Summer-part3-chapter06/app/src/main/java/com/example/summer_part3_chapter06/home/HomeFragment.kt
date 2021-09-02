package com.example.summer_part3_chapter06.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summer_part3_chapter06.DBKey.Companion.CHILD_CHAT
import com.example.summer_part3_chapter06.DBKey.Companion.DB_ARTICLES
import com.example.summer_part3_chapter06.DBKey.Companion.DB_USERS
import com.example.summer_part3_chapter06.R
import com.example.summer_part3_chapter06.chatlist.ChatListItem
import com.example.summer_part3_chapter06.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 중고 거래 메인 화면을 보여준다.
 * 사용자들이 올린 리스트(사진, 내용, 날짜 등) 가 뜨게 된다.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {

    // article 에 대한 레퍼런스 값
    private lateinit var articleDB: DatabaseReference

    // user 에 대한 레퍼런스 값
    private lateinit var userDB: DatabaseReference

    // article 데이터를 리스트에 표현하기위해 처리하는 어댑터
    private lateinit var articleAdapter: ArticleAdapter

    // articleModel 형식으로 저장되는 리스트
    private val articleList = mutableListOf<ArticleModel>()

    /**
     * 데이터가 추가될 때 처리하는 Listener
     */
    private val listener = object : ChildEventListener {
        // Child(데이터) 가 추가된 경우
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            // ArticleModel 에 대한 포맷을 그대로 저장할 수 있음
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            // 리스트에 추가
            articleList.add(articleModel)
            // 어댑터 리스트에 추가
            articleAdapter.submitList(articleList)
        }

        // 미사용
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}

    }

    // fragment_home.xml 을 바인딩 한다.
    private var binding: FragmentHomeBinding? = null

    // FirebaseAuth 값 초기화
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    /**
     * Fragment 생명주기 중 View 가 생성되었을 때,
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fragment_home.xml 바인딩
        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        // 리스트를 비워준다.
        articleList.clear()

        // user 레퍼런스에 있는 값들을 불러온다.
        userDB = Firebase.database.reference.child(DB_USERS)
        // article 레퍼런스에 있는 값들을 불러온다.
        articleDB = Firebase.database.reference.child(DB_ARTICLES)

        // 어댑터의 아이템이 클릭되었을 때, 이벤트 처리
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            // 로그인을 한 상태
            if (auth.currentUser != null) {
                // 현재 사용자의 ID 와 seller 의 ID 가 맞치 않는 경우
                if (auth.currentUser!!.uid != articleModel.sellerId) {
                    // 채팅방을 생성한다.
                    val chatRoom = ChatListItem(
                        // 구매자 ID
                        buyerId = auth.currentUser!!.uid,
                        // 판매자 ID
                        sellerId = articleModel.sellerId,
                        // item Title
                        itemTitle = articleModel.title,
                        // 고유 key 값, 시간으로 함
                        key = System.currentTimeMillis()
                    )

                    // 현재 사용자의 chat 레퍼런스에 ChatRoom 을 넣는다.
                    userDB.child(auth.currentUser!!.uid)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    // 판매자의 chat 레퍼런스에 ChatRoom 을 넣는다.
                    userDB.child(articleModel.sellerId)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    // Snackbar 로 알림
                    Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해 주세요", Snackbar.LENGTH_LONG).show()
                } else {
                    // 내가 올린 아이템
                    Snackbar.make(view, "내가 올린 아이템입니다.", Snackbar.LENGTH_LONG).show()
                }
            } else {
                // 로그인을 안한 상태
                Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG).show()
            }
        })

        // 어댑터를 통해서 RecyclerView 를 그려준다.
        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        // 오른쪽 밑에 있는 + 버튼에 대한 클릭 이벤트 처리
        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            context?.let {
                // 로그인 한 상태이면
                if (auth.currentUser != null) {
                    val intent = Intent(requireContext(), AddArticleActivity::class.java)
                    // item 을 추가하는 AddArticleActivity 시작
                    startActivity(intent)
                } else {
                    // 스낵바 알림, 로그인 안했을 경우
                    Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG).show()
                }
            }
        }
        // article 정보를 추가 하는 경우
        articleDB.addChildEventListener(listener)
    }

    // 잠깐 정지했을 경우 어댑터 업데이트
    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()
    }

    // 완전히 종료되었을 때, 리스너 제거
    override fun onDestroy() {
        super.onDestroy()
        articleDB.removeEventListener(listener)
    }
}