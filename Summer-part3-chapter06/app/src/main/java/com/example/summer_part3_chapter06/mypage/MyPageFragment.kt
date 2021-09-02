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