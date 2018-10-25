package com.bunker.profile.utils;

public class Settings {

    private static boolean allowUserQr = true;

    public static void setAllowUserQr(boolean value) { allowUserQr = value; }
    public static boolean getAllowUserQr() { return allowUserQr; }
}
