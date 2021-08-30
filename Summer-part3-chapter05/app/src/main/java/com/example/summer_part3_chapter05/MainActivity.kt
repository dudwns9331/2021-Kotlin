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
            // 정상적으로 로그인 된 경우 LikeActivity 를 실행한다.
            startActivity(Intent(this, LikeActivity::class.java))

            // 백 스택에 남아 있지 않도록 종료한다.
            finish()
        }
    }
}