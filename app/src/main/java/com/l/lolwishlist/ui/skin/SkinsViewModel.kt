package com.l.lolwishlist.ui.skin

import androidx.lifecycle.*
import com.l.lolwishlist.data.repository.DDragonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class SkinsViewModel @Inject constructor(
    private val repository: DDragonRepository
) : ViewModel() {

    private val _query = MutableLiveData<String>()
    private val _shouldFilterSelected = MutableLiveData<Boolean>()

    val skins = Transformations.switchMap(_shouldFilterSelected.distinctUntilChanged()) { shouldFilterSelected ->
        Transformations.switchMap(_query.distinctUntilChanged()) { query ->

            var skinsFlow = repository.getAllSkins().distinctUntilChanged()

            skinsFlow = skinsFlow.mapLatest { list ->
                if (shouldFilterSelected) {
                    list.filter { it.selected }
                }
                else {
                    list
                }
            }
                .flowOn(Dispatchers.Default)

            skinsFlow = skinsFlow.map { list ->
                if (query.isNullOrBlank()) {
                    list
                }
                else {
                    list.filter {
                        (it.name.contains(query, true)
                                || it.championId.contains(query, true))
                    }
                }
            }
                .flowOn(Dispatchers.Default)

            skinsFlow.asLiveData()
        }
    }

    fun updateQuery(query: String?) {
        _query.value = query.orEmpty()
    }

    fun shouldFilterSelected(filter: Boolean) {
        _shouldFilterSelected.value = filter
    }

    fun updateSkinSelected(skinId: String, selected: Boolean) = repository.selectSkin(skinId, selected)
        .flowOn(Dispatchers.IO)
        .asLiveData()
}