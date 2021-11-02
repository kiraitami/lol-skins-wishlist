package com.l.lolwishlist.ui.skin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.l.lolwishlist.R
import com.l.lolwishlist.data.model.Skin
import com.l.lolwishlist.databinding.ActivitySkinsBinding
import com.l.lolwishlist.ui.SkinAdapter
import com.l.lolwishlist.utils.hideKeyboard
import com.l.lolwishlist.utils.setSafeOnClickListener
import com.l.lolwishlist.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class SkinsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySkinsBinding

    private val viewModel: SkinsViewModel by viewModels()

    private val adapterSkin: SkinAdapter by lazy {
        SkinAdapter(
            glide = Glide.with(this),
            onSkinClick = ::onSkinClick
        )
    }

    private var shouldFilterSelectedSkins = false

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
        viewModel.skins.observe(this) { result ->
            adapterSkin.submitSkinList(result)
        }

        binding.searchView.onTextWatch = {
            viewModel.updateQuery(it)
        }
    }

    private fun setButtons() {
        binding.searchView.onCloseClick = {
            hideSearchView()
        }

        binding.searchView.onSearchClick = {
            binding.searchView.editText.hideKeyboard(this)
        }

        binding.fabSearch.setOnClickListener {
            showSearchView()
        }

        binding.selectedSkinsButton.setSafeOnClickListener {
            shouldFilterSelectedSkins = !shouldFilterSelectedSkins

            viewModel.shouldFilterSelected(shouldFilterSelectedSkins)

            if (shouldFilterSelectedSkins) {
                setWishlistSkinsView()
            }
            else {
                setAllSkinsViews()
            }
        }
    }

    private fun onSkinClick(skin: Skin, position: Int) {
        viewModel.updateSkinSelected(skin.id, !skin.selected).observe(this) { result ->
            result.handle(
                onSuccess = {
                    adapterSkin.notifySelected(position)
                },
                onError = {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun setAllSkinsViews() {
        binding.title.text = getString(R.string.all_skins)
        binding.selectedSkinsButton.elevation = 8f
        binding.selectedSkinsButton.translationZ = 8f
    }

    private fun setWishlistSkinsView() {
        binding.title.text = getString(R.string.wishlist)
        binding.selectedSkinsButton.elevation = 0f
        binding.selectedSkinsButton.translationZ = 0f
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
        binding.searchView.editText.text = null
    }

}