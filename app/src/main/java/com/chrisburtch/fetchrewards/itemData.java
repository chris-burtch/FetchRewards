package com.chrisburtch.fetchrewards;

import java.util.Comparator;

public class itemData {
    int id;
    int listID;
    String name;

    //#region GETTERS/SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //#endregion

    @Override
    public String toString(){
        return "listID: "+listID+" name: "+name+" id: "+id;
    }

    static class SortByListIDAndName implements Comparator<itemData>{

        @Override
        public int compare(itemData item1, itemData item2) {
            int listIdCompare = item1.getListID() - item2.getListID();
            int nameLengthCompare = item1.getName().length() - item2.getName().length();
            int nameCompare  = item1.getName().compareTo(item2.getName());
            if(listIdCompare == 0){
                if(nameLengthCompare == 0){
                    return nameCompare;
                }
                return nameLengthCompare;
            }
            return listIdCompare;
        }
    }


}
