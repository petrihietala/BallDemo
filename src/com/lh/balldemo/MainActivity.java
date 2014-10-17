package com.lh.balldemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class MainActivity extends Activity {

	public static final int 	MAX_GRAVITY = 18000;
	public static final String 	PREFS_FILE_NAME = "PrefsFile";
	public static final String 	GRAVITY_PREF_NAME = "GravityPrefs";

	private int				mCurrentGravity = MAX_GRAVITY / 2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mCurrentGravity = GetGravityFromPrefs();

		setContentView(R.layout.activity_main);	
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.set_gravity:
			showGravitySetting(this.getCurrentFocus());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void startGame(View view) {
		Intent intent = new Intent(this, GameActivity.class);
	    intent.putExtra(GameActivity.MSG_GRAVITY, mCurrentGravity);
	    startActivity(intent);
	}

	public void showGravitySetting(View v) {
		LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.gravity_setting, null);
		
		AlertDialog gravityDialog = new AlertDialog.Builder(this).create();
		gravityDialog.setTitle(R.string.set_gravity);
		gravityDialog.setView(view);
		gravityDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		gravityDialog.show();

		SeekBar gravitySetting = (SeekBar)gravityDialog.findViewById(R.id.gravityValue);
		if (gravitySetting != null)
		{
			gravitySetting.setMax(MAX_GRAVITY);
			gravitySetting.setProgress(GetGravityFromPrefs());

			gravitySetting.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
					SetGravityToPrefs(progress);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}
			});
		}		
	}

	private int GetGravityFromPrefs()
	{
		SharedPreferences settings = getSharedPreferences(PREFS_FILE_NAME, 0);
		return settings.getInt(GRAVITY_PREF_NAME, mCurrentGravity);
	}

	private void SetGravityToPrefs(int gravity)
	{
		SharedPreferences settings = getSharedPreferences(PREFS_FILE_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(GRAVITY_PREF_NAME, gravity);
		editor.commit();
	}


}
