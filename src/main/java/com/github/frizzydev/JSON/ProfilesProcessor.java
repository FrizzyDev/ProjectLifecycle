package com.github.frizzydev.JSON;

import com.github.frizzydev.controller.Profiles.ControllerProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ProfilesProcessor handles deserializing the json profile files.
 *
 * @author FrizzyDev
 * @version 0.1
 * @since 0.1
 */
public class ProfilesProcessor {

    final static Logger log = Logger.getLogger( ProfilesProcessor.class.getName( ) );

    /**
     * The gson object to deserialize the json files.
     */
    private Gson gson;

    /**
     * The location of the profile files.
     */
    private File profilesFolder;


    /**
     *
     */
    public ProfilesProcessor( File profilesFolder ) {
        if ( profilesFolder == null )
            throw new InvalidParameterException( "The provided profilesFolder is null." );

        this.profilesFolder = profilesFolder;
        gson = new GsonBuilder( ).create( );
    }

    /**
     * Retrieves available controller profiles. As a reminder, profiles are not created by ProjectLifecycle,
     * but rather created by me as I have information exported to me by users.
     */
    public @NotNull Map< String, ControllerProfile > getProfiles( ) {
        Map< String, ControllerProfile > profiles = new LinkedHashMap<>( );

        for ( File profileFile : Objects.requireNonNull( profilesFolder.listFiles( ) ) ) {

            try ( JsonReader reader = gson.newJsonReader( new BufferedReader( new FileReader( profileFile ) ) ) ) {

                ControllerProfile profile = gson.fromJson( reader , ControllerProfile.class );
                profiles.put( profile.getName( ) , profile );

            } catch ( IOException e ) {
                log.log( Level.SEVERE, "Could not load controller profiles.", e );
            }
        }

        return profiles;
    }

}
