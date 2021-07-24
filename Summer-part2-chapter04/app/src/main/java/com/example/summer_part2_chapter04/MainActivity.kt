package com.example.summer_part2_chapter04

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.room.Room
import com.example.summer_part2_chapter04.model.History

class MainActivity : AppCompatActivity() {

    private val expressionTextView: TextView by lazy {
        findViewById(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById(R.id.resultTextView)
    }

    private var isOperator = false
    private var hasOperator = false
    private var calculatedValue = false

    private val historyLayout: View by lazy {
        findViewById(R.id.historyLayout)
    }

    private val historyLinearLayout: LinearLayout by lazy {
        findViewById(R.id.historyLinearLayout)
    }

    lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 데이터베이스 생성
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
    }

    fun buttonClicked(v: View) {
        when (v.id) {

            // 숫자
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")

            // 수식
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonMulti -> operatorButtonClicked("x")
            R.id.buttonDivider -> operatorButtonClicked("÷")
            R.id.buttonModulo -> operatorButtonClicked("%")
        }
    }

    /* 버튼이 눌릴 때마다 숫자를 입력하고 실시간으로 계산 결과를 보여준다. */
    private fun numberButtonClicked(number: String) {

        if (calculatedValue) {
            clearValue()
        }

        if (isOperator) {
            expressionTextView.append(" ")
        }
        isOperator = false

        val expressionText = expressionTextView.text.split(" ")

        // 15 자리까지 입력 가능하도록 예외처리
        if (expressionText.isNotEmpty() && expressionText.last().length >= 15) {
            Toast.makeText(this, "15자리 까지만 사용할 수 있습니다. ", Toast.LENGTH_LONG).show()
            return
            // 비어 있거나, 제일 앞에 0이 오는 경우 예외처리
        } else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_LONG).show()
            return
        }

        expressionTextView.append(number)
        // TODO resultTextView 실시간으로 계산 결과를 넣여야 하는 기능
        resultTextView.text = calculateExpression()
    }

    private fun operatorButtonClicked(operator: String) {
        if (expressionTextView.text.isEmpty()) {
            return
        }

        // 연산자가 이미 눌렸을 떄 한번 더 누르면 자동으로 수정하기 위해서
        when {
            isOperator -> {
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1) + operator
            }
            hasOperator -> {
                Toast.makeText(this, "연산자는 한 번만 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
                return
            }
            else -> {
                expressionTextView.append(" $operator")
            }
        }

        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        expressionTextView.text = ssb

        isOperator = true
        hasOperator = true
        calculatedValue = false
    }

    /* 연산 결과를 TextView2(resultText)에 따로 보여준다. */
    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")

        // 비어 있거나 앞에 숫자만 입려된 경우
        if (expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }

        // 숫자와 연산자 까지만 입력된 경우
        if (expressionTexts.size != 3 && hasOperator) {
            Toast.makeText(this, "아직 완성되지 않은 수식입니다.", Toast.LENGTH_LONG).show()
            return
        }

        // 중간에 코드가 잘못된 경우. 혹시 모를 상황에 대비한다. => 첫번재 숫자와 두번째 숫자가 제대로 치환되지 않은 경우
        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            return
        }

        // 계산 수식과 결과 값을 history 에서 보여주기 위해 DB에 저장할 값을 지정한다.
        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        // TODO DB에 넣어주는 부분

        Thread(Runnable {
            db.historyDao().insertHistory(History(null, expressionText, resultText))
        }).start()

        // 계산 결과가 나오면 결과 값이 다시 수식으로 올라가는 기능을 구현하기 위함.
        resultTextView.text = ""
        expressionTextView.text = resultText

        isOperator = false
        hasOperator = false
        calculatedValue = true

    }

    private fun calculateExpression(): String {
        val expressionTexts = expressionTextView.text.split(" ")

        if (hasOperator.not() || expressionTexts.size != 3) {
            return ""
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            return ""
        }

        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "x" -> (exp1 * exp2).toString()
            "÷" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }
    }

    /* 입력된 수식, 오퍼레이터 상태 값 초기화 */
    fun clearButtonClicked(v: View) {
        clearValue()
    }

    private fun clearValue() {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
        calculatedValue = false
    }

    /* 스레드 실행 */
    @SuppressLint("SetTextI18n")
    fun historyButtonClicked(v: View) {
        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao().getAll().reversed().forEach {
                runOnUiThread {
                    val historyView =
                        LayoutInflater.from(this).inflate(R.layout.history_row, null, false)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"
                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()

        // TODO 디비에서 모든 기록 가져오기
        // TODO 뷰에 모든 기록 가져오기
    }

    fun closeHistoryButtonClicked(v: View) {
        historyLayout.isVisible = false
    }

    fun historyClearButtonClicked(v: View) {
        historyLinearLayout.removeAllViews()
        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()

        // TODO DB에서 모든 기록 삭제
        // TODO 뷰에서 모든 기록 삭제
    }

}

/* 확장 함수 : 객체에 들어있는 함수를 추가하여 정의한다. 마치 원래 있는 함수처럼 사용 가능*/
fun String.isNumber(): Boolean {
    return try {
        this.toBigInteger()
        true
    } catch (e: NumberFormatException) {
        false
    }
}
