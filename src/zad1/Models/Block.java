package zad1.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Block model
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Block {
        private int itemId;
        private int hardness;
        private int toolId;
    }

