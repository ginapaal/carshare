package com.codecool.carshare.model;

public enum VehicleType {
    Car, Motor, Bike;

    private String string = this.toString();

    public String getString() {
        return string;
    }

    public static VehicleType getTypeFromString(String typeString) {
        if (typeString != null) {
            switch (typeString) {
                case "Car":
                    return VehicleType.Car;
                case "Motor":
                    return VehicleType.Motor;
                case "Bike":
                    return VehicleType.Bike;
                default:
                    break;
            }
        }
        return null;
    }
}
