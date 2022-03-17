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
import com.openclassrooms.realestatemanager.models.ListingFull
import java.text.NumberFormat

class ListingAdapter(
    private val onItemClicked: (Int?) -> Unit
) :
    ListAdapter<ListingFull, ListingAdapter.ViewHolder>(ListingDiffCallback) {

    private val nFormat: NumberFormat = NumberFormat.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(
            ItemListingListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holder.itemView.setOnClickListener { onItemClicked(holder.bindingAdapterPosition) }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class ViewHolder(private val binding: ItemListingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListingFull) {

            binding.listTitle.text = item.listing.type
            binding.listNeighborhood.text = item.listing.neighborhood
            binding.listPrice.text = String.format(
                itemView.resources.getString(R.string.price_format),
                nFormat.format(item.listing.price)
            )

            Glide.with(itemView.context)
                .load(item.thumbnail?.uri)
                .placeholder(R.drawable.ic_placeholder_building)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.listPhoto)
        }
    }
}

object ListingDiffCallback : DiffUtil.ItemCallback<ListingFull>() {
    override fun areItemsTheSame(oldItem: ListingFull, newItem: ListingFull): Boolean {
        return oldItem.listing.id == newItem.listing.id
    }

    override fun areContentsTheSame(oldItem: ListingFull, newItem: ListingFull): Boolean {
        return oldItem == newItem
    }
}