package com.example.emw010.gravitysim;

/**
 * Created by emw010 on 4/23/16.
 */
public class SolarSystem {
    public int m_numObjects;
    final static int MAX_OBJECTS = 100;
    final static double GRAVITATIONAL_CONSTANT = 6.6741E-11;

    GravityObject[] m_objects = new GravityObject[MAX_OBJECTS];

    SolarSystem()
    {
        int index;

        m_numObjects = 0;

        //                                                                name     mass (kg)               position (meters)                 velocity(m/s)
        //                                                                                   x         y     z            x    y        z    angle.
        m_objects[ m_numObjects ] = new GravityObject("Sun"           , 1.98892E+30,             0.0,          0.0  ,    0.00     ,      0.0            ,       0.0      , null );  m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Mercury"       ,  3.302E+23 ,      4.6375E+10,     58715.58  ,    7.00     ,     29.124           ,     48.331    , null ); m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Venus"         ,  4.865E+24 ,      1.0741E+11,     35265.65  ,    3.39     ,     54.852          ,      76.680    , null ); m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Earth"         ,  5.974E+24 ,      1.4661E+11,     30381.18  ,    0.00     ,    114.207          ,     348.739    , null ); m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Moon"          ,   7.35E+22 ,      3.8400E+08,      1043.81  ,    5.14     ,      0              ,       0        , m_objects[ m_numObjects-1 ]); m_numObjects++;

        m_objects[ m_numObjects ] = new GravityObject("Mars"          ,  6.419E+23 ,      2.0645E+11,     26527.81  ,    1.85     ,    286.462          ,     49.578     , null); m_numObjects++;
        //m_objects[ m_numObjects ] = new GravityObject("Deimos"        ,   1.80E+15 ,      2.3463E+07,      1351.05  ,    27.58    ,      0              ,       0        , m_objects[ m_numObjects-1 ]); m_numObjects++;
        //m_objects[ m_numObjects ] = new GravityObject("Phobos"        ,   1.08E+16 ,      9.3770E+06,      2152.77  ,    26.04    ,      0              ,       0        , m_objects[ m_numObjects-2 ]); m_numObjects++;


        m_objects[ m_numObjects ] = new GravityObject("Jupiter"       , 1.898E+27  ,      7.4051E+11,     13702.87  ,    1.30     ,    274.197          ,     100.556    , null); m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Io"            ,  8.93E+22  ,      4.2160E+08,     17331.09  ,    2.21     ,      0              ,       0        , m_objects[ m_numObjects-1 ]); m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Europa"        ,  4.80E+22  ,      6.7090E+08,     13738.75  ,    1.79     ,      0              ,       0        , m_objects[ m_numObjects-2 ]); m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Ganymede"      ,  1.48E+23  ,      1.0700E+09,     10878.88  ,    2.21     ,      0              ,       0        , m_objects[ m_numObjects-3 ]); m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Callisto"      ,  1.08E+23  ,      1.8830E+09,      8200.70  ,    2.02     ,      0              ,       0        , m_objects[ m_numObjects-4 ]); m_numObjects++;

        m_objects[ m_numObjects ] = new GravityObject("Saturn"        ,  5.685E+26 ,      1.3494E+12,     10167.73  ,    2.484    ,    338.716          ,    113.715     , null); m_numObjects++;
//   m_objects[ m_numObjects ] = new GravityObject("Dione"         ,   1.1e+21  ,      3.7701E+08,     10024.43  ,26.73+0.02   ,      0              ,       0        , m_objects[ m_numObjects-1 ]); m_numObjects++;
//   m_objects[ m_numObjects ] = new GravityObject("Rhea"          ,   2.3e+21  ,      5.2701E+08,      8478.62  ,26.73+0.35   ,      0              ,       0        , m_objects[ m_numObjects-2 ]); m_numObjects++;
//   m_objects[ m_numObjects ] = new GravityObject("Titan"         ,  1.34e+23  ,      1.2965E+09,      5323.93  ,26.73+0.33   ,      0              ,       0        , m_objects[ m_numObjects-3 ]); m_numObjects++;
//   m_objects[ m_numObjects ] = new GravityObject("Iapetus"       ,   1.6e+21  ,      3.7813E+09,      3117.48  ,26.73+7.52   ,      0              ,       0        , m_objects[ m_numObjects-4 ]); m_numObjects++;
//   m_objects[ m_numObjects ] = new GravityObject("Phoebe"        ,   5.5E+15  ,      1.7886E+10,      1333.89  ,26.73+175.3  ,      0              ,       0        , m_objects[ m_numObjects-5 ]); m_numObjects++;

        m_objects[ m_numObjects ] = new GravityObject("Uranus"        ,  8.683E+25 ,      2.7376E+12,      7122.62 ,     0.77     ,     96.734          ,     74.229     , null); m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Miranda"       ,  6.30E+19  ,      3.8400E+08,      3889.40 ,     90+4.22  ,       0             ,      0      , m_objects[ m_numObjects-1 ]); m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Ariel"         ,  1.27E+21  ,      1.2980E+08,      6689.77 ,     90+0.31  ,       0             ,      0      , m_objects[ m_numObjects-2 ]); m_numObjects++;
        m_objects[ m_numObjects ] = new GravityObject("Umbriel"       ,  1.27E+21  ,      1.9120E+08,      5517.43 ,     90+0.36  ,       0             ,      0      , m_objects[ m_numObjects-3 ]); m_numObjects++;
//   m_objects[ m_numObjects ] = new GravityObject("Titania"       ,  3.49E+21  ,      4.36E+08  ,      3649.12 ,     90+0.1   ,       0             ,      0      , m_objects[ m_numObjects-4 ]); m_numObjects++;
//   m_objects[ m_numObjects ] = new GravityObject("Oberon"        ,  3.03E+21  ,      5.8260E+08,      3154.49 ,     90+0.1   ,       0             ,      0      , m_objects[ m_numObjects-5 ]); m_numObjects++;


        m_objects[ m_numObjects ] = new GravityObject("Pluto"         , 1.320E+22  ,      4.4431E+12,      6118.54  ,   17.14     ,    113.763          ,    110.303        , null); m_numObjects++;
//   m_objects[ m_numObjects ] = new GravityObject("Charon"        ,  1.90E+21  ,      1.9600E+07,       208.93  ,   112.78    ,      0              ,      0      , m_objects[ m_numObjects-1 ] ); m_numObjects++;

        m_objects[ m_numObjects ] = new GravityObject("Neptune"       , 1.024E+26  ,      4.4879E+12,      5450.48  ,    1.769    ,    273.249          ,    131.721      ,   null); m_numObjects++;
//   m_objects[ m_numObjects ] = new GravityObject("Triton"        ,  2.15E+22  ,      3.5470E+08,      4389.68  ,    156.8    ,       0             ,       0     , m_objects[ m_numObjects-1 ]); m_numObjects++;


        // name         ,   mass (kg), radial dist (m), tang Vel (m/s),  incl (degs), arg of perig (angle), long of asc (deg), parent
        //m_objects[ m_numObjects ] = new GravityObject("Sedna"         ,    4.00E+21,        1.14E+13,       4647.82 ,   11.934    ,    311.122          ,     144.514    ); m_numObjects++;
        //m_objects[ m_numObjects ] = new GravityObject("Halley's Comet",  6.5417E+10,      8.9160E+10,     54099.92  ,  162.3      ,    111.332          ,      58.420    ); m_numObjects++;
        //m_objects[ m_numObjects ] = new GravityObject("Quaoar"        ,  2.00E+21  ,        6.27E+12,      4678.08  ,    7.983    ,    154.850          ,     188.791    ); m_numObjects++;
        //m_objects[ m_numObjects ] = new GravityObject("Xena (UB313)"  ,  1.46E+22  ,        5.65E+12,      5818.82  ,   44.187    ,    151.4305         ,     35.8696    ); m_numObjects++;
        //m_objects[ m_numObjects ] = new GravityObject("Gabrielle"     ,  3.62E+14  ,        3.00E+07,       188.32  ,    0        ,      0              ,       0     , m_objects[ m_numObjects-1 ] ); m_numObjects++;
        //m_objects[ m_numObjects ] = new GravityObject("Ceres"         ,  9.50E+20  ,        3.81E+11,     19402.32  ,   10.587    ,     73.271          ,     80.410     ); m_numObjects++;

        //m_objects[ m_numObjects ] = new GravityObject("Mu Arae b"     ,  2.85E+27  ,      1.5483E+11,     33502.95  ,   1         ,       0             ,     0          ); m_numObjects++;




        // Set the overall system momentum to 0.
        resetNetMomentum();
    }


    void computeNewVelocity( double timeInterval )
    {
        int baseObjIndex;
        Vector3D deltaVelocity = new Vector3D();
        double speed;

        for(baseObjIndex = 0; baseObjIndex<m_numObjects ; baseObjIndex++ )
        {
            // Velocity.final = Velocity.initial + Force*t/m

            deltaVelocity.set( m_objects[baseObjIndex].m_force );

            deltaVelocity.multiply( timeInterval );

            deltaVelocity.divide( m_objects[baseObjIndex].m_mass );

            m_objects[baseObjIndex].m_vel.add( deltaVelocity );

            // compute the speed relative to the sun (index 0).
            deltaVelocity.set( m_objects[0].m_vel );
            deltaVelocity.subtract( m_objects[baseObjIndex].m_vel );
            speed = deltaVelocity.getMagnitude();

            if ( speed < m_objects[baseObjIndex].m_min_sol_speed )
            {
                m_objects[baseObjIndex].m_min_sol_speed = speed;
            }
            if ( speed > m_objects[baseObjIndex].m_max_sol_speed )
            {
                m_objects[baseObjIndex].m_max_sol_speed = speed;
            }
        }
    }

    void resetNetMomentum(  )
    {
        int baseObjIndex;
        Vector3D netMomentum = new Vector3D();
        Vector3D tempMomentum = new Vector3D();
        double totalMass = 0;

        netMomentum.reset();

        //
        // Compute the net momentum vector.
        //

        for(baseObjIndex = 0; baseObjIndex<m_numObjects ; baseObjIndex++ )
        {
            // Compute the momentum vector = m*Velocity.vector
            tempMomentum.set( m_objects[baseObjIndex].m_vel );
            tempMomentum.multiply( m_objects[baseObjIndex].m_mass );

            // Add the momentum vector to the netMomentum vector.
            netMomentum.add( tempMomentum );
            totalMass += m_objects[baseObjIndex].m_mass;
        }

        // Compute net system velocity.
        netMomentum.divide( totalMass );
        //netMomentum.dump();

        // Subtract off the net system velocity from each object.
        for(baseObjIndex = 0; baseObjIndex<m_numObjects ; baseObjIndex++ )
        {
            // Subtract the velocity compensation vector.
            m_objects[baseObjIndex].m_vel.subtract( netMomentum );
        }
    }

    void computeNewPosition( double timeInterval )
    {
        int baseObjIndex;
        Vector3D deltaPosition = new Vector3D();
        double distance;

        for(baseObjIndex = 0; baseObjIndex<m_numObjects ; baseObjIndex++ )
        {
            // Position.final = Position.initial + Velocity*time

            deltaPosition.set( m_objects[baseObjIndex].m_vel );

            deltaPosition.multiply( timeInterval );

            m_objects[baseObjIndex].m_pos.add( deltaPosition );
        }
    }

    void processTimeInterval( double tickDuration )
    {
        resetNetForces();
        computeNetForces();
        computeNewVelocity( tickDuration );
        computeNewPosition( tickDuration );
    }

    void computeInteraction( GravityObject object_1, GravityObject object_2 )
    {

        Vector3D distanceV = new Vector3D();
        double distanceS;
        double forceS;

        //F = G*m1*m2/(r*r)

        // Compute the distance vector separating the objects.
        distanceV.set( (object_1.m_pos) );
        distanceV.subtract( (object_2.m_pos) );

        // Compute the scalar distance separating the objects.
        distanceS = distanceV.getMagnitude();

        if ( distanceS == 0 )
        {
            // Maybe combine the objects or something.
        }
        else
        {
            // Compute the gravitational attractive force between the objects.
            forceS = GRAVITATIONAL_CONSTANT * object_1.m_mass * object_2.m_mass / ( distanceS*distanceS);

            // normalize the vector.
            distanceV.divide( distanceV.getMagnitude() );

            // Set the magnitude of the force vector.
            distanceV.multiply( forceS );

            // Apply the force to each of the two objects involved.
            object_1.m_force.subtract( distanceV );
            object_2.m_force.add( distanceV );
        }
    }

    String getGravityObjectInfo( int objectIndex )
    {
        String returnVal = null;

        if ( objectIndex < m_numObjects )
        {
            returnVal = m_objects[objectIndex].m_name;
        }
        else
        {
            returnVal = "Unknown";
        }

        return returnVal;
    }

    int getNumObjects( )
    {
        return ( ( int)m_numObjects );
    }

    void getGravityObjectPosition( int objectIndex, Vector3D vectorPtr )
    {
        if ( objectIndex < m_numObjects )
        {
            vectorPtr = m_objects[objectIndex].m_pos;
        }
    }

    double getGravityObjectX( int objectIndex )
    {
        double retVal = 0;

        if ( objectIndex < m_numObjects )
        {
            retVal = m_objects[objectIndex].m_pos.x;
        }

        return retVal;
    }

    double getGravityObjectY( int objectIndex )
    {
        double retVal = 0;

        if ( objectIndex < m_numObjects )
        {
            retVal = m_objects[objectIndex].m_pos.y;
        }

        return retVal;
    }

    double getGravityObjectZ( int objectIndex )
    {
        double retVal = 0;

        if ( objectIndex < m_numObjects )
        {
            retVal = m_objects[objectIndex].m_pos.z;
        }

        return retVal;
    }

    void resetNetForces( )
    {
        int baseObjIndex;

        for(baseObjIndex = 0; baseObjIndex<m_numObjects ; baseObjIndex++ )
        {
            m_objects[baseObjIndex].m_force.reset();
        }
    }

    void computeNetForces( )
    {
        int baseObjIndex;
        int otherObjectIndex;

        for(baseObjIndex = 0; baseObjIndex<m_numObjects-1 ; baseObjIndex++ )
        {
            for(otherObjectIndex = baseObjIndex+1; otherObjectIndex<m_numObjects ; otherObjectIndex++ )
            {
                //      printf("base=%d; other=%d\n",baseObjIndex,otherObjectIndex);

                // Compute the interaction between these objects.  Both will be updated.
                computeInteraction( m_objects[baseObjIndex], m_objects[otherObjectIndex] );
            }
        }
    }


}
