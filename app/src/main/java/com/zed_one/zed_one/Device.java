package com.zed_one.zed_one;

import java.util.HashMap;
import java.util.TreeMap;

public class Device {
    private  String Name;
    private HashMap<String,Coordinates> Loc;

    public Device(String id, HashMap<String,Coordinates> Loc) {
        this.Name = id;
        this.Loc =  Loc;
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
}
