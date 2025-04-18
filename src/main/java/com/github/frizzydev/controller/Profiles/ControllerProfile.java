package com.github.frizzydev.controller.Profiles;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * ControllerProfile is a POJO created from a JSON file containing information on a peripheral device.
 * The profile data is loaded from custom json files and the usage data is tracked while Project Lifecycle
 * is running in the background.
 *
 * @author FrizzyDev
 * @version 0.1
 * @since 0.1
 */
public class ControllerProfile {

    /**
     * The name of the device/controller.
     */
    @SerializedName( "DeviceName" )
    private final String name;

    /**
     * The description of the device/controller. JInput does not supply this, this is supplied via the profile system.
     */
    @SerializedName( "DeviceDescription" )
    private final String description;

    /**
     * The manufacturer of the wheel base.
     */
    @SerializedName( "DeviceManufacturer" )
    private final String manufacturer;

    /**
     * The type of device/controller. See {@link net.java.games.input.Controller.Type} for what is supplied by JInput.
     */
    @SerializedName( "DeviceType" )
    private final String type;

    /**
     * The port type the device/controller is connected through. See {@link net.java.games.input.Controller.PortType} for what is supplied by JInput.
     */
    @SerializedName( "DevicePortType" )
    private final String portType;



    /**
     * @param name The name of the device/controller.
     * @param description The description of the device/controller. JInput does not supply this, this is supplied via the profile system.
     * @param manufacturer The manufacturer of the device/controller.
     * @param type The type of device/controller. See {@link net.java.games.input.Controller.Type} for what is supplied by JInput.
     * @param portType The port type the device/controller is connected through. See {@link net.java.games.input.Controller.PortType} for what is supplied by JInput.
     */
    public ControllerProfile( final String name , final String description , final String manufacturer, final String type , final String portType ) {
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.type = type;
        this.portType = type;
    }
    /**
     * Return the name of the controller.
     */
    public final String getName( ) {
        return name;
    }

    /**
     * Returns the description of the controller.
     */
    public final String getDescription( ) {
        return description;
    }

    /**
     * Returns the manufacturer of the controller.
     */
    public final String getManufacturer ( ) {
        return manufacturer;
    }

    /**
     * Returns the type of device the controller is.
     */
    public final String getType( ) {
        return type;
    }

    /**
     * Returns the port type of the device, typically this is USB.
     */
    public final String getPortType( ) {
        return portType;
    }
}
