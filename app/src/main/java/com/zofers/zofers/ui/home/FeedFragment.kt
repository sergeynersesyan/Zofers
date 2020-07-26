package com.zofers.zofers.ui.home


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.zofers.zofers.R
import com.zofers.zofers.adapter.OffersAdapter
import com.zofers.zofers.databinding.FragmentFeedBinding
import com.zofers.zofers.model.Offer
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.ui.create.CreateOfferActivity
import com.zofers.zofers.ui.offer.OfferActivity

/**
 * A simple [Fragment] subclass.
 */
class FeedFragment : Fragment(), SearchView.OnQueryTextListener, View.OnClickListener {

	private lateinit var adapter: OffersAdapter
	private lateinit var viewModel: FeedViewModel

	private lateinit var searchView: SearchView
	private lateinit var binding: FragmentFeedBinding


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		activity?.title = ""
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

		setupViewModel()


		val root = inflater.inflate(R.layout.fragment_feed, container, false)
		binding = DataBindingUtil.bind(root)!!

		adapter = OffersAdapter()
		binding.offersRecyclerView.layoutManager = LinearLayoutManager(context)
		binding.offersRecyclerView.adapter = adapter

		adapter.setListener(object : OffersAdapter.Listener {
			override fun onItemClick(offer: Offer) {
				val intent = Intent(context, OfferActivity::class.java)
				intent.putExtra(OfferActivity.EXTRA_OFFER, offer)
				startActivity(intent)
			}

			override fun loadMore() {
				viewModel.loadMore()
			}
		})

		binding.swipeRefresh.setOnRefreshListener {
			viewModel.loadFeed()
		}
		binding.fab.setOnClickListener(this)

		return root
	}

	private fun setupViewModel() {
		val tm = activity?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
		viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
		viewModel.init(tm.networkCountryIso)
		viewModel.offersList.observe(viewLifecycleOwner, Observer<List<Offer>> { offers ->
			adapter.setItems(offers)
		})
		viewModel.state.observe(viewLifecycleOwner, Observer<Int> { state ->
			when (state) {
				States.LOADING -> binding.swipeRefresh.isRefreshing = true
				States.ERROR -> MessageHelper.showErrorToast(context, "")
				States.FAIL -> MessageHelper.showNoConnectionToast(context)
				States.NONE -> binding.swipeRefresh.isRefreshing = false
			}
		})
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.menu_search, menu)

		val searchItem = menu.findItem(R.id.action_search)
		searchView = MenuItemCompat.getActionView(searchItem) as SearchView
		searchView.setMaxWidth(Integer.MAX_VALUE);
		//        EditText searchEditText = searchView.findViewById(R.id.search_src_text);
		//        searchItem.expandActionView();

		searchView.setOnQueryTextListener(this)
		searchView.isIconified = false
		searchView.gravity = Gravity.START
		searchView.queryHint = context?.getString(R.string.action_search)
		searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
			override fun onMenuItemActionExpand(item: MenuItem): Boolean {
				return false
			}

			override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
				activity?.onBackPressed()
				return true
			}
		})
		searchView.clearFocus()

		val closeButton = searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn)
		closeButton?.setOnClickListener(this)
	}


	override fun onQueryTextSubmit(s: String?): Boolean {
		viewModel.search(s)
		return false
	}

	override fun onQueryTextChange(s: String): Boolean {
		if (TextUtils.isEmpty(s)) {
			onQueryTextSubmit(null)
			return true
		}
		return false
	}


	override fun onClick(v: View) {
		when (v.id) {
			R.id.fab -> {
				val intent1 = Intent(activity, CreateOfferActivity::class.java)
				startActivity(intent1)
			}
			androidx.appcompat.R.id.search_close_btn -> {
				searchView.setQuery("", false)
				searchView.clearFocus()
			}
		}
	}

}
