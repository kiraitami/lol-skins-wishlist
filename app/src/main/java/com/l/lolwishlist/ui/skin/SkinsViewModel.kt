package com.l.lolwishlist.ui.skin

import androidx.lifecycle.*
import com.l.lolwishlist.data.model.Skin
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

    private val _query = MutableLiveData<String>("")
    val query: LiveData<String>
        get() = _query

    val skins = Transformations.switchMap(query.distinctUntilChanged()) { query ->
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
}