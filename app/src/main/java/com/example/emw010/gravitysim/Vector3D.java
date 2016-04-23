package com.example.emw010.gravitysim;
import java.lang.*;

/**
 * Created by emw010 on 4/22/16.
 * Represents a 3D physics vector in rectangular coordinate system.
 */
public class Vector3D
{
    /* The values. */
    public double x;
    public double y;
    public double z;

    public void reset()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public void set(double new_x, double new_y, double new_z)
    {
        x = new_x;
        y = new_y;
        z = new_z;
    }

    public void set(Vector3D vector)
    {
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }

    public Vector3D(double new_x, double new_y, double new_z)
    {
        x = new_x;
        y = new_y;
        z = new_z;
    }

    public Vector3D()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public void add(Vector3D B_val)
    {
        x += B_val.x;
        y += B_val.y;
        z += B_val.z;
    }

    public void subtract(Vector3D B_val)
    {
        x -= B_val.x;
        y -= B_val.y;
        z -= B_val.z;
    }

    public double getMagnitude()
    {
        double length;

        length = Math.sqrt(x*x + y*y + z*z );

        return length;
    }

    public void multiply(double multiplier)
    {
        x *= multiplier;
        y *= multiplier;
        z *= multiplier;
    }

    public void divide(double divisor)
    {
        x /= divisor;
        y /= divisor;
        z /= divisor;
    }


    public void rotateAboutZ( double angle_in_degs )
    {
        double old_x = x;
        double old_y = y;

        double new_x;
        double new_y;

        double angle_in_radians = Math.toRadians(angle_in_degs);

        // perform the rotation into the new coordinate system.
        new_x = old_x*Math.cos(angle_in_radians) - old_y*Math.sin(angle_in_radians);
        new_y = old_x*Math.sin(angle_in_radians) + old_y*Math.cos(angle_in_radians);

        // copy the results back to the vector.
        x = new_x;
        y = new_y;


    }

    public void rotateAboutX( double angle_in_degs )
    {
        double old_x = y;
        double old_y = z;

        double new_x;
        double new_y;

        double angle_in_radians = Math.toRadians(angle_in_degs);

        // perform the rotation into the new coordinate system.
        new_x = old_x*Math.cos(angle_in_radians) - old_y*Math.sin(angle_in_radians);
        new_y = old_x*Math.sin(angle_in_radians) + old_y*Math.cos(angle_in_radians);

        // copy the results back to the vector.
        y = new_x;
        z = new_y;

    }

    public void dump(double normalizeFactor )
    {
        System.out.format("( %g, %g, %g )  Mag=%g\n",x/normalizeFactor,y/normalizeFactor,z/normalizeFactor,getMagnitude()/normalizeFactor );
    }

}
