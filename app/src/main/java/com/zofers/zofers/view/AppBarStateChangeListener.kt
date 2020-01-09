package com.zofers.zofers.view

import com.google.android.material.appbar.AppBarLayout

abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {

	private var mCurrentState = State.EXPANDED

	enum class State {
		EXPANDED,
		COLLAPSED,
	}

	override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
		if (verticalOffset + appBarLayout.totalScrollRange >= 100) {
			if (mCurrentState != State.EXPANDED) {
				onStateChanged(appBarLayout, State.EXPANDED)
				mCurrentState = State.EXPANDED
			}
		} else if (mCurrentState != State.COLLAPSED) {
			onStateChanged(appBarLayout, State.COLLAPSED)
			mCurrentState = State.COLLAPSED
		}
	}

	abstract fun onStateChanged(appBarLayout: AppBarLayout, state: State)
}