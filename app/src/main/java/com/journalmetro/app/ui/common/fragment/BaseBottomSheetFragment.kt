package com.journalmetro.app.ui.common.fragment

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.journalmetro.app.ui.MainViewModel
import com.journalmetro.app.ui.common.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Abstract base class for bottom sheet fragment creation.
 *
 * Base implementation of the basic needs and actions of a bottom sheet fragment class
 *
 * @param LayoutBinding Reference to the fragment's layout binding class
 * @param VM Reference to the fragment's ViewModel class
 */

abstract class BaseBottomSheetFragment<LayoutBinding : ViewDataBinding, VM : ViewModel> :
    BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    protected lateinit var viewModel: VM
    abstract var viewModelBindingVariable: Int
    protected abstract var layoutResId: Int

    private var _binding: LayoutBinding? = null
    protected val layoutBinding get() = _binding!!

    protected lateinit var mainViewModel : MainViewModel

    protected var isCancelableDialog  = true

    var dialogHeightPer  = 0.98f

    var dialogCancellableOnBack = true

    abstract fun provideViewModel(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        super.onCreate(savedInstanceState)
        viewModel = provideViewModel()
        mainViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
                MainViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return layoutBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setCustomHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.isHideable = !isCancelableDialog
                behaviour.isDraggable = !isCancelableDialog
            }
        }
        dialog.setCancelable(!isCancelableDialog)
        dialog.setCanceledOnTouchOutside(!isCancelableDialog)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataBindingVariables(layoutBinding)
        layoutBinding.lifecycleOwner = this
        layoutBinding.executePendingBindings()
    }


    /**
     * Avoid bottom sheet gets dismissed on back button press
     */
    private fun setupBackPressListener() {
        this.view?.isFocusableInTouchMode = true
        this.view?.requestFocus()
        this.view?.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }

    override fun onResume() {
        super.onResume()
        if(dialogCancellableOnBack)
        setupBackPressListener()
    }
    protected open fun setDataBindingVariables(binding: ViewDataBinding) {
        binding.setVariable(viewModelBindingVariable, viewModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setCustomHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = (Resources.getSystem().displayMetrics.heightPixels * dialogHeightPer).toInt()
        bottomSheet.layoutParams = layoutParams
    }

    private fun performDependencyInjection() = run { AndroidSupportInjection.inject(this) }


}