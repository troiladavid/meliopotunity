package com.davidtroila.melioportunity.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.davidtroila.melioportunity.R
import com.squareup.picasso.Picasso


class ItemAdapter(
    private val layoutManager: GridLayoutManager? = null,
    private val context: Context
): ListAdapter<Item, ItemAdapter.ViewHolder>(ItemDiffCallback()) {

    var mItems = mutableListOf<Item>()

    fun addMoreContacts(newContacts: List<Item>) {
        mItems.addAll(mItems.size , newContacts)
        submitList(mItems)
    }

    fun submitResult(result: List<Item>){
        submitList(result)
    }

override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Picasso.with(context).load(item.imageURL).into(holder.itemImage)
        holder.itemTitle.text = item.title
        holder.itemPrice.text = context.getString(R.string.item_price, item.price)
        holder.itemCondition?.text = if (item.state == "new") context.getString(R.string.item_condition_new) else context.getString(
            R.string.item_condition_used
        )
        holder.container.setOnClickListener{ Navigation.findNavController(it).navigate(
            R.id.action_searchResultFragment_to_itemDetailFragment, bundleOf(
                "item" to item
            )
        )}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = if (viewType == ViewType.LIST.ordinal) R.layout.item_view_holder else R.layout.grid_item_view_holder
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val container: View = itemView.findViewById(R.id.itemContainer)
        val itemTitle: TextView = itemView.findViewById(R.id.titleTextView)
        val itemPrice: TextView = itemView.findViewById(R.id.priceTextView)
        val itemCondition: TextView? = itemView.findViewById(R.id.conditionTextView)
        val itemImage: ImageView = itemView.findViewById(R.id.itemImageView)
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (layoutManager?.spanCount == 1) ViewType.LIST.ordinal
        else ViewType.GRID.ordinal
    }

    enum class ViewType {
        LIST,
        GRID
    }

}