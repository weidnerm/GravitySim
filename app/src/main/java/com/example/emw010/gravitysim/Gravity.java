package com.example.emw010.gravitysim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
//import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.DisplayMetrics;
import android.view.*;
import android.support.v4.widget.ExploreByTouchHelper;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;


//public class Gravity extends AppCompatActivity
public class Gravity extends Activity
{
    private SensorManager mSensorManager = null;
    private GravityView mGravityView;
    private DisplayMetrics mDisplayMetrics;

    /**
     * Constant representing the diameter in pixels of the planet object circles
     * for the display.
     */
    final public int PLANETSIZE = 5;
    /**
     * Number of history points to maintain in the simulation.  Determines
     * the length of the history tail.
     */
    final public int MAX_OBJECT_HISTORY = 10;
    /**
     * One Astronomical unit represented in meters.
     */
    final public double ONE_AU_METERS = 149.6E+9;// meters
    /**
     * Scale factor that is used to scale the actual distances in meters to the
     * display coordinates in pixels.
     */
    double mDisplayScale = ONE_AU_METERS / 40; // start with xx pixels per AU
    double mDisplayScaleMin = mDisplayScale/10000; // start with xx pixels per AU
    double mDisplayScaleMax = mDisplayScale*10000; // start with xx pixels per AU
    /**
     * History of display coordinates for all the gravity objects.
     */
    DisplayPoint myGravityObjectCoordHistory[][];
    /**
     * History of display coordinates for all the gravity objects.
     */
    DisplayPoint myGravityObjectCoordHistoryRight[][];
    /**
     * History of display coordinates for all the gravity objects.
     */
    DisplayPoint myGravityObjectCoordHistoryLeft[][];
    /**
     * Array of points representing the point of the vertical projection onto the
     * plane of the ecliptic.
     */
    DisplayPoint myGravityObjectShadowRight[];
    /**
     * Array of points representing the point of the vertical projection onto the
     * plane of the ecliptic.
     */
    DisplayPoint myGravityObjectShadowLeft[];
    /**
     * Positions of the gravity objects in 3D space with history.
     */
    VectorPoint myGravityObjectVectorHistory[][];
    /**
     * Array of names of gravity objects.
     */
    public static String myGravityObjectNames[];
    /**
     * Tracks the current number of history entries present in the arrays
     */
    int myGravityObjectCoordHistoryDepth = 0;
    /**
     * Number of gravity objects tracked by the simulation.
     */
    int mNumGravityObjects = 0;
    /**
     * Tracks the screen center.  Used as an offset for the display coordinates
     * to center the simulation.
     */
    DisplayPoint mDisplayOriginOffset;
    /**
     * Index of the object to be displayed at the screen center.
     */
    int mCenterObjectIndex = -1; // -1 = center at 0,0
    /**
     * Time interval in seconds of the computation iteration time.
     * This time is the duration of the iteration associated with
     * computing new speed change based on acceleration, and distance
     * change based on speed.
     */
    public double mComputationTimeInterval = (1*60*60);
    /**
     * Number of seconds in a day.
     */
    final public double ONE_DAY_TIME_INTERVAL = (60*60*24);
    /**
     * Number of iterations of the calculation per display update.
     */
    int mNumIterationsPerDisplayPoint = 24;
    /**
     * Count of the number of times that the current GravityView.OnDraw method
     * is called.
     */
    int mOnDrawCount = 0;
    long mFirstOnDrawTimestamp = 0;
    double mViewingAngle = 45; // degrees
    int mTouchCount = 0;
    public String mDebugText_1,mDebugText_2,mDebugText_3,mDebugText_4 = null;
    public double mFocalPointDistanceMain3DAxis = 1600;  // distance in display pixels to the observer
    public double mFocalPointDistance3DEyeSeparation = mFocalPointDistanceMain3DAxis/10;
    public boolean mExcludeGreenColor = false;  // allows green to be excluded for displays that bleed green into red.
    public boolean mAccelEnabled = true;
    public boolean m3dDisplay = false;
    public boolean mSeeGrid = false;
    public SolarSystem mySolarSystem;
    float mDisplayScaleFactor;
    float mTextSize;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mySolarSystem = new SolarSystem();

        initGravityEngine(); // init the App side of the simulation.

        // instantiate our simulation view and set it as the activity's content
        mGravityView = new GravityView(this);
        setContentView(mGravityView);
        mDisplayOriginOffset = new DisplayPoint();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // pref menu item
        Intent prefsIntent = new Intent(getApplicationContext(),
                GravityPreferencesActivity.class);
        MenuItem preferences = menu.findItem(R.id.settings);
        preferences.setIntent(prefsIntent);

        // object list menu item
        Intent objectListIntent = new Intent(getApplicationContext(),
                GravityObjectListActivity.class);
        MenuItem objectList = menu.findItem(R.id.viewlist);
        objectList.setIntent(objectListIntent);

        // object list menu item
        Intent aboutIntent = new Intent(getApplicationContext(),
                GravityAboutActivity.class);
        MenuItem aboutMenu = menu.findItem(R.id.about);
        aboutMenu.setIntent(aboutIntent);

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
    //            case R.id.settings:
    //            	return true;
    //            case R.id.viewlist:
    //                return true;
    //            case R.id.about:
    //                showHelp();
    //                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





    class GravityView extends View implements SensorEventListener
    {
        private Sensor mAccelerometer = null;
        private RectF mPlanetRect;
        private Paint mPlanetPaint;
        private Paint mShadowPaint;
        private Paint mDistanceScalePaint;
        private Paint mTimeScalePaint;
        private Paint mAnglePaint;
        private Paint mElapsedTimePaint;
        private float mAccelerometerX,mAccelerometerY,mAccelerometerZ;
        private float mTouchX;
        private float mTouchY;
        private float mTouchLastHandledX;
        private float mTouchLastHandledY;
        private double mOldDisplayScale = 1.000;
        final int CLICK_DISTANCE_PIXELS=20;
        private int mTouchEventInProgress = 0;
        private int mHandle1TouchEventsCount = 0;
        private float mLastAccelerometerX,mLastAccelerometerY,mLastAccelerometerZ = 10000; // some invalid value
        public Paint mLeft3DPaint,mRight3DPaint;
        public Paint mLeft3DShadowPaint,mRight3DShadowPaint;
        public double mLastScaleLength = 1;
        public GravityAccessHelper mGravityAccessHelper;

        private double mElapsedTime = 0;
        private double mElapsedTimeDisplayed = 0;
        Rect mElapsedTimeTextBounds = new Rect();

        private double mSimulationRate = 0;
        Rect mSimulationRateBounds = new Rect();

        private double mComputationIntervalDisplayed = 0;
        Rect mComputationIntervalBounds = new Rect();

        private int mDistanceScaleBarLength = 0;
        Rect mDistanceScaleBarBounds = new Rect();

        private double mDistanceScaleAuForDisplay = 0;
        Rect mDistanceScaleTextBounds = new Rect();

        /**
         * Custom view that is used to represent the gravity simulation graphical
         * output area.
         *
         * @param context Context for the view.
         */
        public GravityView(Context context)
        {
            super(context);
            if ( mSensorManager != null )
            {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }

            mDisplayMetrics = new DisplayMetrics();
            mDisplayMetrics.widthPixels = getWidth();
            mDisplayMetrics.heightPixels = getHeight();


            mPlanetRect = new RectF();

            // set up paint for the planet object
            mPlanetPaint = new Paint();
            mPlanetPaint.setColor(Color.CYAN);

            mDistanceScalePaint = new Paint();
            mDistanceScalePaint.setColor(Color.YELLOW);

            mTimeScalePaint = new Paint();
            mTimeScalePaint.setColor(Color.YELLOW);

            mAnglePaint = new Paint();
            mAnglePaint.setColor(Color.YELLOW);

            mElapsedTimePaint = new Paint();
            mElapsedTimePaint.setColor(Color.YELLOW);


            mLeft3DPaint = new Paint();
            mLeft3DPaint.setColor(0xffff0000);
            mLeft3DPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

            mRight3DPaint = new Paint();
            mRight3DPaint.setColor(0xff00ffff);
            mRight3DPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

            // set up paint for the dashed line drawn to plane of ecliptic
            mLeft3DShadowPaint = new Paint();
            mLeft3DShadowPaint.setColor(0xff800000);

            DashPathEffect dashPath = new DashPathEffect(new float[] { 2, 2 }, 1);
            mLeft3DShadowPaint.setPathEffect(dashPath);
            mLeft3DShadowPaint.setStrokeWidth(1);

            mRight3DShadowPaint = new Paint();
            mRight3DShadowPaint.setColor(0xff008080);

            dashPath = new DashPathEffect(new float[] { 2, 2 }, 1);
            mRight3DShadowPaint.setPathEffect(dashPath);
            mRight3DShadowPaint.setStrokeWidth(1);

            mShadowPaint = new Paint();
            mShadowPaint.setColor(Color.DKGRAY);
            dashPath = new DashPathEffect(new float[] { 2, 2 }, 1);
            mShadowPaint.setPathEffect(dashPath);
            mShadowPaint.setStrokeWidth(1);

            if (( mAccelerometer != null) && (mSensorManager != null ))
            {
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            }

            // Set up accessibility helper class.
            mGravityAccessHelper = new GravityAccessHelper(this);
            ViewCompat.setAccessibilityDelegate(this, mGravityAccessHelper);
            setFocusable(true);

        }

        @Override
        protected void onDraw(Canvas canvas)
        {

			/*
			 * draw the background
			 */

            mDisplayMetrics.widthPixels = getWidth();
            mDisplayMetrics.heightPixels = getHeight();

            canvas.drawRGB(0, 0, 0); // clear the screen. all black.

            displayDistanceScale(canvas);
            displayTheViewingAngle(canvas);
            displayTheTimeScale(canvas);
            displayElapsedTime(canvas);
            displayGrid(canvas);
            displayDebugInformation(canvas);

            // Retrieve the size of the drawing area
            mDisplayOriginOffset.x = mDisplayMetrics.widthPixels / 2;
            mDisplayOriginOffset.y = mDisplayMetrics.heightPixels / 2;

            // set up some limits (they are past the actual display edges. just for sanity)
            // should height/widths be swapped?
            final int extremeLeft = mDisplayOriginOffset.x -mDisplayMetrics.heightPixels;
            final int extremeRight = mDisplayOriginOffset.x + mDisplayMetrics.heightPixels;
            final int extremeTop = mDisplayOriginOffset.y -mDisplayMetrics.widthPixels;
            final int extremeBottom = mDisplayOriginOffset.y + mDisplayMetrics.widthPixels;

            int objectIndex;



            if ( mExcludeGreenColor )
            {
                mRight3DPaint.setColor(0xff0000ff);
            }
            else
            {
                mRight3DPaint.setColor(0xff00ffff);
            }

            // update paints to scale to display
            mDisplayScaleFactor = (float)mDisplayMetrics.widthPixels/480.0f;
            mTextSize = 10*mDisplayScaleFactor;
            mDistanceScalePaint.setTextSize( mTextSize );
            mTimeScalePaint.setTextSize( mTextSize );
            mAnglePaint.setTextSize( mTextSize );
            mElapsedTimePaint.setTextSize( mTextSize );
            mLeft3DPaint.setTextSize( mTextSize );
            mRight3DPaint.setTextSize( mTextSize );


            // for(objectIndex=0 ; objectIndex<numObjects ; objectIndex++)
            for (objectIndex = mNumGravityObjects - 1; objectIndex >= 0; objectIndex--)
            {

                float objectX = mDisplayOriginOffset.x + myGravityObjectCoordHistoryLeft[objectIndex][0].x;
                float objectXRightEye = mDisplayOriginOffset.x + myGravityObjectCoordHistoryRight[objectIndex][0].x;
                float objectY = mDisplayOriginOffset.y + myGravityObjectCoordHistoryLeft[objectIndex][0].y;

                if (    ( extremeLeft < objectY ) &&
                        ( objectY < extremeRight ) &&
                        ( extremeTop < objectX ) &&
                        ( objectX < extremeBottom ) )        // only try to draw objects that are close to being on the screen.
                {
                    // Draw the textual name of the object.
                    canvas.drawText(myGravityObjectNames[objectIndex],objectX + 2, objectY - 2, mLeft3DPaint);
                    // Draw the textual name of the object.
                    canvas.drawText(myGravityObjectNames[objectIndex],objectXRightEye + 2, objectY - 2, mRight3DPaint);

                    final int planetRadius = (int)(2*mDisplayScaleFactor);
                    // Draw the Planet
                    mPlanetRect.left = objectX - planetRadius;
                    mPlanetRect.top = objectY - planetRadius;
                    mPlanetRect.right = objectX + planetRadius;
                    mPlanetRect.bottom = objectY + planetRadius;
                    canvas.drawOval(mPlanetRect, mLeft3DPaint);
                    // Draw the Planet
                    mPlanetRect.left = objectXRightEye - planetRadius;
                    mPlanetRect.top = objectY - planetRadius;
                    mPlanetRect.right = objectXRightEye + planetRadius;
                    mPlanetRect.bottom = objectY + planetRadius;
                    canvas.drawOval(mPlanetRect, mRight3DPaint);

                    // Draw the history "tail"
                    if ( myGravityObjectCoordHistoryDepth >= 2 )
                    {
                        for(int histIndex=0; histIndex<myGravityObjectCoordHistoryDepth-1 ; histIndex++)
                        {
                            int leftX0 = mDisplayOriginOffset.x + myGravityObjectCoordHistoryLeft[objectIndex][histIndex].x ;
                            int leftY0 = mDisplayOriginOffset.y + myGravityObjectCoordHistoryLeft[objectIndex][histIndex].y ;
                            int leftX1 = mDisplayOriginOffset.x + myGravityObjectCoordHistoryLeft[objectIndex][histIndex+1].x;
                            int leftY1 = mDisplayOriginOffset.y + myGravityObjectCoordHistoryLeft[objectIndex][histIndex+1].y;

                            int rightX0 = mDisplayOriginOffset.x + myGravityObjectCoordHistoryRight[objectIndex][histIndex].x;
                            int rightY0 = mDisplayOriginOffset.y + myGravityObjectCoordHistoryRight[objectIndex][histIndex].y;
                            int rightX1 = mDisplayOriginOffset.x + myGravityObjectCoordHistoryRight[objectIndex][histIndex+1].x;
                            int rightY1 = mDisplayOriginOffset.y + myGravityObjectCoordHistoryRight[objectIndex][histIndex+1].y;

                            if (
                               (extremeTop  < leftX0  ) && (leftX0  < extremeBottom ) &&
                               (extremeLeft < leftY0  ) && (leftY0  < extremeRight ) &&
                               (extremeTop  < leftX1  ) && (leftX1  < extremeBottom ) &&
                               (extremeLeft < leftY1  ) && (leftY1  < extremeRight ) &&

                               (extremeTop  < rightX0 ) && (rightX0 < extremeBottom ) &&
                               (extremeLeft < rightY0 ) && (rightY0 < extremeRight ) &&
                               (extremeTop  < rightX1 ) && (rightX1 < extremeBottom ) &&
                               (extremeLeft < rightY1 ) && (rightY1 < extremeRight ) )
                            {
                                canvas.drawLine(leftX0,
                                        leftY0,
                                        leftX1,
                                        leftY1,
                                        mLeft3DPaint);
                                canvas.drawLine(rightX0,
                                        rightY0,
                                        rightX1,
                                        rightY1,
                                        mRight3DPaint);
                            }

                        }
                    }

                    // Draw the line to the shadow position.
                    {
                        float shadowX = mDisplayOriginOffset.x + myGravityObjectShadowLeft[objectIndex].x;
                        float shadowY = mDisplayOriginOffset.y + myGravityObjectShadowLeft[objectIndex].y;

                        if      (shadowX > extremeBottom ) { shadowX = extremeBottom; }
                        else if (shadowX < extremeTop)     { shadowX = extremeTop; }

                        canvas.drawLine(shadowX,shadowY, objectX, objectY, mLeft3DShadowPaint);
                    }
                    {
                        float shadowX = mDisplayOriginOffset.x + myGravityObjectShadowRight[objectIndex].x;
                        float shadowY = mDisplayOriginOffset.y + myGravityObjectShadowRight[objectIndex].y;

                        if      (shadowX > extremeBottom ) { shadowX = extremeBottom; }
                        else if (shadowX < extremeTop)     { shadowX = extremeTop; }

                        canvas.drawLine(shadowX,shadowY, objectXRightEye, objectY, mRight3DShadowPaint);
                    }
                }

            }


            // Calculate the new object positions.
            mElapsedTime +=
                    computeNewPositions(mNumIterationsPerDisplayPoint,mComputationTimeInterval);
            // Fetch the calculated data.
            storeNewSetOfCoords();
            // Update user preferences.
            handlePreferences();
            // Calculate the new display coordinates of objects, trails, and shadows.
            computeNewDisplayCoords();

            // and make sure to redraw asap
            invalidate();
        }

        /**
         * Display the current viewing angle.
         *
         * @param canvas    The canvas upon which the viewing angle is drawn.
         */
        private void displayTheViewingAngle(Canvas canvas)
        {
            String angleText = String.format("%3.1f degs", mViewingAngle);

            int orig10Pix = (int)(10*mDisplayScaleFactor);
            canvas.drawText(angleText, mDisplayMetrics.widthPixels - 6*orig10Pix,
                    mDisplayMetrics.heightPixels - orig10Pix/4, mAnglePaint);
        }

        /**
         * Displays the total elapsed time of the simultion
         *
         * @param canvas    The canvas upon which the viewing angle is drawn.
         */
        private void displayElapsedTime(Canvas canvas)
        {
            mElapsedTimeDisplayed = mElapsedTime/(24*60*60);
            String timeText = String.format("%d days", (int)mElapsedTimeDisplayed);

            int orig10Pix = (int)(10*mDisplayScaleFactor);
            canvas.drawText(timeText, orig10Pix, orig10Pix, mElapsedTimePaint);
            mElapsedTimePaint.getTextBounds(timeText,0,timeText.length(),mElapsedTimeTextBounds);
            mElapsedTimeTextBounds.offset(orig10Pix,orig10Pix);
//            mGravityAccessHelper.invalidateVirtualView(0);
        }

        private void displayGrid(Canvas canvas)
        {
            double gridSize = mLastScaleLength;
            int steps = 2;
            double gridPosition;

            if ( mSeeGrid == true )
            {
                for (gridPosition = -gridSize; gridPosition <= gridSize; gridPosition += gridSize / steps) {
                    DisplayPointPair end_1 = translatePointForDisplay(gridPosition, -gridSize, 0);
                    DisplayPointPair end_2 = translatePointForDisplay(gridPosition, gridSize, 0);

                    canvas.drawLine(
                            mDisplayOriginOffset.x + end_1.left_x,
                            mDisplayOriginOffset.y + end_1.left_y,
                            mDisplayOriginOffset.x + end_2.left_x,
                            mDisplayOriginOffset.y + end_2.left_y, mLeft3DPaint);
                    canvas.drawLine(
                            mDisplayOriginOffset.x + end_1.right_x,
                            mDisplayOriginOffset.y + end_1.right_y,
                            mDisplayOriginOffset.x + end_2.right_x,
                            mDisplayOriginOffset.y + end_2.right_y, mRight3DPaint);

                }
                for (gridPosition = -gridSize; gridPosition <= gridSize; gridPosition += gridSize / steps) {
                    DisplayPointPair end_1 = translatePointForDisplay(-gridSize, gridPosition, 0);
                    DisplayPointPair end_2 = translatePointForDisplay(gridSize, gridPosition, 0);

                    canvas.drawLine(
                            mDisplayOriginOffset.x + end_1.left_x,
                            mDisplayOriginOffset.y + end_1.left_y,
                            mDisplayOriginOffset.x + end_2.left_x,
                            mDisplayOriginOffset.y + end_2.left_y, mLeft3DPaint);
                    canvas.drawLine(
                            mDisplayOriginOffset.x + end_1.right_x,
                            mDisplayOriginOffset.y + end_1.right_y,
                            mDisplayOriginOffset.x + end_2.right_x,
                            mDisplayOriginOffset.y + end_2.right_y, mRight3DPaint);

                }

            }
        }

        /**
         * Displays the distance scale bar with legend.
         *
         * @param canvas    The canvas upon which the viewing angle is drawn.
         */
        private void displayDistanceScale(Canvas canvas)
        {
            double scaleLength = 1000000;  // number of AU
            int displayWidth;
            String scaleText;

            displayWidth = mDisplayMetrics.widthPixels;

            while ( ( (ONE_AU_METERS*scaleLength/mDisplayScale) < ((displayWidth*3 )/100) ) ||
                    ( (ONE_AU_METERS*scaleLength/mDisplayScale) > ((displayWidth*40)/100) ) )
            {
                scaleLength /= 10;

                if ( scaleLength < 0.0001 )
                {
                    break;
                }
            }
            mLastScaleLength = scaleLength*ONE_AU_METERS;

            int lineSegmentSize = (int)(10*mDisplayScaleFactor);
            int bottomEdge = mDisplayMetrics.heightPixels-lineSegmentSize/2;
            int topEdge    = mDisplayMetrics.heightPixels-lineSegmentSize;
            int middle     = (topEdge+bottomEdge)/2;
            int leftEdge   = lineSegmentSize;
            int rightEdge  = (int)(lineSegmentSize+(ONE_AU_METERS*scaleLength/mDisplayScale));
            canvas.drawLine(leftEdge, topEdge, leftEdge, bottomEdge, mDistanceScalePaint);
            canvas.drawLine(rightEdge, topEdge,rightEdge, bottomEdge, mDistanceScalePaint);
            canvas.drawLine(leftEdge, middle,rightEdge, middle, mDistanceScalePaint);
            mDistanceScaleBarBounds.set(leftEdge-lineSegmentSize/4,topEdge-10,rightEdge+lineSegmentSize/4,bottomEdge+lineSegmentSize/4);
            mDistanceScaleBarLength = rightEdge-leftEdge;

            scaleText = String.format("%f AU", scaleLength);
            mDistanceScaleAuForDisplay = scaleLength;

            int textX = (int)(10+(ONE_AU_METERS*scaleLength/mDisplayScale)+lineSegmentSize);
            int textY = (int)(mDisplayMetrics.heightPixels-lineSegmentSize/5);
            canvas.drawText(scaleText, textX, textY, mDistanceScalePaint);

            mDistanceScalePaint.getTextBounds(scaleText,0,scaleText.length(),mDistanceScaleTextBounds);
            mDistanceScaleTextBounds.offset(textX, textY);
        }




        /**
         * Displays the time scale of the simulation.
         *
         * @param canvas    The canvas upon which the time scale is drawn.
         */
        public void displayTheTimeScale(Canvas canvas)
        {
            mOnDrawCount += 1;
            long currentTimestamp = System.nanoTime();
            if ( mFirstOnDrawTimestamp == 0)
            {
                mFirstOnDrawTimestamp= currentTimestamp;
            }
            long elapsedTime_msec = (currentTimestamp-mFirstOnDrawTimestamp)/1000000;


            int displayWidth;
            String scaleText;

            displayWidth = mDisplayMetrics.widthPixels;

            if ( elapsedTime_msec != 0 )
            {
                mSimulationRate = (double)mNumIterationsPerDisplayPoint*mOnDrawCount*mComputationTimeInterval/ONE_DAY_TIME_INTERVAL*1000/elapsedTime_msec;
            }
            else
            {
                mSimulationRate = 0;
            }

            int orig10Pix = (int)(10*mDisplayScaleFactor);
            scaleText = String.format("%4.1f days/sec", mSimulationRate);

            int displayX = (int)(displayWidth-orig10Pix*10);
            int displayY = (int)(orig10Pix);
            canvas.drawText(scaleText, displayX,
                    displayY, mTimeScalePaint);
            mTimeScalePaint.getTextBounds(scaleText,0,scaleText.length(),mSimulationRateBounds);
            mSimulationRateBounds.offset(displayX, displayY);

            mComputationIntervalDisplayed = mComputationTimeInterval/(60*60);
            scaleText = String.format("%2.2f hours/calc", mComputationIntervalDisplayed);

            displayX = (int)(displayWidth-orig10Pix*10);
            displayY = (int)(orig10Pix*2);
            mTimeScalePaint.getTextBounds(scaleText,0,scaleText.length(),mComputationIntervalBounds);
            mComputationIntervalBounds.offset(displayX, displayY);
            canvas.drawText(scaleText, displayX, displayY, mTimeScalePaint);


        }

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;
            /*
             * record the accelerometer data, the event's timestamp as well as
             * the current time. The latter is needed so we can calculate the
             * "present" time during rendering. In this application, we need to
             * take into account how the screen is rotated with respect to the
             * sensors (which always return data in a coordinate space aligned
             * to with the screen in its native orientation).
             */

    //            switch (mDisplay.getRotation())
            switch (Surface.ROTATION_90)
            {
                case Surface.ROTATION_0:
                    mAccelerometerX = event.values[0];
                    mAccelerometerY = event.values[1];
                    mAccelerometerZ = event.values[2];
                    break;
                case Surface.ROTATION_90:
                    mAccelerometerX = -event.values[1];
                    mAccelerometerY = event.values[0];
                    mAccelerometerZ = event.values[2];
                    break;
                case Surface.ROTATION_180:
                    mAccelerometerX = -event.values[0];
                    mAccelerometerY = -event.values[1];
                    mAccelerometerZ = event.values[2];
                    break;
                case Surface.ROTATION_270:
                    mAccelerometerX = event.values[1];
                    mAccelerometerY = -event.values[0];
                    mAccelerometerZ = event.values[2];
                    break;
            }

    //     		mDebugText_4 = String.format("mAccelerometerY=%.1f  mAccelerometerZ=%.1f mAccelEventsCount=%d",mAccelerometerY, mAccelerometerZ, mAccelEventsCount);

            if ( mAccelEnabled )
            {
                double newViewingAngle = 90 - Math.toDegrees( Math.atan2(mAccelerometerY,mAccelerometerZ) );

                if (( newViewingAngle-mViewingAngle)/mViewingAngle > .1 )
                {
                    mViewingAngle = newViewingAngle;  // if they tilt fast, just go with it.
                }
                mViewingAngle = mViewingAngle*.9 + newViewingAngle*.10 ;
            }

            mLastAccelerometerX = mAccelerometerX;
            mLastAccelerometerY = mAccelerometerY;
            mLastAccelerometerZ = mAccelerometerZ;
        }




        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            mTouchCount = event.getPointerCount();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchX = x;
                    mTouchY = y;
                    mOldDisplayScale = 1;
                    mTouchLastHandledX = x;
                    mTouchLastHandledY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    final int pointerCount = event.getPointerCount();

                    if ( pointerCount == 1)
                    {
                        handle1TouchEvents(event);
                    }
                    else if ( pointerCount == 2)
                    {
                        handle2TouchEvents(event);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    mOldDisplayScale = 1;
    //                	if ( ( Math.abs(x) < 2*CLICK_DISTANCE_PIXELS*mDisplayScaleFactor ) &&
    //                			( Math.abs(y) < 2*CLICK_DISTANCE_PIXELS*mDisplayScaleFactor ) )
    //                 	{
    //                		mDisplayMode = mDisplayMode==DISPLAY_MODE_2D ? DISPLAY_MODE_3D : DISPLAY_MODE_2D;// We have tap of the 2d/3d button.
    //                 	}
    //                	else
                    if ( ( Math.abs(mTouchX-x) < CLICK_DISTANCE_PIXELS ) &&
                            ( Math.abs(mTouchY-y) < CLICK_DISTANCE_PIXELS ) )
                    {
                        // We have a release that was near by a press.  its a non-slide tap.
                        handleNewOriginClick(mTouchX,mTouchY);
                    }




                    mTouchEventInProgress = 0;
    //             		mDebugText_4 = String.format("mTouchEventInProgress=%d  mHandle1TouchEventsCount=%d",mTouchEventInProgress, mHandle1TouchEventsCount);
                    break;
            }


            return true;
        }

        /**
         * Calculates the pixel distance between the multitouch points.
         *
         * @param ev MotionEvent containing touch information
         * @return Pixel distance between two touch points.
         */
        private double getTwoTouchDistance(MotionEvent ev)
        {
            final int pointerCount = ev.getPointerCount();
            double distance = 0;

            if ( pointerCount == 2 )
            {
                double deltaX = ev.getX(0)-ev.getX(1);
                double deltaY = ev.getY(0)-ev.getY(1);

                distance = Math.sqrt( deltaX*deltaX + deltaY*deltaY );
            }

            return distance;
        }

        /**
         * Handles a multi touch event involving 2 points.  Will do logarithmic zoom.
         *
         * @param ev MotionEvent containing touch information
         */
        private void handle2TouchEvents(MotionEvent ev)
        {
            double distance = getTwoTouchDistance(ev);
            double newScale;
            distance = Math.floor(distance/10);  // scale it down a bit.
            if ( ( distance >= 2 ) &&
                    ((mTouchEventInProgress==1)||(mTouchEventInProgress==0)) )
            {
                mTouchEventInProgress = 1; // signify its a multi touch event in progress.

                newScale = Math.pow(1.1, distance);

                if ( mOldDisplayScale != 1 )
                {
                    mDisplayScale = mDisplayScale*mOldDisplayScale/ newScale ;

                    if (mDisplayScale > 1e+12)
                    {
                        mDisplayScale = 1e+12;
                    }
                    else if ( mDisplayScale < 1e+6 )
                    {
                        mDisplayScale = 1e+6;
                    }
                }
                mOldDisplayScale = newScale;
            }
        }

        /**
         * Handles a single touch event involving 1 points.  Will do time scale adjust or logarithmic zoom
         *
         * @param ev MotionEvent containing touch information
         */
        private void handle1TouchEvents(MotionEvent ev)
        {
            float currentX = ev.getX(0);
            float currentY = ev.getY(0);
            float overallDeltaX = mTouchX - currentX;
            float overallDeltaY = mTouchY - currentY;
            float deltaX = mTouchLastHandledX - currentX;
            float deltaY = mTouchLastHandledY - currentY;

            mHandle1TouchEventsCount += 1;

            if ( ( ( Math.abs(overallDeltaY) > Math.abs(3*overallDeltaX) ) && // make sure the motion is mostly vertical
                    ( Math.abs(overallDeltaY) > 2*CLICK_DISTANCE_PIXELS ) ) || // make sure its non-trivially vertical.
                    (mTouchEventInProgress==2) || (mTouchEventInProgress==3) ) // or if we are already in a touch mode.
            {
                if ( (( currentX < 2*mDisplayMetrics.widthPixels/5 ) && (mTouchEventInProgress==0) )||
                        (mTouchEventInProgress==2) )
                {
                    mTouchEventInProgress = 2; // signify its a time adjust event in progress.
                    // handle time adjust


                    double newScale;
                    newScale = Math.pow(1.1, deltaY/10 );
                    mComputationTimeInterval = mComputationTimeInterval* newScale ;

                    if ( mComputationTimeInterval < 90)
                    {
                        mComputationTimeInterval = 90;
                    }
                    else if ( mComputationTimeInterval > 3600*2)
                    {
                        mComputationTimeInterval = 3600*2;
                    }

                    mTouchLastHandledX = currentX;
                    mTouchLastHandledY = currentY;
                }
                else if ( ( ( currentX > 3*mDisplayMetrics.widthPixels/5 ) && (mTouchEventInProgress==0))|| // if in the right 40% of screen.
                        (mTouchEventInProgress==3) )
                {
                    mTouchEventInProgress = 3; // signify its a angle adjust event in progress.
                    // handle angle adjust
                    mViewingAngle += (deltaY/mDisplayMetrics.heightPixels)*90;



    ////             		mFocalPointDistanceMain3DAxis += (deltaY/mDisplayMetrics.heightPixels)*900;  // distance in display pixels to the observer
    //             		mFocalPointDistance3DEyeSeparation += (deltaY/mDisplayMetrics.heightPixels)*90;
    ////             		mFocalPointDistance3DEyeSeparation += mFocalPointDistanceMain3DAxis/10;
    //             		mDebugText_3 = String.format("mFocalPointDistanceMain3DAxis=%.1f ",mFocalPointDistanceMain3DAxis );
    //             		mDebugText_4 = String.format("mFocalPointDistance3DEyeSeparation=%.1f ",mFocalPointDistance3DEyeSeparation );

                    mTouchLastHandledX = currentX;
                    mTouchLastHandledY = currentY;

                }
            }
            else if ( (( Math.abs(overallDeltaX) > Math.abs(3*overallDeltaY) ) && // make sure the motion is mostly horizontal
                    ( Math.abs(overallDeltaX) > 2*CLICK_DISTANCE_PIXELS ) && // make sure its non-trivially horizontal.
                    (mTouchEventInProgress==0) )||
                    (mTouchEventInProgress==4) )
            {
                mTouchEventInProgress = 4; // signify its a distance adjust event in progress.
                // handle distance adjust

                double newScale;
                newScale = Math.pow(1.1, deltaX/10 );
                mDisplayScale = mDisplayScale* newScale ;
                mTouchLastHandledX = currentX;
                mTouchLastHandledY = currentY;
                if (mDisplayScale < mDisplayScaleMin) mDisplayScale = mDisplayScaleMin;  // put a floor on it
                if (mDisplayScale > mDisplayScaleMax) mDisplayScale = mDisplayScaleMax;  // put a ceiling on it

    //         		mDebugText_1 = String.format("mDisplayScale=%f newScale=%f", (float)mDisplayScale,(float)newScale);

            }



    //     		mDebugText_3 = String.format("overallDeltaX=%.1f  overallDeltaY=%.1f  ratio=%.1f",overallDeltaX, overallDeltaY, (overallDeltaY==0)?(float)1000:overallDeltaX/overallDeltaY );
    //     		mDebugText_4 = String.format("mTouchEventInProgress=%d  mHandle1TouchEventsCount=%d mAccelEventsCount=%d",mTouchEventInProgress, mHandle1TouchEventsCount, mAccelEventsCount);


        }

        /**
         * Display the current viewing angle.
         *
         * @param canvas    The canvas upon which the viewing angle is drawn.
         */
        private void displayDebugInformation(Canvas canvas)
        {
            if ( mDebugText_1 != null )
            {
                canvas.drawText(mDebugText_1, 0,
                        mDisplayMetrics.heightPixels - 12, mAnglePaint);
            }
            if ( mDebugText_2 != null )
            {
                canvas.drawText(mDebugText_2, 0,
                        mDisplayMetrics.heightPixels - 22, mAnglePaint);
            }
            if ( mDebugText_3 != null )
            {
                canvas.drawText(mDebugText_3, 0,
                        mDisplayMetrics.heightPixels - 32, mAnglePaint);
            }
            if ( mDebugText_4 != null )
            {
                canvas.drawText(mDebugText_4, 0,
                        mDisplayMetrics.heightPixels - 42, mAnglePaint);
            }
        }


        /**
         * Handles a quick screen tap to select a new origin
         * @param xPos  Horizontal coordinate of user tap
         * @param yPos  Vertical coordinate of user tap
         */
        private void handleNewOriginClick(float xPos, float yPos)
        {
            int index;
            mCenterObjectIndex = -1;
            for(index=0 ; index<mNumGravityObjects ; index++)
            {
                if ( ( mDisplayOriginOffset.x+myGravityObjectCoordHistoryLeft[index][0].x > xPos-CLICK_DISTANCE_PIXELS*mDisplayScaleFactor ) &&
                        ( mDisplayOriginOffset.x+myGravityObjectCoordHistoryLeft[index][0].x < xPos+CLICK_DISTANCE_PIXELS*mDisplayScaleFactor ) &&
                        ( mDisplayOriginOffset.y+myGravityObjectCoordHistoryLeft[index][0].y > yPos-CLICK_DISTANCE_PIXELS*mDisplayScaleFactor ) &&
                        ( mDisplayOriginOffset.y+myGravityObjectCoordHistoryLeft[index][0].y < yPos+CLICK_DISTANCE_PIXELS*mDisplayScaleFactor ) )
                {
                    // We're within the click range.  find the first object in the click range.
                    mCenterObjectIndex = index;
                    break;
                }
            }
        }

        @Override
        public boolean dispatchHoverEvent(MotionEvent event) {
            // Always attempt to dispatch hover events to accessibility first.
            if (mGravityAccessHelper != null)
            {
                return mGravityAccessHelper.dispatchHoverEvent(event);
            }

            return super.dispatchHoverEvent(event);
        }




























        //
        // Gravity Accessibility Helper
        //

        private class GravityAccessHelper extends ExploreByTouchHelper
        {
            final public int VIRTUAL_VIEW_ID_ELAPSED_TIME    =  0; // 0	elapsed time
            final public int VIRTUAL_VIEW_ID_SIMULATION_RATE =  10; // 10	days/sec indicator
            final public int VIRTUAL_VIEW_ID_CALC_INTERVAL   =  12; // 12	hours/calc indicator
            final public int VIRTUAL_VIEW_ID_DIST_SCALE_BAR  =  20; // 20	scale bar
            final public int VIRTUAL_VIEW_ID_DIST_SCALE_TEXT =  22; // 22	scale text
            final public int VIRTUAL_VIEW_ID_VIEWING_ANGLE =  30; // 30	viewing angle
//            final public int VIRTUAL_VIEW_ID_xx =  40; // 40	hours per calc slider
//            final public int VIRTUAL_VIEW_ID_xx =  50; // 50	viewing angle slider
//            final public int VIRTUAL_VIEW_ID_xx =  60; // 60	distance scale slider
            final public int VIRTUAL_VIEW_ID_VERT1_GRID_LEYE  =  70; // 70	first vert gridline lefteye
            final public int VIRTUAL_VIEW_ID_VERT2_GRID_LEYE  =  71; // ..
            final public int VIRTUAL_VIEW_ID_VERT3_GRID_LEYE  =  72; // ..
            final public int VIRTUAL_VIEW_ID_VERT4_GRID_LEYE  =  73; // ..
            final public int VIRTUAL_VIEW_ID_VERT5_GRID_LEYE  =  74; // 74	last vert gridline lefteye
            final public int VIRTUAL_VIEW_ID_HORIZ1_GRID_LEYE =  75; // 75	first horiz gridline lefteye
            final public int VIRTUAL_VIEW_ID_HORIZ2_GRID_LEYE =  76; // ..
            final public int VIRTUAL_VIEW_ID_HORIZ3_GRID_LEYE =  77; // ..
            final public int VIRTUAL_VIEW_ID_HORIZ4_GRID_LEYE =  78; // ..
            final public int VIRTUAL_VIEW_ID_HORIZ5_GRID_LEYE =  79; // 79	last horiz gridline lefteye
            final public int VIRTUAL_VIEW_ID_VERT1_GRID_REYE  =  80; // 80	first vert gridline righteye
            final public int VIRTUAL_VIEW_ID_VERT2_GRID_REYE  =  81; // ..
            final public int VIRTUAL_VIEW_ID_VERT3_GRID_REYE  =  82; // ..
            final public int VIRTUAL_VIEW_ID_VERT4_GRID_REYE  =  83; // ..
            final public int VIRTUAL_VIEW_ID_VERT5_GRID_REYE  =  84; // 84	last vert gridline righteye
            final public int VIRTUAL_VIEW_ID_HORIZ1_GRID_REYE =  85; // 85	first horiz gridline righteye
            final public int VIRTUAL_VIEW_ID_HORIZ2_GRID_REYE =  86; // ..
            final public int VIRTUAL_VIEW_ID_HORIZ3_GRID_REYE =  87; //..
            final public int VIRTUAL_VIEW_ID_HORIZ4_GRID_REYE =  88; // ..
            final public int VIRTUAL_VIEW_ID_HORIZ5_GRID_REYE =  89; // 89	last horiz gridline righteye
//            final public int VIRTUAL_VIEW_ID_xx =
//            final public int VIRTUAL_VIEW_ID_xx =  99; // 99	background click	name/click
//            virtualViewIds.add(100); // 100+10n+0 - object n left		name/click
//            virtualViewIds.add(101); // 100+10n+1 - object n right		name


            public GravityAccessHelper(View parentView)
            {
                super(parentView);
            }

            @Override
            protected int getVirtualViewAt(float x, float y)
            {
                {
                    // We already map (x,y) to bar index for onTouchEvent().
//                final int index = getBarIndexAt(x, y);
//                if (index >= 0) {
//                    return index;
//                }
                    if (mElapsedTimeTextBounds.contains((int)x,(int)y))          return VIRTUAL_VIEW_ID_ELAPSED_TIME;
                    else if (mSimulationRateBounds.contains((int)x,(int)y))      return VIRTUAL_VIEW_ID_SIMULATION_RATE;
                    else if (mComputationIntervalBounds.contains((int)x,(int)y)) return VIRTUAL_VIEW_ID_CALC_INTERVAL;
                    else if (mDistanceScaleBarBounds.contains((int)x,(int)y))    return VIRTUAL_VIEW_ID_DIST_SCALE_BAR;
                    else if (mDistanceScaleTextBounds.contains((int)x,(int)y))   return VIRTUAL_VIEW_ID_DIST_SCALE_TEXT;
                    else return ExploreByTouchHelper.HOST_ID;
                }
            }



            @Override
            protected void getVisibleVirtualViews(List<Integer> virtualViewIds)
            {
                virtualViewIds.add(VIRTUAL_VIEW_ID_DIST_SCALE_BAR); // 20	scale bar
                virtualViewIds.add(VIRTUAL_VIEW_ID_ELAPSED_TIME); // 0	elapsed time
                virtualViewIds.add(VIRTUAL_VIEW_ID_SIMULATION_RATE); // 10	days/sec indicator
                virtualViewIds.add(VIRTUAL_VIEW_ID_CALC_INTERVAL); // 12	hours/calc indicator
                virtualViewIds.add(VIRTUAL_VIEW_ID_DIST_SCALE_TEXT); // 22	scale text
//                virtualViewIds.add(30); // 30	viewing angle
//                virtualViewIds.add(40); // 40	hours per calc slider
//                virtualViewIds.add(50); // 50	viewing angle slider
//                virtualViewIds.add(60); // 60	distance scale slider
//                virtualViewIds.add(70); // 70	first vert gridline lefteye
//                virtualViewIds.add(71); // ..
//                virtualViewIds.add(72); // ..
//                virtualViewIds.add(73); // ..
//                virtualViewIds.add(74); // 74	last vert gridline lefteye
//                virtualViewIds.add(75); // 75	first horiz gridline lefteye
//                virtualViewIds.add(76); // ..
//                virtualViewIds.add(77); // ..
//                virtualViewIds.add(78); // ..
//                virtualViewIds.add(79); // 79	last horiz gridline lefteye
//                virtualViewIds.add(80); // 80	first vert gridline righteye
//                virtualViewIds.add(81); // ..
//                virtualViewIds.add(82); // ..
//                virtualViewIds.add(83); // ..
//                virtualViewIds.add(84); // 84	last vert gridline righteye
//                virtualViewIds.add(85); // 85	first horiz gridline righteye
//                virtualViewIds.add(86); // ..
//                virtualViewIds.add(87); // ..
//                virtualViewIds.add(88); // ..
//                virtualViewIds.add(89); // 89	last horiz gridline righteye
//
//                virtualViewIds.add(99); // 99	background click	name/click
//                virtualViewIds.add(100); // 100+10n+0 - object n left		name/click
//                virtualViewIds.add(101); // 100+10n+1 - object n right		name
            }

            private CharSequence getDescriptionForIndex(int index)
            {
                CharSequence returnVal;

                switch(index)
                {
                    case VIRTUAL_VIEW_ID_ELAPSED_TIME:
                        returnVal = getContext().getString(R.string.desc_elapsed_days, (int)mElapsedTimeDisplayed);
                        break;
                    case VIRTUAL_VIEW_ID_SIMULATION_RATE:
                        returnVal = getContext().getString(R.string.desc_simulation_rate, mSimulationRate);
                        break;
                    case VIRTUAL_VIEW_ID_CALC_INTERVAL:
                        returnVal = getContext().getString(R.string.desc_computation_interval, mComputationIntervalDisplayed);
                        break;
                    case VIRTUAL_VIEW_ID_DIST_SCALE_BAR:
                        returnVal = getContext().getString(R.string.desc_distance_scale_bar, mDistanceScaleBarLength);
                        break;
                    case VIRTUAL_VIEW_ID_DIST_SCALE_TEXT:
                        returnVal = getContext().getString(R.string.desc_distance_scale_text, mDistanceScaleAuForDisplay);
                        break;
                    default:
                        returnVal = getContext().getString(R.string.desc_unknown_field);
                        break;
                }

                return returnVal;
            }

            @Override
            protected void onPopulateEventForVirtualView(int virtualViewId, AccessibilityEvent event)
            {
                final CharSequence desc = getDescriptionForIndex(virtualViewId);
                event.setContentDescription(desc);
            }

            @Override
            protected boolean onPerformActionForVirtualView(int virtualViewId, int action, Bundle arguments)
            {
//                switch (action)
//                {
//                    case AccessibilityNodeInfoCompat.ACTION_FOCUS:
//                        // Click handling should be consistent with onTouchEvent().
////                        onBarClicked(virtualViewId);
//                        return true;
//                }

                // Only need to handle actions added in populateNode.
                return false;
            }

            private Rect getBoundsForIndex(int index, Rect out)
            {
                Rect returnVal;

                switch(index)
                {
                    case VIRTUAL_VIEW_ID_ELAPSED_TIME:
                        returnVal = mElapsedTimeTextBounds;
                        break;
                    case VIRTUAL_VIEW_ID_SIMULATION_RATE:
                        returnVal = mSimulationRateBounds;
                        break;
                    case VIRTUAL_VIEW_ID_CALC_INTERVAL:
                        returnVal = mComputationIntervalBounds;
                        break;
                    case VIRTUAL_VIEW_ID_DIST_SCALE_BAR:
                        returnVal = mDistanceScaleBarBounds;
                        break;
                    case VIRTUAL_VIEW_ID_DIST_SCALE_TEXT:
                        returnVal = mDistanceScaleTextBounds;
                        break;
                    default:
                        returnVal = new Rect(0,0,0,0);
                        break;
                }

                if (out != null)
                {
                    out.set(returnVal);
                }
                return returnVal;
            }

            @Override
            protected void onPopulateNodeForVirtualView( int virtualViewId, AccessibilityNodeInfoCompat node)
            {
                // Node and event descriptions are usually identical.
                final CharSequence desc = getDescriptionForIndex(virtualViewId);
                node.setContentDescription(desc);

//                // Since the user can tap a bar, add the CLICK action.
//                node.addAction(AccessibilityNodeInfoCompat.ACTION_FOCUS);

                // Reported bounds should be consistent with onDraw().
                final Rect bounds = getBoundsForIndex(virtualViewId, null);
                node.setBoundsInParent(bounds);
                node.setFocusable(true);
//                node.setEnabled(true);
            }

        }



    }


    /**
     * Initializes the GravityEngine and allocates necessary tracking memory.
     * The vector and display point history buffers are allocated and
     * initialized with the initial state of the gravity system.
     */
    private void initGravityEngine()
    {
        mNumGravityObjects = getGravityNumObjects();

        // Allocate the pointers to the objects.
        myGravityObjectCoordHistory = new DisplayPoint[mNumGravityObjects][MAX_OBJECT_HISTORY];
        myGravityObjectCoordHistoryLeft = new DisplayPoint[mNumGravityObjects][MAX_OBJECT_HISTORY];
        myGravityObjectCoordHistoryRight = new DisplayPoint[mNumGravityObjects][MAX_OBJECT_HISTORY];

        myGravityObjectShadowLeft = new DisplayPoint[mNumGravityObjects];
        myGravityObjectShadowRight = new DisplayPoint[mNumGravityObjects];
        myGravityObjectVectorHistory = new VectorPoint[mNumGravityObjects][MAX_OBJECT_HISTORY];
        myGravityObjectNames = new String[mNumGravityObjects];

        // Allocate the objects.
        for (int objectIndex = 0; objectIndex < mNumGravityObjects; objectIndex++) {
            myGravityObjectShadowLeft[objectIndex] = new DisplayPoint();
            myGravityObjectShadowRight[objectIndex] = new DisplayPoint();
            myGravityObjectNames[objectIndex] = getGravityObjectName(objectIndex);

            for (int historyIndex = 0; historyIndex < MAX_OBJECT_HISTORY; historyIndex++) {
                myGravityObjectCoordHistory[objectIndex][historyIndex] = new DisplayPoint();
                myGravityObjectVectorHistory[objectIndex][historyIndex] = new VectorPoint();
                myGravityObjectCoordHistoryLeft[objectIndex][historyIndex] = new DisplayPoint();
                myGravityObjectCoordHistoryRight[objectIndex][historyIndex] = new DisplayPoint();
            }
        }

        storeNewSetOfCoords(); // Populate the objects with good stuff.
        computeNewDisplayCoords();

    }

    /**
     * Fetches the coordinates from the Gravity Engine via JNI and stores
     * the coordinates in the myGravityObjectVectorHistory area.
     */
    private void storeNewSetOfCoords() {
        int index;
        int tempIndex;

        for (index = 0; index < mNumGravityObjects; index++) {
            // Save the historical values.
            for (tempIndex = myGravityObjectCoordHistoryDepth; tempIndex > 0; tempIndex--) {
                myGravityObjectVectorHistory[index][tempIndex].x = myGravityObjectVectorHistory[index][tempIndex - 1].x;
                myGravityObjectVectorHistory[index][tempIndex].y = myGravityObjectVectorHistory[index][tempIndex - 1].y;
                myGravityObjectVectorHistory[index][tempIndex].z = myGravityObjectVectorHistory[index][tempIndex - 1].z;
            }

            // fetch the recent coordinates from the computation engine.
            myGravityObjectVectorHistory[index][0].x = getGravityObjectPosX(index);
            myGravityObjectVectorHistory[index][0].y = getGravityObjectPosY(index);
            myGravityObjectVectorHistory[index][0].z = getGravityObjectPosZ(index);

        }
        if (myGravityObjectCoordHistoryDepth < MAX_OBJECT_HISTORY - 1) {
            myGravityObjectCoordHistoryDepth += 1; // Only advance the history
            // depth till it reaches
            // max.
        }
    }

    /**
     * converts the physical positions into display coordinates.
     */
    private void computeNewDisplayCoords() {
        int index;
        double xRot,yRot,zRot;
        double xTran,yTran,zTran;
        double xTempLeft,xTempRight,yTemp;
        int tempIndex;
        double sinOfAngle = Math.sin(Math.toRadians(-mViewingAngle));
        double cosOfAngle = Math.cos(Math.toRadians(-mViewingAngle));
        VectorPoint currentVectorPoint;
        double eyeSeparation;

        if ( m3dDisplay )
        {
            eyeSeparation = mFocalPointDistance3DEyeSeparation;
        }
        else
        {
            eyeSeparation = 0;
            mExcludeGreenColor = false; // force cyan if not 3d.
        }


        for (index = 0; index < mNumGravityObjects; index++)
        {
            // Save the historical values.
            for (tempIndex = 0; tempIndex < myGravityObjectCoordHistoryDepth; tempIndex++)
            {
                // Compute the projected ccordinates based on the viewing angle.

                // Set up the initial points.  may slide them to new center later.
                xTran = myGravityObjectVectorHistory[index][tempIndex].x;
                yTran = myGravityObjectVectorHistory[index][tempIndex].y;
                zTran = myGravityObjectVectorHistory[index][tempIndex].z;

                // Translate it so the origin is one of the objects.
                if (mCenterObjectIndex != -1)
                {
                    xTran -= myGravityObjectVectorHistory[mCenterObjectIndex][0].x;
                    yTran -= myGravityObjectVectorHistory[mCenterObjectIndex][0].y;
                    zTran -= myGravityObjectVectorHistory[mCenterObjectIndex][0].z;
                }

                DisplayPointPair mDisplayPoint = translatePointForDisplay( xTran,  yTran, zTran);

                myGravityObjectCoordHistoryRight[index][tempIndex].x = mDisplayPoint.right_x;
                myGravityObjectCoordHistoryRight[index][tempIndex].y = mDisplayPoint.right_y;
                myGravityObjectCoordHistoryLeft[index][tempIndex].x = mDisplayPoint.left_x;
                myGravityObjectCoordHistoryLeft[index][tempIndex].y = mDisplayPoint.left_y;

            }

            // Set up the initial points.  may slide them to new center later.
            xTran = myGravityObjectVectorHistory[index][0].x;
            yTran = myGravityObjectVectorHistory[index][0].y;
            zTran = 0;

            // Translate it so the origin is one of the objects.
            if (mCenterObjectIndex != -1)
            {
                xTran -= myGravityObjectVectorHistory[mCenterObjectIndex][0].x;
                yTran -= myGravityObjectVectorHistory[mCenterObjectIndex][0].y;
                zTran -= myGravityObjectVectorHistory[mCenterObjectIndex][0].z;
            }

            DisplayPointPair mDisplayPoint = translatePointForDisplay( xTran,  yTran, zTran);

            myGravityObjectShadowRight[index].x = mDisplayPoint.right_x;
            myGravityObjectShadowRight[index].y = mDisplayPoint.right_y;
            myGravityObjectShadowLeft[index].x = mDisplayPoint.left_x;
            myGravityObjectShadowLeft[index].y = mDisplayPoint.left_y;
        }

    }

    DisplayPointPair translatePointForDisplay(double xTran, double yTran, double zTran)
    {
        double xRot,yRot,zRot;
        double xTempLeft,xTempRight,yTemp;
        double sinOfAngle = Math.sin(Math.toRadians(-mViewingAngle));
        double cosOfAngle = Math.cos(Math.toRadians(-mViewingAngle));
        double eyeSeparation;

        if ( m3dDisplay )
        {
            eyeSeparation = mFocalPointDistance3DEyeSeparation;
        }
        else
        {
            eyeSeparation = 0;
            mExcludeGreenColor = false; // force cyan if not 3d.
        }

        // Rotate the system about the x axis.
        xRot = xTran;
        yRot = yTran*cosOfAngle - zTran*sinOfAngle;
        zRot = yTran*sinOfAngle	+ zTran*cosOfAngle;

        // Scale it down so that its in approximately display units instead of meters.
        xRot /= mDisplayScale; // One AU is about 149 pixels.
        yRot /= mDisplayScale; // One AU is about 149 pixels.
        zRot /= mDisplayScale; // One AU is about 149 pixels.

        // Do the stereo vision calculation.
        yTemp = zRot*mFocalPointDistanceMain3DAxis/(mFocalPointDistanceMain3DAxis+yRot);
        xTempLeft  = ((xRot+eyeSeparation)*mFocalPointDistanceMain3DAxis)/(yRot+mFocalPointDistanceMain3DAxis)-eyeSeparation;
        xTempRight = ((xRot-eyeSeparation)*mFocalPointDistanceMain3DAxis)/(yRot+mFocalPointDistanceMain3DAxis)+eyeSeparation;

        DisplayPointPair mDisplayPointPair = new DisplayPointPair();

        mDisplayPointPair.left_x = (int)xTempLeft;
        mDisplayPointPair.left_y = (int)yTemp;
        mDisplayPointPair.right_x = (int)xTempRight;
        mDisplayPointPair.right_y = (int)yTemp;

        return( mDisplayPointPair);
    }

    /**
     * Fetches the application preferences for use.
     */
    public void handlePreferences()
    {
        SharedPreferences mySharedPreferences;
        mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        m3dDisplay          = mySharedPreferences.getBoolean("enable_3d", false);
        mExcludeGreenColor  = mySharedPreferences.getBoolean("no_green", false);
        mAccelEnabled       = mySharedPreferences.getBoolean("enable_accel_tilt", false);
        mSeeGrid           = mySharedPreferences.getBoolean("see_grid", false);

    }


    /**
     * Class that represents a 3D vector in non-polar format using x,y,z.
     *
     * @author weidnerm
     *
     */
    class VectorPoint {
        double x;
        double y;
        double z;
    }

    /**
     * Class that represents a point on the display using x,y coordinates.
     */
    class DisplayPoint {
        int x;
        int y;
    }
    /**
     * Class that represents a left and right eye set of points on the display using x,y coordinates.
     */
    class DisplayPointPair {
        public int left_x;
        public int left_y;
        public int right_x;
        public int right_y;
    }

//    static {
//        System.loadLibrary("GravityEngine");
//    }


    //
    // Replace with JNI later
    //
    double computeNewPositions(int numIterationsPerDisplayPoint, double computationTimeInterval )
    {
        int index;

        for(index=0;index<numIterationsPerDisplayPoint ; index++)
        {
            mySolarSystem.processTimeInterval(computationTimeInterval);
        }

        return numIterationsPerDisplayPoint*computationTimeInterval;
    }

    int getGravityNumObjects( )
    {
        int retVal;

        retVal = mySolarSystem.getNumObjects();

        return retVal;
    }

    String getGravityObjectName( int index )
    {
        return mySolarSystem.getGravityObjectInfo( index );
    }

    double getGravityObjectPosX( int index )
    {
        double retVal;

        retVal = mySolarSystem.getGravityObjectX(index);

        return retVal;
    }
    double getGravityObjectPosY( int index )
    {
        double retVal;

        retVal = mySolarSystem.getGravityObjectY(index);

        return retVal;
    }
    double getGravityObjectPosZ( int index )
    {
        double retVal;

        retVal = mySolarSystem.getGravityObjectZ(index);

        return retVal;
    }


}
