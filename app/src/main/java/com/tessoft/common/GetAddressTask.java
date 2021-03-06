package com.tessoft.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

public class GetAddressTask extends AsyncTask<Location, Void, HashMap> {

	// Store the context passed to the AsyncTask when the system instantiates it.
	Context localContext;
	AddressTaskDelegate delegate;
	int requestCode;

	// Constructor called by the system to instantiate the task
	public GetAddressTask(Context context, AddressTaskDelegate delegate, int requestCode ) {

		// Required by the semantics of AsyncTask
		super();

		// Set a Context for the background task
		localContext = context;

		this.delegate = delegate;
		
		this.requestCode = requestCode;
		
	}

	/**
	 * Get a geocoding service instance, pass latitude and longitude to it, format the returned
	 * address, and return the address to the UI thread.
	 */
	@Override
	protected HashMap doInBackground(Location... params) {
		/*
		 * Get a new geocoding service instance, set for localized addresses. This example uses
		 * android.location.Geocoder, but other geocoders that conform to address standards
		 * can also be used.
		 */
		Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

		// Get the current location from the input parameter list
		Location location = params[0];

		// Create a list to contain the result address
		List <Address> addresses = null;

		HashMap resultMap = new HashMap();
		resultMap.put("latitude", String.valueOf(location.getLatitude()));
		resultMap.put("longitude", String.valueOf(location.getLongitude()));

		// Try to get an address for the current location. Catch IO or network problems.
		try {

			/*
			 * Call the synchronous getFromLocation() method with the latitude and
			 * longitude of the current location. Return at most 1 address.
			 */
			addresses = geocoder.getFromLocation(location.getLatitude(),
					location.getLongitude(), 1
					);

			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);
				String city = address.getAdminArea();
				// Locality is usually a city
				String gu = address.getLocality();
				if ( Util.isEmptyString( gu ) )
					gu = address.getSubLocality();
				// The country of the address
				String country = address.getCountryName();
				resultMap.put("address", country + "|" + city + "|" + gu + "|" + address.getThoroughfare() + "|" + address.getFeatureName() );
			}

			// Catch network or other I/O problems.
		} catch (IOException exception1) {

			// print the stack trace
			exception1.printStackTrace();

			// Catch incorrect latitude or longitude values
		} catch (IllegalArgumentException exception2) {

			exception2.printStackTrace();

		}

		return resultMap;
	}

	/**
	 * A method that's called once doInBackground() completes. Set the text of the
	 * UI element that displays the address. This method runs on the UI thread.
	 */
	@Override
	protected void onPostExecute(HashMap result) {
		delegate.onAddressTaskPostExecute(requestCode, result);
	}
}
