package com.example.summer_part4_chapter01

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.summer_part4_chapter01.adapter.VideoAdapter
import com.example.summer_part4_chapter01.dto.VideoDto
import com.example.summer_part4_chapter01.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    // video 의 RecyclerView 를 컨트롤 하기 위한 어댑터
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 재생에 대한 프래그먼트를 설정해준다. ➡ 이후 슬라이드를 통해서 미니 재생기를 만듬
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayerFragment())
            .commit()

        // 어댑터 초기화 및 fragment 지정
        videoAdapter = VideoAdapter(callback = { url, title ->
            supportFragmentManager.fragments.find { it is PlayerFragment }?.let {
                (it as PlayerFragment).play(url, title)
            }
        })

        // 영상들이 기본적으로 보여지는 RecyclerView
        findViewById<RecyclerView>(R.id.mainRecyclerView).apply {
            adapter = videoAdapter
            // LinearLayout 으로 설정해준다.
            layoutManager = LinearLayoutManager(context)
        }
        // mocky 서버에 올라가 있는 비디오 리스트 가져오기
        getVideoList()
    }

    /**
     * 영상 리스트를 가져오는 함수
     * retrofit 을 이용해서 mocky 에서 생성된 데이터를 불러온다.
     */
    private fun getVideoList() {
        // retrofit 으로 불러옴
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 서비스 생성
        retrofit.create(VideoService::class.java).also { it ->
            // VideoService 인터페이스를 통해서 해당 url의 데이터를 GET 을 통해서 가져온다.
            it.listVideos()
                .enqueue(object : Callback<VideoDto> {
                    // Callback은 VideoDto 형식을 가진다.
                    override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                        // 불러오는 것이 성공하지 못한다면
                        if (response.isSuccessful.not()) {
                            Log.d("MainActivity", "response fail")
                            return
                        }
                        // 만약에 비디오 리스트를 불러오는 것이 성공한다면
                        response.body()?.let { videoDto ->
                            Log.d("MainActivity", it.toString())
                            // 어댑터에 videos 형식을 넣는다.
                            //    val title: String,
                            //    val sources: String,
                            //    val subtitle: String,
                            //    val thumb: String,
                            //    val description: String
                            videoAdapter.submitList(videoDto.videos)
                        }
                    }

                    override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                        // 예외처리
                    }
                })
        }
    }
}