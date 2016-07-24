package com.example.emw010.gravitysim;

import android.app.Application;
import android.test.ApplicationTestCase;
//import org.junit.Test;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }



//    @Test
    public void test_vector_constructor_zero() throws Exception
    {
        Vector3D myvector;
        myvector = new Vector3D(0,0,0);

        assertEquals(0, myvector.x, 0.000000000000001 );
        assertEquals(0, myvector.y, 0.000000000000001 );
        assertEquals(0, myvector.z, 0.000000000000001 );
    }

}