package com.newlinegaming.runix.common.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UtilLog {

    private static final Logger RLog = LogManager.getLogger(UtilInfo.MOD_ID.toUpperCase());

    public static void log(Level logLevel, Object obj) {
        RLog.log(logLevel, obj.toString());
    }

    public static void debug(Object obj) {
        log(Level.DEBUG, obj.toString());
    }

    public static void warn(Object obj) {
        log(Level.WARN, obj.toString());
    }

    public static void info(Object obj) {
        log(Level.INFO, obj.toString());
    }

    public static void fatal(Object obj) {
        log(Level.FATAL, obj.toString());
    }
}
