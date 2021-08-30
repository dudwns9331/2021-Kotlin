package com.example.summer_part3_chapter05

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    // FirebaseAuth 의인스턴스를 선언한다.
    private lateinit var auth: FirebaseAuth

    // callbackManager 를 선언한다.
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //FirebaseAuth 인스턴스를 초기화 시킨다.
        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()

        // 로그인 버튼 초기화
        initLoginButton()
        // 회원가입 버튼 초기화
        initSignUpButton()
        // 이메일과 패스워드 EditText 초기화
        initEmailAndPasswordEditText()
        // 페이스북 로그인 초기화
        initFacebookLoginButton()
    }


    /**
     * 로그인 버튼
     * 로그인 버튼을 눌렀을 때, 이메일과 패스워드 값이 FirebaseAuth 에 있는 값인지 확인한다.
     * 만약에 email, password 값이 제대로 넘어왔다면 handleSuccessLogin 을 통해서 처리하도록 함.
     */
    private fun initLoginButton() {
        // 로그인 버튼 바인딩
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener { // 로그인 버튼이 눌렸을 때,
            // 이메일 입력창에서 값을 가져옴
            val email = getInputEmail()
            // 패스워드 입력창에서 값을 가져옴
            val password = getInputPassword()

            // FirebaseAuth 의 signInWithEmailAndPassword() 함수를 통해 로그인을 한다.
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // email, password 값이 제대로 넘어온 경우
                    if (task.isSuccessful) {
                        // 로그인 처리 함수
                        handleSuccessLogin()
                    } else {
                        // 실패 시,
                        Toast.makeText(this, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요", Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }
    }

    /**
     * 회원가입 버튼
     * 이메일과 패스워드 값을 가져와서 Firebase auth 에 등록하도록 한다.
     *
     */
    private fun initSignUpButton() {
        // 회원가입 버튼 바인딩
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener { // 회원가입 버튼이 눌렸을 때,
            // 이메일 입력창에서 값을 가져옴
            val email = getInputEmail()
            // 패스워드 입력창에서 값을 가져옴
            val password = getInputPassword()

            // FirebaseAuth 의 createUserWithEmailAndPassword() 함수를 통해 Firebase Auth 에 등록한다.
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 성공한 경우
                        Toast.makeText(this, "회원가입에 성공했습니다. 로그인 버튼을 눌러 로그인해주세요.", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        // 실패한 경우
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_LONG)
                            .show()
                    }

                }
        }
    }

    /**
     * 이메일과 패스워드 EditText 에 대한 활성화 처리를 해준다.
     */
    private fun initEmailAndPasswordEditText() {
        // 이메일 EditText 바인딩
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        // 패스워드 EditText 바인딩
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        // 로그인 Button 바인딩
        val loginButton = findViewById<Button>(R.id.loginButton)
        // 회원가입 Button 바인딩
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        // 이메일 EditText 가 변할때마다 enable 값을 체킹한다.
        emailEditText.addTextChangedListener {
            // 만약 emailEditText 가 비어있지 않고, 패스워드가 비어있지 않으면.
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            // 로그인 버튼 활성화
            loginButton.isEnabled = enable
            // 회원가입 버튼 활성화
            signUpButton.isEnabled = enable

        }

        // 이메일 EditText 가 변할때마다 enable 값을 체킹한다.
        passwordEditText.addTextChangedListener {
            // 만약 emailEditText 가 비어있지 않고, 패스워드가 비어있지 않으면.
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            // 로그인 버튼 활성화
            loginButton.isEnabled = enable
            // 회원가입 버튼 활성화
            signUpButton.isEnabled = enable
        }
    }

    /**
     * 페이스북 로그인 버튼
     * 페이스북 로그인 버튼을 활성화 한다. LoginButton Type 으로 정의된 facebookLoginButton
     */
    private fun initFacebookLoginButton() {
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)

        // 이메일과 public_profile 을 받아오도록 한다.
        facebookLoginButton.setPermissions("email", "public_profile")

        // callbackManager 를 통해서 로그인 하는 과정을 통제한다.
        facebookLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                // 로그인이 성공적일때,
                override fun onSuccess(result: LoginResult) {
                    // credential 값을 FacebookAuthProvider 를 통해서 토큰 값으로 받아온다.
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    // FirebaseAuth 에서 해당 credential 값을 통해서 회원가입 한다.
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            // 로그인에 성공한 경우
                            if (task.isSuccessful) {
                                handleSuccessLogin()
                            } else {
                                // 로그인에 실패한 경우
                                Toast.makeText(
                                    this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

                override fun onCancel() {}

                // 페이스북 로그인에서 오류가 발생한 경우
                override fun onError(error: FacebookException?) {
                    Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    /**
     * emailEditText 에 들어있는 값을 가져온다.
     */
    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }

    /**
     * passwordEditText 에 들어있는 값을 가져온다.
     */
    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

    /**
     * 페이스북 로그인 버튼으로 로그인한 경우?
     * 결과를 담는다.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 페이스북 로그인 결과를 콜백매니저에 담는다.
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 로그인에 성공한 경우, 처리 함수
     */
    private fun handleSuccessLogin() {
        // 만약에 로그인한 유저가 기록이 없다면
        if (auth.currentUser == null) {
            // 로그인에 실패
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 유저 아이디 저장.
        val userId = auth.currentUser?.uid.orEmpty()
        // 최근 유저 아이디 데이터베이스에 "Users" 로 저장한다.
        val currentUserDB = Firebase.database.reference.child("Users").child(userId)
        // user Map 생성
        val user = mutableMapOf<String, Any>()
        // "userId 에 최근에 로그인한 유저를 넣어준다.
        user["userId"] = userId
        // currentUserDB 에 해당 값을 업데이트 해준다.
        currentUserDB.updateChildren(user)

        finish()
    }
}