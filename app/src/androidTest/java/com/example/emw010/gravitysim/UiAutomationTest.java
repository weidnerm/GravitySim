package com.example.emw010.gravitysim;

/**
 * Created by emw010 on 7/24/16.
 */
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.*;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;


import org.junit.Test;

import java.util.Scanner;


@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 10)
public class UiAutomationTest {

    private static final String BASIC_SAMPLE_PACKAGE
            = "com.example.emw010.gravitysim";

    private static final int LAUNCH_TIMEOUT = 5000;

    private static final String STRING_TO_BE_TYPED = "UiAutomator";

    private UiDevice mDevice;

    final int CHECKBOX_INDEX_3D_ENABLE = 0;
    final int CHECKBOX_INDEX_EXCLUDE_GREEN = 1;
    final int CHECKBOX_INDEX_SEE_GRID = 2;
    final int CHECKBOX_INDEX_ACCEL = 3;

    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the blueprint app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void checkPreconditions()
    {
        assertThat(mDevice, notNullValue());
    }

    @Test
    public void testMenuButton()
    {
        mDevice.pressMenu();

        UiObject aboutButton = mDevice.findObject(new UiSelector()
                .text("About"));
        assertThat(aboutButton, notNullValue());
        assertEquals( true, aboutButton.exists() );

        UiObject settingsButton = mDevice.findObject(new UiSelector()
                .text("Settings"));
        assertThat(settingsButton, notNullValue());
        assertEquals( true, settingsButton.exists() );

        UiObject objectListButton = mDevice.findObject(new UiSelector()
                .text("Object List"));
        assertThat(objectListButton, notNullValue());
        assertEquals( true, objectListButton.exists() );

    }

    @Test
    public void testAboutButton()
    {
        mDevice.pressMenu();

        UiObject aboutButton = mDevice.findObject(new UiSelector()
                .text("About"));
        assertThat(aboutButton, notNullValue());
        assertEquals( true, aboutButton.exists() );

        try {
            aboutButton.click();
        }
        catch(UiObjectNotFoundException e) {
            fail();
        }
        UiObject2 changedText = mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "textView1")),500 /* wait 500ms */);
        assertThat(changedText, notNullValue());
        assertThat("GravitySim", is(equalTo(changedText.getText())));
    }

    @Test
    public void testObjectListButton()
    {
        mDevice.pressMenu();

        UiObject aboutButton = mDevice.findObject(new UiSelector()
                .text("Object List"));
        assertThat(aboutButton, notNullValue());
        assertEquals( true, aboutButton.exists() );

        try {
            aboutButton.click();
        }
        catch(UiObjectNotFoundException e) {
            fail();
        }

        UiObject2 changedText = mDevice.wait(Until.findObject(By.text("Sun")),500);
        assertThat(changedText, notNullValue());
        assertEquals("Sun", changedText.getText());
    }

    @Test
    public void testObjectSettingsButton() throws UiObjectNotFoundException, InterruptedException {
        mDevice.pressMenu();

        UiObject aboutButton = mDevice.findObject(new UiSelector()
                .text("Settings"));
        assertThat(aboutButton, notNullValue());
        assertEquals( true, aboutButton.exists() );

        try {
            aboutButton.click();
        }
        catch(UiObjectNotFoundException e) {
            fail();
        }

        UiObject2 changedText = mDevice.wait(Until.findObject(By.text("3D Options")),500);
        assertThat(changedText, notNullValue());

        UiCollection settings = new UiCollection(new UiSelector()
                .className("android.widget.ListView"));

        int count = settings.getChildCount(new UiSelector()
                .className("android.widget.CheckBox"));
        assertEquals(4,count);

        Context appContext = InstrumentationRegistry.getTargetContext();

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        assertThat(mySharedPreferences, notNullValue());
        boolean m3dDisplay          = mySharedPreferences.getBoolean("enable_3d", false);
        boolean mExcludeGreenColor  = mySharedPreferences.getBoolean("no_green", false);
        boolean mAccelEnabled       = mySharedPreferences.getBoolean("enable_accel_tilt", false);
        boolean mSeeGrid           = mySharedPreferences.getBoolean("see_grid", false);

        UiObject myCheckbox0 = settings.getChildByInstance(new UiSelector().className("android.widget.CheckBox"),CHECKBOX_INDEX_3D_ENABLE);
        assertThat(myCheckbox0, notNullValue());
        assertEquals(m3dDisplay,myCheckbox0.isChecked());  // make sure preference match gui
        myCheckbox0.click();
        Thread.sleep(1000);
        assertEquals(m3dDisplay,mySharedPreferences.getBoolean("enable_3d", false) ? false: true);  // make sure preference is toggled
        myCheckbox0.click();   // put it back to the original state

        UiObject myCheckbox1 = settings.getChildByInstance(new UiSelector().className("android.widget.CheckBox"),CHECKBOX_INDEX_EXCLUDE_GREEN);
        assertThat(myCheckbox1, notNullValue());
        assertEquals(mExcludeGreenColor,myCheckbox1.isChecked());  // make sure preference match gui
        myCheckbox1.click();
        assertEquals(mExcludeGreenColor,mySharedPreferences.getBoolean("no_green", false) ? false: true);  // make sure preference is toggled
        myCheckbox1.click();   // put it back to the original state

        UiObject myCheckbox2 = settings.getChildByInstance(new UiSelector().className("android.widget.CheckBox"),CHECKBOX_INDEX_SEE_GRID);
        assertThat(myCheckbox2, notNullValue());
        assertEquals(mSeeGrid,myCheckbox2.isChecked());  // make sure preference match gui
        myCheckbox2.click();
        assertEquals(mSeeGrid,mySharedPreferences.getBoolean("see_grid", false) ? false: true);  // make sure preference is toggled
        myCheckbox2.click();   // put it back to the original state

        UiObject myCheckbox3 = settings.getChildByInstance(new UiSelector().className("android.widget.CheckBox"),CHECKBOX_INDEX_ACCEL);
        assertThat(myCheckbox3, notNullValue());
        assertEquals(mAccelEnabled,myCheckbox3.isChecked());  // make sure preference match gui
        myCheckbox3.click();
        assertEquals(mAccelEnabled,mySharedPreferences.getBoolean("enable_accel_tilt", false) ? false: true);  // make sure preference is toggled
        myCheckbox3.click();   // put it back to the original state
    }

    /**
     * Test the elapsed time display field by making sure the elapsed number of days changes after a brief delay.
     *
     * @throws UiObjectNotFoundException
     * @throws InterruptedException
     */
    @Test
    public void testElapsedTime() throws UiObjectNotFoundException, InterruptedException {

        UiObject elapsedText = mDevice.findObject(new UiSelector()
                .descriptionContains("elapsed time"));
        assertThat(elapsedText, notNullValue());

        Scanner scanner = new Scanner( elapsedText.getContentDescription() );
        assertEquals( "elapsed", scanner.next() );
        assertEquals( "time", scanner.next() );
        int firstTime = scanner.nextInt();
        assertNotEquals( 0, firstTime );
        assertEquals( "days", scanner.next() );
        Thread.sleep(500);

        Scanner scanner2 = new Scanner( elapsedText.getContentDescription() );
        assertEquals( "elapsed", scanner2.next() );
        assertEquals( "time", scanner2.next() );
        int secondTime = scanner2.nextInt();
        assertNotEquals( secondTime, firstTime );
        assertEquals( "days", scanner2.next() );

        Rect bounds = elapsedText.getVisibleBounds();
        assertNotEquals(0, bounds.left);
        assert(bounds.left < bounds.right);
        assertNotEquals(0, bounds.top);
        assert(bounds.top < bounds.bottom);
    }

    /**
     * Test the simulation rate by making sure the accessibility field for it exists and that the simulation rate is non zero
     * and that the computation interval is nonzero
     *
     * @throws UiObjectNotFoundException
     * @throws InterruptedException
     */
    @Test
    public void testSimulationRate() throws UiObjectNotFoundException, InterruptedException
    {
        //
        // validate the simulation rate field.
        //
        {
            UiObject elapsedText = mDevice.findObject(new UiSelector()
                    .descriptionContains("simulation rate"));
            assertThat(elapsedText, notNullValue());

            Scanner scanner = new Scanner( elapsedText.getContentDescription() );
            assertEquals( "simulation", scanner.next() );
            assertEquals( "rate", scanner.next() );
            float firstTime = scanner.nextFloat();
            assertNotEquals( 0, firstTime, 0.000001 );
            assertEquals( "days", scanner.next() );
            assertEquals( "per", scanner.next() );
            assertEquals( "second", scanner.next() );

            Rect bounds = elapsedText.getVisibleBounds();
            assertNotEquals(0, bounds.left);
            assert(bounds.left < bounds.right);
            assertNotEquals(0, bounds.top);
            assert(bounds.top < bounds.bottom);
        }

        //
        // validate the calculation interval field.
        //
        {
            UiObject elapsedText = mDevice.findObject(new UiSelector()
                    .descriptionContains("hours per iteration"));
            assertThat(elapsedText, notNullValue());

            Scanner scanner = new Scanner( elapsedText.getContentDescription() );
            float rate = scanner.nextFloat();
            assertEquals( 1.0, rate , 0.000001);
            assertEquals( "hours", scanner.next() );
            assertEquals( "per", scanner.next() );
            assertEquals( "iteration", scanner.next() );

            Rect bounds = elapsedText.getVisibleBounds();
            assertNotEquals(0, bounds.left);
            assert(bounds.left < bounds.right);
            assertNotEquals(0, bounds.top);
            assert(bounds.top < bounds.bottom);
        }
    }

    final int DRAG_LEFT_SIDE_DOWN_10PCT  = 0;
    final int DRAG_LEFT_SIDE_UP_10PCT    = 1;
    final int DRAG_LEFT_SIDE_DOWN_80PCT  = 2;
    final int DRAG_LEFT_SIDE_UP_80PCT    = 3;

    private void performDragEvent(int type)
    {
        int displayWidth = mDevice.getDisplayWidth();
        int displayHeight = mDevice.getDisplayHeight();

        int startX,endX ,startY,endY,duration;

        switch(type)
        {
            default:
            case DRAG_LEFT_SIDE_DOWN_10PCT:
                startX = (displayWidth*20)/100;  // 20% across screen
                endX   = (displayWidth*22)/100;
                startY = (displayHeight*10)/100; // 20% from top
                endY   = (displayHeight*20)/100; // swipe 10% down
                duration = 100;  // 5msec per interval
                break;

            case DRAG_LEFT_SIDE_UP_10PCT:
                startX = (displayWidth*19)/100;  // 20% across screen
                endX   = (displayWidth*20)/100;
                startY = (displayHeight*90)/100; // 20% from top
                endY   = (displayHeight*80)/100; // swipe 10% down
                duration = 100;  // 5msec per interval
                break;

            case DRAG_LEFT_SIDE_DOWN_80PCT:
                startX = (displayWidth*20)/100;  // 20% across screen
                endX = startX + (displayWidth*10)/100;
                startY = (displayHeight*10)/100; // 20% from top
                endY   = (displayHeight*90)/100;
                duration = 100;  // 5msec per interval
                break;

            case DRAG_LEFT_SIDE_UP_80PCT:
                startX = (displayWidth*20)/100;  // 20% across screen
                endX   = (displayWidth*30)/100;  // 30%
                startY = (displayHeight*90)/100; // 90% from top
                endY   = (displayHeight*10)/100; // 10% from top
                duration = 100;  // 5msec per interval
                break;

        }

        mDevice.drag(startX,startY,endX,endY,duration);

    }

    /**
     * Test the slider to adjust calculation interval
     *
     */
    @Test
    public void testCalculationIntervalAdjustment() throws UiObjectNotFoundException, InterruptedException {

        {

            UiObject elapsedText = mDevice.findObject(new UiSelector()
                    .descriptionContains("hours per iteration"));
            assertThat(elapsedText, notNullValue());

            Scanner scanner = new Scanner( elapsedText.getContentDescription() );
            float rate = scanner.nextFloat();
            assertEquals( 1.0, rate , 0.000001);


            performDragEvent(DRAG_LEFT_SIDE_DOWN_10PCT);
            Thread.sleep(500);

            Scanner scanner2 = new Scanner( elapsedText.getContentDescription() );
            float rate2 = scanner2.nextFloat();
            assertTrue( rate2<rate );
            assertNotEquals( 0.02, rate2 , 0.000001 );




            performDragEvent(DRAG_LEFT_SIDE_UP_10PCT);
            Thread.sleep(500);

            Scanner scanner3 = new Scanner( elapsedText.getContentDescription() );
            float rate3 = scanner3.nextFloat();
            assertTrue( rate3>rate2 );
            assertNotEquals( 2.00, rate3 , 0.000001 );
        }
    }

    /**
     * Test the slider to adjust calculation interval
     *
     */
    @Test
    public void testCalculationIntervalAdjustmentToRails() throws UiObjectNotFoundException, InterruptedException {

        {

            UiObject elapsedText = mDevice.findObject(new UiSelector()
                    .descriptionContains("hours per iteration"));
            assertThat(elapsedText, notNullValue());

            assertEquals( "1.00 hours per iteration", elapsedText.getContentDescription());




            performDragEvent(DRAG_LEFT_SIDE_DOWN_80PCT);
            Thread.sleep(500);

            assertEquals( "0.02 hours per iteration", elapsedText.getContentDescription());



            performDragEvent(DRAG_LEFT_SIDE_UP_80PCT);
            Thread.sleep(500);

            assertEquals( "2.00 hours per iteration", elapsedText.getContentDescription());
        }
    }


    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}

/*
tests to do

test landscape vs portrait

normal stuff
	adjust time scale
	adjust adjust viewing angle
		slider
		accelerometer
	adjust zoom scale
	new origin
		background
		object

	3d on/off
	green on/off
	accel on/off
	grid on/off

	short slide (considered a tap)

	menu - ovearll
	menu - about
	menu - object list
	menu - settings

abnormal stuff
	swipe in center
	diagonal swipe(left, center, right)



 */