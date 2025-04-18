package com.github.frizzydev.App;

import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.github.frizzydev.UI.ConnectedDevicesUI;
import com.github.frizzydev.controller.ControllerManager;
import com.github.frizzydev.controller.LController;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
import net.java.games.input.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class of Project Lifecycle.
 *
 * @author FrizzyDev
 * @version 0.1
 * @since 0.1
 */
public class ProjectLifecycle {

    final static Logger log = Logger.getLogger( ProjectLifecycle.class.getName( ) );

    /**
     *
     */
    private List < LController > controllers;

    /**
     *
     */
    private SystemTray lifecycleTray;

    /**
     *
     */
    private ControllerEnvironment environment = ControllerEnvironment.getDefaultEnvironment( );

    /**
     *
     */
    public ProjectLifecycle( ) {
        setUI( );
        start( );

        try {
            setupSystemTray( );
        } catch ( IOException e ) {
            log.log( Level.SEVERE, "System Tray setup failed.", e );
        }

    }

    /**
     * Sets the UI LAF for Project Lifecycle.
     */
    private void setUI( ) {

        try {
            UIManager.setLookAndFeel( new FlatArcDarkIJTheme( ) );
        } catch ( UnsupportedLookAndFeelException e ) {
            log.log( Level.SEVERE , "Settings UI LAF failed." , e );
        }
    }

    /**
     * Starts the boot process.
     */
    private void start( ) {
        Installer installer = new Installer( );

        String reason = installer.install( );

        if ( reason.equals( "SUCCESSFUL" ) || reason.equals( "ALREADY INSTALLED" ) ) {
            System.out.println( "Successful" );
            ControllerManager cManager = new ControllerManager( );
            cManager.loadLControllers( );

            controllers = cManager.getLControllers( );

            for ( LController controller : controllers ) {
                controller.startEventPolling( );
            }

            environment.addControllerListener( new ControllerListener( ) {

                /**
                 * Retrieves the removed controller, stops polling, then removes it from
                 * the LControllers list.
                 */
                @Override
                public void controllerRemoved( ControllerEvent controllerEvent ) {
                    Controller removed = controllerEvent.getController( );

                    for ( LController lc : controllers ) {
                        if ( lc.getController( ).equals( removed ) ) {
                            lc.stopEventPolling( );
                            controllers.remove( lc );
                            break;
                        }
                    }
                }

                /**
                 * Retrieves the added controller and creates a new LController instance.
                 */
                @Override
                public void controllerAdded( ControllerEvent controllerEvent ) {
                    Controller added = controllerEvent.getController( );
                    LController lc = cManager.createLController( added );
                    lc.startEventPolling( );

                    controllers.add( lc );
                }
            } );
        }
    }

    /**
     *
     */
    private void startAllPolling( ) {
        for ( LController lc : controllers ) {
            lc.startEventPolling( );
        }
    }

    /**
     *
     */
    private void stopAllPolling( ) {
        for ( LController lc : controllers ) {
            lc.stopEventPolling( );
        }
    }

    /**
     *
     */
    private void setupSystemTray( ) throws IOException, NullPointerException {
        Image redDot = ImageIO.read( ProjectLifecycle.class.getResourceAsStream( "/main/resources/reddot.png" ) );
        Image greenDot = ImageIO.read( ProjectLifecycle.class.getResourceAsStream( "/main/resources/greendot.png" ) );

        lifecycleTray = SystemTray.get( );
        lifecycleTray.setTooltip( "Project Lifecycle" );
        lifecycleTray.setImage( ImageIO.read( ProjectLifecycle.class.getResourceAsStream( "/main/resources/tempicon.png" ) ) );

        MenuItem status = new MenuItem( );
        status.setText( "Tracking Usage" );
        status.setImage( greenDot );

        status.setCallback( e -> {
            if ( status.getText( ).equals( "Tracking Usage" ) ) {
                status.setText( "Tracking Stopped" );

                if ( redDot != null )
                    status.setImage( redDot );

                stopAllPolling( );
            } else if ( status.getText( ).equals( "Tracking Stopped" ) ) {
                status.setText( "Tracking Usage" );

                if ( greenDot != null )
                    status.setImage( greenDot );

                startAllPolling( );
            }
        } );

        dorkbox.systemTray.Menu menu = lifecycleTray.getMenu();

        menu.add( status );

        menu.add( new JSeparator(  ) );

        menu.add( new MenuItem( "View Usage" , e -> {

        } ) );

        menu.add( new MenuItem( "View Connected Devices" , e -> {
            new ConnectedDevicesUI().showConnectedDevices( controllers );
        } ) );

        menu.add( new JSeparator( ) );

        menu.add( new MenuItem( "Settings" , e -> {

        } ) );

        menu.add( new JSeparator( ) );

        menu.add( new MenuItem( "Quit" , e -> {
            System.exit( 0 );
        } ) ).setShortcut( 'q' );
    }

    public static void main( String[] args ) {
        new ProjectLifecycle( );
    }
}
