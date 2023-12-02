package com.example.ezvault;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class upcAPI {

    private static final String TAG = "upcAPI";
    private Thread bgThread;

    public upcAPI() {
    }

    /**
     * Looks up the item name from the UPC database
     *
     * @param UPC         the UPC to look up
     * @param destination the EditText to put the item name in
     */
    public void upcLookup(String UPC, EditText destination, Activity activity) {

        // Create a new thread to handle the network request
        this.bgThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // Create the URL string for the API endpoint
                    String UPCurl = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + URLEncoder.encode(UPC);
                    URL url = null;

                    // Store exit status for processing - 0 is normal, 2 is network error, 1 is item not found
                    int code = 0;

                    // Create the URL object
                    try {
                        url = new URL(UPCurl);
                    } catch (MalformedURLException e) {
                        code = 2;
                        Log.e(TAG, "Malformed URL");
                    }

                    // Open the connection and parse the JSON response
                    URLConnection connection;
                    String itemname = "";
                    try {
                        connection = url.openConnection();
                        connection.connect();
                        JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) connection.getContent()));
                        Log.i(TAG, root.toString());
                        JsonObject rootobj = root.getAsJsonObject();
                        // Check if the response is empty or no items are returned
                        if (rootobj.isEmpty() || rootobj.get("items").getAsJsonArray().isEmpty()) {
                            code = 1;
                            Log.i(TAG, "No items found");
                        } else {
                            // Get the item name from the JSON response
                            itemname = rootobj.get("items").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();
                        }
                    } catch (IOException e) {
                        code = 2;
                        Log.e("TAG", e.toString());
                    }

                    // Make the item name and exit code effectively final so it can be passed to the UI thread
                    String finalItemname = itemname;
                    Log.i(TAG, itemname);
                    int finalCode = code;

                    // Run the UI update on the UI thread
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // Set the text of the destination EditText based on the exit code
                            switch (finalCode) {
                                case 0:
                                    // If a valid item name was found, set the text to that
                                    destination.setText(finalItemname);
                                    break;
                                case 1:
                                    // If no items were found, set the text to "No items found"
                                    destination.setText("No items found");
                                    break;
                                case 2:
                                    // If there was a network error, set the text to "Network error"
                                    destination.setText("Network error");
                                    break;
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Start the network thread
        bgThread.start();


    }

    /**
     * Returns the background thread created by upcLookup
     * @return
     */
    public Thread getBgThread() {
        return this.bgThread;
    }

}