package com.github.frizzydev.controller;

import com.github.frizzydev.App.ProjectLifecycle;
import com.github.frizzydev.App.Testing;
import com.github.frizzydev.JSON.ProfilesProcessor;
import com.github.frizzydev.JSON.UsageProcessor;
import com.github.frizzydev.controller.Profiles.ControllerProfile;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.DirectAndRawInputEnvironmentPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * @author FrizzyDev
 * @version 0.1
 * @since 0.1
 */
public class ControllerManager {

    final static Logger log = Logger.getLogger( ControllerManager.class.getName( ) );

    /**
     * The preferences object that tracks settings for Project Lifecycle.
     */
    final Preferences preferences = Preferences.userNodeForPackage( ProjectLifecycle.class );

    /**
     * The folder that contains the profiles folder.
     */
    private File profilesFolder;

    /**
     * The folder that contains the usage data json files.
     */
    private File jsonFolder;

    /**
     * A list of LControllers.
     */
    private final List < LController > lControllers;

    /**
     *
     */
    public ControllerManager( ) {
        String profilesPath = preferences.get( "profiles-location" , "Unavailable" );
        String jsonPath = preferences.get( "json-location" , "Unavailable" );
        System.out.println( "Profiles Path: " + profilesPath );
        System.out.println( "Json Path: " + jsonPath );

        if ( !profilesPath.equals( "Unavailable" ) )
            profilesFolder = new File( profilesPath );

        if ( !jsonPath.equals( "Unavailable" ) )
            jsonFolder = new File( jsonPath );

        lControllers = new LinkedList <>( );
    }

    /**
     * Returns the list of lControllers.
     */
    public List < LController > getLControllers( ) {
        return lControllers;
    }

    /**
     * Retrieves all controllers currently connected to the system and loads the profiles ( if available ) for the controllers
     * and the usage data. If usage data is not available during this process, an empty map will be created so that data
     * can be tracked in the future and usage data json file can be created.
     */
    public void loadLControllers( ) {
        ProfilesProcessor pp = new ProfilesProcessor( profilesFolder );
        UsageProcessor uP = new UsageProcessor( jsonFolder );

        Map < String, ControllerProfile > profiles = pp.getProfiles( );
        /*
         * Retrieves all the connected controllers/devices but removes
         * any duplicated devices.
         */
        List < Controller > controllers = getSortedControllers( );

        /*
         * Load a list of device exclusions that we do not want to track.
         * Usually this is devices such as HID-compliant mouse or devices that don't have
         * buttons to track like Corsair Commander Core or Aura lighting. The exclusion list
         * should in theory grow larger over time.
         */
        List < String > exclusions = getExclusions( );

        for ( Controller c : controllers ) {
            if ( !exclusions.contains( c.getName( ) ) || c.getType() != Controller.Type.KEYBOARD || c.getType() != Controller.Type.MOUSE ) {
                ControllerProfile profile = profiles.get( c.getName( ) );

                Map < String, Integer > usageMap = uP.deserializeUsageFor( c.getName( ) );
                LController lController = new LController( c , profile );
                lController.setUsageMap( usageMap );

                this.lControllers.add( lController );
            }
        }
    }

    public LController createLController ( Controller controller ) {
        //TODO
        return null;
    }

    /**
     * Returns a list of devices/controllers that should not be enumerated in loadControllers ( ).
     * Eventually the exclusions.txt file will be moved to an online repo that can be pulled from as needed.
     */
    private List < String > getExclusions( ) {
        try ( BufferedReader inStream = new BufferedReader( new InputStreamReader( Objects.requireNonNull( Testing.class.getResourceAsStream( "/main/resources/Exclusions.txt" ) ) ) ) ) {
            return inStream.lines( ).toList( );
        } catch ( IOException | NullPointerException e ) {
            log.log( Level.SEVERE , "Failed to load device exclusions." , e );
            return Collections.emptyList( );
        }
    }

    /**
     * Retrieves all connected controllers/devices and removes duplicates.
     * This can potentially slow down in the future if someone has a lot of different
     * connected devices, but for now it'll suffice.
     */
    private List < Controller > getSortedControllers( ) {
        DirectAndRawInputEnvironmentPlugin plugin = new DirectAndRawInputEnvironmentPlugin();

        Controller[] controllers = plugin.getControllers();
        List < Controller > sorted = new ArrayList <>( );

        for ( Controller c1 : controllers ) {
            boolean doNotAdd = false;

            if ( !c1.getName( ).toLowerCase( ).contains( "keyboard" ) ||
                    c1.getType( ) != Controller.Type.KEYBOARD ||
                    !c1.getName( ).toLowerCase( ).contains( "mouse" ) ||
                    c1.getType( ) != Controller.Type.MOUSE ) {

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
