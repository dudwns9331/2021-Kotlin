# Part3 - 6 (home)

## Summber Kotlin part3 2021-08-31

<br>

### **_레이아웃 xml_**

<br>

Part3 부터는 기본적인 XML 레이아웃에 대한 설명은 생략한다.

<br>

---

### **_Home, MyPage, MainActicity, DBKey - Kotlin_**

**MainActivity.kt**

```kotlin
package com.example.summer_part3_chapter06

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.summer_part3_chapter06.chatlist.ChatListFragment
import com.example.summer_part3_chapter06.home.HomeFragment
import com.example.summer_part3_chapter06.mypage.MyPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * MainActivity : 홈, 채팅, 내 페이지로 이동하는 프래그먼트 컨트롤 지정
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // HomeFragment 초기화
        val homeFragment = HomeFragment()
        // ChatListFragment 초기화
        val chatListFragment = ChatListFragment()
        // MyPageFragment
        val myPageFragment = MyPageFragment()

        // BottomNavigationView 지정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // HomeFragment 를 표시하도록 초기값 지정
        replaceFragment(homeFragment)

        // BottomNavigationView 에 각각 버튼에 대한 페이지 지정
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList -> replaceFragment(chatListFragment)
                R.id.myPage -> replaceFragment(myPageFragment)
            }
            true
        }
    }

    /**
     * Fragment 변경
     */
    private fun replaceFragment(fragment: Fragment) {
        // supportFragmentManager.beginTransaction() 함수를 통해서 fragment 변경
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }
}
```

**_참고 문서_**

- Fragment Part of Android Jetpack

  https://developer.android.com/guide/components/fragments?hl=ko

- Fragment Guide

  https://developer.android.com/guide/fragments

- Fragment 생성

  https://developer.android.com/guide/fragments/create

`Fragment` 는 앱 UI의 재사용 가능한 부분을 나타낸다. 프래그먼트는 자체 레이아웃을 정의 및 관리하고 자체 수명 주기를 보유하며 자체 입력 이벤트를 처리할 수 있다. 프래그먼트는 독립적으로 존재할 수 없고 활동이나 다른 프래그먼트에서 호스팅되어야 한다. 프래그먼트의 뷰 계층 구조는 호스트 뷰 계층 구조의 일부가 되거나 여기에 연결된다.

<br/>

**DBKey.kt**

```kotlin
package com.example.summer_part3_chapter06

/**
 * 데이터베이스 쿼리에 대한 상수 값 지정
 */
class DBKey {
    companion object {
        // Articles : 판매 정보에 대한 것
        const val DB_ARTICLES = "Articles"

        // Users : DB 사용자
        const val DB_USERS = "Users"

        // chat : 채팅 하나에 대한 내역
        const val CHILD_CHAT = "chat"

        // Chats : 채팅방에 대한 내역
        const val DB_CHATS = "Chats"
    }
}
```

파이어베이스에 레퍼런스 값을 지정하고 데이터를 넣을 때 실수나 오류가 나지 않도록 상수값으로 지정한다.

<br/>

**MyPageFragment.kt**

```kotlin
package com.example.summer_part3_chapter06.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.summer_part3_chapter06.R
import com.example.summer_part3_chapter06.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * 내 계정에 대한 정보를 보여주는 Fragment 페이지
 * fragment_mypage.xml 을 보여준다.
 */
class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    // fragment_mypage.xml 에 대한 바인딩 초기화
    private var binding: FragmentMypageBinding? = null

    // Firebase auth 값 초기화
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }


    /**
     * Fragment 생명 주기중 View 가 생성되었을 때,
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fragment_mypage.xml 에 대한 바인딩 지정
        val fragmentMypageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding

        // signInOutButton 이 눌렸을 때,
        fragmentMypageBinding.signInOutButton.setOnClickListener {
            binding?.let { binding ->

                // 이메일에 대한 정보를 가져옴
                val email = binding.emailEditText.text.toString()
                // 패스워드에 대한 정보를 가져옴
                val password = binding.passwordEditText.text.toString()

                // 최근 유저가 비어있을 때, 처음에 접속한 경우
                if (auth.currentUser == null) {
                    // 로그인
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task -> // 액티비티를 가져온다.
                            if (task.isSuccessful) {
                                // 로그인 처리
                                successSignIn()
                            } else {
                                // 로그인 실패 시,
                                Toast.makeText(
                                    context,
                                    "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {    // 비어있지 않은 경우
                    // 로그아웃
                    auth.signOut()
                    // email, password 를 비우고 활성화
                    binding.emailEditText.text.clear()
                    binding.emailEditText.isEnabled = true
                    binding.passwordEditText.text.clear()
                    binding.passwordEditText.isEnabled = true

                    // 로그인 버튼 비활성화, 회원가입 버튼 비활성화 및 초기화
                    binding.signInOutButton.text = "로그인"
                    binding.signInOutButton.isEnabled = false
                    binding.signUpButton.isEnabled = false
                }
            }
        }

        /**
         * 회원가입 버튼이 눌렸을 때,
         */
        fragmentMypageBinding.signUpButton.setOnClickListener {
            binding?.let { binding ->
                // 이메일에 대한 정보를 가져옴
                val email = binding.emailEditText.text.toString()
                // 패스워드에 대한 정보를 가져옴
                val password = binding.passwordEditText.text.toString()

                // 새로운 유저 생성, email, password 값을 통해서 생성
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공 시
                            Toast.makeText(
                                context,
                                "회원가입에 성공했습니다. 로그인 버튼을 눌러주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // 회원가입 실패 시
                            Toast.makeText(
                                context,
                                "회원가입에 실패했습니다. 이미 가입한 이메일일 수 있습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
            }
        }

        /**
         * emailEditText 에 값이 들어갔을 때,
         * 이메일값과 패스워드값이 비어 있지 않으면 활성화.
         */
        fragmentMypageBinding.emailEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable =
                    binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signInOutButton.isEnabled = enable
                binding.signUpButton.isEnabled = enable
            }
        }

        /**
         * passwordEditText 에 값이 들어갔을 때,
         * 이메일값과 패스워드값이 비어 있지 않으면 활성화.
         */
        fragmentMypageBinding.passwordEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable =
                    binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signInOutButton.isEnabled = enable
                binding.signUpButton.isEnabled = enable
            }
        }
    }

    // Fragment 를 시작할 때, 각 요소를 초기화 시켜준다.
    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            binding?.let { binding ->
                binding.emailEditText.text.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.text.clear()
                binding.emailEditText.isEnabled = true

                binding.signInOutButton.text = "로그인"
                binding.signInOutButton.isEnabled = false
                binding.signUpButton.isEnabled = false
            }
        } else {
            // 이미 로그인 되어 있는 경우 나머지를 비활성화 시켜주고 비밀번호를 가려준다.
            binding?.let { binding ->
                binding.emailEditText.setText(auth.currentUser!!.email)
                binding.passwordEditText.setText("************")

                binding.emailEditText.isEnabled = false
                binding.passwordEditText.isEnabled = false

                binding.signInOutButton.text = "로그아웃"
                binding.signInOutButton.isEnabled = true
                binding.signUpButton.isEnabled = false
            }
        }
    }

    /**
     * 로그인에 성공했을 때
     */
    private fun successSignIn() {
        // 만약 최근 로그인한 유저가 없다면
        if (auth.currentUser == null) {
            // 실패처리
            Toast.makeText(context, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        // 정상적으로 된 경우, 이메일, 패스워드 비활성화, 로그아웃 버튼 활성화
        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
        binding?.signUpButton?.isEnabled = false
        binding?.signInOutButton?.text = "로그아웃"
    }
}
```

**_참고 문서_**

- Fragment Lifecycle

  https://developer.android.com/guide/fragments/lifecycle

- `addTextChangeListener`

  https://developer.android.com/reference/android/widget/TextView#addTextChangedListener(android.text.TextWatcher)

- ViewDataBinding

  https://developer.android.com/reference/android/databinding/ViewDataBinding

- `createUserWithEmailAndPassword`

  https://firebase.google.com/docs/auth/android/password-auth?hl=ko

<br/>

**HomeFragment.kt**

```kotlin
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
```

**_참고 문서_**

- Fragment Lifecycle

  https://developer.android.com/guide/fragments/lifecycle

- DatabaseReference

  https://firebase.google.com/docs/reference/android/com/google/firebase/database/DatabaseReference

- com.google.firebase.database.ktx

  https://firebase.google.com/docs/reference/kotlin/com/google/firebase/database/ktx/package-summary

<br/>

**AddArticleActivity.kt**

```kotlin
package com.example.summer_part3_chapter06.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.summer_part3_chapter06.DBKey.Companion.DB_ARTICLES
import com.example.summer_part3_chapter06.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

/**
 * 중고거래 Item 에 대해서 데이터베이스에 값을 추가하는 역할
 */
class AddArticleActivity : AppCompatActivity() {

    // 사용자 갤러리 앱에서 지정된 Uri 를 저장하는 변수
    private var selectedUri: Uri? = null

    // FirebaseAuth 인스턴스 초기화
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    // FirebaseStorage 인스턴스 초기화
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    // article 레퍼런스 초기화
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_article)

        // 이미지 업로드 버튼
        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
            when {
                // 이미지 업로드를 위한 권한 요청
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }
            }
        }

        // 제출 버튼 클릭 이벤트 처리
        findViewById<Button>(R.id.submitButton).setOnClickListener {
            // 제목 : titleEditText
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            // 가격 : priceEditText
            val price = findViewById<EditText>(R.id.priceEditText).text.toString()
            // 판매자 ID : 최근 유저의 uid 값을 가져온다.
            val sellerId = auth.currentUser?.uid.orEmpty()

            // 사진이 올라오는 동안 진행바 보여줌
            showProgress()

            // 중간에 이미지가 있으면 업로드 과정을 추가
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                // 사진 올리는 함수
                uploadPhoto(
                    photoUri,
                    successHandler = { uri ->
                        // Article 업로드
                        uploadArticle(sellerId, title, price, uri)
                    },
                    // 에러 처리
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        // 진행바 숨기기
                        hideProgress()
                    }
                )
            } else {
                // 이미지가 추가되지 않은 경우
                uploadArticle(sellerId, title, price, "")
            }

        }
    }

    /**
     * 사진을 업로드하는 함수
     */
    private fun uploadPhoto(
        uri: Uri,
        successHandler: (String) -> Unit,
        errorHandler: () -> Unit
    ) {
        // FirebaseStorage 에 업로드 될 파일 이름 설정, 현재 시간 + .png 형식
        val fileName = "${System.currentTimeMillis()}.png"
        // FirebaseStorage 에 경로를 article/photo 로 지정하고 fileName 으로 레퍼런스 지정
        storage.reference.child("article/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                // 파일 업로드가 성공했을 때,
                if (it.isSuccessful) {
                    // FirebaseStorage 에서 해당 값 불러오기
                    storage.reference.child("article/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            // 성공했을 때 핸들러 함수
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            // 실패 했을 때 핸들러 함수
                            errorHandler()
                        }
                } else {
                    // 실패 했을 때 핸들러 함수
                    errorHandler()
                }
            }
    }

    /**
     * Article 을 업로드하는 함수
     */
    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl: String) {
        // ArticleModel 에 대한 값을 받아온다.
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), "$price 원", imageUrl)
        // 데이터베이스에 업로드
        articleDB.push().setValue(model)
        // 진행바 사라짐
        hideProgress()
        // 액티비티 종료
        finish()
    }

    /**
     * 권한 처리에 대한 함수,
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1010 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /**
     * Content(image) 를 가져오기 위한 intent 를 넣어준다.
     */
    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)
    }

    /**
     * 프로그래스 진행 바를 보여준다.
     */
    private fun showProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true
    }

    /**
     * 프로그래스 진행 바를 숨긴다.
     */
    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }

    /**
     * requestCode : 2020 에 대한 동작
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 결과 코드가 다를 경우 종료
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        /**
         * 요청 코드가 2020 일 때, photoImageView 에 이미지 업로드
         */
        when (requestCode) {
            2020 -> {
                val uri = data?.data
                if (uri != null) {
                    findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
                    selectedUri = uri
                } else {
                    // 실패 처리
                    Toast.makeText(this, "사진을 가져오기 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // 실패 처리
                Toast.makeText(this, "사진을 가져오기 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 권한 요청 팝업(교육용 팝업)
     * 앱을 사용할 때 반드시 필요한 권한 요청이 거부당했을 때,
     * 다시 한번 묻고 필요함을 알려주는 팝업
     */
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }
}
```

- FirebaseStorage

  https://firebase.google.com/docs/storage/android/start?hl=ko

- 앱 권한 요청

  https://developer.android.com/training/permissions/requesting?hl=ko

<br/>

**ArticleAdapter.kt**

```kotlin
package com.example.summer_part3_chapter06.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.summer_part3_chapter06.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * onItemClicked 를 통해서 ArticleModel 이 클릭 당했을 때의 기능을 포함하게 인자로 받는다.
 * ListAdapter : RecyclerView 로 구성되어 있음.
 */
class ArticleAdapter(val onItemClicked: (ArticleModel) -> Unit) :
    ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {

    /**
     * ViewHolder RecyclerView : 뷰홀더 내부 클래스로 작성
     */
    inner class ViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * bind 함수를 통해서 ArticleModel 에서  날짜, title, price, 가져온다.
         * Image 는 Glide 라이브러리를 통해서 업로드한다.
         */
        @SuppressLint("SimpleDateFormat")
        fun bind(articleModel: ArticleModel) {

            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(articleModel.createdAt)

            binding.titleTextView.text = articleModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.priceTextView.text = articleModel.price

            if (articleModel.imageUrl.isNotEmpty()) {
                Glide.with(binding.thumbnailImageView)
                    .load(articleModel.imageUrl)
                    .into(binding.thumbnailImageView)
            }

            // RecyclerView 의 요소가 클릭 했을 때,
            binding.root.setOnClickListener {
                onItemClicked(articleModel)
            }
        }
    }

    /**
     * 뷰 홀더가 생성되었을 때,
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArticleBinding.inflate(
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
        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}

```

- Glide 로 이미지 올리기

  https://github.com/bumptech/glide

<br/>

**ArticleModel.kt**

```kotlin
package com.example.summer_part3_chapter06.home

/**
 * Article 이 저장되는 형식
 */
data class ArticleModel(
    val sellerId: String,   // 판매자 ID
    val title: String,      // 제목
    val createdAt: Long,    // 어디에 생성되었는지
    val price: String,      // 가격
    val imageUrl: String,   // 이미지 주소 URL
) {
    // 파이어베이스에서 바로 데이터 클래스를 가져오려면 초기값에 대한 설정이 필요하다.
    constructor() : this("", "", 0, "", "")
}
```

- kotlin data class

  https://kotlinlang.org/docs/data-classes.html#data-classes-and-destructuring-declarations

<br/>

---

[나머지 코드 보기 : 채팅, 채팅방 목록](https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/Part3-6_chat.md)
