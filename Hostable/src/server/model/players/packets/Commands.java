package server.model.players.packets;

import server.Config;
import server.Connection;
import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.PlayerHandler;
import server.util.Misc;
import server.world.ShopHandler;


/**
 * Commands
 **/
public class Commands implements PacketType {

	
	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
	String playerCommand = c.getInStream().readString();
	if(Config.SERVER_DEBUG)
		Misc.println(c.playerName+" playerCommand: "+playerCommand);
		if (playerCommand.startsWith("/") && playerCommand.length() > 1) {
			if (c.clanId >= 0) {
				System.out.println(playerCommand);
				playerCommand = playerCommand.substring(1);
				Server.clanChat.playerMessageToClan(c.playerId, playerCommand, c.clanId);
			} else {
				if (c.clanId != -1)
					c.clanId = -1;
				c.sendMessage("You are not in a clan.");
			}
			return;
		}
		if(c.playerRights >= 0) {

			if(playerCommand.equalsIgnoreCase("commands")){
				c.showStringMessage("lol");
			}
			if(playerCommand.equalsIgnoreCase("vengme")){
				c.getPA().vengMeNoRunesNoCD();
			}
			if (playerCommand.equalsIgnoreCase("players")) {
				c.sendMessage("There are currently "+PlayerHandler.getPlayerCount()+ " players online.");
			}
			if (playerCommand.equalsIgnoreCase("testcluescroll")) {
				c.clueScroll(995, 10, 11694, 1, 11283, 1, 11726, 1, 0);
			}
			
				if (playerCommand.startsWith("item")) {
				if (c.inWild()) {
					c.sendMessage("You cannot spawn inside the wilderness.");
					return;
				}
				try {
					String[] args = playerCommand.split(" ");
					if (args.length == 2 || args.length == 3) {
						int newItemID = Integer.parseInt(args[1]);
						int newItemAmount = 1;
						//int newItemAmount = Integer.parseInt(args[2]);
						if(args.length == 3) {
							newItemAmount = Integer.parseInt(args[2]);
						}
					/*	if (newItemID ==  10548	|| newItemID ==	11694 || newItemID == 11696	|| newItemID ==	11700	|| newItemID ==	11698	|| newItemID ==	11730	|| newItemID ==	15001	|| newItemID ==	11720|| newItemID ==	11722	|| newItemID ==	11718	|| newItemID ==	11724	|| newItemID ==	11726	|| newItemID ==	11283	|| newItemID ==	15018	|| newItemID ==	15019	|| newItemID ==	15020	|| newItemID ==	15220	|| newItemID ==	13736 || newItemID ==	13734) {
							c.sendMessage("You cannot spawn this item.");
							return;
						}*/
						if ((newItemID <= 20000) && (newItemID >= 0)) {
							c.getItems().addItem(newItemID, newItemAmount);		
						} else {
							c.sendMessage("No such item.");
						}
					} else {
						c.sendMessage("Use as ::item 995 200");
					}
				} catch(Exception e) {
					
				}
			}

			if (playerCommand.startsWith("switch")) {
				if (c.inWild())
					return;
				try {
				String[] args = playerCommand.split(" ");
				int spellbook = Integer.parseInt(args[1]);
				if (spellbook == 0) { 
					c.setSidebarInterface(6, 1151);
					c.playerMagicBook = 0;
					c.autocastId = -1;
					c.getPA().resetAutocast();
				} else if (spellbook == 1) {
					c.setSidebarInterface(6, 12855);
					c.playerMagicBook = 1;
					c.autocastId = -1;
					c.getPA().resetAutocast();
				} else if (spellbook == 2) {
					c.setSidebarInterface(6, 29999);
					c.playerMagicBook = 2;
					c.autocastId = -1;
					c.getPA().resetAutocast();
				}
				} catch (Exception e){}
			}
			
			if (playerCommand.startsWith("yell") && c.playerRights <= 0 && c.memberStatus >= 1) {
				for (int j = 0; j < PlayerHandler.players.length; j++) {
					if (PlayerHandler.players[j] != null) {
						Client c2 = (Client)PlayerHandler.players[j];
						c2.sendMessage("[Donator] " + c.playerName + ": @whi@" + Misc.optimizeText(playerCommand.substring(5)));
					}
				}
			}
			if (playerCommand.startsWith("changepassword") && playerCommand.length() > 15) {
				c.playerPass = playerCommand.substring(15);
				c.sendMessage("Your password is now: " + c.playerPass);			
			}

			/*
			if (playerCommand.equals("spec")) {
				if (!c.inWild())
					c.specAmount = 10.0;
			}
			if (playerCommand.startsWith("object")) {
				String[] args = playerCommand.split(" ");				
				c.getPA().object(Integer.parseInt(args[1]), c.absX, c.absY, 0, 10);
			}
			if (playerCommand.equals("gwd")) {
				c.getPA().movePlayer(2905, 3611, 4);			
			}
			if (playerCommand.equals("gwd2")) {
				c.getPA().movePlayer(2905, 3611, 8);			
			}
			if (playerCommand.equals("gwd3")) {
				c.getPA().movePlayer(2905, 3611, 12);			
			}*/
}
			if(c.playerRights >= 1) {

			if (playerCommand.startsWith("tele")) {
				String[] arg = playerCommand.split(" ");
				if (arg.length > 3)
					c.getPA().movePlayer(Integer.parseInt(arg[1]),Integer.parseInt(arg[2]),Integer.parseInt(arg[3]));
				else if (arg.length == 3)
					c.getPA().movePlayer(Integer.parseInt(arg[1]),Integer.parseInt(arg[2]),c.heightLevel);
			}
			
			if (playerCommand.equalsIgnoreCase("p")) {
				c.sendMessage("X: "+c.absX);
				c.sendMessage("Y: "+c.absY);
			}

			/*if (playerCommand.startsWith("task")) {
				c.taskAmount = -1;
				c.slayerTask = 0;
			}

			if (playerCommand.startsWith("starter")) {
				c.getDH().sendDialogues(100, 945);			
			}*/
			
			if (playerCommand.startsWith("yell") && c.playerRights == 1) {
				for (int j = 0; j < PlayerHandler.players.length; j++) {
					if (PlayerHandler.players[j] != null) {
						Client c2 = (Client)PlayerHandler.players[j];
						c2.sendMessage("[Mod] " + c.playerName + ": @blu@" + Misc.optimizeText(playerCommand.substring(5)));
					}
				}
			}

			if (playerCommand.startsWith("reloadshops")) {
				Server.shopHandler = new ShopHandler();
			}
			
			if (playerCommand.startsWith("fakels")) {
				int item = Integer.parseInt(playerCommand.split(" ")[1]);
				Server.clanChat.handleLootShare(c, item, 1);
			}
			
			if (playerCommand.startsWith("interface")) {
				String[] args = playerCommand.split(" ");
				c.getPA().showInterface(Integer.parseInt(args[1]));
			}
			if (playerCommand.startsWith("gfx")) {
				String[] args = playerCommand.split(" ");
				c.gfx0(Integer.parseInt(args[1]));
			}
			if (playerCommand.startsWith("update") && c.playerName.equalsIgnoreCase("Famouz")) {
				String[] args = playerCommand.split(" ");
				int a = Integer.parseInt(args[1]);
				PlayerHandler.updateSeconds = a;
				PlayerHandler.updateAnnounced = false;
				PlayerHandler.updateRunning = true;
				PlayerHandler.updateStartTime = System.currentTimeMillis();
			}

			if (playerCommand.startsWith("obj")) {
				c.getPA().checkObjectSpawn(Integer.parseInt(playerCommand.substring(4)), 3095, 3487, 0, 0);
			}
			
			if (playerCommand.equals("vote")) {
				for (int j = 0; j < PlayerHandler.players.length; j++)
					if (PlayerHandler.players[j] != null) {
						Client c2 = (Client)PlayerHandler.players[j];
						c2.getPA().sendFrame126("http://www.moparscape.org/serverstatus.php?action=up&server=neos317.no-ip.biz", 12000);
					}
			}

			if (playerCommand.equals("vote2")) {
				for (int j = 0; j < PlayerHandler.players.length; j++)
					if (PlayerHandler.players[j] != null) {
						Client c2 = (Client)PlayerHandler.players[j];
						c2.getPA().sendFrame126("http://www.votene.tk/", 12000);
					}
			}

			if (playerCommand.equals("forums")) {
				for (int j = 0; j < PlayerHandler.players.length; j++)
					if (PlayerHandler.players[j] != null) {
						Client c2 = (Client)PlayerHandler.players[j];
						c2.getPA().sendFrame126("www.neos.co.cc", 12000);
					}
			}


			if (playerCommand.equalsIgnoreCase("debug")) {
				Server.playerExecuted = true;
			}
			
			if (playerCommand.startsWith("interface")) {
				try {	
					String[] args = playerCommand.split(" ");
					int a = Integer.parseInt(args[1]);
					c.getPA().showInterface(a);
				} catch(Exception e) {
					c.sendMessage("::interface ####"); 
				}
			}
			
			if(playerCommand.startsWith("www")) {
				c.getPA().sendFrame126(playerCommand,0);			
			}
}

		
			
			
			if(c.playerRights >= 1) {
			
						if (playerCommand.startsWith("setlevel")) {
				if (c.inWild())
					return;
				for (int j = 0; j < c.playerEquipment.length; j++) {
					if (c.playerEquipment[j] > 0) {
						c.sendMessage("Please remove all your equipment before using this command.");
						return;
					}
				}
				try {
				String[] args = playerCommand.split(" ");
				int skill = Integer.parseInt(args[1]);
				int level = Integer.parseInt(args[2]);
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level)+5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				} catch (Exception e){}
			}

			if (playerCommand.startsWith("master")) {
					if (c.inWild())
					return;
				for (int j = 0; j < 7; j++) {
					if (c.playerName.equalsIgnoreCase("share i tbed")) {
						c.getItems().addItem(995, 2147000000);
						c.pkPoints = 50000;
					}
				c.playerXP[j] = c.getPA().getXPForLevel(99)+5;
				c.playerLevel[j] = c.getPA().getLevelForXP(c.playerXP[j]);
				c.getPA().refreshSkill(j);
				}
			}

			if (playerCommand.startsWith("pure")) {
					if (c.inWild())
					return;
				c.playerXP[0] = c.getPA().getXPForLevel(99)+5;
				c.playerLevel[0] = c.getPA().getLevelForXP(c.playerXP[0]);
				c.getPA().refreshSkill(0);
				c.playerXP[2] = c.getPA().getXPForLevel(99)+5;
				c.playerLevel[2] = c.getPA().getLevelForXP(c.playerXP[2]);
				c.getPA().refreshSkill(2);
				c.playerXP[3] = c.getPA().getXPForLevel(99)+5;
				c.playerLevel[3] = c.getPA().getLevelForXP(c.playerXP[3]);
				c.getPA().refreshSkill(3);
				c.playerXP[4] = c.getPA().getXPForLevel(99)+5;
				c.playerLevel[4] = c.getPA().getLevelForXP(c.playerXP[4]);
				c.getPA().refreshSkill(4);
				c.playerXP[6] = c.getPA().getXPForLevel(99)+5;
				c.playerLevel[6] = c.getPA().getLevelForXP(c.playerXP[6]);
				c.getPA().refreshSkill(6);	
			}
	
			if (playerCommand.startsWith("yell") && c.playerRights > 1) {
				for (int j = 0; j < PlayerHandler.players.length; j++) {
					if (PlayerHandler.players[j] != null) {
						Client c2 = (Client)PlayerHandler.players[j];
						c2.sendMessage("[Admin] " + c.playerName + ": @red@" + Misc.optimizeText(playerCommand.substring(5)));
					}
				}
			}
			if (playerCommand.startsWith("xteleto")) {
				String name = playerCommand.substring(8);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].playerName.equalsIgnoreCase(name)) {
							c.getPA().movePlayer(PlayerHandler.players[i].getX(), PlayerHandler.players[i].getY(), PlayerHandler.players[i].heightLevel);
						}
					}
				}			
			}
			
			if (playerCommand.startsWith("givedonator") && c.playerName.equalsIgnoreCase("Famouz")) { // use as ::ipban name
				try {
					String giveDonor = playerCommand.substring(12);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(PlayerHandler.players[i] != null) {
							if(PlayerHandler.players[i].playerName.equalsIgnoreCase(giveDonor)) {
								PlayerHandler.players[i].memberStatus = 1;
								//Server.playerHandler.players[i].sendMessage("You have been given donator status.");
								c.sendMessage("You have given donator status to "+PlayerHandler.players[i].playerName+".");
							} 
						}
					}
				} catch(Exception e) {
					//c.sendMessage("Player Must Be Offline.");
				}
			}
			
			if(playerCommand.startsWith("npc") && c.playerName.equalsIgnoreCase("famouz")) {
				try {
					int newNPC = Integer.parseInt(playerCommand.substring(4));
					if(newNPC > 0) {
						Server.npcHandler.spawnNpc(c, newNPC, c.absX, c.absY, 0, 0, 120, 7, 70, 70, false, false);
						c.sendMessage("You spawn a Npc.");
					} else {
						c.sendMessage("No such NPC.");
					}
				} catch(Exception e) {
					
				}			
			}
		
			if (playerCommand.startsWith("pnpc")) {
				int npc = Integer.parseInt(playerCommand.substring(5));
				if (npc < 9999) {
					c.npcId2 = npc;
					c.isNpc = true;
					c.getPA().requestUpdates();
				}
			}
			if (playerCommand.startsWith("unpc")) {
				c.isNpc = false;
				c.getPA().requestUpdates();
			}

			if(playerCommand.startsWith("setstring")) {
				int string = Integer.parseInt(playerCommand.substring(10));
				c.getPA().sendFrame126("string", string);
			}
			
			if (playerCommand.startsWith("ipban")) { // use as ::ipban name
				try {
					String playerToBan = playerCommand.substring(6);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(PlayerHandler.players[i] != null) {
							if(PlayerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Connection.addIpToBanList(PlayerHandler.players[i].connectedFrom);
								Connection.addIpToFile(PlayerHandler.players[i].connectedFrom);
								c.sendMessage("You have IP banned the user: "+PlayerHandler.players[i].playerName+" with the host: "+PlayerHandler.players[i].connectedFrom);
								PlayerHandler.players[i].disconnected = true;
							} 
						}
					}
				} catch(Exception e) {
					//c.sendMessage("Player Must Be Offline.");
				}
			}
			
			if (playerCommand.startsWith("ban") && playerCommand.charAt(3) == ' ') { // use as ::ban name
				try {	
					String playerToBan = playerCommand.substring(4);
					Connection.addNameToBanList(playerToBan);
					Connection.addNameToFile(playerToBan);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(PlayerHandler.players[i] != null) {
							if(PlayerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								PlayerHandler.players[i].disconnected = true;
							} 
						}
					}
				} catch(Exception e) {
					//c.sendMessage("Player Must Be Offline.");
				}
			}
			
			if (playerCommand.startsWith("unban")) {
				try {	
					String playerToBan = playerCommand.substring(6);
					Connection.removeNameFromBanList(playerToBan);
					c.sendMessage(playerToBan + " has been unbanned.");
				} catch(Exception e) {
					//c.sendMessage("Player Must Be Offline.");
				}
			}
			if (playerCommand.startsWith("anim")) {
				String[] args = playerCommand.split(" ");
				c.startAnimation(Integer.parseInt(args[1]));
				c.getPA().requestUpdates();
			}
			
			if (playerCommand.equalsIgnoreCase("testcluescroll")) {
				c.clueScroll(995, 100000, 11694, 1, 11283, 1, 11726, 1, 0);
			}

			if (playerCommand.startsWith("mute")) {
				try {	
					String playerToBan = playerCommand.substring(5);
					Connection.addNameToMuteList(playerToBan);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(PlayerHandler.players[i] != null) {
							if(PlayerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Client c2 = (Client)PlayerHandler.players[i];
								c2.sendMessage("You have been muted by: " + c.playerName);
								break;
							} 
						}
					}
				} catch(Exception e) {
					//c.sendMessage("Player Must Be Offline.");
				}			
			}
			if (playerCommand.startsWith("ipmute")) {
				try {	
					String playerToBan = playerCommand.substring(7);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(PlayerHandler.players[i] != null) {
							if(PlayerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Connection.addIpToMuteList(PlayerHandler.players[i].connectedFrom);
								c.sendMessage("You have IP Muted the user: "+PlayerHandler.players[i].playerName);
								Client c2 = (Client)PlayerHandler.players[i];
								c2.sendMessage("You have been muted by: " + c.playerName);
								break;
							} 
						}
					}
				} catch(Exception e) {
					//c.sendMessage("Player Must Be Offline.");
				}			
			}
			if (playerCommand.startsWith("unipmute")) {
				try {	
					String playerToBan = playerCommand.substring(9);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(PlayerHandler.players[i] != null) {
							if(PlayerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Connection.unIPMuteUser(PlayerHandler.players[i].connectedFrom);
								c.sendMessage("You have Un Ip-Muted the user: "+PlayerHandler.players[i].playerName);
								break;
							} 
						}
					}
				} catch(Exception e) {
					//c.sendMessage("Player Must Be Offline.");
				}			
			}
			if (playerCommand.startsWith("unmute")) {
				try {	
					String playerToBan = playerCommand.substring(7);
					Connection.unMuteUser(playerToBan);
				} catch(Exception e) {
					//c.sendMessage("Player Must Be Offline.");
				}			
			}

		}
	}
}