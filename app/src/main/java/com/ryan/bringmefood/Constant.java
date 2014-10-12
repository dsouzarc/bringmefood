package com.ryan.bringmefood;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
/**
 * Created by Ryan on 8/30/14.
 */
public class Constant {

    public static void log(final String message) {
        Log.e("com.ryan.bringmefood", message);
    }

    public static void makeToast(final Activity theActivity, final String message) {
        Toast.makeText(theActivity, message, Toast.LENGTH_LONG).show();
    }

    public static final String[] allRestaurants = {"Cheeburger Cheeburger",
            "Chucks", "Contes", "Georges", "Hoagie Haven",
            "Olives", "Slice Between", "Soup Co.", "Subway",
            "Taste of Mexico", "Teresas", "Tortugas", "Wawa"};

    public interface Address {
        final String Cheeburger = "182 Nassau St, Princeton, NJ 08542";
        final String Chucks = "16 Spring Street Princeton, NJ 08540";
        final String Contes = "339 Witherspoon Street, Princeton, NJ 08540";
        final String Georges = "244 Nassau St, Princeton, NJ";
        final String Hoagie_Haven = "242 NASSAU STREET PRINCETON, NJ 08542";
        final String Olives = "22 Witherspoon St, Princeton, NJ 08542";
        final String SliceBetween = "242 Nassau St, Princeton, NJ 08542";
        final String SoupCo = "30 Palmer Square East, Princeton NJ 08542";
        final String Subway = "18 Witherspoon St, Princeton, NJ";
        final String Taste_Of_Mexico = "180 Nassau St, Princeton, NJ 08542, United States";
        final String Teresas = "23 Palmer Square E, Princeton, NJ 08542";
        final String Tortugas = "41 Leigh Avenue, Princeton, NJ 08542";
        final String Wawa = "140 University Pl, Princeton, NJ 08540";
    }

    public static String getAddress(final String restaurant) {
        if(restaurant.contains("Cheeburger")) {
            return Address.Cheeburger;
        }
        if(restaurant.contains("Chucks")) {
            return Address.Chucks;
        }
        if(restaurant.contains("Contes")) {
            return Address.Contes;
        }
        if(restaurant.contains("Georges")) {
            return Address.Georges;
        }
        if(restaurant.contains("Hoagie Haven")) {
            return Address.Hoagie_Haven;
        }
        if(restaurant.contains("Olives")) {
            return Address.Olives;
        }
        if(restaurant.contains("Slice Between")) {
            return Address.SliceBetween;
        }
        if(restaurant.contains("Soup")) {
            return Address.SoupCo;
        }
        if(restaurant.contains("Subway")) {
            return Address.Subway;
        }
        if(restaurant.contains("Taste of Mexico")) {
            return Address.Taste_Of_Mexico;
        }
        if(restaurant.contains("Teresa")) {
            return Address.Teresas;
        }
        if(restaurant.contains("Tortugas")) {
            return Address.Tortugas;
        }
        if(restaurant.contains("Wawa")) {
            return Address.Wawa;
        }
        return "151 Moore Street, Princeton, NJ, 08540";
    }
}
