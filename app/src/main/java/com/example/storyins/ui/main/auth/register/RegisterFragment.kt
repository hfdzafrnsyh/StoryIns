package com.example.storyins.ui.main.auth.register

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.storyins.R
import com.example.storyins.databinding.FragmentRegisterBinding
import com.example.storyins.source.model.Wrapper
import com.example.storyins.source.model.remote.request.RegisterRequest
import com.example.storyins.ui.main.auth.AuthActivity
import com.example.storyins.ui.factory.StoryViewModelFactory


class RegisterFragment : Fragment() {


    private lateinit var fragmentRegisterBinding: FragmentRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private var progressBar : Dialog?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return fragmentRegisterBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerViewModel = ViewModelProvider(this , StoryViewModelFactory(requireContext()))[RegisterViewModel::class.java]

        initView()
        viewLoading()
    }


    private fun initView(){

        fragmentRegisterBinding.btnRegister.setOnClickListener {
           actionRegister()
        }

    }


    private fun actionRegister(){

        val name = fragmentRegisterBinding.edRegisterName.text.toString()
        val email = fragmentRegisterBinding.edRegisterEmail.text.toString()
        val password = fragmentRegisterBinding.edRegisterPassword.text.toString()

        if(name.isEmpty()) {
            fragmentRegisterBinding.edRegisterName.error = getString(R.string.error_name)
            fragmentRegisterBinding.edRegisterName.requestFocus()
        }else if(email.isEmpty()){
            fragmentRegisterBinding.edRegisterEmail.error = getString(R.string.error_email)
            fragmentRegisterBinding.edRegisterEmail.requestFocus()
        }else if(password.isEmpty()){
            fragmentRegisterBinding.edRegisterPassword.error = getString(R.string.error_password)
            fragmentRegisterBinding.edRegisterPassword.requestFocus()
        }else{

            val registerRequest = RegisterRequest(name, email,password)
            registerViewModel.register(registerRequest).observe(viewLifecycleOwner){
                when (it) {
                    is Wrapper.Loading -> {
                        showLoading()
                    }
                    is Wrapper.Success -> {
                        dismissLoading()
                        showDialogSuccess(it.data.message)
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


    private fun showDialogError(message : String?){
        AlertDialog.Builder(context).apply {
            setMessage(message)
            setTitle(getString(R.string.error_title))
            setNegativeButton(getString(R.string.cancel)
            ) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            create()
            show()
        }
    }


    private fun showDialogSuccess(message : String?){
        AlertDialog.Builder(context).apply {
            setMessage(message)
            setTitle(getString(R.string.register_success))
            setPositiveButton(getString(R.string.login)
            ) { _, _ ->
                (activity as AuthActivity).onBackPressed()
            }
            create()
            show()
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