# Part3 - 4

## Summber Kotlin part3 2021-08-29

<br>

### **_레이아웃 xml_**

<br>

Part3 부터는 기본적인 XML 레이아웃에 대한 설명은 생략한다.

<br>

---

### **_Activity - Kotlin_**

**MainActivity.kt**

```kotlin
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
```

`MainActivity` 에서는 인터파크 API 에서 불러온 값을 `RecyclerView` 에 그려준다. 또한 검색어 창을 눌렀을 때, 검색한 내역을 표시하게 하고 `RecyclerView` 에 표시된 아이템을 클릭했을 때 해당 아이템 값에 대한 정보를 `Retrofit2` 를 통해서 값을 `model` 에 저장하고 `DetailActivity` 가 실행되도록 `intent` 를 넘겨준다.

**_MainActivity 참고사항_**

1. [뷰 결합, 바인딩 설명 바로가기](https://developer.android.com/topic/libraries/view-binding?hl=ko)

2. [Retrofit2 사용 및 설명 바로가기](https://square.github.io/retrofit/)

   - [Retrofit2 추가 설명 자료 1](https://jaejong.tistory.com/33)

   - [Retrofit2 추가 설명 자료 2](https://velog.io/@hhb041127/AndroidKotlin-Retrofit2%EB%A5%BC-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90)

3. [안드로이드 room 설명 바로가기](https://developer.android.com/training/data-storage/room?hl=ko)

<br/>

**DetailActivity.kt**

```kotlin
package com.example.summer_part3_chapter04

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.summer_part3_chapter04.databinding.ActivityDetailBinding
import com.example.summer_part3_chapter04.model.Book
import com.example.summer_part3_chapter04.model.Review

class DetailActivity : AppCompatActivity() {

    // Activity_detail.xml 에 대한 바인딩
    private lateinit var binding: ActivityDetailBinding

    // 로컬 데이터베이스 객체 선언
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = getAppDatabase(this)

        // MainActivity 에서 intent 를 통해서 실행한 bookModel 을 가져온다.
        val model = intent.getParcelableExtra<Book>("bookModel")

        // titleTextView 에 title 값 가져옴.
        binding.titleTextView.text = model?.title.orEmpty()

        // Glide 를 이용해서 이미지를 가져온다.
        Glide
            .with(binding.coverImageView.context)
            .load(model?.coverSmallUrl.orEmpty())
            .into(binding.coverImageView)

        // API 에서 받아온 model 의 description 값을 저장한다.
        binding.descriptionTextView.text = model?.description.orEmpty()

        // 로컬 데이터베이스에 review 에 대한 값을 저장한다.
        Thread {
            val review = db.reviewDao().getOneReview(model?.id?.toInt() ?: 0)
            runOnUiThread {
                binding.reviewEditText.setText(review?.review.orEmpty())
            }
        }.start()

        // 저장 버튼 클릭에 대한 이벤트 처리
        binding.saveButton.setOnClickListener {
            Thread {
                // 리뷰에 있는 Text 값을 저장한다.
                db.reviewDao().saveReview(
                    Review(
                        model?.id?.toInt() ?: 0,
                        binding.reviewEditText.text.toString()
                    )
                )
            }.start()
        }
    }
}
```

`MainActivitiy` 에서 `RecyclerView` 에 표시된 책들의 내용을 클릭했을 때 실행되는 액티비티이다. 책에 대한 제목, 이미지, 설명 등 인터파크 API 에서 제공하는 값을 `Retrofit2` 를 통해서 JSON 값을 `model` 에 저장한 값을 `intent` 를 통해서 넘겨준 값을 받아온다. 또한, 로컬 데이터베이스에 작성한 리뷰를 저장하는 작업을 한다.

**_DetailActivity 참고사항_**

1. [스레드 설명 바로가기](https://developer.android.com/reference/java/lang/Thread)

2. [안드로이드 room 설명 바로가기](https://developer.android.com/training/data-storage/room?hl=ko)

3. [Glide 레파지토리 및 설명 바로가기](https://github.com/bumptech/glide)

<br/>

**AppDatabase.kt**

```kotlin
package com.example.summer_part3_chapter04

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.summer_part3_chapter04.dao.HistoryDao
import com.example.summer_part3_chapter04.dao.ReviewDao
import com.example.summer_part3_chapter04.model.History
import com.example.summer_part3_chapter04.model.Review

/**
 * 데이터베이스 정의
 * entities History, Review : Dao
 */
@Database(entities = [History::class, Review::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}

/**
 * 데이터베이스를 가져오는 메소드
 * 테스트 작업을 하면서 Database 의 버전이 다르기 떄문에 오류가 난다.
 * 다음 오류를 잡기 위해서 Migration 코드를 짠다.
 */
fun getAppDatabase(context: Context): AppDatabase {

    val migration_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `REVIEW` (`id` INTEGER, `review` TEXT, PRIMARY KEY(`id`))")
        }
    }
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "BookSearchDB"
    )
        .addMigrations(migration_1_2)
        .build()
}
```

### **_Model(DTO) - Kotlin_**

**Book.kt**

```kotlin
package com.example.summer_part3_chapter04.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 커스텀 객체를 intent 로 넘기기 위해서는
 * Parcelable 객체로 만들어 직렬화를 시켜서 내보내준다.
 */
@Parcelize
data class Book(
    // 해당 값들은 인터파크 API 를 통해서 얻을 수 있는 JSON 값들이다.
    @SerializedName("itemId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverSmallUrl") val coverSmallUrl: String,
) : Parcelable

```

<br/>

**BestSellerDto.kt**

```kotlin
package com.example.summer_part3_chapter04.model

import com.google.gson.annotations.SerializedName

/**
 * 해당 값은 Gson 라이브러리를 통해서 인터파크 API 에서 받아온 JSON 값을
 * title 과 item 값에 Book Dto 형태로 넣어준다.
 */
data class BestSellerDto(
    @SerializedName("title") val title: String,
    @SerializedName("item") val books: List<Book>,
)
```

<br/>

**SearchBookDto.kt**

```kotlin
package com.example.summer_part3_chapter04.model

import com.google.gson.annotations.SerializedName

/**
 * 해당 값은 Gson 라이브러리를 통해서 인터파크 API 에서 받아온 JSON 값을
 * title 과 item 값에 Book Dto 형태로 넣어준다.
 */
data class SearchBookDto(
    @SerializedName("title") val title: String,
    @SerializedName("item") val books: List<Book>,
)
```

<br/>

**History.kt**

```kotlin
package com.example.summer_part3_chapter04.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 해당 값들은 Room 라이브러리를 통해서 로컬 데이터베이스에
 * 저장되는 값들로 기본키와 해당 열에 대한 값을 지정해준다.
 */
@Entity
data class History(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "keyword") val keyword: String?
)
```

**Review.kt**

```kotlin
package com.example.summer_part3_chapter04.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 해당 값들은 Room 라이브러리를 통해서 로컬 데이터베이스에
 * 저장되는 값들로 기본키와 해당 열에 대한 값을 지정해준다.
 */
@Entity
data class Review(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "review") val review: String?
)
```

<br/>

### **_DAO - Kotlin_**

**HistoryDao.kt**

```kotlin
package com.example.summer_part3_chapter04.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.summer_part3_chapter04.model.History

/**
 * 로컬 데이터베이스를 사용하기위해 함수를 인터페이스로 정의한다.
 * Dao : Database Access Object : DB를 사용해 데이터를 조회하거나 조작하는 기능을 전담하도록 만든 오브젝트
 */
@Dao
interface HistoryDao {

    // 기록 가져오기
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    // 기록 넣기
    @Insert
    fun insertHistory(history: History)

    // 기록 지우기
    @Query("DELETE FROM history WHERE keyword == :keyword")
    fun delete(keyword: String)

}
```

<br/>

**ReviewDao.kt**

```kotlin
package com.example.summer_part3_chapter04.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.summer_part3_chapter04.model.Review

/**
 * 로컬 데이터베이스를 사용하기위해 함수를 인터페이스로 정의한다.
 * Dao : Database Access Object : DB를 사용해 데이터를 조회하거나 조작하는 기능을 전담하도록 만든 오브젝트
 */
@Dao
interface ReviewDao {

    // 리뷰 불러오기
    @Query("SELECT * FROM review WHERE id == :id")
    fun getOneReview(id: Int): Review?

    // 리뷰 저장하기
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReview(review: Review?)

}
```

<br/>

### **_adapter - Kotlin_**

**BookAdapter.kt**

```kotlin
package com.example.summer_part3_chapter04.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.summer_part3_chapter04.databinding.ItemBookBinding
import com.example.summer_part3_chapter04.model.Book

/**
 *  책에 대한 내용을 담는 List 에 대한 어댑터
 *  itemClickedListener 를 받아서 아이템을 클릭했을 때의 이벤트 처리를 한다.
 */
class BookAdapter(private val itemClickedListener: (Book) -> Unit) :
    ListAdapter<Book, BookAdapter.BookItemViewHolder>(diffUtil) {

    inner class BookItemViewHolder(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bookModel: Book) {
            // 책 제목과 설명을 보여준다.
            binding.titleTextView.text = bookModel.title
            binding.descriptionTextView.text = bookModel.description

            // 아이템을 눌렀을 떄, itemClickedListener 에서 처리하도록 함
            binding.root.setOnClickListener {
                itemClickedListener(bookModel)
            }

            // Glide 를 통해서 리스트에 이미지가 표시되도록 한다.
            // 리스트는 이미지가 좌측, 제목과 설명이 우측에 정렬되어 있다.
            Glide
                .with(binding.coverImageView.context)
                .load(bookModel.coverSmallUrl)
                .into(binding.coverImageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        return BookItemViewHolder(
            ItemBookBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
```

<br/>

**HistoryAdapter.kt**

```kotlin
package com.example.summer_part3_chapter04.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.summer_part3_chapter04.databinding.ItemHistoryBinding
import com.example.summer_part3_chapter04.model.History

/**
 * BookAdapter 와 거의 유사하다.
 * historyDeleteClickedListener 를 통해서 클릭했을 때, 삭제하는 이벤트 처리를 해준다.
 */
class HistoryAdapter(val historyDeleteClickedListener: (String) -> Unit) :
    ListAdapter<History, HistoryAdapter.HistoryItemViewHolder>(diffUtil) {

    inner class HistoryItemViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(historyModel: History) {
            binding.historyKeywordTextView.text = historyModel.keyword

            binding.historyKeywordDeleteButton.setOnClickListener {
                historyDeleteClickedListener(historyModel.keyword.orEmpty())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        return HistoryItemViewHolder(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem.keyword == newItem.keyword
            }
        }
    }

}
```

<br/>

### 결과화면

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap4-result.PNG" width="360px" height="640px"/>

</p>

<br>

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
