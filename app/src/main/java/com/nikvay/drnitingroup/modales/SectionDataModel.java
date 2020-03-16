package com.nikvay.drnitingroup.modales;

import java.util.ArrayList;

public class SectionDataModel {
    private String headerTitle;
    private ArrayList<HomeMenu> allItemsInSection;

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<HomeMenu> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<HomeMenu> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }
}
