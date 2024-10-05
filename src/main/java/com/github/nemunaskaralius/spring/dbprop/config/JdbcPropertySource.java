package com.github.nemunaskaralius.spring.dbprop.config;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.EnumerablePropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.ArrayUtils.EMPTY_STRING_ARRAY;

public class JdbcPropertySource extends EnumerablePropertySource<DataSource> {

    private static final Logger log = LoggerFactory.getLogger(JdbcPropertySource.class);

    private static final String SQL_GET_PROPERTY = "SELECT %s FROM %s WHERE %s = ?";
    private static final String SQL_GET_KEYS = "SELECT DISTINCT %s FROM %s";

    private final DbPropertySourceProperties sourceProperties;

    protected JdbcPropertySource(String name) {
        super(name);
        this.sourceProperties = null;
    }

    public JdbcPropertySource(String name, DataSource source, DbPropertySourceProperties sourceProperties) {
        super(name, source);
        this.sourceProperties = requireNonNull(sourceProperties, "sourceProperties is null");
        sourceProperties.validate();
    }

    @Override
    public Object getProperty(String name) {
        return execute(
                connection -> {
                    var statement = connection.prepareStatement(format(
                            SQL_GET_PROPERTY,
                            sourceProperties.getValueColumn(),
                            sourceProperties.getTableName(),
                            sourceProperties.getKeyColumn()
                    ));
                    statement.setString(1, name);
                    return statement;
                },
                resultSet -> {
                    if (!resultSet.next()) {
                        return null;
                    }
                    return resultSet.getString(1);
                }
        );
    }

    @Override
    public String[] getPropertyNames() {
        //TODO: spring seems to call getPropertyNames() method too many times
        // (multiple times during single reload of single property bean)
        // Implement some short lived caching to avoid unnecessary calls to a DB
        return execute(
                connection ->
                        connection.prepareStatement(format(
                                SQL_GET_KEYS,
                                sourceProperties.getKeyColumn(),
                                sourceProperties.getTableName()
                        )),
                resultSet -> {
                    var resultList = new ArrayList<String>();
                    while (resultSet.next()) {
                        resultList.add(resultSet.getString(1));
                    }
                    return resultList.toArray(EMPTY_STRING_ARRAY);
                }
        );
    }

    private <T> T execute(
            ThrowingFunction<Connection, PreparedStatement> prepareStatement,
            ThrowingFunction<ResultSet, T> processResultSet
    ) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getSource().getConnection();
            statement = prepareStatement.apply(connection);
            resultSet = statement.executeQuery();
            return processResultSet.apply(resultSet);
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        } finally {
            close(connection, statement, resultSet);
        }
    }

    private void close(final Connection conn, final Statement stmt, final ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            log.error("An error occurred while closing the result set", e);
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            log.error("An error occurred while closing the statement", e);
        }

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            log.error("An error occurred while closing the connection", e);
        }
    }

    /**
     * Just like {@link Function} but can throw checked exceptions (e.g. {@link SQLException})
     */
    private interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
