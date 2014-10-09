package com.lh.balldemo;

import android.graphics.PointF;
import android.opengl.Matrix;

public class BallPhysics {

	private static final float[] UNIT_VECTOR_Z = {0,0,1,1};

	private float 	mAreaHeight = 0;
	private float 	mAreaWidth = 0;
	private float 	mBallDiameter = 0;
	private float   mVelocityX = 0;
	private float   mVelocityY = 0;
	private long 	mTimestamp = 0;
	private int 	mGravity = 0;


	public BallPhysics(float width, float height, float ballDiameter)
	{
		mAreaWidth = width;
		mAreaHeight = height;
		mBallDiameter = ballDiameter;
		mTimestamp = System.currentTimeMillis();
	}

	public void SetGravity(int gravity) {
		mGravity = gravity;
	}

	public int GetGravity() {
		return mGravity;
	}

	public PointF GetNextPoint(PointF currentPoint, float[] mRotationMatrix)
	{
		long newTimestamp = System.currentTimeMillis();

		float seconds = (float)(newTimestamp - mTimestamp) / 1000f;
		mTimestamp = newTimestamp;

		PointF nextPoint = new PointF();
		nextPoint.set(mAreaWidth / 2, mAreaHeight / 2); 

		if (currentPoint != null)
		{
			nextPoint.set(currentPoint);

			float[] rotatedVec = new float[4];

			Matrix.multiplyMV(rotatedVec, 0, mRotationMatrix, 0, UNIT_VECTOR_Z, 0);

			float acceleration = (float)mGravity * (1-(float)Math.pow(rotatedVec[2], 2));

			mVelocityX = mVelocityX -rotatedVec[0] * acceleration * seconds;
			mVelocityY = mVelocityY + rotatedVec[1] * acceleration * seconds;

			float dX = mVelocityX * seconds;
			float dY = mVelocityY * seconds;

			if (currentPoint.x - mBallDiameter + dX < 0)
			{
				dX = -(currentPoint.x - mBallDiameter);
				mVelocityX = 0;
			}
			else if (currentPoint.x + mBallDiameter + dX > mAreaWidth)
			{
				dX = mAreaWidth - currentPoint.x - mBallDiameter;
				mVelocityX = 0;
			}

			if (currentPoint.y - mBallDiameter + dY < 0 )
			{
				dY = -(currentPoint.y - mBallDiameter);
				mVelocityY = 0;
			}
			else if (currentPoint.y + dY + mBallDiameter > mAreaHeight)
			{
				dY = mAreaHeight - currentPoint.y - mBallDiameter;
				mVelocityY = 0;
			}

			nextPoint.offset(dX, dY);
		}

		return nextPoint;
	}
}
