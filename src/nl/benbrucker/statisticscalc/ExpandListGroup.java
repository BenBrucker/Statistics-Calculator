package nl.benbrucker.statisticscalc;

import java.util.ArrayList;

public class ExpandListGroup {
	 
	private String name;
	private ArrayList<ExpandListChild> items;
	private int groupValue;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<ExpandListChild> getItems() {
		return items;
	}
	public void setItems(ArrayList<ExpandListChild> items) {
		this.items = items;
	}
	public int getGroupValue() {
		return groupValue;
	}
	public void setGroupValue(int groupValue) {
		this.groupValue = groupValue;
	}
	
	
}
