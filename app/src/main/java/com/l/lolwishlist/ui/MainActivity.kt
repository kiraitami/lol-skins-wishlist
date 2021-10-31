package com.l.lolwishlist.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.l.lolwishlist.api.DDragonService
import com.l.lolwishlist.data.DDragonRepository
import com.l.lolwishlist.databinding.ActivityMainBinding
import com.l.lolwishlist.model.ChampionsResponse
import com.l.lolwishlist.model.Result
import com.l.lolwishlist.ui.skin.AllSkinsFragment
import com.l.lolwishlist.ui.skin.WishlistFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var biding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(biding.root)

        setTabLayout()


    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            val service = DDragonService.create()
            val repo = DDragonRepository(service)


            var version = repo.getVersion()
                .flowOn(Dispatchers.IO)
                .map { it?.firstOrNull() }
                .first()!!


            val champions = repo.getChampionsBase(version)
                .flowOn(Dispatchers.IO)
                .map {
                    Log.d("_RES_", "main activity champions: $it")
                    it?.data?.values?.filterNot { c ->
                        c.id.equals("qiyana", true) ||
                                c.id.equals("lulu", true) ||
                                c.id.equals("lilia", true) ||
                                c.id.equals("senna", true) ||
                                c.id.equals("seraphine", true)
                    }
                }
                .flowOn(Dispatchers.Default)
                .firstOrNull()
                ?: return@launch

            Log.d("_RES_", "main activity champions mapped: $champions")


            repo.getChampionDetails(version, champions.random().id)
                .collectLatest {
                    Log.d("_RES_", "getChampionDetails: ${it.data}")
                }

        }
    }

    private fun setTabLayout() {
        biding.viewPager.adapter = SimpleTabAdapter(supportFragmentManager, lifecycle,
            arrayListOf(
                AllSkinsFragment.newInstance(),
                WishlistFragment.newInstance()
            )
        )

        val tabNames = listOf("All Skins", "Wishlist")

        TabLayoutMediator(biding.tabLayout, biding.viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }

}