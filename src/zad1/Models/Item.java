package zad1.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Item model
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Item {
        private int id;
        private String name;
        private String description;
        private int stackSize;
        private boolean craftable;
        private String versionAdded;
    }
