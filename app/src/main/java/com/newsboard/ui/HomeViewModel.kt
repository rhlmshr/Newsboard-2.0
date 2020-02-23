package com.newsboard.ui

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.newsboard.data.local.LocalDataManager
import com.newsboard.data.models.sources.Source
import com.newsboard.utils.base.ResponseState

class HomeViewModel : ViewModel() {

    var sourcesLiveData: MediatorLiveData<ResponseState<List<Source>>> = MediatorLiveData()

    private val homeRepo = HomeRepo()

    fun getSources() {
        sourcesLiveData.addSource(LocalDataManager.getAllSources()) {
            if (it.isNullOrEmpty()) {
                homeRepo.getSources(sourcesLiveData)
            } else {
                sourcesLiveData.value = ResponseState.Success(it)
            }
        }
    }
}