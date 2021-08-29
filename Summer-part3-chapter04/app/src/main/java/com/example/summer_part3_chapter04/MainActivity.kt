package com.example.summer_part3_chapter04

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summer_part3_chapter04.adapter.BookAdapter
import com.example.summer_part3_chapter04.adapter.HistoryAdapter
import com.example.summer_part3_chapter04.api.BookService
import com.example.summer_part3_chapter04.databinding.ActivityMainBinding
import com.example.summer_part3_chapter04.model.BestSellerDto
import com.example.summer_part3_chapter04.model.History
import com.example.summer_part3_chapter04.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    // binding : ActivityMainBinding activity_main.xml 파일에 대한 연결
    private lateinit var binding: ActivityMainBinding

    // 책 리스트 데이터를 관리하기 위한 어댑터
    private lateinit var adapter: BookAdapter

    // 검색 결과에 대한 기록을 관리하기 위한 어댑터
    private lateinit var historyAdapter: HistoryAdapter

    // BookService : JSON 데이터를 처리하기 위한 서비스
    private lateinit var bookService: BookService

    // 히스토리에 대한 내역 저장을 하기 위한= AppDatabase
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate() 메서드를 통해서 프래그먼트에서 사용할 결합 클래스 인스턴스 생성
        binding = ActivityMainBinding.inflate(layoutInflater)
        // activity_main.xml == root
        setContentView(binding.root)

        // 인터파크 API 를 통해서 가져온 책 리스트 초기화
        initBookRecyclerView()
        // 검색 했던 책에 대한 히스토리 리스트 초기화
        initHistoryRecyclerView()
        // 검색 기록 저장 및, 리뷰를 저장하기 위한 데이터베이스 초기화
        db = getAppDatabase(this)

        // retrofit2 를 통해서 해당 URL 에서 제공해주는 JSON 데이터를
        // GsonConverterFactory 를 사용해서 생성.
        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        // BookService 인터페이스의 getBestSellerBooks 함수를 통해서 베스트 셀러 정보 가져옴.
        bookService.getBestSellerBooks(getString(R.string.interParkAPIkey))
            .enqueue(object : Callback<BestSellerDto> {
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {

                    // 데이터를 가져오기 실패 시,
                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "NOT!! SUCCESS")
                        return
                    }
                    // 데이터 가져오기 성공 시,
                    response.body()?.let {
                        Log.d(TAG, it.toString())

                        // forEach 문을 통해서 책 리스트를 보여주는 어댑터에 넣어준다.
                        it.books.forEach { book ->
                            Log.d(TAG, book.toString())
                        }
                        // 데이터 형식은 BestSellerDto
                        adapter.submitList(it.books)
                    }
                }

                // 완전히 실패했을 경우.
                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    // todo 실패처리
                    Log.e(TAG, t.toString())
                }
            })
    }

    /**
     * 검색 기능 : getBooksByName 을 통해서 검색가능
     */
    private fun search(keyword: String) {
        bookService.getBooksByName(getString(R.string.interParkAPIkey), keyword)
            .enqueue(object : Callback<SearchBookDto> {
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    // 기록보여주는 리스트를 숨긴다.
                    hideHistoryView()
                    // 검색한 키워드를 저장한다.
                    saveSearchKeyword(keyword)
                    // 만약 반응이 없고 실패했을 경우.
                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "NOT!! SUCCESS")
                        return
                    }
                    // 실패하지 않았다면, 검색 결과 값을  통째로 넣어준다.
                    adapter.submitList(response.body()?.books.orEmpty())
                }

                // 완전히 실패했을 경우.
                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    hideHistoryView()
                    Log.e(TAG, t.toString())
                }
            })
    }

    /**
     * 책 리스트를 보여주는 RecyclerView 를 초기화한다.
     * 어댑터에 itemClickedListener 를 넣어주어 해당 아이템을 클릭했을 때, 디테일을 보여주도록 함.
     */
    private fun initBookRecyclerView() {
        adapter = BookAdapter(itemClickedListener = {
            // 디테일을 보여주는 액티비티 실행
            val intent = Intent(this, DetailActivity::class.java)
            // 해당 bookModel 을 넘겨준다. it은 Book : Model
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })
        // 클릭 안했을 때, LayoutManager 를 통해서 해당 리스트를 보여준다.
        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        // 어댑터를 연결해준다.
        binding.bookRecyclerView.adapter = adapter
    }

    /**
     * 검색기록에 대한 히스토리를 초기화한다.
     * 삭제 버튼이 눌렸을 때, 해당 데이터 값을 어댑터에서 삭제한다.
     * 클릭하지 않은 경우 해당 리스트를 화면에 보여준다.
     */
    private fun initHistoryRecyclerView() {
        historyAdapter = HistoryAdapter(historyDeleteClickedListener = {
            deleteSearchKeyword(it)
        })
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter

        // 검색어를 입력받는 EditText 를 초기화한다.
        initSearchEditText()
    }

    /**
     * searchEditText 에 대한 바인딩을 하고,
     * ENTER 값이 눌렸을 떄의 String 을 Search 함수에 넘겨준다.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchEditText() {
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        /**
         * 검색창을 눌렀을 때, 이전에 검색했던 결과를 보여준다.
         */
        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }

    /**
     * 이전에 검색했던 값을 보여준다
     */
    private fun showHistoryView() {
        // 데이터베이스에서 저장한 값을 역순으로 가져온다. (최신 검색 순)
        Thread {
            val keywords = db.historyDao().getAll().reversed()
            runOnUiThread {
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
            }
        }.start()
        binding.historyRecyclerView.isVisible = true
    }

    /**
     * 히스토리를 보여주는 RecyclerView 를 숨긴다.
     */
    private fun hideHistoryView() {
        binding.historyRecyclerView.isVisible = false
    }

    /**
     * 검색어 창에 입력된 String 값을 데이터베이스에 저장한다.
     */
    private fun saveSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().insertHistory(History(null, keyword))
        }.start()
    }

    /**
     * historyRecyclerView 에서 저장된 값을 데이터베이스에서 삭제한다.
     */
    private fun deleteSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().delete(keyword)
            showHistoryView()
        }.start()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}