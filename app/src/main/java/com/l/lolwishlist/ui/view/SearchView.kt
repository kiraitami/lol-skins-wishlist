package com.l.lolwishlist.ui.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.doOnAttach
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.l.lolwishlist.R
import com.l.lolwishlist.databinding.SearchViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: SearchViewBinding = SearchViewBinding.inflate(LayoutInflater.from(context), this, true)

    var onCloseClick: (() -> Unit)? = null
    var onSearchClick: (() -> Unit)? = null
    var onTextWatch: ((text: String) -> Unit)? = null
    private var shouldClose = false

    val editText: EditText
        get() = binding.inputSearch

    init {
        doOnAttach { _ ->
            binding.button.setOnClickListener {
                if (shouldClose) {
                    onCloseClick?.invoke()
                }
                else {
                    onSearchClick?.invoke()
                }
            }

            findViewTreeLifecycleOwner()?.lifecycleScope?.let { lifecycleCoroutineScope ->
                binding.inputSearch.watchTextAsFlow()
                    .onEach {
                        onTextWatch?.invoke(it)
                        handleButtonIcon(it)
                    }
                    .launchIn(lifecycleCoroutineScope)
            }

            binding.inputSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchClick?.invoke()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private fun handleButtonIcon(text: String) {
        shouldClose = text.isNotBlank()

        if (shouldClose) {
            binding.button.setImageResource(R.drawable.ic_close_x)
        }
        else {
            binding.button.setImageResource(R.drawable.ic_search)
        }
    }

    private fun EditText.watchTextAsFlow(delayInMillis: Long = 100L) = callbackFlow<String> {
        var job: Job? = null
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    job?.cancel()
                    job = launch (Dispatchers.Unconfined) {
                        try {
                            delay(delayInMillis)
                            trySend(it.toString())
                        }
                        catch (e: Exception) {
                            trySend("")
                        }
                    }
                }
            }
        }

        this@watchTextAsFlow.addTextChangedListener(watcher)

        awaitClose {
            this@watchTextAsFlow.removeTextChangedListener(watcher)
        }
    }


}