package com.example.emw010.gravitysim;


/**
 * Created by emw010 on 4/23/16.
 */
public class GravityObject
{
    final static int MAX_NAME_LENGTH = 16;
    final static int RECT = 0;
    final static int POLAR = 1;

    String m_name;
    double m_mass;
    Vector3D m_pos = new Vector3D();
    Vector3D m_vel = new Vector3D();
    Vector3D m_force = new Vector3D();
    double m_max_sol_dist;
    double m_min_sol_dist;
    double m_max_sol_speed;
    double m_min_sol_speed;



    GravityObject( String name, double mass, int type, double pos_1, double pos_2, double pos_3, double vel_1, double vel_2, double vel_3, double angle, GravityObject baseObjectPtr )
    {
        // Save the name.
        m_name = name;

        m_mass = mass;

        m_pos.x = 0;
        m_pos.y = 0;
        m_pos.z = 0;

        m_vel.x = 0;
        m_vel.y = 0;
        m_vel.z = 0;

        if ( type == RECT )
        {
            // If we are specified relative to another object, then add its vector to ours.
            if ( baseObjectPtr != null )
            {
                m_pos.x += baseObjectPtr.m_pos.x;
                m_pos.y += baseObjectPtr.m_pos.y;
                m_pos.z += baseObjectPtr.m_pos.z;

                m_vel.x += baseObjectPtr.m_vel.x;
                m_vel.y += baseObjectPtr.m_vel.y;
                m_vel.z += baseObjectPtr.m_vel.z;
            }

            m_pos.x += pos_1*Math.cos(Math.toRadians( angle )) ;
            m_pos.y += pos_2;
            m_pos.z += pos_1*Math.sin(Math.toRadians( angle ));

            m_vel.x += vel_1;
            m_vel.y += vel_2;
            m_vel.z += vel_3;
        }
        else if ( type == POLAR )
        {
            // If we are specified relative to another object, then add its vector to ours.
            if ( baseObjectPtr != null )
            {
                m_pos.x += baseObjectPtr.m_pos.x;
                m_pos.y += baseObjectPtr.m_pos.y;
                m_pos.z += 0;

                m_vel.x += baseObjectPtr.m_vel.x;
                m_vel.y += baseObjectPtr.m_vel.y;
                m_vel.z += baseObjectPtr.m_vel.z;
            }

            // pos1 = r in xy plane.
            // pos2 = angle from x axis of point.
            // pos3 = unused.

            m_pos.x += pos_1*Math.cos(Math.toRadians( pos_2 )) ;
            m_pos.y += pos_1*Math.sin(Math.toRadians( pos_2 ));
            m_pos.z += 0;

            m_vel.x = vel_1;
            m_vel.y = vel_2;
            m_vel.z = vel_3;
        }

        m_min_sol_speed = 9.99E+99;
        m_min_sol_dist  = 9.99E+99;
        m_max_sol_speed = 0;
        m_max_sol_dist  = 0;

    }

    GravityObject( String name, double mass, double radialDistance, double tangentialVelocity, double inclination, double omegaArgOfPerigee, double capOmegaLongOfAscNode, GravityObject baseObjectPtr )
    {


        double xpos,ypos,zpos;
        double xpos1,ypos1,zpos1;
        double xpos2,ypos2,zpos2;
        double xvel,yvel,zvel;
        double xvel1,yvel1,zvel1;
        double xvel2,yvel2,zvel2;


        // Save the name.
        m_name = name;

        m_mass = mass;

        m_pos.x = radialDistance;
        m_pos.y = 0;
        m_pos.z = 0;

        m_vel.x = 0;
        m_vel.y = tangentialVelocity;
        m_vel.z = 0;

        // rotate the position and velocity vectors according to the argument of the perigee in the plane of rotation (initially the plane of ecliptic too).
        m_pos.rotateAboutZ( omegaArgOfPerigee );
        m_vel.rotateAboutZ( omegaArgOfPerigee );


        // rotate the system along the x axis by the inclination angle.
        m_pos.rotateAboutX( -1*inclination );
        m_vel.rotateAboutX( -1*inclination );

        // rotate the system about the Z axis to accommodate the longitude of node of ascention.
        // (i.e. rotate the line of intersection of ecliptic and plane of orbit )
        m_pos.rotateAboutZ( capOmegaLongOfAscNode );
        m_vel.rotateAboutZ( capOmegaLongOfAscNode );


        // If we are specified relative to another object, then add its vector to ours.
        if ( baseObjectPtr != null )
        {
            m_pos.x += baseObjectPtr.m_pos.x;
            m_pos.y += baseObjectPtr.m_pos.y;
            m_pos.z += baseObjectPtr.m_pos.z;

            m_vel.x += baseObjectPtr.m_vel.x;
            m_vel.y += baseObjectPtr.m_vel.y;
            m_vel.z += baseObjectPtr.m_vel.z;
        }

        m_min_sol_speed = 9.99E+99;
        m_min_sol_dist  = 9.99E+99;
        m_max_sol_speed = 0;
        m_max_sol_dist  = 0;

    }

}
