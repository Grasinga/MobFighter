package net.grasinga.MobFighter;

import java.util.ArrayList;

public class PlayerNames {
	
	ArrayList<String> names = new ArrayList<String>();
	
	public ArrayList<String> getAllNames(){return names;}
	
	public void setTheArray(ArrayList<String> a){names = a;}
	
	public void addName(String name){names.add(name);}
	
	public void removeName(String name){names.remove(name);}
	
	public void removeAll()
	{
		for(int i=names.size()-1; i >= 0;i--)
			removeName(names.get(i));
	}
	
	public String getName(int a){return names.get(a);}
	
	public int getSize(){return (names.size());}
}
