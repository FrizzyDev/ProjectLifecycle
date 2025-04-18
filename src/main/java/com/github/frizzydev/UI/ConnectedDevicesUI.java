package com.github.frizzydev.UI;

import com.github.frizzydev.controller.LController;
import de.milchreis.uibooster.UiBooster;
import de.milchreis.uibooster.model.ListElement;

import java.util.ArrayList;
import java.util.List;

public class ConnectedDevicesUI {

    public ConnectedDevicesUI ( ) {

    }

    public void showConnectedDevices ( List < LController > controllers ) {

        new UiBooster(  )
                .createForm( "Connected Devices" )
                .addList( "Connected Devices", getNames( controllers ) )
                .show();
    }

    private ListElement[] getNames ( List < LController > controllers ) {
        System.out.println( "Controllers Size: " + controllers.size() );
        ListElement[] elements = new ListElement[controllers.size()];

        for ( int i = 0; i < controllers.size(); i++ ) {
            LController controller = controllers.get( i );
            ListElement element = new ListElement( controller.getControllerName(), "Tracking Usage: " + Boolean.toString( controller.isPolling() ) );

            elements[i] = element;
        }

        return elements;
    }
}
