package chiaradecaria.agenda;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Marco on 02/01/2017.
 */

public class GPS implements LocationListener {
    private double lat, lng;
    public GPS(Context context, LocationManager locationManager) throws SecurityException{
        LocationManager lc = locationManager;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }
    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    public double getLat(){return lat;}
    public double getLng(){return lng;}
}
