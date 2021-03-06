package com.cmpt276.indigo.carbontracker.carbon_tracker_model;

/*
    Implements RouteModel that describes a route object
 */

public class RouteModel implements CarbonFootprintComponent{
    private long id;
    private String name;
    private double cityDistance;
    private double highwayDistance;
    private double totalDistance;
    private boolean isDeleted;

    public RouteModel(){
        name = new String();
        cityDistance = 0;
        highwayDistance = 0;
        totalDistance = 0;
        isDeleted = false;          // when a Route is deleted, we should hide it instead of removing it
    }

    public RouteModel(long id, String name, double cityDistance, double highwayDistance, double totalDistance, boolean isDeleted){
        this.id = id;
        this.name = name;
        this.cityDistance = cityDistance;
        this.highwayDistance = highwayDistance;
        this.totalDistance = totalDistance;
        this.isDeleted = isDeleted;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCityDistance() {
        return cityDistance;
    }

    public double getHighwayDistance() {
        return highwayDistance;
    }

    public double getTotalDistance() {
        // calculate the all distance
        totalDistance = cityDistance + highwayDistance;
        return totalDistance;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCityDistance(double cityDistance) {
        if(cityDistance < 0){
            throw new IllegalArgumentException("City distance must be a positive number.");
        }
        this.cityDistance = cityDistance;
    }

    public void setHighwayDistance(double highwayDistance) {
        if(highwayDistance < 0){
            throw new IllegalArgumentException("Highway distance must be a positive number.");
        }
        this.highwayDistance = highwayDistance;
    }

    public void setTotalDistance(double totalDistance) {
        if(totalDistance < 0){
            throw new IllegalArgumentException("Total distance must be a positive number.");
        }
        this.totalDistance = totalDistance;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted){
        this.isDeleted = isDeleted;
    }

    @Override
    public boolean equals(Object other){
        if (other == null){
            return false;
        }
        if (this == other){
            return true;
        }
        if (!(other instanceof RouteModel)){
            return false;
        }
        RouteModel o = (RouteModel) other;
        // a deleted object should never be compared to other components
        if(o.isDeleted || this.isDeleted){
            return false;
        }
        return this.name.equals(o.name);
    }
}
