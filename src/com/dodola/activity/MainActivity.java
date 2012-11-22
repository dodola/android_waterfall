package com.dodola.activity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dodola.R;

public class MainActivity extends ActivityGroup {
	public LinearLayout	newsContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		initView();
	}

	private void initView() {

		newsContent = (LinearLayout) findViewById(R.id.news_content);
		addView();

		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

	}

	public void addView() {
		newsContent.removeAllViews();
		Intent intent = new Intent();

		intent.setClass(MainActivity.this, ContentActivity.class);
		newsContent.addView(getLocalActivityManager().startActivity("duitang", intent)
			.getDecorView());
		newsContent.setMinimumWidth(getWindowManager().getDefaultDisplay().getWidth());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 1, R.string.refresh);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		addView();
		return true;
	}
}
