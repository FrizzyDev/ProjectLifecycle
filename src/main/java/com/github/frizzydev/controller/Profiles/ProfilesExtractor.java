package com.github.frizzydev.controller.Profiles;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Extracts the profile.json files from the downloaded profiles.zip file.
 *
 * @author FrizzyDev
 * @version 0.1
 * @since 0.1
 */
public class ProfilesExtractor {

    private static final Logger log = Logger.getLogger( ProfilesExtractor.class.getName( ) );

    /**
     *
     */
    public ProfilesExtractor( ) {

    }

    /**
     * Extracts the zip file contents from the provided source path to the provided save path.
     *
     * @param sourcePath The path of the zip file.
     * @param savePath   The path the contents will be extracted to.
     * @return Returns true if this process was successful, false otherwise.
     */
    public boolean extractTo( String sourcePath , String savePath ) throws FileNotFoundException {

        if ( !Files.exists( Path.of( sourcePath ) ) )
            throw new FileNotFoundException( "File at " + sourcePath + " does not exist." );

        try ( ZipInputStream zipInStream = new ZipInputStream( new FileInputStream( sourcePath ) ) ) {
            ZipEntry entry = zipInStream.getNextEntry( );

            while ( entry != null ) {
                String extractPath = savePath + File.separator + entry.getName( );

                /*
                 * There should not be directories in the profile zip file, but this is here
                 * just in case.
                 */
                if ( entry.isDirectory( ) ) {
                    File dir = new File( extractPath );
                    boolean created = dir.mkdir( );

                    if ( !created ) {
                        //TODO: For now we do nothing, but will eventually abort this process.
                    }
                } else {
                    File pFile = new File( extractPath );

                    try ( FileOutputStream zipOutStream = new FileOutputStream( pFile ) ) {
                        byte[] bytesIn = new byte[ 1024 ];
                        int read;
                        while ( ( read = zipInStream.read( bytesIn ) ) != -1 ) {
                            zipOutStream.write( bytesIn , 0 , read );
                        }
                    }
                }

                entry = zipInStream.getNextEntry();
            }
        } catch ( IOException e ) {
            log.log( Level.SEVERE , "Zip extraction failed." , e );
            return false;
        }

        return true;
    }
}
