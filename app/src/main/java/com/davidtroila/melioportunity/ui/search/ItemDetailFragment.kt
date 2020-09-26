package com.davidtroila.melioportunity.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.davidtroila.melioportunity.R
import com.davidtroila.melioportunity.model.Item
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_item_detail.*


private const val ARG_PARAM1 = "item"

/**
 * A simple [Fragment] subclass.
 * Use the [ItemDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemDetailFragment : Fragment() {

    private var item : Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        arguments?.let {
            item = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_item_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Picasso.with(requireContext()).load(item?.imageURL).into(itemImageView)
        item?.let {item ->
            titleTextView.text = item.title
            priceTextView.text = getString(R.string.item_price, item.price)
            sellerTextView.text = item.city
            buyTextView.setOnClickListener {
                val url: String = item.permalink
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }
    }
}