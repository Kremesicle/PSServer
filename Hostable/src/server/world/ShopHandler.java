package server.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import server.Config;
import server.Server;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
* Shops
**/

public class ShopHandler {

	public static int MaxShops = 101;
	public static int MaxShopItems = 101;
	public static int MaxInShopItems = 20;
	public static int MaxShowDelay = 10;
	public static int MaxSpecShowDelay = 60;
	public static int TotalShops = 0;
	public static int[][] ShopItems = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsN = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsDelay = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsSN = new int[MaxShops][MaxShopItems];
	public static int[] ShopItemsStandard = new int[MaxShops];
	public static String[] ShopName = new String[MaxShops];
	public static int[] ShopSModifier = new int[MaxShops];
	public static int[] ShopBModifier = new int[MaxShops];

	public ShopHandler() {
		for(int i = 0; i < MaxShops; i++) {
			for(int j = 0; j < MaxShopItems; j++) {
				ResetItem(i, j);
				ShopItemsSN[i][j] = 0;
			}
			ShopItemsStandard[i] = 0;
			ShopSModifier[i] = 0;
			ShopBModifier[i] = 0;
			ShopName[i] = "";
		}
		TotalShops = 0;
		loadShops("shops.cfg");
	}

	public static void shophandler() {
	Misc.println("Shop Handler class successfully loaded");
	}

	public void process() {
		boolean DidUpdate = false;
		for(int i = 1; i <= TotalShops; i++) {
			for(int j = 0; j < MaxShopItems; j++) {
				if (ShopItems[i][j] > 0) {
					if (ShopItemsDelay[i][j] >= MaxShowDelay) {
						if (j <= ShopItemsStandard[i] && ShopItemsN[i][j] <= ShopItemsSN[i][j]) {
							if (ShopItemsN[i][j] < ShopItemsSN[i][j]) {
								ShopItemsN[i][j] += 1;
								DidUpdate = true;
								ShopItemsDelay[i][j] = 1;
								ShopItemsDelay[i][j] = 0;
								DidUpdate = true;
							}
						} else if (ShopItemsDelay[i][j] >= MaxSpecShowDelay) {
							DiscountItem(i, j);
							ShopItemsDelay[i][j] = 0;
							DidUpdate = true;
						}
					}
					ShopItemsDelay[i][j]++;
				}
			}
			if (DidUpdate == true) {
				for (int k = 1; k < Config.MAX_PLAYERS; k++) {
					if (PlayerHandler.players[k] != null) {
						if (PlayerHandler.players[k].isShopping == true && PlayerHandler.players[k].myShopId == i) {
							PlayerHandler.players[k].updateShop = true;
							DidUpdate =false;
							PlayerHandler.players[k].updateshop(i);
						}
					}
				}
				DidUpdate = false;
			}
		}
	}

	public void DiscountItem(int ShopID, int ArrayID) {
		ShopItemsN[ShopID][ArrayID] -= 1;
		if (ShopItemsN[ShopID][ArrayID] <= 0) {
			ShopItemsN[ShopID][ArrayID] = 0;
			ResetItem(ShopID, ArrayID);
		}
	}

	public void ResetItem(int ShopID, int ArrayID) {
		ShopItems[ShopID][ArrayID] = 0;
		ShopItemsN[ShopID][ArrayID] = 0;
		ShopItemsDelay[ShopID][ArrayID] = 0;
	}

	public boolean loadShops(String FileName) {
		List<ShopIDName> AllShopIDS = new ArrayList<ShopIDName>();
		List<ShopItemIDs> ShopItemLinkIDs = new ArrayList<ShopItemIDs>();
    	String connectionUrl = "jdbc:sqlserver://localhost:1433;instanceName=DESKTOP-92GJDD3;databaseName=RunescapeServer;";
			try (Connection con = DriverManager.getConnection(connectionUrl, "Kremesicle", "lol"); CallableStatement stmt = con.prepareCall("{call GetShopData()}");) {
				ResultSet rs = stmt.executeQuery();


				// Iterate through the data in the result set and display it.
				while (rs.next()) {
					ShopIDName idname = new ShopIDName(rs.getInt("StoreID"), rs.getString("StoreName"), rs.getInt("Sell"), rs.getInt("Buy"));
					AllShopIDS.add(idname);
				}
				stmt.getMoreResults();
				ResultSet rs2 = stmt.getResultSet();
				while (rs2.next()){
					ShopItemIDs shop =  new ShopItemIDs(rs2.getInt("StoreID"), rs2.getInt("ItemID"), rs2.getInt("Amount"));
					ShopItemLinkIDs.add(shop);
				}
	}
			// Handle any errors that may have occurred.
			catch (SQLException e) {
				e.printStackTrace();
			}

			for(int i = 0; i < AllShopIDS.size(); i++){
				int ShopID = AllShopIDS.get(i).ShopId;
				ShopName[ShopID] =  AllShopIDS.get(i).ShopName;
				ShopSModifier[ShopID] = AllShopIDS.get(i).Sell;
				ShopBModifier[ShopID] = AllShopIDS.get(i).Buy;;
				int k = 0;
				for (int ii = 0; ii < ShopItemLinkIDs.size(); ii++) {
					if (ShopID == ShopItemLinkIDs.get(ii).ShopId) {
						ShopItems[ShopID][k] = ShopItemLinkIDs.get(ii).ItemId +1;
						ShopItemsN[ShopID][k] = ShopItemLinkIDs.get(ii).Amount;
						ShopItemsSN[ShopID][k] = ShopItemLinkIDs.get(ii).Amount;
						ShopItemsStandard[ShopID]++;
						k++;
					}
				}
				TotalShops++;
			}

return true;

	}
}
class ShopItemIDs{
	public int ShopId;
	public int ItemId;
	public int Amount;
	public ShopItemIDs(int shopid, int itemid, int amount){
this.ShopId = shopid;
this.ItemId = itemid;
this.Amount = amount;
	}
}
class ShopIDName{
	public int ShopId;
	public String ShopName;
	public int Sell;
	public int Buy;
	public ShopIDName(int shopid, String shopName, int sell, int buy){
		this.ShopId = shopid;
		this.ShopName = shopName;
		this.Sell = sell;
		this.Buy = 	buy;
	}

}
