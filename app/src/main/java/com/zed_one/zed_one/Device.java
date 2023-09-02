package com.zed_one.zed_one;

import java.util.HashMap;
import java.util.TreeMap;

public class Device {
    private  String Name,DeviceID,Serial_Code;
    private HashMap<String,Coordinates> Loc;

    public Device(String Name,String deviceID, HashMap<String,Coordinates> Loc) {
        this.Name = Name;
        this.DeviceID=deviceID;
        this.Loc =  Loc;
    }
    public Device(String Name,String deviceID , String SCode) {
        this.Name = Name;
        this.DeviceID=deviceID;
        this.Loc =  new HashMap<>();
        this.Serial_Code =SCode;
    }
    public Device() {
        // Empty constructor required for Firebase
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public HashMap<String,Coordinates> getLoc() {
        return Loc;
    }
    public TreeMap<String,Coordinates> getOrderedLoc() {
        TreeMap<String,Coordinates> m = new TreeMap<>(Loc);
        return m;
    }
    public void setLoc(HashMap<String,Coordinates> loc) {
        Loc = loc;
    }


    public Coordinates GetLastLocation(){
        if(Loc!=null){
            TreeMap<String,Coordinates> m = new TreeMap<>(Loc);

            return m.lastEntry().getValue();
        }
        return new Coordinates("null coordinates",0,0);
    }

    public void setNewLocation(Coordinates newCoordinates){
        Loc.put(newCoordinates.getTimestamp(),newCoordinates);
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getSerial_Code() {
        return Serial_Code;
    }

    public void setSerial_Code(String serial_Code) {
        Serial_Code = serial_Code;
    }
}
