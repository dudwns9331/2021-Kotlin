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