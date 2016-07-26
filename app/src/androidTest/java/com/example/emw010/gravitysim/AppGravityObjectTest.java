package com.example.emw010.gravitysim;

//import static org.junit.Assert.*;
import android.app.Application;
import android.test.ApplicationTestCase;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by emw010 on 7/24/16.
 */
@RunWith(AndroidJUnit4.class)
public class AppGravityObjectTest extends ApplicationTestCase<Application> {
        public AppGravityObjectTest() {
            super(Application.class);
        }
//    @Test
    public void test_GravityObject() throws Exception
    {
        GravityObject myGravityObjectA = new GravityObject(
                "Earth", // String name
                1000, // double mass,
                GravityObject.RECT,  // type
                2345, 4567, 7890, // double pos_1, double pos_2, double pos_3,
                12, 13, 14, // double vel_1, double vel_2, double vel_3,
                0, // double angle,
                null );

        assertEquals("Earth", myGravityObjectA.m_name );
        assertEquals(1000, myGravityObjectA.m_mass, 1e-14 );

        assertEquals(2345, myGravityObjectA.m_pos.x, 2345/1e-14 );
        assertEquals(4567, myGravityObjectA.m_pos.y, 4567/1e-14 );
        assertEquals(7890, myGravityObjectA.m_pos.z, 7890/1e-14 );

        assertEquals(12, myGravityObjectA.m_vel.x, 12/1e-14 );
        assertEquals(13, myGravityObjectA.m_vel.y, 13/1e-14 );
        assertEquals(14, myGravityObjectA.m_vel.z, 14/1e-14 );

        assertEquals(0, myGravityObjectA.m_force.x, 0/1e-14 );
        assertEquals(0, myGravityObjectA.m_force.y, 0/1e-14 );
        assertEquals(0, myGravityObjectA.m_force.z, 0/1e-14 );

        assertEquals(1000, myGravityObjectA.m_mass, 1e-14 );
    }

    @Test
    public void test_GravityObject_relative() throws Exception
    {
        GravityObject myGravityObjectA = new GravityObject(
                "Earth", // String name
                1000, // double mass,
                GravityObject.RECT,  // type
                2345, 4567, 7890, // double pos_1, double pos_2, double pos_3,
                12, 13, 14, // double vel_1, double vel_2, double vel_3,
                0, // double angle,
                null );
        GravityObject myGravityObjectB = new GravityObject(
                "Moon", // String name
                2000, // double mass,
                GravityObject.RECT,  // type
                0.01, 0.02, 0.03, // double pos_1, double pos_2, double pos_3,
                0.1, 0.2, 0.3, // double vel_1, double vel_2, double vel_3,
                0, // double angle,
                myGravityObjectA );

        //
        // make sure the base object is unchanged.
        //
        assertEquals("Earth", myGravityObjectA.m_name );
        assertEquals(1000, myGravityObjectA.m_mass, 1e-14 );

        assertEquals(2345, myGravityObjectA.m_pos.x, 2345*1e-14 );
        assertEquals(4567, myGravityObjectA.m_pos.y, 4567*1e-14 );
        assertEquals(0, myGravityObjectA.m_pos.z, 7890*1e-14 );  // note that thiis is zero because point must be on z axis prior to rotation.

        assertEquals(12, myGravityObjectA.m_vel.x, 12*1e-14 );
        assertEquals(13, myGravityObjectA.m_vel.y, 13*1e-14 );
        assertEquals(14, myGravityObjectA.m_vel.z, 14*1e-14 );

        assertEquals(0, myGravityObjectA.m_force.x, 0*1e-14 );
        assertEquals(0, myGravityObjectA.m_force.y, 0*1e-14 );
        assertEquals(0, myGravityObjectA.m_force.z, 0*1e-14 );


        //
        // make sure the relative stuff worked
        //
        assertEquals("Moon", myGravityObjectB.m_name );
        assertEquals(2000, myGravityObjectB.m_mass, 1e-14 );

        assertEquals(2345.01, myGravityObjectB.m_pos.x, 2345*1e-14);
        assertEquals(4567.02, myGravityObjectB.m_pos.y, 4567*1e-14 );
        assertEquals(0, myGravityObjectB.m_pos.z, 7890*1e-14 );

        assertEquals(12.1, myGravityObjectB.m_vel.x, 12*1e-14 );
        assertEquals(13.2, myGravityObjectB.m_vel.y, 13*1e-14 );
        assertEquals(14.3, myGravityObjectB.m_vel.z, 14*1e-14 );

        assertEquals(0, myGravityObjectB.m_force.x, 0*1e-14 );
        assertEquals(0, myGravityObjectB.m_force.y, 0*1e-14 );
        assertEquals(0, myGravityObjectB.m_force.z, 0*1e-14 );

    }

}