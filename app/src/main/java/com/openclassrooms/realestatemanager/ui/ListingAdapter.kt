package com.openclassrooms.realestatemanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemListingListBinding
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import java.text.NumberFormat

class ListingAdapter(
    private val viewModel: MainViewModel,
    private val slidingPaneLayout: SlidingPaneLayout
) :
    ListAdapter<Listing, ListingAdapter.ViewHolder>(ListingDiffCallback) {

    private val nFormat: NumberFormat = NumberFormat.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemListingListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        val view = holder.itemView

        // Is this the selected item ?
        view.isSelected = ( viewModel.selectedItem == position ) && !slidingPaneLayout.isSlideable

        view.setOnClickListener {
            // Reset selected item
            notifyItemChanged(viewModel.selectedItem)
            viewModel.selectedItem = holder.bindingAdapterPosition
            // Highlight newly selected item
            notifyItemChanged(position)
            slidingPaneLayout.openPane()
        }
        holder.bind(current)
    }

    inner class ViewHolder(private val binding: ItemListingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Listing) {

            binding.listTitle.text = item.type
            binding.listNeighborhood.text = item.neighborhood
            binding.listPrice.text = String.format(
                itemView.resources.getString(R.string.price_format),
                nFormat.format(item.price)
            )

            Glide.with(itemView.context)
                .load(item.thumbnail?.uri)
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