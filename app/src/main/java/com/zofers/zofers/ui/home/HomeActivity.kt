package com.zofers.zofers.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityHomeBinding
import com.zofers.zofers.ui.BackClickHandler
import com.zofers.zofers.ui.login.LoginActivity
import com.zofers.zofers.ui.notifications.ConversationsFragment
import com.zofers.zofers.ui.notifications.messenger.MessengerActivity
import com.zofers.zofers.ui.profile.ProfileFragment


class HomeActivity : BaseActivity() {

	companion object {
		const val EXTRA_OPENING_TAB = "e-o-t"
		const val EXTRA_CONV_ID = "conversationID" // this string used in backend

		const val OPENING_TAB_HOME = 0
		const val OPENING_TAB_NOTIFICATION = 1
		const val OPENING_TAB_PROFILE = 2

		const val TAG_HOME = "th"
		const val TAG_NOTIF = "tn"
		const val TAG_PROFILE = "TP"

	}

	private lateinit var viewModel: HomeViewModel
	private var currentFragment: Fragment? = null
	private lateinit var binding: ActivityHomeBinding
	private val tagSet = linkedSetOf<String>()

	override fun onCreate(savedInstanceState: Bundle?) {
		viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
		openFragmentByTag(TAG_HOME, true)

		binding.navView.setOnNavigationItemSelectedListener { menuItem ->
			if (viewModel.isLoggedOut() && menuItem.itemId != R.id.navigation_home) {
				LoginActivity.startForResult(this)
				false
			} else {
				val tag = getTagByMenuItemID(menuItem.itemId)
				openFragmentByTag(tag, true)
				true
			}
		}
		binding.navView.setOnNavigationItemReselectedListener {
			(currentFragment as? FeedFragment)?.scrollToTop()
		}
		if (intent.extras?.containsKey(EXTRA_CONV_ID) == true) {
			MessengerActivity.start(this, intent.getStringExtra(EXTRA_CONV_ID))
		} else if (intent.extras?.containsKey(EXTRA_OPENING_TAB) == true) {
			val tag = when (intent.getIntExtra(EXTRA_OPENING_TAB, 0)) {
				OPENING_TAB_HOME -> TAG_HOME
				OPENING_TAB_NOTIFICATION -> TAG_NOTIF
				OPENING_TAB_PROFILE -> TAG_PROFILE
				else -> TAG_HOME
			}
			openFragmentByTag(tag, true)
		}

	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		if (intent?.extras?.containsKey(EXTRA_CONV_ID) == true) {
			MessengerActivity.start(this, intent.getStringExtra(EXTRA_CONV_ID))
		}
	}

	private fun openFragmentByTag(tag: String, pushTag: Boolean) {
		var newFragment = supportFragmentManager.findFragmentByTag(tag)
		if (newFragment == null) {
			newFragment = createFragmentByTag(tag)
		}
		openFragment(fragment = newFragment, tag = tag)
		if (pushTag) {
			pushToTagSet(tag)
		}
	}

	override fun onBackPressed() {
		val handled = (currentFragment as? BackClickHandler)?.onBackClicked() ?: false
		if (handled) return

//		if (tagSet.isEmpty()) {
//			super.onBackPressed()
//			return
//		}
		tagSet.remove(tagSet.last())
		if (tagSet.isEmpty()) {
			if (currentFragment !is FeedFragment) {
				binding.navView.menu[0].isChecked = true
				openFragmentByTag(TAG_HOME, true)
			} else {
				super.onBackPressed()
			}
			return
		}

		binding.navView.menu[getIndexByTag(tagSet.last())].isChecked = true
		openFragmentByTag(tagSet.last(), false)
	}

	private fun openFragment(fragment: Fragment, tag: String) {
		val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
		if (!fragment.isAdded) {
			transaction.add(R.id.fragment_container, fragment, tag)
		}
		if (fragment.isHidden) {
			transaction.show(fragment)
		}
		currentFragment?.let {
			transaction.hide(it)
		}
		transaction.commit()
		currentFragment = fragment
	}

	private fun getTagByMenuItemID(itemID: Int): String {
		return when (itemID) {
			R.id.navigation_home -> TAG_HOME
			R.id.navigation_notifications -> TAG_NOTIF
			R.id.navigation_profile -> TAG_PROFILE
			else -> TAG_HOME
		}
	}

	private fun createFragmentByTag(tag: String): Fragment {
		return when (tag) {
			TAG_HOME -> FeedFragment()
			TAG_NOTIF -> ConversationsFragment()
			TAG_PROFILE -> ProfileFragment()
			else -> FeedFragment()
		}
	}

	private fun getIndexByTag(tag: String): Int {
		return when (tag) {
			TAG_HOME -> 0
			TAG_NOTIF -> 1
			TAG_PROFILE -> 2
			else -> 0
		}
	}

	private fun pushToTagSet(tag: String) {
		tagSet.remove(tag)
		tagSet.add(tag)
	}

}