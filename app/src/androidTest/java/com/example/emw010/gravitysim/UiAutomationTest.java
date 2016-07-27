package com.example.emw010.gravitysim;

/**
 * Created by emw010 on 7/24/16.
 */
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;


@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 10)
public class UiAutomationTest {

    private static final String BASIC_SAMPLE_PACKAGE
            = "com.example.emw010.gravitysim";

    private static final int LAUNCH_TIMEOUT = 5000;

    private static final String STRING_TO_BE_TYPED = "UiAutomator";

    private UiDevice mDevice;

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
    public void checkPreconditions() {
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
    public void testObjectSettingsButton() throws UiObjectNotFoundException {
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

        UiObject myCheckbox0 = settings.getChildByInstance(new UiSelector().className("android.widget.CheckBox"),0);
        assertThat(myCheckbox0, notNullValue());
        assertEquals(m3dDisplay,myCheckbox0.isChecked());  // make sure preference match gui
        myCheckbox0.click();
        assertEquals(m3dDisplay,mySharedPreferences.getBoolean("enable_3d", false) ? false: true);  // make sure preference is toggled
        myCheckbox0.click();   // put it back to the original state

        UiObject myCheckbox1 = settings.getChildByInstance(new UiSelector().className("android.widget.CheckBox"),1);
        assertThat(myCheckbox1, notNullValue());
        assertEquals(mExcludeGreenColor,myCheckbox1.isChecked());  // make sure preference match gui
        myCheckbox1.click();
        assertEquals(mExcludeGreenColor,mySharedPreferences.getBoolean("no_green", false) ? false: true);  // make sure preference is toggled
        myCheckbox1.click();   // put it back to the original state

        UiObject myCheckbox2 = settings.getChildByInstance(new UiSelector().className("android.widget.CheckBox"),2);
        assertThat(myCheckbox2, notNullValue());
        assertEquals(mSeeGrid,myCheckbox2.isChecked());  // make sure preference match gui
        myCheckbox2.click();
        assertEquals(mSeeGrid,mySharedPreferences.getBoolean("see_grid", false) ? false: true);  // make sure preference is toggled
        myCheckbox2.click();   // put it back to the original state

        UiObject myCheckbox3 = settings.getChildByInstance(new UiSelector().className("android.widget.CheckBox"),3);
        assertThat(myCheckbox3, notNullValue());
        assertEquals(mAccelEnabled,myCheckbox3.isChecked());  // make sure preference match gui
        myCheckbox3.click();
        assertEquals(mAccelEnabled,mySharedPreferences.getBoolean("enable_accel_tilt", false) ? false: true);  // make sure preference is toggled
        myCheckbox3.click();   // put it back to the original state



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