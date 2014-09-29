package com.stuffaboutcode.canaryraspberryjuice;

import java.util.Iterator;
import net.canarymod.chat.Colors;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.system.ServerTickHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.plugin.*;
import net.canarymod.chat.Colors;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.system.ServerTickHook;


public class CanaryRaspberryJuiceListener implements PluginListener {
	
	private CanaryRaspberryJuicePlugin plugin;
	
	public CanaryRaspberryJuiceListener(CanaryRaspberryJuicePlugin plugin) {
		this.plugin = plugin;
	}
    
	@HookHandler
    public void onLogin(ConnectionHook hook) {
		hook.getPlayer().message(Colors.YELLOW+"Hello " + hook.getPlayer().getName() + " Raspberry Juice is running");
    }

	@HookHandler
	public void onTick(ServerTickHook tickHook) {
		Iterator<RemoteSession> sI = plugin.sessions.iterator();
		while(sI.hasNext()) {
			RemoteSession s = sI.next();
			if (s.pendingRemoval) {
				s.close();
				sI.remove();
			} else {
				s.tick();
			}
		}
	}
    
}