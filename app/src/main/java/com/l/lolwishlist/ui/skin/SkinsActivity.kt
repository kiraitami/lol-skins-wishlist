package com.l.lolwishlist.ui.skin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.l.lolwishlist.R
import com.l.lolwishlist.data.remote.DDragonService
import com.l.lolwishlist.data.repository.DDragonRepository
import com.l.lolwishlist.databinding.ActivitySkinsBinding
import com.l.lolwishlist.ui.SkinAdapter
import com.l.lolwishlist.ui.SkinUIModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SkinsActivity : AppCompatActivity() {

    private lateinit var biding: ActivitySkinsBinding

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
        biding = ActivitySkinsBinding.inflate(layoutInflater)
        setContentView(biding.root)

        setRecycler()
        setObservers()
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

        biding.recyclerSkins.run {
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
    }

}