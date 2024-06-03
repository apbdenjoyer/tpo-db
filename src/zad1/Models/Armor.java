package zad1.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Armor model
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Armor {
        private int itemId;
        private int durability;
        private int protection;
    }
