package cl.ucn.disc.dam.twinpic.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase para obtener informacion del dispositivo.
 *
 * @author Diego P. Urrutia Astorga
 * @version 20161102
 */

@Slf4j
public class DeviceUtils {

    /**
     * @param context
     * @return the DeviceID
     */
    public static String getDeviceId(final Context context) {

        @SuppressLint("HardwareIds")
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if ("9774d56d682e549c".equals(androidId)) {
            log.warn("AndroidId Broken!");
        }

        return androidId;
    }

    /**
     * Obtiene el nombre del dispositivo.
     *
     * @param context
     * @return the DeviceName
     */
    public static String getDeviceName(final Context context) {
        final String manufacturer = Build.MANUFACTURER;
        final String undecodedModel = Build.MODEL;
        String model = null;

        try {
            Properties prop = new Properties();
            InputStream fileStream;
            // Read the device name from a precomplied list:
            // see http://making.meetup.com/post/29648976176/human-readble-android-device-names
            fileStream = context.getAssets().open("android_models.properties");
            prop.load(fileStream);
            fileStream.close();
            String decodedModel = prop.getProperty(undecodedModel.replaceAll(" ", "_"));
            if (decodedModel != null && !decodedModel.trim().equals("")) {
                model = decodedModel;
            }
        } catch (IOException e) {
            log.error("Error", e);
        }

        if (model == null) {  //Device model not found in the list
            if (undecodedModel.startsWith(manufacturer)) {
                model = capitalize(undecodedModel);
            } else {
                model = capitalize(manufacturer) + " " + undecodedModel;
            }
        }
        return model;
    }

    /**
     * @param s
     * @return the Capitalized String
     */
    private static String capitalize(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        final char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
