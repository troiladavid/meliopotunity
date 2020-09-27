package com.davidtroila.melioportunity.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.davidtroila.melioportunity.R
import com.davidtroila.melioportunity.model.Item
import com.davidtroila.melioportunity.ui.main.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_item_detail.*
import timber.log.Timber


/**
 * Created by David Troila
 */
class ItemDetailFragment : Fragment() {

    private var item : Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        arguments?.let {
            item = it.getParcelable(ITEM_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_item_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("Screen loaded")
        item?.let { item ->
            Picasso.with(requireContext()).load(item.imageURL).into(itemImageView)
            titleTextView.text = item.title
            priceTextView.text = getString(R.string.item_price, item.price)
            sellerTextView.text = item.city
            buyTextView.setOnClickListener {
                Timber.d("Navigating to ML web")
                val url: String = item.permalink
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                (activity as MainActivity?)!!.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object{
        const val ITEM_ARG = "item"
    }
}