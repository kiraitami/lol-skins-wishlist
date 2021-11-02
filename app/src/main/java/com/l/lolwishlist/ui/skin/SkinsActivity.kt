package com.l.lolwishlist.ui.skin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.l.lolwishlist.R
import com.l.lolwishlist.databinding.ActivitySkinsBinding
import com.l.lolwishlist.ui.SkinAdapter
import com.l.lolwishlist.ui.SkinUIModel
import com.l.lolwishlist.utils.hideKeyboard
import com.l.lolwishlist.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class SkinsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySkinsBinding

    private val viewModel: SkinsViewModel by viewModels()

    private val adapterSkin: SkinAdapter by lazy {
        SkinAdapter(
            Glide.with(this),
            onSkinClick = { s, p ->

            },
            onMySkinClick = { s, p ->

            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkinsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecycler()
        setObservers()
        setButtons()
    }

    override fun onBackPressed() {
        if (binding.searchView.isVisible) {
            hideSearchView()
        }
        else {
            super.onBackPressed()
        }
    }

    private fun setRecycler() {
        val lm = GridLayoutManager(this@SkinsActivity, 3)

        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(adapterSkin.getItemViewType(position)) {
                    R.layout.adapter_skin -> 1
                    else -> 3
                }
            }
        }

        binding.recyclerSkins.run {
            layoutManager = lm
            adapter = adapterSkin
        }
    }

    private fun setObservers() {
        viewModel.champions.observe(this) { result ->
            result.handle(
                onLoading = {

                },
                onSuccess = { list ->
                    val uiSkinModelList = mutableListOf<SkinUIModel>()

                    list?.groupBy { it.championName }
                        ?.forEach { (s, l) ->
                            uiSkinModelList.add(SkinUIModel.Separator(s))
                            uiSkinModelList.addAll(l.map { SkinUIModel.SkinItem(it) })
                        }

                    adapterSkin.submitList(uiSkinModelList)
                },
                onError = {
                    Toast.makeText(this, it?.message ?: "Error", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.searchView.onTextWatch = {
            println(it)
        }

    }

    private fun setButtons() {
        binding.searchView.onCloseClick = {
            hideSearchView()
        }

        binding.fabSearch.setOnClickListener {
            showSearchView()
        }
    }

    private fun showSearchView() {
        binding.searchView.isVisible = true
        binding.fabSearch.isVisible = false
        binding.searchView.editText.showKeyboard(this)
    }

    private fun hideSearchView() {
        binding.searchView.isVisible = false
        binding.fabSearch.isVisible = true
        binding.searchView.editText.hideKeyboard(this)
    }

}