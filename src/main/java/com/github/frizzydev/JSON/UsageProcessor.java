package com.github.frizzydev.JSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UsageProcessor handles the serialization/deserialization of the usage data of
 * devices/controllers.
 *
 * @author Frizzy
 * @version 0.1
 * @since 0.1
 */
public class UsageProcessor {

    final static Logger log = Logger.getLogger( UsageProcessor.class.getName( ) );

    /**
     * Gson object to serialize/deserialize the json data.
     */
    private Gson gson;

    /**
     * The location where usage data is saved to.
     */
    private File jsonFolder;

    /**
     *
     */
    public UsageProcessor( File jsonFolder ) {
        if ( jsonFolder == null )
            throw new InvalidParameterException( "The provided jsonFolder is null." );

        this.jsonFolder = jsonFolder;

        gson = new GsonBuilder( ).setPrettyPrinting().serializeNulls().create( );
    }

    /**
     * Loads the usage data for the specified device name. The returned map can be null if the
     * device name does not have usage data.
     */
    public @Nullable Map< String, Integer > deserializeUsageFor( String deviceName ) {
        Map< String, Integer > usage = null;

        try {
            for ( File jsonFile : Objects.requireNonNull( jsonFolder.listFiles( ) ) ) {
                if ( jsonFile.getName( ).equals( deviceName ) ) {

                    try ( JsonReader reader = gson.newJsonReader( new BufferedReader( new FileReader( jsonFile ) ) ) ) {
                        usage = gson.fromJson( reader , new TypeToken< Map< String, Integer > >( ) {
                        }.getType( ) );
                    } catch ( IOException e ) {
                        log.log( Level.SEVERE , "Could not load usage data." , e );
                    }
                }
            }
        } catch ( NullPointerException e ) {
            log.log( Level.SEVERE , "Json files array was null." , e );
        }

        return usage;
    }

    /**
     * Exports the usage data to the json file of the specified device.
     *
     * @param deviceName The name of the device, this is used to locate the correct file.
     * @param usageMap   The usage data for the device. This information will be exported to the json file.
     */
    public void serializeUsageFor( String deviceName , Map< String, Integer > usageMap ) {

        try {
            for ( File jsonFile : Objects.requireNonNull( jsonFolder.listFiles( ) ) ) {
                if ( jsonFile.getName().equals( deviceName ) ) {
                    try ( JsonWriter writer = gson.newJsonWriter( new FileWriter( jsonFile ) ) ) {
                        gson.toJson( usageMap, new TypeToken< Map< String, Integer > >( ) { }.getType(), writer );
                    } catch ( IOException e ) {
                        log.log( Level.SEVERE, "Serialization failed." , e );
                    }
                }
            }
        } catch ( NullPointerException e ) {
            log.log( Level.SEVERE, "Json files array was null." , e );
        }
    }
}
