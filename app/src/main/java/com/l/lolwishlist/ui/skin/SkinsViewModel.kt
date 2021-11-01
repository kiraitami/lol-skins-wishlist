package com.l.lolwishlist.ui.skin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.l.lolwishlist.data.repository.DDragonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject


@HiltViewModel
class SkinsViewModel @Inject constructor(
    private val repository: DDragonRepository
) : ViewModel() {


    val champions = repository.getAllSkins()
        .distinctUntilChanged()
        .asLiveData()
}