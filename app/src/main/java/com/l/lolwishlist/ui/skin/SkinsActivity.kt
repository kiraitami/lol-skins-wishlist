package com.l.lolwishlist.ui.skin

import android.graphics.Color
import android.os.Bundle
import android.util.LayoutDirection
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.l.lolwishlist.R
import com.l.lolwishlist.databinding.ActivitySkinsBinding
import com.l.lolwishlist.model.Skin
import com.l.lolwishlist.utils.hideKeyboard
import com.l.lolwishlist.utils.setSafeOnClickListener
import com.l.lolwishlist.utils.showKeyboard
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SkinsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySkinsBinding

    private val viewModel: SkinsViewModel by viewModels()

    private val adapterSkin: SkinAdapter by lazy {
        SkinAdapter(
            glide = Glide.with(this),
            onCheckClick = ::onCheckClick,
            onSkinClick = ::onSkinClick
        )
    }

    private var isWishlistVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkinsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecycler()
        setObservers()
        setButtons()
    }

    override fun onBackPressed() {
        when {
            binding.searchView.isVisible -> {
                hideSearchView()
            }
            isWishlistVisible -> {
                showAllSkins()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun setRecycler() {
        val lm =  FlexboxLayoutManager(this).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            flexWrap = FlexWrap.WRAP
        }

        binding.recyclerSkins.run {
            layoutManager = lm
            adapter = adapterSkin
            itemAnimator = null
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
            if (isWishlistVisible) {
                showAllSkins()
            }
            else {
                showWishlistSkins()
            }
        }
    }

    private fun onCheckClick(skin: Skin, position: Int) {
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

    private fun onSkinClick(skin: Skin, imageView: ImageView) {
        StfalconImageViewer.Builder(this, listOf(skin.fullImageUrl)) { view, image ->
            Glide.with(this).load(image).into(view)
        }

            .withBackgroundColor(Color.BLACK)
            .allowZooming(true)
            .allowSwipeToDismiss(true)
            .withTransitionFrom(imageView)
            .withOverlayView(TextView(this).apply {
                text = skin.name
                textSize = 18f
                setTextColor(Color.LTGRAY)
                gravity = GravityCompat.getAbsoluteGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, LayoutDirection.INHERIT)
                requestLayout()
            })
            .show()

    }

    private fun showAllSkins() {
        isWishlistVisible = false
        viewModel.shouldFilterSelected(isWishlistVisible)
        binding.title.text = getString(R.string.all_skins)
    }

    private fun showWishlistSkins() {
        isWishlistVisible = true
        viewModel.shouldFilterSelected(isWishlistVisible)
        binding.title.text = getString(R.string.wishlist)
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