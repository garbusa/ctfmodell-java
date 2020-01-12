package ctfmodell.database;

import ctfmodell.controller.Controller;
import ctfmodell.model.Example;
import ctfmodell.model.Landscape;
import ctfmodell.serialization.XMLSerialization;

import javax.xml.stream.*;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"SqlDialectInspection", "StringConcatenationInsideStringBufferAppend"})
public class DatabaseManager {

    private static String dbName = "examples";

    private static String createExampleTable = "CREATE TABLE Examples (e_id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
            "code CLOB NOT NULL," +
            "landscape CLOB NOT NULL," +
            "officer_name VARCHAR(255) NOT NULL" +
            ")";
    private static String createTagTable = "CREATE TABLE Tags (tag VARCHAR(255) NOT NULL, " +
            "example_id INTEGER NOT NULL REFERENCES Examples(e_id)," +
            "PRIMARY KEY(tag, example_id)) ";

    private static String checkExamplesTable = "SELECT count(*) FROM Examples";
    private static String checkTagsTable = "SELECT count(*) FROM Tags";

    private static String insertExample = "INSERT INTO Examples (code, landscape, officer_name) values(?, ?, ?)";
    private static String insertTags = "INSERT INTO Tags (tag, example_id) values(?, ?)";

    private static String selectTags = "SELECT * FROM Tags WHERE tag = ?";
    private static String selectExample = "SELECT * FROM Examples WHERE e_id = ?";

    private static Connection connection = null;

    public static void createDatabase() {
        System.out.println("Versuche Datenbank zu erstellen");

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection connection = DatabaseManager.getConnection();

            if (connection == null) {
                System.err.println("Etwas ist schiefgelaufen");
                return;
            }

            Statement checkStatement = connection.createStatement();
            Statement stmt = connection.createStatement();

            boolean tableDoesNotExists = false;

            try {
                checkStatement.execute(checkExamplesTable);
                checkStatement.execute(checkTagsTable);
            } catch (SQLSyntaxErrorException ex) {
                tableDoesNotExists = true;
            }

            if (tableDoesNotExists) {
                stmt.execute(createExampleTable);
                stmt.execute(createTagTable);
                System.out.println("Datenbank wird erstellt");
            } else {
                System.err.println("Datenbank wurde schon erstellt");
            }

            stmt.close();
            checkStatement.close();
            connection.close();

        } catch (ClassNotFoundException e) {
            System.err.println("Apache Derby Driver konnte nicht gefunden werden");
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    connection = DriverManager.getConnection("jdbc:derby:" + dbName + ";create=true");
                } catch (SQLException e) {
                    connection = null;
                    System.err.println("JDBC Connection konnte nicht aufgebaut werden.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    public static boolean saveExample(Landscape landscape, String code, List<String> tags, String officerName) throws XMLStreamException {
        StringWriter stringWriter = new StringWriter();

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(stringWriter);
        XMLSerialization.writeXML(xmlWriter, landscape);

        String landscapeXML = stringWriter.toString();

        PreparedStatement stmt = null;
        try {
            connection = DatabaseManager.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.prepareStatement(insertExample, Statement.RETURN_GENERATED_KEYS);
            Clob codeClob = connection.createClob();
            codeClob.setString(1, code);
            stmt.setClob(1, codeClob);
            Clob landscapeClob = connection.createClob();
            landscapeClob.setString(1, landscapeXML);
            stmt.setClob(2, landscapeClob);
            stmt.setString(3, officerName);
            stmt.execute();

            ResultSet exampleId = stmt.getGeneratedKeys();

            int id = -1;
            while (exampleId.next()) {
                id = exampleId.getInt(1);
            }

            if (id != -1) {
                for (String tag : tags) {
                    stmt = connection.prepareStatement(insertTags);
                    stmt.setString(1, tag);
                    stmt.setInt(2, id);
                    stmt.execute();
                }

            } else {
                throw new Throwable();
            }

            connection.commit();
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            runFinally(stmt);
        }

        return false;
    }

    private static void runFinally(PreparedStatement stmt) {
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Example> getExamplesOfTag(String tag) {
        PreparedStatement stmt = null;
        try {
            connection = DatabaseManager.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.prepareStatement(selectTags);
            stmt.setString(1, tag);
            ResultSet exampleIds = stmt.executeQuery();

            List<Example> examples = new ArrayList<>();
            while (exampleIds.next()) {
                int exampleId = exampleIds.getInt(2);
                stmt = connection.prepareStatement(selectExample);
                stmt.setInt(1, exampleId);
                ResultSet exampleSet = stmt.executeQuery();
                while (exampleSet.next()) {
                    int id = exampleSet.getInt(1);
                    Clob codeLob = exampleSet.getClob(2);
                    Clob xmlLob = exampleSet.getClob(3);
                    String officerName = exampleSet.getString(4);

                    StringBuilder codeStringBuffer = new StringBuilder();
                    StringBuilder xmlStringBuffer = new StringBuilder();
                    Reader r = codeLob.getCharacterStream();
                    int ch;
                    while ((ch = r.read()) != -1) {
                        codeStringBuffer.append("" + (char) ch);
                    }
                    r = xmlLob.getCharacterStream();
                    while ((ch = r.read()) != -1) {
                        xmlStringBuffer.append("" + (char) ch);
                    }

                    Example example = new Example(id, codeStringBuffer.toString(),
                            xmlStringBuffer.toString(), officerName);
                    examples.add(example);
                }
            }


            connection.commit();
            return examples;
        } catch (Throwable th) {
            th.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            runFinally(stmt);
        }

        return Collections.emptyList();
    }

    public static void loadExample(Example example, Controller controller) throws XMLStreamException {
        controller.setOfficerLabel(example.getOfficerName());
        controller.getCodeEditor().setText(example.getCode());
        controller.compile();

        Reader r = new StringReader(example.getXml());
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(r);

        XMLSerialization.loadXML(parser, controller);


    }

    public static void shutdownDatabase() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:derby:" + dbName + ";shutdown=true");
        } catch (Exception e) {
            System.out.println("Database Shutdown");
        }
    }

}
