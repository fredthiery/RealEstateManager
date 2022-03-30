package com.openclassrooms.realestatemanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemListingListBinding
import com.openclassrooms.realestatemanager.models.ListingFull
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import java.text.NumberFormat

class ListingAdapter(
    private val onItemClicked: (Long) -> Unit,
    private val viewModel: MainViewModel,
    private val slidingPaneLayout: SlidingPaneLayout
) :
    ListAdapter<ListingFull, ListingAdapter.ViewHolder>(ListingDiffCallback) {

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
        holder.itemView.setOnClickListener {
            onItemClicked(current.listing.id)
            slidingPaneLayout.openPane()
        }
        holder.bind(current)
    }

    inner class ViewHolder(private val binding: ItemListingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListingFull) {
            itemView.isSelected =
                item.listing.id == viewModel.currentListing.value?.id && !slidingPaneLayout.isSlideable

            binding.listTitle.text = item.listing.type
            binding.listNeighborhood.text = item.listing.neighborhood

            // If property isn't sold, display price in dollar or euro
            item.listing.sellDate?.let {
                binding.listPrice.text = itemView.context.getString(R.string.sold)
            } ?: item.listing.price?.let {
                itemView.context?.let { context ->
                    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                    when (preferences.getString("currency", "dollar")) {
                        "euro" -> binding.listPrice.text = String.format(
                            context.resources.getString(R.string.price_format_euro),
                            nFormat.format(Utils.convertDollarToEuro(it))
                        )
                        else -> binding.listPrice.text = String.format(
                            context.resources.getString(R.string.price_format_dollar),
                            nFormat.format(it)
                        )
                    }
                }
            }

            // If thumbnail is set, load it, otherwise load the first photo
            val thumbnail = item.thumbnail ?: item.photos.getOrNull(0)

            Glide.with(itemView.context)
                .load(thumbnail?.uri)
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