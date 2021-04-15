package server.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.sql.*;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import server.Config;
import server.Server;
import server.model.items.GroundItem;
import server.model.items.ItemList;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
* Handles ground items
**/

public class ItemHandler {

	public List<GroundItem> items = new ArrayList<GroundItem>();
	public static final int HIDE_TICKS = 100;
	
	public ItemHandler() {			
		for(int i = 0; i < Config.ITEM_LIMIT; i++) {
			ItemList[i] = null;
		}
		loadItemList();
	}
	
	/**
	* Adds item to list
	**/	
	public void addItem(GroundItem item) {
		items.add(item);
	}
	
	/**
	* Removes item from list
	**/	
	public void removeItem(GroundItem item) {
		items.remove(item);
	}
	
	/**
	* Item amount
	**/	
	public int itemAmount(int itemId, int itemX, int itemY) {
		for(GroundItem i : items) {
			if(i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY) {
				return i.getItemAmount();
			}
		}
		return 0;
	}
	
	
	/**
	* Item exists
	**/	
	public boolean itemExists(int itemId, int itemX, int itemY) {
		for(GroundItem i : items) {
			if(i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY) {
				return true;
			}
		}
		return false;
	}
	
	/**
	* Reloads any items if you enter a new region
	**/
	public void reloadItems(Client c) {
		for(GroundItem i : items) {
			if(c != null){
				if (c.getItems().tradeable(i.getItemId()) || i.getName().equalsIgnoreCase(c.playerName)) {
					if (c.distanceToPoint(i.getItemX(), i.getItemY()) <= 60) {
						if(i.hideTicks > 0 && i.getName().equalsIgnoreCase(c.playerName)) {
							c.getItems().removeGroundItem(i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
							c.getItems().createGroundItem(i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
						}
						if(i.hideTicks == 0) {
							c.getItems().removeGroundItem(i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
							c.getItems().createGroundItem(i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
						}
					}
				}	
			}
		}
	}
	
	public void process() {
		ArrayList<GroundItem> toRemove = new ArrayList<GroundItem>();
		for (int j = 0; j < items.size(); j++) {			
			if (items.get(j) != null) {
				GroundItem i = items.get(j);
				if(i.hideTicks > 0) {
					i.hideTicks--;
				}
				if(i.hideTicks == 1) { // item can now be seen by others
					i.hideTicks = 0;
					createGlobalItem(i);
					i.removeTicks = HIDE_TICKS;
				}
				if(i.removeTicks > 0) {
					i.removeTicks--;
				}
				if(i.removeTicks == 1) {
					i.removeTicks = 0;
					toRemove.add(i);
					//removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
				}
			
			}
		
		}
		
		for (int j = 0; j < toRemove.size(); j++) {
			GroundItem i = toRemove.get(j);
			removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());	
		}
		/*for(GroundItem i : items) {
			if(i.hideTicks > 0) {
				i.hideTicks--;
			}
			if(i.hideTicks == 1) { // item can now be seen by others
				i.hideTicks = 0;
				createGlobalItem(i);
				i.removeTicks = HIDE_TICKS;
			}
			if(i.removeTicks > 0) {
				i.removeTicks--;
			}
			if(i.removeTicks == 1) {
				i.removeTicks = 0;
				removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
			}
		}*/
	}
	
	
	
	/**
	* Creates the ground item 
	**/
	public int[][] brokenBarrows = {{4708,4860},{4710,4866},{4712,4872},{4714,4878},{4716,4884},
	{4720,4896},{4718,4890},{4720,4896},{4722,4902},{4732,4932},{4734,4938},{4736,4944},{4738,4950},
	{4724,4908},{4726,4914},{4728,4920},{4730,4926},{4745,4956},{4747,4926},{4749,4968},{4751,4994},
	{4753,4980},{4755,4986},{4757,4992},{4759,4998}};
	public void createGroundItem(Client c, int itemId, int itemX, int itemY, int itemAmount, int playerId) {
		if(itemId > 0) {
			if (itemId >= 2412 && itemId <= 2414) {
				c.sendMessage("The item vanishes as it touches the ground.");
				return;
			}
			if (itemId > 4705 && itemId < 4760) {
				for (int j = 0; j < brokenBarrows.length; j++) {
					if (brokenBarrows[j][0] == itemId) {
						itemId = brokenBarrows[j][1];
						break;
					}
				}
			}
			if (!server.model.items.Item.itemStackable[itemId] && itemAmount > 0) {
				for (int j = 0; j < itemAmount; j++) {
					c.getItems().createGroundItem(itemId, itemX, itemY, 1);
					GroundItem item = new GroundItem(itemId, itemX, itemY, 1, c.playerId, HIDE_TICKS, PlayerHandler.players[playerId].playerName);
					addItem(item);
				}	
			} else {
				c.getItems().createGroundItem(itemId, itemX, itemY, itemAmount);
				GroundItem item = new GroundItem(itemId, itemX, itemY, itemAmount, c.playerId, HIDE_TICKS, PlayerHandler.players[playerId].playerName);
				addItem(item);
			}
		}
	}
	
	
	/**
	* Shows items for everyone who is within 60 squares
	**/
	public void createGlobalItem(GroundItem i) {
		for (Player p : PlayerHandler.players){
			if(p != null) {
			Client person = (Client)p;
				if(person != null){
					if(person.playerId != i.getItemController()) {
						if (!person.getItems().tradeable(i.getItemId()) && person.playerId != i.getItemController())
							continue;
						if (person.distanceToPoint(i.getItemX(), i.getItemY()) <= 60) {
							person.getItems().createGroundItem(i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
						}
					}
				}
			}
		}
	}
			

			
	/**
	* Removing the ground item
	**/
	
	public void removeGroundItem(Client c, int itemId, int itemX, int itemY, boolean add){
		for(GroundItem i : items) {
			if(i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY) {
				if(i.hideTicks > 0 && i.getName().equalsIgnoreCase(c.playerName)) {
					if(add) {
						if (!c.getItems().specialCase(itemId)) {
							if(c.getItems().addItem(i.getItemId(), i.getItemAmount())) {   
								removeControllersItem(i, c, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
								break;
							}
						} else {
							c.getItems().handleSpecialPickup(itemId);
							removeControllersItem(i, c, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
							break;
						}
					} else {
						removeControllersItem(i, c, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
						break;
					}
				} else if (i.hideTicks <= 0) {
					if(add) {
						if(c.getItems().addItem(i.getItemId(), i.getItemAmount())) {  
							removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
							break;
						}
					} else {
						removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
						break;
					}
				}
			}
		}
	}
	
	/**
	* Remove item for just the item controller (item not global yet)
	**/
	
	public void removeControllersItem(GroundItem i, Client c, int itemId, int itemX, int itemY, int itemAmount) {
		c.getItems().removeGroundItem(itemId, itemX, itemY, itemAmount);
		removeItem(i);
	}
	
	/**
	* Remove item for everyone within 60 squares
	**/
	
	public void removeGlobalItem(GroundItem i, int itemId, int itemX, int itemY, int itemAmount) {
		for (Player p : PlayerHandler.players){
			if(p != null) {
			Client person = (Client)p;
				if(person != null){
					if (person.distanceToPoint(itemX, itemY) <= 60) {
						person.getItems().removeGroundItem(itemId, itemX, itemY, itemAmount);
					}
				}
			}
		}
		removeItem(i);
	}
		
	

	/**
	*Item List
	**/
	
	public ItemList ItemList[] = new ItemList[Config.ITEM_LIMIT];
	

	public void newItemList(int ItemId, String ItemName, String ItemDescription, double ShopValue, double LowAlch, double HighAlch, int Bonuses[]) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 0; i < 11740; i++) {
			if (ItemList[i] == null) {
				slot = i;
				break;
			}
		}

		if(slot == -1) return;		// no free slot found
		ItemList newItemList = new ItemList(ItemId);
		newItemList.itemName = ItemName;
		newItemList.itemDescription = ItemDescription;
		newItemList.ShopValue = ShopValue;
		newItemList.LowAlch = LowAlch;
		newItemList.HighAlch = HighAlch;
		newItemList.Bonuses = Bonuses;
		ItemList[slot] = newItemList;
	}
	
	public ItemList getItemList(int i) {
		for (int j = 0; j < ItemList.length; j++) {
			if (ItemList[j] != null) {
				if (ItemList[j].itemId == i) {
					return ItemList[j];				
				}		
			}		
		}
		return null;
	}
	
	public boolean loadItemList() {
		List<ItemListInfo> AllItems = new ArrayList<ItemListInfo>();
		String connectionUrl = "jdbc:sqlserver://localhost:1433;instanceName=DESKTOP-92GJDD3;databaseName=RunescapeServer;";
		try (Connection con = DriverManager.getConnection(connectionUrl, "Kremesicle", "lol"); CallableStatement stmt = con.prepareCall("SELECT * FROM ItemList");) {

			ResultSet rs = stmt.executeQuery();
					while(rs.next()){
						ItemListInfo item = new ItemListInfo(rs.getInt("ItemID"), rs.getString("ItemName"), rs.getString("ItemDescription"), rs.getInt("LowAlch"),
								rs.getInt("HighAlch"), rs.getInt("ShopCost"), rs.getInt("AtkStabBonus"), rs.getInt("AtkSlashBonus"), rs.getInt("AtkCrushBonus"), rs.getInt("AtkMagicBonus"),
								rs.getInt("AtkRangingBonus"), rs.getInt("DefStabBonus"), rs.getInt("DefSlashBonus"), rs.getInt("DefCrushBonus"), rs.getInt("DefMagicBonus"),
								rs.getInt("DefRangingBonus"), rs.getInt("StrengthBonus"), rs.getInt("PrayerBonus"));
						AllItems.add(item);
					}
					for (int k = 0; k < AllItems.size(); k++){
					int[] Bonuses = new int[12];
					    Bonuses[0] = AllItems.get(k).AtkStabBonus;
						Bonuses[1] = AllItems.get(k).AtkSlashBonus;
						Bonuses[2] = AllItems.get(k).AtkCrushBonus;
						Bonuses[3] = AllItems.get(k).AtkMagicBonus;
						Bonuses[4] = AllItems.get(k).AtkRangingBonus;
						Bonuses[5] = AllItems.get(k).DefStabBonus;
						Bonuses[6] = AllItems.get(k).DefSlashBonus;
						Bonuses[7] = AllItems.get(k).DefCrushBonus;
						Bonuses[8] = AllItems.get(k).DefMagicBonus;
						Bonuses[9] = AllItems.get(k).DefRangingBonus;
						Bonuses[10] = AllItems.get(k).StrengthBonus;
						Bonuses[11] = AllItems.get(k).PrayerBonus;


					newItemList(AllItems.get(k).ItemID, AllItems.get(k).ItemName.replaceAll("_", " "), AllItems.get(k).ItemDescription.replaceAll("_", " "),
							AllItems.get(k).ShopCost, AllItems.get(k).LowAlch, AllItems.get(k).HighAlch, Bonuses);
					}

		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
class ItemListInfo
{
	public int ItemID;
    public String ItemName;
    public String ItemDescription;
    public int LowAlch;
    public int HighAlch;
    public int ShopCost;
    public int AtkStabBonus;
    public int AtkCrushBonus;
    public int AtkMagicBonus;
    public int AtkRangingBonus;
    public int AtkSlashBonus;
    public int DefStabBonus;
    public int DefSlashBonus;
    public int DefCrushBonus;
    public int DefMagicBonus;
    public int DefRangingBonus;
    public int StrengthBonus;
    public int PrayerBonus;

    public ItemListInfo(int itemId, String itemName, String itemDescription, int lowAlch, int highAlch, int shopCost, int atkStabBonus, int atkSlashBonus, int atkCrushBonus, int atkMagicBonus, int atkRangingBonus, int defStabBonus, int defSlashBonus,
						int defCrushBonus, int defMagicBonus, int defRangingBonus, int strengthBonus, int prayerBonus){
    	this.ItemID = itemId;
    	this.ItemName = itemName;
    	this.ItemDescription = itemDescription;
    	this.LowAlch = lowAlch;
    	this.HighAlch = highAlch;
    	this.ShopCost = shopCost;
    	this.AtkStabBonus = atkStabBonus;
    	this.AtkSlashBonus = atkSlashBonus;
    	this.AtkCrushBonus = atkCrushBonus;
    	this.AtkMagicBonus = atkMagicBonus;
    	this.AtkRangingBonus = atkRangingBonus;
    	this.DefStabBonus = defStabBonus;
    	this.DefSlashBonus = defSlashBonus;
    	this.DefCrushBonus = defCrushBonus;
    	this.DefMagicBonus = defMagicBonus;
    	this.DefRangingBonus = defRangingBonus;
    	this.StrengthBonus = strengthBonus;
    	this.PrayerBonus = prayerBonus;


	}


}

