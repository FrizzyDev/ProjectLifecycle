package com.github.frizzydev.controller;

import com.github.frizzydev.controller.Profiles.ControllerProfile;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Wraps a JInput Controller instance with a ControllerProfile to provide accurate information
 * in the event JInput does NOT detect the controller type, port type, and port number. The internal
 * ControllerProfile will also track usage information that will be called upon to update the JSON usage
 * files.
 *
 * @author Frizzy
 * @version 0.1
 * @since 0.1
 */
public class LController {

    /**
     * The JInput controller instance.
     */
    private final Controller controller;

    /**
     * A ControllerProfile contains the information provided by a JSON
     * profile file to give access to more accurate information.
     * <br>
     * ControllerProfile information is manually created as JInput does not
     * always provide the correct information.
     */
    private final ControllerProfile profile;

    /**
     *
     */
    private final LControllerPoller cPoller;

    /**
     * The usage map contains the usage information of device buttons, keys, or axes.
     * The String key is the name/title/letter of the button and the integer value is
     * the number of uses of that specific action.
     */
    private Map < String, Integer > usageMap;

    /**
     * @param c       The JInput Controller instance.
     * @param profile The profile loaded for the Controller.
     */
    public LController( Controller c , ControllerProfile profile ) {
        this.controller = c;
        this.profile = profile;
        this.cPoller = new LControllerPoller( c );
    }

    /**
     *
     */
    public synchronized void startEventPolling ( ) {
        cPoller.startPolling();
    }

    /**
     *
     */
    public synchronized void stopEventPolling ( ) {
        cPoller.stopPolling();
    }

    public synchronized boolean isPolling ( ) {
        return cPoller.isPolling( );
    }

    /**
     * Sets the usage map for this ControllerProfile.
     */
    public void setUsageMap( Map < String, Integer > map ) {
        this.usageMap = map;
    }

    /**
     * Returns the controller wrapped in the LController instance.
     */
    public final Controller getController( ) {
        return controller;
    }

    /**
     * Returns the ControllerProfile for the LController.
     */
    public final ControllerProfile getProfile( ) {
        return profile;
    }

    /**
     * Returns the name of the controller. This should always be specified.
     */
    public final String getControllerName( ) {
        return controller.getName( );
    }

    /**
     * Returns the description of the device. This is only supplied by ControllerProfiles and
     * not JInput. If the profile is null, "Unavailable" will be returned.
     *
     */
    public final String getControllerDescription( ) {
        return profile != null ? profile.getDescription( ) : "Unavailable";
    }

    /**
     * Returns the manufacturer of the device. This is only supplied by ControllerProfiles and
     * not JInput. If the profile is null, "Unavailable" will be returned.
     */
    public final String getControllerManufacturer ( ) {
        return profile != null ? profile.getManufacturer() : "Unavailable";
    }

    /**
     * Returns the type of controller. If it is not unknown, as JInput does detect some devices
     * types correctly, that result from the Controller instance will be returned. If JInput set
     * the type to Unknown, this will attempt to return the name provided by the ControllerProfile,
     * if a profile has been loaded for this controller.
     */
    public final String getControllerType( ) {
        String type = controller.getType( ).toString( );

        if ( controller.getType( ).toString( ).equals( "Unknown" ) ) {
            return profile != null ? profile.getName( ) : "Unknown";
        } else {
            return type;
        }
    }

    /**
     * Returns the port type of the device/controller.
     */
    public final String getControllerPortType( ) {
        String type = controller.getPortType( ).toString( );

        if ( controller.getPortType().toString().equals( "Unknown" ) ) {
            return profile != null ? profile.getPortType() : "Unknown";
        } else {
            return type;
        }

    }

    /**
     * Returns the usage map of the device/controller.
     */
    public final Map < String, Integer > getUsageMap( ) {
        return usageMap;
    }


    /**
     * A class that launches a virtual thread to poll events on this LController.
     *
     * @author FrizzyDev
     * @version 0.1
     * @since 0.1
     */
    class LControllerPoller {


        /**
         * The JInput controller instance that will be polled for events.
         */
        private final Controller controller;

        /**
         * Boolean flag determining if the virtual thread should
         * continue polling the controller.
         */
        private volatile boolean polling = false;

        /**
         * Creates the LControllerPoller.
         */
        public LControllerPoller( final Controller controller ) {
            this.controller = controller;
        }

        /**
         *
         * @return
         */
        public synchronized boolean isPolling ( ) {
            return polling;
        }

        /**
         * Starts the polling process.
         */
        public void startPolling( ) {
            polling = true;

            System.out.println( "Starting polling for: " + controller.getName() );

            Thread thread = Thread.ofVirtual( ).name( controller.getName() ).start( ( ) -> {
                while ( polling ) {
                    controller.poll( );

                    Event event = new Event( );
                    Event previousEvent = new Event( );
                    EventQueue queue = controller.getEventQueue( );

                    while ( queue.getNextEvent( event ) ) {

                        if ( event.getComponent( ).equals( previousEvent.getComponent( ) ) ) {

                            final long eventMS = TimeUnit.NANOSECONDS.toMillis( event.getNanos( ) );

                            if ( eventMS != TimeUnit.NANOSECONDS.toMillis( previousEvent.getNanos( ) ) ) {

                                float pollData = event.getComponent( ).getPollData( );

                                if ( pollData != previousEvent.getComponent( ).getPollData( ) ) {
                                    processPoll( controller , event );
                                }
                            }
                        } else {
                            processPoll( controller , event );
                        }

                        Event prev = new Event( );
                        prev.set( event.getComponent( ) , event.getValue( ) , event.getNanos( ) );

                        previousEvent = prev;
                    }
                }
            } );
        }

        /**
         * Stops the polling process.
         */
        public synchronized void stopPolling( ) {
            polling = false;
        }

        /**
         * Processes the poll event.
         */
        private void processPoll( Controller controller , Event event ) {
            Component comp = event.getComponent( );
            Component.Identifier identifier = comp.getIdentifier( );

            float pollData = comp.getPollData( );

            if ( identifier instanceof Component.Identifier.Axis ) {

                /*
                 * For now, we are not handling axis events for steering wheels.
                 * My theory is due to the high encoding resolutions of steering axes, it constantly
                 * fluctuates a very small amount which sets off events constantly. In my testing, it
                 * appears the poll data fluctuates between 3 and 4 consistent numbers.
                 *
                 * I also do not see the need to track wheel usage at the moment, as a full rotation to -1.0f
                 * to 1.0f will likely seldom occur compared to other axis such as flight sticks, pedals, or handbrakes.
                 */
                if ( controller.getType( ) != Controller.Type.WHEEL ) {
                    if ( pollData == 1.0f ) {
                        System.out.println( "Incrementing usage of: " + comp.getName( ) );
                        System.out.println( "Component Identifier: " + comp.getIdentifier( ).toString( ) );
                        System.out.println( "Poll Data: " + comp.getPollData( ) );

                        increment( identifier.getName() );
                    }
                }
            } else if ( identifier instanceof Component.Identifier.Button ) {
                if ( pollData == 0.0f ) {
                    System.out.println( "Incrementing usage of: " + comp.getName( ) );
                    System.out.println( "Component Identifier: " + comp.getIdentifier( ).toString( ) );
                    System.out.println( "Poll Data: " + comp.getPollData( ) );

                    increment( identifier.getName() );
                }
            } else if ( identifier instanceof Component.Identifier.Key ) {
                if ( pollData == 0.0f ) {
                    System.out.println( "Incrementing usage of: " + comp.getName( ) );
                    System.out.println( "Component Identifier: " + comp.getIdentifier( ).toString( ) );
                    System.out.println( "Poll Data: " + comp.getPollData( ) );

                    increment( identifier.getName() );
                }
            }
        }
    }

    /**
     * Increments the usage count of the provided component identifier.
     */
    private void increment ( String identifierName ) {
        if ( usageMap == null ) {
            usageMap = new HashMap <>();
        }

        int currentValue = usageMap.getOrDefault( identifierName , 0 );

        usageMap.put( identifierName , ++currentValue );
    }
}
