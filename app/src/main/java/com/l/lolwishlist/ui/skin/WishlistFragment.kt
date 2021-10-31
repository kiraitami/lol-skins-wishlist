package com.l.lolwishlist.ui.skin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.l.lolwishlist.R

class WishlistFragment : Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            WishlistFragment().apply {

            }
    }
}