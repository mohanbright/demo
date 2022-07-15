package com.journalmetro.app.ui.web

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.journalmetro.app.R
import com.journalmetro.app.databinding.FragmentWebViewBinding
import com.journalmetro.app.databinding.LayoutTopBarWebBinding
import com.journalmetro.app.ui.common.fragment.*

/**
 * Created by App Developer on September/2021.
 */
class WebViewFragment : BaseFragment<FragmentWebViewBinding, WebViewViewModel>() {

    // *****
    // Var_Private.
    private lateinit var topBarBinding: LayoutTopBarWebBinding // Top bar.
    private lateinit var webSiteLink: String

    // *****
    // Var_Override.
    override var viewModelBindingVariable: Int = BR.viewModel
    override var layoutResId: Int = R.layout.fragment_web_view

    // *****
    // Fun_Override.
    override fun provideViewModel(): WebViewViewModel {
        return ViewModelProvider(
            this, viewModelFactory).get(WebViewViewModel::class.java)
    }

    override fun setDataBindingVariables(binding: ViewDataBinding) {
        super.setDataBindingVariables(binding)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set top bar binding for buttons.
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        topBarBinding = LayoutTopBarWebBinding.inflate(inflater)

        // Get arguments to pass data from other fragments.
        val args: WebViewFragmentArgs by navArgs()

        webSiteLink = args.webSiteLink // Set link as variable.
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeAsUpEnabled(false)
        showActionBar()
        showBottomNavigationView()
        actionBarCustom(getActionBar())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI() // Define UI.
        setupListeners() // Define listeners.
    }

    // Get Action Bar.
    private fun getActionBar(): View {
        return topBarBinding.root
    }

    private fun setupListeners() {
        // Not using for now.
    }

    private fun setupUI() {
        setupWebView(webSiteLink)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(link: String) {
        layoutBinding.container.settings.javaScriptEnabled = true
        layoutBinding.container.loadUrl(link)
    }
}