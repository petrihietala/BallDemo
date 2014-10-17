package com.lh.balldemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;

public class GameActivity extends Activity {

	public static final String  MSG_GRAVITY = "MSG_GRAVITY";

	private SensorManager 	mSensorManager;
	private GameView 		mView;
	private int				mCurrentGravity = 0;


	private class GameView extends View implements SensorEventListener
	{
		private float[] 		mRotationMatrix = new float[16];
		private Paint 			mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private Paint 			mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private PointF  		mCurrentPoint = new PointF(0,0);
		private int 			mBallDiameter = 20;
		public BallPhysics 		mBallPhysics;

		public GameView(Context context) {
			super(context);
			Matrix.setIdentityM(mRotationMatrix, 0);
			mBallPaint.setColor(Color.DKGRAY);
			mCenterPaint.setStyle(Paint.Style.STROKE);
			mCenterPaint.setStrokeWidth(2);
			mCenterPaint.setColor(Color.GREEN);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
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
					canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, 24, mCenterPaint);
					canvas.drawCircle(nextPoint.x, nextPoint.y, mBallDiameter, mBallPaint);

					mCurrentPoint.set(nextPoint);
				}
			}
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			synchronized (this) {            	
				if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
					SensorManager.getRotationMatrixFromVector(mRotationMatrix , event.values);
					invalidate();
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		Intent intent = getIntent();
		mCurrentGravity = intent.getIntExtra(MSG_GRAVITY, 0);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mView = new GameView(this);
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

}
