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