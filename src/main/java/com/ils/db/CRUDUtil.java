package com.ils.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CRUDUtil {

    public static Object read(String tableName, String fieldName, int fieldDataType,
                              String indexFieldName, int indexDataType, Object index) {
        StringBuilder queryBuilder = new StringBuilder("Select ");
        queryBuilder.append(fieldName);
        queryBuilder.append(" from ");
        queryBuilder.append(tableName);
        queryBuilder.append(" where ");
        queryBuilder.append(indexFieldName);
        queryBuilder.append(" = ");
        queryBuilder.append(convertObjectToSQLField(index, indexDataType));
        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                switch (fieldDataType) {
                    case Types.INTEGER:
                        return rs.getInt(fieldName);
                    case Types.VARCHAR:
                        return rs.getString(fieldName);
                    default:
                        throw new IllegalArgumentException("Index type " + indexDataType + " from sql.Types is not yet supported.");
                }
            }
        } catch (SQLException exception) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not fetch from " + tableName + " by index " + index +
                            " and column " + fieldName);
            return null;
        }
    }

    public static int update(String tableName, String[] columns, Object[] values, int[] types,
                             String indexFieldName, int indexDataType, Object index) {

        int number = Math.min(Math.min(columns.length, values.length), types.length);

        StringBuilder queryBuilder = new StringBuilder("UPDATE " + tableName + " SET ");
        for (int i = 0; i < number; i++) {
            queryBuilder.append(columns[i]);
            queryBuilder.append(" = ");
            queryBuilder.append(convertObjectToSQLField(values[i], types[i]));
            if (i < number - 1) queryBuilder.append(", ");
        }
        queryBuilder.append(" WHERE ");
        queryBuilder.append(indexFieldName);
        queryBuilder.append(" = ");
        queryBuilder.append(convertObjectToSQLField(index, indexDataType));

        try (Connection conn = Database.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());

            return pstmt.executeUpdate(); //number of affected rows
        } catch (SQLException ex) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not add customer to database");
            return -1;
        }
    }

    public static long create(String tableName, String[] columns, Object[] values, int[] types) {
        int number = Math.min(Math.min(columns.length, values.length), types.length);

        StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + tableName + " (");
        for (int i = 0; i < number; i++) {
            queryBuilder.append(columns[i]);
            if (i < number - 1) queryBuilder.append(", ");
        }
        queryBuilder.append(") ");
        queryBuilder.append(" VALUES (");
        for (int i = 0; i < number; i++) {
            switch (types[i]) {
                case Types.VARCHAR:
                    queryBuilder.append("'");
                    queryBuilder.append((String) values[i]);
                    queryBuilder.append("'");
                    break;
                case Types.INTEGER:
                    queryBuilder.append((int) values[i]);
                    break;
                case Types.TIMESTAMP:
                    queryBuilder.append("'");
                    queryBuilder.append(values[i].toString());
                    queryBuilder.append("'");
                    break;
                default:
                        throw new IllegalArgumentException("Field type of " + types[i] + "is not yet supported.");
            }
            if (i < number - 1) queryBuilder.append(", ");
        }
        queryBuilder.append(");");

        try (Connection conn = Database.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());

            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not add customer to database" + ex.getMessage());
            return -1;
        }
        return -1;
    }

    public static int delete(String tableName, int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";

        try (Connection conn = Database.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();

        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not delete from " + tableName + " by id " + id +
                            " because " + e.getCause());
            return -1;
        }
    }

    public static void createTable(Class<?> model) {
        Field[] fields = model.getDeclaredFields();
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        queryBuilder.append(model.getSimpleName() +  " (\n");
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            queryBuilder.append(" " + f.getName().toUpperCase());
            switch (f.getType().getSimpleName()) {
                case "String":
                case "Action":
                    queryBuilder.append(" VARCHAR(255)");
                    break;
                case "Integer":
                    queryBuilder.append(" INTEGER");
                    break;
                case "Optional":
                    queryBuilder.append("ID INTEGER");
                    break;
                case "LocalDateTime":
                    queryBuilder.append(" TIMESTAMP");
                    break;
                case "Customer":
                    queryBuilder.append("ID INTEGER");
                    break;
                case "Product":
                    queryBuilder.append("ID INTEGER");
                    break;
                case "Part":
                    queryBuilder.append("ID INTEGER");
                    break;
                default:
                    throw new IllegalArgumentException("Type " + f.getType().getSimpleName() + " is not yet supported.");
            }
            if (f.getName().substring(f.getName().length()-2).equals("Id")) {
                queryBuilder.append(" PRIMARY KEY AUTOINCREMENT");
            } else if (!f.getName().equals("defaultPart")) {
                queryBuilder.append(" NOT NULL");
            }
            if (i < fields.length - 1) {
                queryBuilder.append(",\n");
            }
        }
        queryBuilder.append(");");
        try (Connection conn = Database.connect()) {
            PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not create table " + model.getSimpleName() +
                            " because " + e.getMessage() + "\n" + queryBuilder.toString());
        }
    }

    private static String convertObjectToSQLField(Object value, int type) {
        StringBuilder queryBuilder = new StringBuilder();
        switch (type) {
            case Types.VARCHAR:
                queryBuilder.append("'");
                queryBuilder.append(value);
                queryBuilder.append("'");
                break;
            case Types.INTEGER:
                queryBuilder.append(value);
                break;
            case Types.TIMESTAMP:
                queryBuilder.append("DATETIME(");
                queryBuilder.append(value.toString());
                queryBuilder.append(")");
                break;
            default:
                throw new IllegalArgumentException("Index type " + type + " from sql.Types is not yet supported.");
        }
        return queryBuilder.toString();
    }
}
