/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.geofencing;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Constants used in this sample.
 */

final class Constants {

    private Constants() {
    }

    private static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 912;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    static final float GEOFENCE_RADIUS_IN_METERS = 50; // 1 mile, 1.6 km



    static final float GEOFENCE_RADIUS_HO_THE_GRAND = 100;
    static final float GEOFENCE_RADIUS_HMCO = 200;


    static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();

    static {

        BAY_AREA_LANDMARKS.put("Hero", new LatLng(28.412402603746372, 77.0433149267526));


        BAY_AREA_LANDMARKS.put("Motocorps", new LatLng(28.412402603746372,77.0433149267526));
    }



    static final HashMap<String ,LatLng> HO_THE_GRAND = new HashMap<>();

    static
    {
        HO_THE_GRAND.put("HO , The Grand Hotel",new LatLng(28.5388096,77.1514862));
    }


    static final HashMap<String ,LatLng> HMCI_SOHNA_ROAD = new HashMap<>();

    static
    {
        HMCI_SOHNA_ROAD.put("HMCI , Sohna Road",new LatLng(28.412402603746372,77.0433149267526));
    }

    static final HashMap<String ,LatLng> HMCO = new HashMap<>();

    static
    {
        HMCO.put("HMCO",new LatLng(28.53988,77.15401));
    }

}
