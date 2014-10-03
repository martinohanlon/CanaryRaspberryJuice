package com.stuffaboutcode.canaryraspberryjuice;

import net.visualillusionsent.utils.PropertiesFile;
import net.canarymod.config.Configuration;
import net.canarymod.plugin.Plugin;

public class CanaryRaspberryJuiceConfiguration {
	private PropertiesFile cfg;
	
	public CanaryRaspberryJuiceConfiguration(Plugin plugin) {
        cfg = Configuration.getPluginConfig(plugin);
    }
    
    public int getPort() {
        int port = cfg.getInt("port", 4711);
        cfg.save();
        return port;
    }
}
