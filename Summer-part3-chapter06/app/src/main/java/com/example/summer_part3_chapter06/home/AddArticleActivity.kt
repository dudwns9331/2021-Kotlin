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
