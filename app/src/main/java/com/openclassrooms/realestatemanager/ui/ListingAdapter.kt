package com.openclassrooms.realestatemanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemListingListBinding
import com.openclassrooms.realestatemanager.models.Listing

class ListingAdapter(private val onItemClicked: (Listing) -> Unit) :
    ListAdapter<Listing, ListingAdapter.ViewHolder>(ListingDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListingListBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemListingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Listing) {
            binding.listTitle.text = item.type
            binding.listNeighborhood.text = item.address
            binding.listPrice.text = item.price.toString()
            Glide.with(itemView.context)
                .load(item.thumbnail)
                .placeholder(R.drawable.ic_placeholder_building)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.listPhoto)
        }
    }
}

object ListingDiffCallback : DiffUtil.ItemCallback<Listing>() {
    override fun areItemsTheSame(oldItem: Listing, newItem: Listing): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Listing, newItem: Listing): Boolean {
        return oldItem == newItem
    }
}