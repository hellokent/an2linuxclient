/*
 * Copyright 2017 rootkiwi
 *
 * AN2Linux-client is licensed under GNU General Public License 3.
 *
 * See LICENSE for more details.
 */

package kiwi.root.an2linuxclient.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import kiwi.root.an2linuxclient.R;

public class ConnectionHelper {

    public static byte[] intToByteArray(int value){
        return new byte[] {
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    public static byte[] readAll(int size, InputStream in) throws IOException {
        byte[] buf = new byte[size];
        int len = 0;
        while (len < size){
            len += in.read(buf, len, size-len);
        }
        return buf;
    }

    public static boolean checkIfSsidIsAllowed(String ssidWhitelist, Context c){
        if (ssidWhitelist == null){
            return true;
        }
        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String connectedToSsid = info.getSSID();

        for(String ssid : ssidWhitelist.split(",")){
            if(connectedToSsid.equals("\""+ssid.trim()+"\"")){
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfValidIpOrHostname(String ipOrHostname) {
        // source: http://stackoverflow.com/a/3824105
        String validHostnameRegex = "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";
        // source: http://stackoverflow.com/a/106223
        String validIpAddressRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        return ipOrHostname.matches(validHostnameRegex) && ipOrHostname.length() <= 255 || ipOrHostname.matches(validIpAddressRegex);
    }

    public static boolean checkIfValidAddressAndPortInput(EditText ipOrHostnameEditText,
                                                          EditText portNumberEditText,
                                                          Context c){
        String ipOrHostname = ipOrHostnameEditText.getText().toString().trim();
        if (ConnectionHelper.checkIfValidIpOrHostname(ipOrHostname)){
            try {
                int portNumber = Integer.parseInt(portNumberEditText.getText().toString());
                if (portNumber < 0 || portNumber > 65535) {
                    sendToast(R.string.port_range, c);
                    return false;
                }
            } catch (NumberFormatException e) {
                sendToast(R.string.invalid_port, c);
                return false;
            }
        } else {
            sendToast(R.string.invalid_address, c);
            return false;
        }
        return true;
    }

    private static void sendToast(int msg, Context c){
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

}