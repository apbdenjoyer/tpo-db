package zad1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = "/mc")
public class TpoServlet extends HttpServlet {

    private DataSource dataSource;
    private DbManager dbManager;
    private String defaultType = "Armor"; // Default selected type

    @Override
    public void init() throws ServletException {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            this.dataSource = (DataSource) envContext.lookup("jdbc/mc_comp");
            this.dbManager = DbManager.getInstance(dataSource);
        } catch (NamingException e) {
            throw new ServletException("Error initializing servlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<title>Item List</title>");
        out.println("<style>");
        out.println("table { width: 100%; border-collapse: collapse; }");
        out.println("table, th, td { border: 1px solid black; padding: 8px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h2>Item List</h2>");

        // Display search form
        out.println("<form method=\"get\">");
        out.println("<label for=\"type\">Type:</label>");
        out.println("<select id=\"type\" name=\"type\">");

        // Populate select options
        String[] itemTypes = {"Armor", "Block", "Food", "Tool"};
        for (String type : itemTypes) {
            if (type.equals(defaultType)) {
                out.println("<option value=\"" + type + "\" selected>" + type + "</option>");
            } else {
                out.println("<option value=\"" + type + "\">" + type + "</option>");
            }
        }

        out.println("</select>");

        out.println("<label for=\"name\">Name contains:</label>");
        out.println("<input type=\"text\" id=\"name\" name=\"name\">");

        out.println("<label for=\"description\">Description contains:</label>");
        out.println("<input type=\"text\" id=\"description\" name=\"description\">");

        out.println("<br>");
        out.println("<button type=\"submit\" name=\"searchType\" value=\"type\">Search by Type</button>");
        out.println("<button type=\"submit\" name=\"searchType\" value=\"name\">Contains in Name</button>");
        out.println("<button type=\"submit\" name=\"searchType\" value=\"description\">Contains in Description</button>");
        out.println("</form>");

        // Process form submission
        String type = req.getParameter("type");
        String keywordName = req.getParameter("name");
        String keywordDescription = req.getParameter("description");
        String searchType = req.getParameter("searchType");

        // Update defaultType if type is selected
        if (type != null && !type.isEmpty()) {
            defaultType = type;
        }

        try (Connection connection = dataSource.getConnection()) {
            List<Item> items = searchItems(connection, searchType, type, keywordName, keywordDescription);

            if (items.isEmpty()) {
                out.println("<p>No items found.</p>");
            } else {
                out.println("<table>");
                out.println("<tr>");
                out.println("<th>ID</th>");
                out.println("<th>Name</th>");
                out.println("<th>Description</th>");
                out.println("</tr>");

                for (Item item : items) {
                    out.println("<tr>");
                    out.println("<td>" + item.getId() + "</td>");
                    out.println("<td>" + item.getName() + "</td>");
                    out.println("<td>" + item.getDescription() + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");
            }
        } catch (SQLException e) {
            throw new ServletException("Error retrieving data from database", e);
        }

        out.println("</body>");
        out.println("</html>");
    }

    private List<Item> searchItems(Connection connection, String searchType, String type, String keywordName, String keywordDescription) throws SQLException {
        List<Item> items = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Item WHERE 1=1");

        if ("type".equals(searchType) && type != null && !type.isEmpty()) {
            queryBuilder.append(" AND id IN (SELECT Item_id FROM ").append(type).append(")");
        } else if ("name".equals(searchType) && keywordName != null && !keywordName.isEmpty()) {
            queryBuilder.append(" AND LOWER(name) LIKE ?");
        } else if ("description".equals(searchType) && keywordDescription != null && !keywordDescription.isEmpty()) {
            queryBuilder.append(" AND LOWER(description) LIKE ?");
        }

        try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            int parameterIndex = 1;

            if ("name".equals(searchType) && keywordName != null && !keywordName.isEmpty()) {
                statement.setString(parameterIndex++, "%" + keywordName.toLowerCase() + "%");
            } else if ("description".equals(searchType) && keywordDescription != null && !keywordDescription.isEmpty()) {
                statement.setString(parameterIndex++, "%" + keywordDescription.toLowerCase() + "%");
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                items.add(new Item(id, name, description));
            }
        }

        return items;
    }

    private static class Item {
        private int id;
        private String name;
        private String description;

        public Item(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
