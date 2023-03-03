package com.chrisburtch.fetchrewards;

import androidx.annotation.NonNull;

import java.util.Comparator;

public class ItemData {
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
    @NonNull
    public String toString(){
        return "ListID: "+this.listID+" Name: "+this.name+" ID: "+this.id;
    }

    static class SortByListIDAndName implements Comparator<ItemData>{
        /*********************************************************************************
         Purpose: Compare override.
         Sort will start with ListID value (assumption made that there are only single digit ListID values)
         Then will evaluate name length to ensure proper order (ex. 6 appears before 54)
         Then will evaluate name value to ensure proper order (ex. 54 appears before 55)
         *********************************************************************************/
        @Override
        public int compare(ItemData item1, ItemData item2) {
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
