package com.newsboard.utils.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.newsboard.R
import com.newsboard.data.models.articles.ArticlesResponse


abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    abstract val layoutId: Int
    protected lateinit var dataBinding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initLiveData()
        setupViews()
        setData()
        setListeners()
    }

    /**
     * Method to initialize class variables.
     */
    protected open fun init() {}

    /**
     * Method to initialize LiveData
     */
    protected open fun initLiveData() {}

    /**
     * Method to setup views.
     */
    protected open fun setupViews() {}

    /**
     * Method to set data.
     */
    protected open fun setData() {}

    /**
     * Method to set listeners
     */
    protected open fun setListeners() {}

    protected open fun showProgressDialog() {}

    protected open fun hideProgressDialog() {}

    protected fun setEmptyErrorStates(
        responseState: ResponseState<ArticlesResponse>,
        errorImg: ImageView,
        errorTitle: TextView,
        errorDesc: TextView
    ) {
        when (responseState) {
            is ResponseState.NoInternet -> {
                errorImg.setImageResource(R.drawable.ic_no_internet)
                errorTitle.text = getString(R.string.sorry)
                errorDesc.text = getString(R.string.no_internet_msg)
            }
            is ResponseState.NoData -> {
                errorImg.setImageResource(R.drawable.ic_empty)
                errorTitle.text = getString(R.string.sad_no_result)
                errorDesc.text = getString(R.string.empty_msg)
            }
            is ResponseState.Error -> {
                errorImg.setImageResource(R.drawable.ic_error)
                errorTitle.text = getString(R.string.aw_sorry)
                errorDesc.text = getString(R.string.error_msg)

                Log.e(this::javaClass.name, responseState.message)
            }
        }
    }

    fun showToastShort(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}