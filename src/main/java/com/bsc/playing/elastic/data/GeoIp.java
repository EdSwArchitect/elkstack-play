package com.bsc.playing.elastic.data;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by EdwinBrown on 7/4/2017.
 */
public class GeoIp {
    // "geo_ip":{"city":"Summerville","country_name":"United States","latitude":32.9647,"longitude":-80.2015,"region_name":"South Carolina"},
    private String city;
    private String countryName;
    private double latitude;
    private double longitude;
    private String regionName;

    public GeoIp() {

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("country_name")
    public String getCountryName() {
        return countryName;
    }

    @JsonProperty("country_name")
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("region_name")
    public String getRegionName() {
        return regionName;
    }

    @JsonProperty("region_name")
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoIp geoIp = (GeoIp) o;

        if (Double.compare(geoIp.latitude, latitude) != 0) return false;
        if (Double.compare(geoIp.longitude, longitude) != 0) return false;
        if (city != null ? !city.equals(geoIp.city) : geoIp.city != null) return false;
        if (countryName != null ? !countryName.equals(geoIp.countryName) : geoIp.countryName != null) return false;
        return regionName != null ? regionName.equals(geoIp.regionName) : geoIp.regionName == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = city != null ? city.hashCode() : 0;
        result = 31 * result + (countryName != null ? countryName.hashCode() : 0);
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (regionName != null ? regionName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GeoIp{" +
                "city='" + city + '\'' +
                ", countryName='" + countryName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", regionName='" + regionName + '\'' +
                '}';
    }
}
