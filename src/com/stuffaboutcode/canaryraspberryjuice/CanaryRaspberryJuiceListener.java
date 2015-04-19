package com.stuffaboutcode.canaryraspberryjuice;

import java.util.*;

//import net.canarymod.chat.Colors;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.system.ServerTickHook;
//import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.hook.player.ChatHook;
import net.canarymod.plugin.*;
import net.canarymod.api.inventory.*;
import net.canarymod.api.entity.living.humanoid.Player;
import com.stuffaboutcode.canaryraspberryjuice.RemoteSession;

public class CanaryRaspberryJuiceListener implements PluginListener {
	
	private CanaryRaspberryJuicePlugin plugin;
	
	// Tools (swords) which can be used to hit blocks
	public static final Set<Integer> blockHitDetectionTools = new HashSet<Integer>(Arrays.asList(
			ItemType.DiamondSword.getId(),
			ItemType.GoldSword.getId(), 
			ItemType.IronSword.getId(), 
			ItemType.StoneSword.getId(), 
			ItemType.WoodSword.getId()));
	
	// Class constructor
	public CanaryRaspberryJuiceListener(CanaryRaspberryJuicePlugin plugin) {
		this.plugin = plugin;
	}
    
	/*@HookHandler
    public void onLogin(ConnectionHook hook) {
		hook.getPlayer().message(Colors.YELLOW+"Hello " + hook.getPlayer().getName() + " Raspberry Juice is running");
    }*/

	@HookHandler
	public void onTick(ServerTickHook tickHook) {
		//called each tick of the server it gets all the remote sessions to run
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
	
	@HookHandler
	public void onBlockHit(BlockRightClickHook hitHook) {
		//DEBUG
		//plugin.getLogman().info("BlockRightHitHook fired");
		//get the player
		Player playerWhoHit = hitHook.getPlayer();
		//get what the player is holding
		Item itemHeld = playerWhoHit.getItemHeld();
		//are they holding something!
		if (itemHeld != null) {
			// is the player holding a sword
			if (blockHitDetectionTools.contains(itemHeld.getId())) {
				// add the hook event to each session, the session can then decide what to do with it
				for (RemoteSession session: plugin.sessions) {
					session.queueBlockHit(hitHook);
				}
			}
		}
	}
	
	@HookHandler
	public void onChatPost(ChatHook chatHook) {
		//DEBUG
		//plugin.getLogman().info("ChatHook fired");
		
		// add the chat hook event to each session, the session can then decide what to do with it
		for (RemoteSession session: plugin.sessions) {
			session.queueChatPost(chatHook);
		}
	}
	
}