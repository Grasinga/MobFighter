package net.grasinga.MobFighter;

import java.util.ArrayList;

class PlayerNames {

    private ArrayList<String> names = new ArrayList<String>();

    ArrayList<String> getAllNames(){return names;}

    void addName(String name){names.add(name);}

    void removeName(String name){names.remove(name);}

    void removeAll()
    {
        for(int i=names.size()-1; i >= 0;i--)
            removeName(names.get(i));
    }

    String getName(int a){return names.get(a);}

    int getSize(){return (names.size());}
}
