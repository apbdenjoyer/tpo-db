package zad1.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Food model
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Food {
        private int itemId;
        private int health;
    }
