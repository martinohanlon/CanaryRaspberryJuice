package com.stuffaboutcode.canaryraspberryjuice;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.canarymod.Canary;
import net.canarymod.plugin.Plugin;
import net.canarymod.api.*;
import net.canarymod.api.world.*;
import net.canarymod.api.world.position.*;
import net.canarymod.api.entity.living.humanoid.*;

import com.stuffaboutcode.canaryraspberryjuice.RemoteSession;
import com.stuffaboutcode.canaryraspberryjuice.ServerListenerThread;

public class CanaryRaspberryJuicePlugin extends Plugin {

	public Player hostPlayer = null;
	
	public ServerListenerThread serverThread;

	public List<RemoteSession> sessions;

	@Override
	public boolean enable() {
		Canary.hooks().registerListener(new CanaryRaspberryJuiceListener(this), this);
		getLogman().info("Enabling " + getName() + " Version " + getVersion()); 
		getLogman().info("Authored by "+getAuthor());
		
		sessions = new ArrayList<RemoteSession>();
		
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
		
	/** called when a new session is established. */
	public void handleConnection(RemoteSession newSession) {
		
		if (checkBanned(newSession)) {
			getLogman().warn("Kicking " + newSession.getSocket().getRemoteSocketAddress() + " because the IP address has been banned.");
			newSession.kick("You've been banned from this server!");
			return;
		}
		synchronized(sessions) {
			sessions.add(newSession);
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
		// TODO get host player property
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

