package com.zofers.zofers.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.zofers.zofers.R;

public class LoadingDialog extends AppCompatDialogFragment {
	public static final int CIRCULAR = 1;
	public static final int HORIZONTAL = 2;
	public static final int HORIZONTAL_INDETERMINATE = 3;

	private TextView messageText;
	private View circularProgressBar = null;
	private ProgressBar horizontalProgressBar = null;
	private int mode = CIRCULAR;
	private int max = 0;
	private int progress = 0;

	public LoadingDialog() {
		setCancelable(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_DayNight_Dialog);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_loading, container, false);
		messageText = rootView.findViewById(R.id.loading_view_message);
		circularProgressBar = rootView.findViewById(R.id.loading_view_progressbar);
		horizontalProgressBar = rootView.findViewById(R.id.loading_view_horizontal_progressbar);

		if (mode != CIRCULAR) {
			updateMode();
		}

		return rootView;
	}

	public void setMessage(CharSequence message) {
		messageText.setText(message);
	}

	public void setMessage(@StringRes int id) {
		messageText.setText(id);
	}

	public void setMode(int mode) {
		this.mode = mode;
		if (circularProgressBar != null) {
			updateMode();
		}
	}

	public void setMax(int max) {
		this.max = max;
		updateProgress();
	}

	public void setProgress(int progress) {
		this.progress = progress;
		updateProgress();
	}

	private void updateMode() {
		if (circularProgressBar != null) {
			circularProgressBar.setVisibility(mode == CIRCULAR ? View.VISIBLE : View.GONE);
			horizontalProgressBar.setVisibility(mode == CIRCULAR ? View.GONE : View.VISIBLE);
			horizontalProgressBar.setIndeterminate(mode == HORIZONTAL_INDETERMINATE);
			updateProgress();
		}
	}

	private void updateProgress() {
		if (horizontalProgressBar != null && mode == HORIZONTAL) {
			horizontalProgressBar.setMax(max);
			horizontalProgressBar.setProgress(progress);
		}
	}
}
