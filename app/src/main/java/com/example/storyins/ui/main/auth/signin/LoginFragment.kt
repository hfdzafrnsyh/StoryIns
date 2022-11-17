package com.example.storyins.ui.main.auth.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.storyins.R
import com.example.storyins.ui.main.auth.AuthActivity
import com.example.storyins.databinding.FragmentLoginBinding
import com.example.storyins.source.model.Wrapper
import com.example.storyins.source.model.remote.reponse.login.LoginResponse
import com.example.storyins.source.model.remote.request.LoginRequest
import com.example.storyins.ui.main.MainActivity
import com.example.storyins.ui.factory.StoryViewModelFactory



class LoginFragment : Fragment() {


    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private  var progressBar : Dialog?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater, container , false)
        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        loginViewModel = ViewModelProvider(this , StoryViewModelFactory(requireContext()))[LoginViewModel::class.java]

        initView()
        viewLoading()
        playAnimate()

    }



    private fun initView(){

        fragmentLoginBinding.btnRegister.setOnClickListener{
            val intent = Intent(activity, AuthActivity::class.java)
            intent.putExtra("PAGE_REQUEST", 2)
            startActivity(intent)
        }

        fragmentLoginBinding.btnLogin.setOnClickListener{
            actionLogin()
        }


        loginViewModel.getToken().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                activity?.finish()
            }
        }



    }

    private  fun actionLogin(){

            val email = fragmentLoginBinding.edLoginEmail.text.toString()
            val password = fragmentLoginBinding.edLoginPassword.text.toString()

            if(email.isEmpty()){
                fragmentLoginBinding.edLoginEmail.error = getString(R.string.error_email)
                fragmentLoginBinding.edLoginEmail.requestFocus()
            }else if(password.isEmpty()){
                fragmentLoginBinding.edLoginPassword.error =getString(R.string.error_password)
                fragmentLoginBinding.edLoginPassword.requestFocus()
            }else{
                val loginReq = LoginRequest(email,password)
                loginViewModel.login(loginReq).observe(viewLifecycleOwner){
                    when(it){
                        is  Wrapper.Loading -> {
                            showLoading()
                        }
                        is Wrapper.Success -> {
                            dismissLoading()
                            loginUser(it.data)
                        }
                        is Wrapper.Error -> {
                            dismissLoading()
                            showDialogError(it.msg)
                        }
                        else -> dismissLoading()
                    }
                }

            }

    }



   private fun loginUser(data: LoginResponse){
       val token = data.loginResult.token
       val user = data.loginResult
       loginViewModel.setToken(token.toString())
       loginViewModel.setUser(user)

       Log.d("user" , user.toString())
   }


    private fun showDialogError(message : String?){
        AlertDialog.Builder(context).apply {
            setMessage(message)
            setTitle(R.string.error_title)
            setNegativeButton(getString(R.string.cancel)
            ) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            create()
            show()
        }
    }


    private fun playAnimate(){

        val tvSignIn = ObjectAnimator.ofFloat(fragmentLoginBinding.tvSignIn , View.ALPHA , 1f ).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(fragmentLoginBinding.tvEmail , View.ALPHA , 1f ).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(fragmentLoginBinding.tvPassword , View.ALPHA , 1f ).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(fragmentLoginBinding.edLoginEmail , View.ALPHA , 1f ).setDuration(500)
        val edtPassword = ObjectAnimator.ofFloat(fragmentLoginBinding.edLoginPassword , View.ALPHA , 1f ).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(fragmentLoginBinding.btnLogin , View.ALPHA , 1f ).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(fragmentLoginBinding.btnRegister , View.ALPHA , 1f ).setDuration(500)


        val together = AnimatorSet().apply {
            playTogether(btnLogin,btnRegister)
        }

        AnimatorSet().apply {
            playSequentially(tvSignIn, tvEmail, edtEmail,tvPassword,edtPassword, together)
            start()
        }


    }


    @SuppressLint("InflateParams")
    private fun viewLoading(){
        progressBar = Dialog(requireContext())
        val progressbarLayout = layoutInflater.inflate(R.layout.dialog_progressbar , null)

        progressBar?.let {
            it.setContentView(progressbarLayout)
            it.setCancelable(false)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }


   private fun showLoading() {
        progressBar?.show()
    }

    private fun dismissLoading() {
        progressBar?.dismiss()
    }

}