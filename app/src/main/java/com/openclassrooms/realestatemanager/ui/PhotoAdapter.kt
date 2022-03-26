package com.openclassrooms.realestatemanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemPhotoBinding
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel

class PhotoAdapter(
    private val viewModel: MainViewModel,
    private val editMode: Boolean,
    private val onItemClicked: (Photo) -> Unit
) :
    ListAdapter<Photo, PhotoAdapter.ViewHolder>(PhotoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener { onItemClicked(current) }
        holder.bind(current)
    }

    inner class ViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Photo) {

            Glide.with(itemView.context)
                .load(item.uri)
                .placeholder(if (item.uri == null) R.drawable.ic_add_a_photo else R.drawable.ic_placeholder_building)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.image)
            binding.title.text = item.title
            binding.title.visibility = if (item.title != "") View.VISIBLE else View.INVISIBLE

            viewModel.currentListing.value?.thumbnailId?.let {
                binding.cardView.isChecked = editMode && item.id == it && it != 0L
            }
        }
    }
}

object PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }

}
