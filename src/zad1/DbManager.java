package zad1;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbManager {
    private static DbManager instance;
    private final DataSource dataSource;
    private boolean created = false;

    private DbManager(DataSource dataSource) {
        this.dataSource = dataSource;
        createDb();
        fillDb();
    }

    private void fillDb() {
        String insertSql = "\n" +
                "INSERT INTO Item (id, name, description, stack_size, craftable, version_added)\n" +
                "VALUES (280, 'Stick', 'A stick is an item used for crafting many tools and items.', 64, true, 'Indev 0.31');\n" +
                "\n" +
                "\n" +
                "INSERT INTO Item (id, name, description, stack_size, craftable, version_added)\n" +
                "VALUES (4, 'Cobblestone', 'Cobblestone is a common block, obtained from mining stone. It is mainly used for crafting or as a building block.', 64, false, 'pre-Classic rd-20090515');\n" +
                "\n" +
                "\n" +
                "INSERT INTO Item (id, name, description, stack_size, craftable, version_added)\n" +
                "VALUES (274, 'Stone Pickaxe', 'A pickaxe is a tool required to mine ores, rocks, rock-based blocks and metal-based blocks quickly and obtain them as items. A pickaxe mines faster and can obtain more block types as items depending on the material it is made from.', 1, true, 'Indev 0.31');\n" +
                "\n" +
                "INSERT INTO Tool (item_id, durability, damage)\n" +
                "VALUES (274, 132, 2);\n" +
                "\n" +
                "INSERT INTO Recipe (result_id, result_quantity, item_1, item_2, item_3, item_5, item_8)\n" +
                "VALUES (274, 1, 4, 4, 4, 280, 280);\n" +
                "\n" +
                "\n" +
                "INSERT INTO Item (id, name, description, stack_size, craftable, version_added)\n" +
                "VALUES (272, 'Stone Sword', 'The sword is a melee weapon that is mainly used to deal damage to entities or for breaking certain blocks faster than by hand. A sword is made from one of six materials, in order of increasing quality and expense: wood, gold, stone, iron, diamond and netherite.', 1, true, 'Indev 0.31');\n" +
                "\n" +
                "INSERT INTO Tool (item_id, durability, damage)\n" +
                "VALUES (272, 132, 5);\n" +
                "\n" +
                "INSERT INTO Recipe (result_id, result_quantity, item_2, item_5, item_8)\n" +
                "VALUES (272, 1, 4, 4, 280);\n" +
                "\n" +
                "\n" +
                "UPDATE Block\n" +
                "SET tool_id = (SELECT item_id FROM Tool WHERE item_id = 274)  \n" +
                "WHERE Item_id = 4;\n";
        executeLongSql(insertSql);

    }

    public static synchronized DbManager getInstance(DataSource dataSource) {
        if (instance == null) {
            instance = new DbManager(dataSource);
        }
        return instance;
    }

    public void createDb() {
        if (created) {
            return;
        }

        String createSql = "CREATE TABLE Armor\n" +
                "(\n" +
                "    Item_id    INT PRIMARY KEY NOT NULL,\n" +
                "    durability INT NOT NULL,\n" +
                "    protection INT NOT NULL\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE Block\n" +
                "(\n" +
                "    Item_id  INT PRIMARY KEY NOT NULL,\n" +
                "    hardness INT NOT NULL,\n" +
                "    tool_id  INT\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE Food\n" +
                "(\n" +
                "    Item_id INT PRIMARY KEY NOT NULL,\n" +
                "    health  INT NOT NULL\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE Item\n" +
                "(\n" +
                "    id            INT PRIMARY KEY NOT NULL,\n" +
                "    name          VARCHAR(100) NOT NULL,\n" +
                "    description   VARCHAR(500) NOT NULL,\n" +
                "    stack_size    INT NOT NULL,\n" +
                "    craftable     BOOLEAN NOT NULL,\n" +
                "    version_added VARCHAR(50) NOT NULL\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE Recipe\n" +
                "(\n" +
                "    result_id       INT PRIMARY KEY NOT NULL,\n" +
                "    result_quantity INT NOT NULL,\n" +
                "    item_1          INT,\n" +
                "    item_2          INT,\n" +
                "    item_3          INT,\n" +
                "    item_4          INT,\n" +
                "    item_5          INT,\n" +
                "    item_6          INT,\n" +
                "    item_7          INT,\n" +
                "    item_8          INT,\n" +
                "    item_9          INT\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE Tool\n" +
                "(\n" +
                "    item_id    INT PRIMARY KEY NOT NULL,\n" +
                "    durability INT NOT NULL,\n" +
                "    damage     INT NOT NULL\n" +
                ");\n" +
                "\n" +
                "-- Adding Foreign Key Constraints\n" +
                "ALTER TABLE Armor\n" +
                "    ADD CONSTRAINT Armor_Item FOREIGN KEY (Item_id) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Block\n" +
                "    ADD CONSTRAINT Block_Item FOREIGN KEY (Item_id) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Block\n" +
                "    ADD CONSTRAINT Block_Tool FOREIGN KEY (tool_id) REFERENCES Tool (item_id);\n" +
                "\n" +
                "ALTER TABLE Food\n" +
                "    ADD CONSTRAINT Food_Item FOREIGN KEY (Item_id) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Recipe\n" +
                "    ADD CONSTRAINT Recipe_Item FOREIGN KEY (result_id) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Recipe\n" +
                "    ADD CONSTRAINT Recipe_Item1 FOREIGN KEY (item_1) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Recipe\n" +
                "    ADD CONSTRAINT Recipe_Item2 FOREIGN KEY (item_2) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Recipe\n" +
                "    ADD CONSTRAINT Recipe_Item3 FOREIGN KEY (item_3) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Recipe\n" +
                "    ADD CONSTRAINT Recipe_Item4 FOREIGN KEY (item_4) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Recipe\n" +
                "    ADD CONSTRAINT Recipe_Item5 FOREIGN KEY (item_5) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Recipe\n" +
                "    ADD CONSTRAINT Recipe_Item6 FOREIGN KEY (item_6) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Recipe\n" +
                "    ADD CONSTRAINT Recipe_Item7 FOREIGN KEY (item_7) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Recipe\n" +
                "    ADD CONSTRAINT Recipe_Item8 FOREIGN KEY (item_8) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Recipe\n" +
                "    ADD CONSTRAINT Recipe_Item9 FOREIGN KEY (item_9) REFERENCES Item (id);\n" +
                "\n" +
                "ALTER TABLE Tool\n" +
                "    ADD CONSTRAINT Tool_Item_fk FOREIGN KEY (item_id) REFERENCES Item (id);\n";
        executeLongSql(createSql);
        created = true;

    }

    public void dropDb() {
        if (!created) {
            return;
        }
        try (Connection connection = DriverManager.getConnection("jdbc:derby:mc_comp;shutdown=true")) {
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        created = false;
    }

    private void executeLongSql(String sql) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            String[] sqlCommands = sql.split(";");

            for (String sqlCommand : sqlCommands) {
                sqlCommand = sqlCommand.trim();

                if (!sqlCommand.isEmpty()) {
                    System.out.println("Executing SQL Command: " + sqlCommand);
                    try {
                        statement.executeUpdate(sqlCommand);
                    } catch (SQLException e) {
                        continue;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing SQL commands", e);
        }
    }


    public boolean isCreated() {
        return created;
    }


}
