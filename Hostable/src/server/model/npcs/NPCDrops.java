package server.model.npcs;

import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * @author Sanity
 */

public class NPCDrops {
	
	public NPCDrops() {
		loadDrops();
	}
	

	
	public static HashMap<Integer, int[][]> normalDrops = new HashMap<Integer, int[][]>();
	public static HashMap<Integer, int[][]> rareDrops = new HashMap<Integer, int[][]>();
	public static HashMap<Integer, int[]> constantDrops = new HashMap<Integer, int[]>();
	public static HashMap<Integer, Integer> dropRarity = new HashMap<Integer,Integer>();
	
	public void loadDrops() {
		try {
			List<NpcDropRates> NpcDropRates = new ArrayList<NpcDropRates>();
			List<NpcDropList> NpcDrops = new ArrayList<NpcDropList>();
			List<NpcDropAmount> NpcAmountDrop = new ArrayList<NpcDropAmount>();
			String connectionUrl = "jdbc:sqlserver://localhost:1433;instanceName=DESKTOP-92GJDD3;databaseName=RunescapeServer;";
			try (Connection con = DriverManager.getConnection(connectionUrl, "Kremesicle", "lol"); CallableStatement stmt = con.prepareCall("{call GetNpcDropInfo()}");) {

				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					NpcDropRates item = new NpcDropRates(rs.getInt("NpcID"), rs.getInt("DropRate"));
					NpcDropRates.add(item);
				}

				stmt.getMoreResults();
				ResultSet rs2 = stmt.getResultSet();

				while (rs2.next()) {
					NpcDropList item = new NpcDropList(rs2.getInt("NpcID"), rs2.getInt("ItemID"), rs2.getInt("DropAmount"), rs2.getString("Rarity").charAt(0));
					NpcDrops.add(item);
				}
				stmt.getMoreResults();
				ResultSet rs3 = stmt.getResultSet();

				while (rs3.next()) {
					NpcDropAmount item = new NpcDropAmount(rs3.getInt("NpcID"), rs3.getString("Rarity").charAt(0), rs3.getInt("ItemAmount"));
					NpcAmountDrop.add(item);
				}

			int[][][] npcDrops = new int [3800][][];
			int[][][] rareDrops2 = new int [3800][][];
			int[] itemRarity = new int [3800];

			for (int i = 0; i < NpcDropRates.size(); i++){
				int npcId = NpcDropRates.get(i).NpcID;
				itemRarity[npcId] = NpcDropRates.get(i).DropRate -1;
				dropRarity.put(npcId, itemRarity[npcId]);

			}

			for (int i = 0; i < NpcDrops.size(); i++){
				if (NpcDrops.get(i).Rarity == 'N'){
					int npcID = NpcDrops.get(i).NpcID;
					int normalCount = 0;
					if(npcDrops[npcID] != null){
						normalCount = npcDrops[npcID].length - 1;
					}
					else{
						int normalDropCount = NpcAmountDrop.stream().filter((NpcID) -> NpcID.NpcID == npcID && NpcID.Rarity == 'N').findFirst().get().ItemAmount;
						npcDrops[npcID] = new int[normalDropCount][2];
					}

					npcDrops[npcID][normalCount][0] = NpcDrops.get(i).ItemID;
					npcDrops[npcID][normalCount][1] = NpcDrops.get(i).DropAmount;
					normalDrops.put(npcID, npcDrops[npcID]);
				}
				if (NpcDrops.get(i).Rarity == 'R'){
					int npcID = NpcDrops.get(i).NpcID;
					int rareCount = 0;
					if(rareDrops2[npcID] != null){
						rareCount = rareDrops2[npcID].length - 1;
					}
					else{
						int rareDropCount = NpcAmountDrop.stream().filter((NpcID) -> NpcID.NpcID == npcID && NpcID.Rarity == 'R').findFirst().get().ItemAmount;
						rareDrops2[npcID] = new int[rareDropCount][2];
					}

					rareDrops2[npcID][rareCount][0] = NpcDrops.get(i).ItemID;
					rareDrops2[npcID][rareCount][1] = NpcDrops.get(i).DropAmount;
					rareDrops.put(npcID, rareDrops2[npcID]);
				}
			}
			} catch (SQLException e) {
				e.printStackTrace();

			}
			loadConstants();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void loadConstants() {
		List<NpcConstantDrops> npcConstantDrops = new ArrayList<NpcConstantDrops>();
		List<NpcConstantDropsAmount> npcConstantDropsAmounts = new ArrayList<NpcConstantDropsAmount>();
		try {
			String connectionUrl = "jdbc:sqlserver://localhost:1433;instanceName=DESKTOP-92GJDD3;databaseName=RunescapeServer;";
			try (Connection con = DriverManager.getConnection(connectionUrl, "Kremesicle", "lol"); CallableStatement stmt = con.prepareCall("{call GetNpcConstantDropInfo()}");) {
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					NpcConstantDrops item = new NpcConstantDrops(rs.getInt("NpcID"), rs.getInt("ItemID"));
					npcConstantDrops.add(item);
				}

				stmt.getMoreResults();
				ResultSet rs2 = stmt.getResultSet();

				while (rs2.next()) {
					NpcConstantDropsAmount item = new NpcConstantDropsAmount(rs2.getInt("NpcID"), rs2.getInt("ItemAmount"));
					npcConstantDropsAmounts.add(item);
				}
			}
			catch (SQLException e){

			}
			for(int i = 0; i < npcConstantDrops.size(); i++){
				int npcId = npcConstantDrops.get(i).NpcID;
				int count = 0;
				if(!constantDrops.containsKey(npcId)){
					int[] temp = new int[npcConstantDropsAmounts.stream().filter((NpcID) -> NpcID.NpcID == npcId).findFirst().get().ConstantItemAmount];
					temp[count] = npcConstantDrops.get(i).ItemID;
					constantDrops.put(npcId,temp);

				}
				else{
					int[] itemArray = constantDrops.get(npcId);
					count = itemArray.length - 1;
					itemArray[count] = npcConstantDrops.get(i).ItemID;
					constantDrops.put(npcId,itemArray);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
}
class NpcDropRates{
	int NpcID;
	int DropRate;

	public NpcDropRates(int npcID, int dropRate){
		NpcID = npcID;
		DropRate = dropRate;
	}

}
class NpcDropList{
	int NpcID;
	int ItemID;
	int DropAmount;
	char Rarity;

	public NpcDropList(int npcID, int itemID, int dropAmount, char rarity){
		NpcID = npcID;
		ItemID = itemID;
		DropAmount = dropAmount;
		Rarity = rarity;
	}
}
class NpcDropAmount{
	int NpcID;
	char Rarity;
	int ItemAmount;

	public NpcDropAmount(int npcID, char rarity, int itemAmount){
		NpcID = npcID;
		Rarity = rarity;
		ItemAmount = itemAmount;
	}
}
class NpcConstantDrops{
	int NpcID;
	int ItemID;

	public NpcConstantDrops(int npcID, int itemID){
		NpcID = npcID;
		ItemID = itemID;
	}
}
class NpcConstantDropsAmount{
	int NpcID;
	int ConstantItemAmount;

	public NpcConstantDropsAmount(int npcID, int constantItemAmount){
		NpcID = npcID;
		ConstantItemAmount = constantItemAmount;
	}
}

