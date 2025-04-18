package com.github.frizzydev.App;

import com.github.frizzydev.controller.Profiles.ProfilesDownloader;
import com.github.frizzydev.controller.Profiles.ProfilesExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Installs the directories and files needed for Project Lifecycle to the users AppData folder.
 *
 * @author FrizzyDev
 * @version 0.1
 * @since 0.1
 */
public class Installer {

    final static Logger log = Logger.getLogger( Installer.class.getName( ) );

    /**
     * Preferences node.
     */
    final Preferences preferences = Preferences.userNodeForPackage( ProjectLifecycle.class );

    /**
     *
     */
    public Installer( ) {
    }

    /**
     * Installs Project Lifecycle.
     *
     * @return Returns if the install was SUCCESSFUL, FAILED, or ALREADY INSTALLED.
     */
    public String install( ) {

        if ( !preferences.getBoolean( "installed" , false ) ) {
            String installPath = buildInstallDirectory( );
            System.out.println( "Install Path: " + installPath );

            if ( !installPath.equals( "Unavailable" ) ) {

                File installDirectory = new File( installPath );

                if ( !installDirectory.exists( ) ) {

                    boolean mainCreated = installDirectory.mkdir( );

                    if ( !mainCreated ) {
                        return "FAILED";
                    }
                } else {
                    File profilesFolder = new File( installPath + File.separator + "Profiles" );
                    File jsonFolder = new File( installPath + File.separator + "JSON" );

                    preferences.put( "profiles-location" , profilesFolder.getAbsolutePath( ) );
                    preferences.put( "json-location" , jsonFolder.getAbsolutePath( ) );
                    preferences.putBoolean( "installed" , true );
                    System.out.println( "Profiles Folder: " + profilesFolder );
                    System.out.println( "Json Folder: " + jsonFolder );

                    boolean profilesCreated = profilesFolder.mkdir( );
                    boolean jsonCreated = jsonFolder.mkdir( );

                    if ( !profilesCreated && !jsonCreated ) {
                        return "FAILED";
                    } else {
                        //TODO Make Downloader and Extractor working

                        ProfilesDownloader downloader = new ProfilesDownloader( );
                        String savedPath = downloader.download( profilesFolder.getAbsolutePath( ) );

                        if ( !savedPath.equals( "Failed" ) ) {

                            ProfilesExtractor extractor = new ProfilesExtractor( );
                            try {
                                extractor.extractTo( savedPath , profilesFolder.getAbsolutePath( ) );
                            } catch ( FileNotFoundException e ) {
                                log.log( Level.SEVERE , "Extraction process failed." , e );
                            }



                            return "SUCCESSFUL";
                        }
                    }
                }
            }
        }

        return "ALREADY INSTALLED";
    }

    /**
     * Builds the Project Lifecycle app folder path based off the users operating system.
     */
    private String buildInstallDirectory( ) {
        final String defaultStr = "Unavailable";

        String OS = System.getProperty( "os.name" ).toLowerCase( );

        //Platform support for Mac and Linux will come later
        if ( isWindows( OS ) ) {
            String home = System.getProperty( "user.home" );
            return home + File.separator + "AppData" + File.separator + "Local" + File.separator + "Project LifeCycle";
        } else if ( isMac( OS ) ) {
            //TODO: Add Mac Support
            return defaultStr;
        } else if ( isLinux( OS ) ) {
            //TODO: Add Linux Support
            return defaultStr;
        } else {
            return defaultStr;
        }
    }

    /**
     * Returns true if the users Operating System is Windows based.
     */
    private boolean isWindows( String OS ) {
        return OS.contains( "win" );
    }

    /**
     * Returns true if the users Operating System is macintosh based.
     */
    private boolean isMac( String OS ) {
        return OS.contains( "mac" );
    }

    /**
     * Returns true if the users Operating System is Linux based.
     */
    private boolean isLinux( String OS ) {
        return OS.contains( "nix" ) || OS.contains( "nux" ) || OS.contains( "aix" );
    }
}
