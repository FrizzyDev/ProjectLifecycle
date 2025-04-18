package com.github.frizzydev.App;

import net.java.games.input.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Testing {

    public static void main( String[] args ) throws IOException {
        DirectAndRawInputEnvironmentPlugin plugin = new DirectAndRawInputEnvironmentPlugin();


        Controller[] controllers = plugin.getControllers();

        for ( Controller c : controllers ) {
            System.out.println( "Controller Name: " + c.getName() );
            System.out.println( "Controller Type: " + c.getType().toString() );
        }
    }

    private static void processPoll( Controller controller, Event event , Event previousEvent ) {
        Component comp = event.getComponent( );
        float pollData = comp.getPollData( );

        if ( comp.getIdentifier( ) instanceof Component.Identifier.Axis ) {

            /*
             * For now, we are not handling axis events for steering wheels.
             * My theory is due to the high encoding resolutions of steering axes, it constantly
             * fluctuates a very small amount which sets off events constantly. In my testing, it
             * appears the poll data fluctuates between 3 and 4 consistent numbers.
             *
             * I also do not see the need to track wheel usage at the moment, as a full rotation to -1.0f
             * to 1.0f will likely seldom occur compared to other axis such as flight sticks, pedals, or handbrakes.
             */
            if ( controller.getType() != Controller.Type.WHEEL ) {
                if ( pollData == 1.0f ) {
                    System.out.println( "Incrementing usage of: " + comp.getName( ) );
                    System.out.println( "Component Identifier: " + comp.getIdentifier( ).toString( ) );
                    System.out.println( "Poll Data: " + comp.getPollData( ) );
                }
            }
        } else if ( comp.getIdentifier( ) instanceof Component.Identifier.Button ) {
            if ( pollData == 0.0f ) {
                System.out.println( "Incrementing usage of: " + comp.getName( ) );
                System.out.println( "Component Identifier: " + comp.getIdentifier( ).toString( ) );
                System.out.println( "Poll Data: " + comp.getPollData( ) );
            }
        } else if ( comp.getIdentifier( ) instanceof Component.Identifier.Key ) {
            //TODO
        }
    }

    private static boolean isAxes( Component.Identifier identifier ) {


        return false;
    }

    private static List < String > getExclusions( ) {
        List < String > toReturn = null;
        try ( BufferedReader inStream = new BufferedReader( new InputStreamReader( Testing.class.getResourceAsStream( "/main/resources/Exclusions.txt" ) ) ) ) {
            toReturn = inStream.lines( ).collect( Collectors.toList( ) );
        } catch ( IOException e ) {
            e.printStackTrace( );
        }

        return toReturn;
    }

    private static List < Controller > getSortedControllers( ) {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment( ).getControllers( );
        List < String > exclusions = getExclusions( );
        List < Controller > sorted = new ArrayList <>( );

        for ( Controller c1 : controllers ) {
            if ( !exclusions.contains( c1.getName( ) ) ) {
                boolean doNotAdd = false;

                for ( Controller c2 : sorted ) {
                    if ( c2.getName( ).equals( c1.getName( ) ) ) {
                        doNotAdd = true;
                    }
                }

                if ( !doNotAdd ) {
                    sorted.add( c1 );
                }
            }
        }

        return sorted;
    }
}
