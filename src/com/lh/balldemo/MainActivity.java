package com.lh.balldemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class MainActivity extends Activity {

	public static final int 	MAX_GRAVITY = 14000;
	public static final String 	PREFS_FILE_NAME = "PrefsFile";
	public static final String 	GRAVITY_PREF_NAME = "GravityPrefs";

	private SensorManager 	mSensorManager;
	private BallView 		mView;
	private int				mCurrentGravity = MAX_GRAVITY / 2;

	private class BallView extends View implements SensorEventListener
	{
		private Canvas  		mCanvas = new Canvas();
		private float[] 		mRotationMatrix = new float[16];
		private Paint 			mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private PointF  		mCurrentPoint = new PointF(0,0);
		private int 			mBallDiameter = 20;
		public BallPhysics 		mBallPhysics;



		public BallView(Context context) {
			super(context);
			Matrix.setIdentityM(mRotationMatrix, 0);
			mBallPaint.setColor(Color.DKGRAY);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			mCanvas.drawColor(Color.WHITE);
			mBallPhysics = new BallPhysics(w, h, mBallDiameter);
			mBallPhysics.SetGravity(mCurrentGravity);
			mCurrentPoint = new PointF(w / 2, h / 2);
			super.onSizeChanged(w, h, oldw, oldh);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			synchronized (this) {
				if (mCurrentPoint != null && mRotationMatrix != null) {
					PointF nextPoint = mBallPhysics.GetNextPoint(mCurrentPoint, mRotationMatrix);

					canvas.drawCircle(nextPoint.x, nextPoint.y, mBallDiameter, mBallPaint);

					mCurrentPoint.set(nextPoint);
				}
			}
		}

		public void onSensorChanged(SensorEvent event) {

			synchronized (this) {            	
				if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
					SensorManager.getRotationMatrixFromVector(mRotationMatrix , event.values);
					invalidate();
				}
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}

	/**
	 * Initialize the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mCurrentGravity = GetGravityFromPrefs();

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mView = new BallView(this);
		setContentView(mView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mSensorManager.registerListener(mView,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
				SensorManager.SENSOR_DELAY_FASTEST);        
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(mView);
		super.onStop();
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
			showGravitySetting(mView);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
			gravitySetting.setProgress(mView.mBallPhysics.GetGravity());

			gravitySetting.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
					mView.mBallPhysics.SetGravity(progress);
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
