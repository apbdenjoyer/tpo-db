package zad1.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tool model
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Tool {
        private int itemId;
        private int durability;
        private int damage;
    }
