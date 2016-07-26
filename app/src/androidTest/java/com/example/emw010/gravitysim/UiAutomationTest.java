package com.example.emw010.gravitysim;

/**
 * Created by emw010 on 7/24/16.
 */
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
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
        UiObject2 changedText = mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "content")),500 /* wait 500ms */);
        assertThat(changedText, notNullValue());
//        assertThat("GravitySim", is(equalTo(changedText.getText())));
    }

//    @Test
//    public void testChangeText_newActivity() {
//        // Type text and then press the button.
//        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "editTextUserInput"))
//                .setText(STRING_TO_BE_TYPED);
//        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "activityChangeTextBtn"))
//                .click();
//
//        // Verify the test is displayed in the Ui
//        UiObject2 changedText = mDevice
//                .wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "show_text_view")),
//                        500 /* wait 500ms */);
//        assertThat(changedText.getText(), is(equalTo(STRING_TO_BE_TYPED)));
//    }

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