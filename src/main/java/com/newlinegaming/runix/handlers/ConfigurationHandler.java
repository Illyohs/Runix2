package com.newlinegaming.runix.handlers;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {
    
    public static Configuration conf;
    
    public static void init(File confFile) {
        
        conf = new Configuration();
        
        try {
            
            conf.load();
            loadConf();
            
        } catch(Exception e) {
            
        } finally {
            conf.save();
        }
    }

    private static void loadConf() {
//        LibConfig.STRUCWORKER = conf.getInt("Structure Moveworker", Configuration.CATEGORY_GENERAL, LibConfig.STRUCWORKER_DEFAULT, 50, 500, "The amount of work the structure moveworker will do");
//        LibConfig.STRUCWORKER cong
        
    }
    
    
}