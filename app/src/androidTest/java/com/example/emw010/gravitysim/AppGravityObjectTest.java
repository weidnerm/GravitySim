package com.example.emw010.gravitysim;

//import static org.junit.Assert.*;
import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Created by emw010 on 7/24/16.
 */
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

}