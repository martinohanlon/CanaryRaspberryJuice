package com.stuffaboutcode.canaryraspberryjuice;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.canarymod.Canary;
import net.canarymod.plugin.Plugin;
import net.canarymod.chat.Colors;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.system.ServerTickHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.plugin.PluginListener;
import net.canarymod.api.*;
import net.canarymod.api.world.*;
import net.canarymod.api.world.position.*;
import net.canarymod.api.entity.living.humanoid.*;

import com.stuffaboutcode.canaryraspberryjuice.RemoteSession;
import com.stuffaboutcode.canaryraspberryjuice.ServerListenerThread;

public class CanaryRaspberryJuicePlugin extends Plugin implements PluginListener {

	public Player hostPlayer = null;
	
	public ServerListenerThread serverThread;

	public List<RemoteSession> sessions;

	@Override
	public boolean enable() {
		Canary.hooks().registerListener(new CanaryRaspberryJuicePlugin(), this);
		getLogman().info("Enabling " + getName() + " Version " + getVersion()); 
		getLogman().info("Authored by "+getAuthor());
		
		sessions = new ArrayList<RemoteSession>();
		if (sessions == null) {
			getLogman().info("sessions is null");
		} else {
			getLogman().info("sessions is not null");
		}
		
		try {
			int port = 4711;
			serverThread = new ServerListenerThread(this, new InetSocketAddress(port));
			new Thread(serverThread).start();
			getLogman().info("Raspberry Juice ThreadListener Started");
		} catch (Exception e) {
			e.printStackTrace();
			getLogman().warn("Failed to start ThreadListener");
			return false;
		}
		//old bukkit code
		/*getServer().getPluginManager().registerEvents(this, this);
		tickTimerId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new TickHandler(), 1, 1);
		*/
		
		return true;
	}
	
	@Override
	public void disable() {
		System.out.println("Raspberry Juice Stopped");
		serverThread.running = false;
		try {
			serverThread.serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		getServer().getScheduler().cancelTasks(this);
		for (RemoteSession session: sessions) {
			try {
				session.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sessions = null;
		serverThread = null;
	}

	
	@HookHandler
    public void onLogin(ConnectionHook hook) {
		hook.getPlayer().message(Colors.YELLOW+"Hello World, "+hook.getPlayer().getName());
    	hook.getPlayer().message(getSpawnLocation().toString());
    }

	@HookHandler
	public void onTick(ServerTickHook tickHook) {
		if (sessions == null) {
			getLogman().info("sessions is null");
		} else {
			getLogman().info("sessions is not null");

			Iterator<RemoteSession> sI = sessions.iterator();
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
		
	/** called when a new session is established. */
	public void handleConnection(RemoteSession newSession) {
		if (sessions == null) {
			getLogman().info("handleconn sessions is null");
		} else {
			getLogman().info("handleconn sessions is not null");
		}
		if (checkBanned(newSession)) {
			System.out.println("Kicking " + newSession.getSocket().getRemoteSocketAddress() + " because the IP address has been banned.");
			newSession.kick("You've been banned from this server!");
			return;
		}
		synchronized(sessions) {
			sessions.add(newSession);
			getLogman().info("new session created");
		}
	}
	
	public boolean checkBanned(RemoteSession session) {
		String sessionIp = session.getSocket().getInetAddress().getHostAddress();
		return Canary.bans().isIpBanned(sessionIp);
	}

	
	public Server getServer() {
		return Canary.getServer();
	}
	
	public World getWorld() {
		return getServer().getWorldManager().getAllWorlds().iterator().next();
	}
	
	public Location getSpawnLocation(){
		return getWorld().getSpawnLocation();
	}
	
	public Player getHostPlayer() {
		if (hostPlayer != null) return hostPlayer;
		List<Player> allPlayers = getServer().getPlayerList();
		if (allPlayers.size() >= 1)
			return allPlayers.iterator().next();
		return null;
	}
	
	public Player getNamedPlayer(String name) {
        if (name == null) return null;
        List<Player> allPlayers = getServer().getPlayerList();
        for (int i = 0; i < allPlayers.size(); ++i) {
            if (name.equals(allPlayers.get(i).getName())) {
                return allPlayers.get(i);
            }
        }
        return null;
    }
	
}

