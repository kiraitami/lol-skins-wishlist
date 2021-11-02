package com.l.lolwishlist.ui.skin

import androidx.lifecycle.*
import com.l.lolwishlist.data.repository.DDragonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@HiltViewModel
class SkinsViewModel @Inject constructor(
    private val repository: DDragonRepository
) : ViewModel() {

    private val _query = MutableLiveData<String>("")

    val skins = Transformations.switchMap(_query.distinctUntilChanged()) { query ->
        if (query.isNullOrBlank()) {
            liveData {
                emitSource(
                    repository.getAllSkins()
                        .distinctUntilChanged()
                        .asLiveData()
                )
            }
        }
        else {
            liveData {
                emitSource(
                    repository.querySkinsByName(query)
                        .distinctUntilChanged()
                        .asLiveData()
                )
            }
        }
    }

    fun updateQuery(query: String?) {
        _query.value = query.orEmpty()
    }

    fun updateSkinSelected(skinId: String, selected: Boolean) = repository.selectSkin(skinId, selected)
        .flowOn(Dispatchers.IO)
        .asLiveData()
}