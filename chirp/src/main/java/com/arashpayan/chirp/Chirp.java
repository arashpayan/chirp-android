package com.arashpayan.chirp;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.arashpayan.chirp.ChirpLog.logi;

/**
 * Start here to use this library.
 */
public class Chirp {

    /**
     * The maximum size of a payload when serialized into JSON. Chirp uses gson for serializing
     * payloads into JSON.
     */
    public static final int MAX_PAYLOAD_BYTES = 32 * 1024;
    private static final Pattern sServiceNamePattern = Pattern.compile("[a-zA-Z0-9\\.\\-]+");
    private static final SecureRandom sSecureRandom = new SecureRandom();
    protected static final char[] sHexArray = "0123456789abcdef".toCharArray();
    protected static final Gson sGson = new GsonBuilder().
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).
            create();
    public static boolean Debug = false;

    private Chirp() {
    }

    /**
     * Convenience method for creating a <code>ChirpBrowser</code>.
     * @param serviceName the name of the service to browse/listen for. Pass in "*" if you want
     *                    to listen for all services on the network
     * @return a <code>ChirpBrowser.Builder</code>
     */
    public static ChirpBrowser.Builder browseFor(@NonNull String serviceName) {
        return new ChirpBrowser.Builder(serviceName);
    }

    protected static String getRandomId() {
        byte[] randData = new byte[16];
        sSecureRandom.nextBytes(randData);

        char[] hexChars = new char[16 * 2];
        for ( int j = 0; j < 16; j++ ) {
            int v = randData[j] & 0xFF;
            hexChars[j * 2] = sHexArray[v >>> 4];
            hexChars[j * 2 + 1] = sHexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Checks if <i>name</i> can be used as a Chirp service name. Service names must a) not be
     * empty, b) consist of characters from a-z, A-Z, 0-9, . (period), or - (hyphen), and c) be
     * less than 32 characters. The ideal service name is a reverse dns style string.
     * e.g. com.example.something
     * @param name the string to test for validity
     * @return true if the string can be used as a Chirp service name. false otherwise.
     */
    public static boolean isValidServiceName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }

        if (name.getBytes().length > 64) {
            return false;
        }

        Matcher matcher = sServiceNamePattern.matcher(name);
        return matcher.matches();
    }

    protected static boolean isValidSenderId(String id) {
        if (TextUtils.isEmpty(id)) {
            return false;
        }

        int strLength = id.length();
        if (strLength != 32) {
            return false;
        }
        for (int i=0; i<strLength; i++) {
            char c = id.charAt(i);
            if ((c < 48 || c > 57) &&
                    (c < 65 || c > 70) &&
                    (c < 97 || c > 102)) {
                logi("invalid character in senderid");
                return false;
            }
        }

        return true;
    }

    /**
     * Convenience method for creating a <code>ChirpPublisher</code>.
     * @param serviceName the name of the service to publish
     * @return a <code>Builder</code> that can be configured further or used to start and return
     * the <code>ChirpPublisher</code>
     */
    public static ChirpPublisher.Builder publish(@NonNull String serviceName) {
        return new ChirpPublisher.Builder(serviceName);
    }

}
