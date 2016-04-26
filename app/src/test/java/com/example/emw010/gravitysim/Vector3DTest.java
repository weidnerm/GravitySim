package com.example.emw010.gravitysim;

import org.junit.Test;
import com.example.emw010.gravitysim.*;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class Vector3DTest {

    @Test
    public void test_vector_constructor_zero() throws Exception
    {
        Vector3D myvector;
        myvector = new Vector3D(0,0,0);

        assertEquals(0, myvector.x, 0.000000000000001 );
        assertEquals(0, myvector.y, 0.000000000000001 );
        assertEquals(0, myvector.z, 0.000000000000001 );
    }

    @Test
    public void test_vector_constructor_default() throws Exception
    {
        Vector3D myvector;
        myvector = new Vector3D();

        assertEquals(0, myvector.x, 0.000000000000001 );
        assertEquals(0, myvector.y, 0.000000000000001 );
        assertEquals(0, myvector.z, 0.000000000000001 );
    }

    @Test
    public void test_vector_constructor_nonzero() throws Exception
    {
        Vector3D myvector;
        myvector = new Vector3D(6.02e+23,3.14159265358979, 1.41421356237309);

        assertEquals(6.02e+23, myvector.x, 10e+10 );
        assertEquals(3.14159265358979, myvector.y, 0.000000000000001 );
        assertEquals(1.41421356237309, myvector.z, 0.000000000000001 );
    }

    @Test
    public void test_vector_set() throws Exception
    {
        Vector3D myvector;
        myvector = new Vector3D(0,0,0);

        myvector.set(1.1, 2.2, 3.3);

        assertEquals(1.1, myvector.x, 0.000000000000001 );
        assertEquals(2.2, myvector.y, 0.000000000000001 );
        assertEquals(3.3, myvector.z, 0.000000000000001 );
    }

    @Test
    public void test_vector_reset() throws Exception
    {
        Vector3D myvector;
        myvector = new Vector3D(6.02e+23,3.14159265358979, 1.41421356237309);

        myvector.reset();

        assertEquals(0, myvector.x, 0.000000000000001 );
        assertEquals(0, myvector.y, 0.000000000000001 );
        assertEquals(0, myvector.z, 0.000000000000001 );
    }

    @Test
    public void test_vector_add() throws Exception
    {
        Vector3D myvectorA = new Vector3D(1,2,3);
        Vector3D myvectorB = new Vector3D(100,200,300);

        myvectorA.add(myvectorB);

        assertEquals(101, myvectorA.x, 0.000000000000001 );
        assertEquals(202, myvectorA.y, 0.000000000000001 );
        assertEquals(303, myvectorA.z, 0.000000000000001 );
    }

    @Test
    public void test_vector_subtract() throws Exception
    {
        Vector3D myvectorA = new Vector3D(101,202,303);
        Vector3D myvectorB = new Vector3D(100,200,300);

        myvectorA.subtract(myvectorB);

        assertEquals(1, myvectorA.x, 0.000000000000001 );
        assertEquals(2, myvectorA.y, 0.000000000000001 );
        assertEquals(3, myvectorA.z, 0.000000000000001 );
    }

    @Test
    public void test_vector_getMagnitude() throws Exception
    {
        Vector3D myvectorA = new Vector3D(101,202,303);

        double result = myvectorA.getMagnitude();

        assertEquals(377.907396064, result, 0.000000001 );
    }

    @Test
    public void test_vector_multiply() throws Exception
    {
        Vector3D myvectorA = new Vector3D(1,2,3);

        myvectorA.multiply(10000);

        assertEquals(10000, myvectorA.x, 0.00000000001 );
        assertEquals(20000, myvectorA.y, 0.00000000001 );
        assertEquals(30000, myvectorA.z, 0.00000000001 );
    }

    @Test
    public void test_vector_divide() throws Exception
    {
        Vector3D myvectorA = new Vector3D(100,200,300);

        myvectorA.divide(2);

        assertEquals(50, myvectorA.x, 0.00000000001 );
        assertEquals(100, myvectorA.y, 0.00000000001 );
        assertEquals(150, myvectorA.z, 0.00000000001 );
    }

    @Test
    public void test_vector_set_vector() throws Exception
    {
        Vector3D myvectorA = new Vector3D(1,2,3);
        Vector3D myvectorB = new Vector3D(100,200,300);

        myvectorA.set(myvectorB);

        assertEquals(100, myvectorA.x, 0.000000000000001 );
        assertEquals(200, myvectorA.y, 0.000000000000001 );
        assertEquals(300, myvectorA.z, 0.000000000000001 );
    }

    @Test
    public void test_vector_rotateAboutZ() throws Exception
    {
        Vector3D myvectorA = new Vector3D(1,0,0);

        myvectorA.rotateAboutZ(90);

        assertEquals(0, myvectorA.x, 0.000000000000001 );
        assertEquals(1, myvectorA.y, 0.000000000000001 );
        assertEquals(0, myvectorA.z, 0.000000000000001 );
    }

    @Test
    public void test_vector_rotateAboutX() throws Exception
    {
        Vector3D myvectorA = new Vector3D(1,0,0);

        myvectorA.rotateAboutX(90);

        assertEquals(1, myvectorA.x, 0.000000000000001 );
        assertEquals(0, myvectorA.y, 0.000000000000001 );
        assertEquals(0, myvectorA.z, 0.000000000000001 );

        myvectorA.dump(1);
    }




}