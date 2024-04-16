import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.schema.SchemaPlus;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Calcite_Main {
    public static void main(String[] args) throws IOException {
        try {
            CuckooFilter cuckooFilter = Load_CSV_To_The_Filter.loadFilterFromCsv("resources/books.csv", 50, 4, 500);

            Class.forName("org.apache.calcite.jdbc.Driver");

            Properties info = new Properties();
            info.setProperty("model", "inline:"
            	    + "{ version: '1.0',"
            	    + "  defaultSchema: 'default',"
            	    + "  schemas: ["
            	    + "     { name: 'default',"
            	    + "       type: 'custom',"
            	    + "       factory: 'org.apache.calcite.adapter.csv.CsvSchemaFactory',"
            	    + "       operand: { directory: 'C:/Users/MKT/git/repository/Cuckoo/resources',"
            	    + "                  file: 'books.csv',"
            	    + "                  flavor: 'scannable' }"
            	    + "     }"
            	    + "  ]"
            	    + "}");

            Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
            CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
            System.out.println("Sub-schemas: " + calciteConnection.getRootSchema().getSubSchemaNames());
            System.out.println("Tables in default schema: " + calciteConnection.getRootSchema().getSubSchema("default").getTableNames());
            SchemaPlus rootSchema = calciteConnection.getRootSchema();
            System.out.println("Tables in default schema: " + calciteConnection.getRootSchema().getSubSchema("default").getTableNames());


            // Build the query using the cuckoo filter
            
            StringBuilder queryBuilder = new StringBuilder("select * from \"books\" where ISBN IN (");
            boolean first = true;
            for (String isbn : cuckooFilter.getAllKeys()) {
                if (first) {
                    first = false;
                } else {
                    queryBuilder.append(", ");
                }
                queryBuilder.append("'").append(isbn).append("'");
            }
            queryBuilder.append(")");

            try (Statement statement = calciteConnection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(queryBuilder.toString());

                while (resultSet.next()) {
                    String isbn = resultSet.getString("ISBN");
                    String title = resultSet.getString("Book-Title");
                    String author = resultSet.getString("Book-Author");
                    String yearOfPublication = resultSet.getString("Year-Of-Publication");
                    String publisher = resultSet.getString("Publisher");
                    String url = resultSet.getString("Image-URL-L");
                    System.out.printf("ISBN: %s, Title: %s, Author: %s, Year: %s, Publisher: %s%n",
                            isbn, title, author, resultSet.getString("Year-Of-Publication"), publisher);

                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}