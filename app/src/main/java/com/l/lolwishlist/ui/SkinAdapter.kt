package com.l.lolwishlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.text.PrecomputedTextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.l.lolwishlist.R
import com.l.lolwishlist.data.model.Skin
import com.l.lolwishlist.databinding.AdapterSkinBinding
import com.l.lolwishlist.databinding.AdapterSkinSeparatorBinding


class SkinAdapter(
    private val glide: RequestManager,
    private val onSkinClick: (skin: Skin, imageView: ImageView) -> Unit,
    private val onCheckClick: (skin: Skin, position: Int) -> Unit,
) : ListAdapter<SkinUIModel, RecyclerView.ViewHolder>(DIFF) {

    class SkinViewHolder(
        private val glide: RequestManager,
        private val binding: AdapterSkinBinding,
        private val onSkinClick: (skin: Skin, imageView: ImageView) -> Unit,
        private val onCheckClick: (skin: Skin, position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(skin: Skin, position: Int) {
            binding.run {
                glide.load(skin.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fitCenter()
                    .override(480, 720)
                    .into(image)

                selectedIcon.setOnClickListener {
                    onCheckClick(skin, position)
                }

                itemView.setOnClickListener {
                    onSkinClick(skin, image)
                }

                selectedIcon.isChecked = skin.selected
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                glide: RequestManager,
                onSkinClick: (skin: Skin, imageView: ImageView) -> Unit,
                onCheckClick: (skin: Skin, position: Int) -> Unit): SkinViewHolder =
                    SkinViewHolder(
                        glide,
                        AdapterSkinBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                        onSkinClick,
                        onCheckClick
                    )
        }
    }

    class SkinSeparatorViewHolder(
        private val binding: AdapterSkinSeparatorBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(championName: String) {
            binding.run {
                title.setTextFuture(PrecomputedTextCompat.getTextFuture(
                    championName,
                    title.textMetricsParamsCompat,
                    null
                ))
            }
        }

        companion object {
            fun create(parent: ViewGroup): SkinSeparatorViewHolder =
                SkinSeparatorViewHolder(
                    AdapterSkinSeparatorBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.adapter_skin -> SkinViewHolder.create(parent, glide, onSkinClick, onCheckClick)
            R.layout.adapter_skin_separator -> SkinSeparatorViewHolder.create(parent)
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is SkinViewHolder -> holder.bind((getItem(position) as SkinUIModel.SkinItem).skin, position)
            is SkinSeparatorViewHolder -> holder.bind((getItem(position) as SkinUIModel.Separator).title)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is SkinUIModel.SkinItem -> R.layout.adapter_skin
            is SkinUIModel.Separator -> R.layout.adapter_skin_separator
            else -> throw IllegalStateException()
        }
    }

    fun submitSkinList(list: List<Skin>?) {
        val uiSkinModelList = mutableListOf<SkinUIModel>()

        list?.groupBy { it.championName }
            ?.forEach { (s, l) ->
                uiSkinModelList.add(SkinUIModel.Separator(s))
                uiSkinModelList.addAll(l.map { SkinUIModel.SkinItem(it) })
            }
        super.submitList(uiSkinModelList)
    }

    fun notifySelected(position: Int) {
        (getItem(position) as? SkinUIModel.SkinItem)?.let {
            it.skin.selected = !it.skin.selected
        }
        notifyItemChanged(position)
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<SkinUIModel>() {
            override fun areItemsTheSame(oldItem: SkinUIModel, newItem: SkinUIModel): Boolean {
                val isSameItem = oldItem is SkinUIModel.SkinItem
                        && newItem is SkinUIModel.SkinItem
                        && oldItem.skin.id == newItem.skin.id

                val isSameSeparator = oldItem is SkinUIModel.Separator
                        && newItem is SkinUIModel.Separator
                        && oldItem.title == newItem.title

                return isSameItem || isSameSeparator
            }

            override fun areContentsTheSame(oldItem: SkinUIModel, newItem: SkinUIModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}

sealed class SkinUIModel {
    class SkinItem(val skin: Skin) : SkinUIModel()
    class Separator(val title: String) : SkinUIModel()
}