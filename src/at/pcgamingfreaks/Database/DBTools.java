/*
 *   Copyright (C) 2016, 2018 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Database;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class DBTools
{
	private static final Pattern CURRENT_TABLE_INFO = Pattern.compile("^\\w*(CREATE TABLE IF NOT EXISTS|CREATE TABLE)\\s+(`(\\w+)`|\\w+)\\s+\\(\\n([\\s\\S]*)\\n\\)(\\s+ENGINE=\\w+)?;?$", Pattern.CASE_INSENSITIVE);
	private static final Pattern COLUMN_NAME_EXTRACTOR_PATTERN = Pattern.compile("^(`(\\w+)`|\\w+) (.*)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern COLUMN_CONSTRAINT_CHECKER_PATTERN = Pattern.compile("^(CONSTRAINT\\s*(`(\\w*)`|\\w*)\\s+)?(PRIMARY KEY|UNIQUE KEY|UNIQUE INDEX|FOREIGN KEY)\\s+(.*)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern COLUMN_KEY_CHECKER_PATTERN = Pattern.compile("^(INDEX|KEY)\\s+(`(\\w*)`|(\\w*))?\\s?\\(((`\\w*`|\\w*)(,\\s*(`\\w*`|\\w*))*)\\)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern COLUMN_TYPE_EXTRACTOR_PATTERN = Pattern.compile("^(\\w*(\\(\\d+(,\\d+)?\\))?)\\s+(.*)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern UNIQUE_INDEX_PATTERN = Pattern.compile("^(`(\\w+)`|\\w+)?\\s*\\((.*)\\)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern FOREIGN_KEY_PATTERN = Pattern.compile("^(`(\\w+)`|\\w+)?\\s*\\(([^)]*)\\)\\s+REFERENCES\\s+(`\\w+`|\\w+)\\s+\\(([^)]*)\\)\\s*(ON DELETE (RESTRICT|CASCADE|SET NULL|NO ACTION))?\\s*(ON UPDATE (RESTRICT|CASCADE|SET NULL|NO ACTION))?$", Pattern.CASE_INSENSITIVE);

	/**
	 * Updates the database so that the given table exists and matches the schema after using this function
	 * <b>Important:</b> Currently only tested and optimised for MySQL! No warranty that it works with SQL databases other than MySQL.
	 *
	 * @param connection The JDBC database connection
	 * @param tableDefinition A MySQL create query using the following style guidelines:
	 *                        1.) One create query per function call
	 *                        2.) Using correct basic create query syntax (syntax: CREATE TABLE [IF NOT EXISTS] tbl_name (\n create_definition,... \n);)
	 *                        3.) A create_definition can contain following:
	 *                        3.1) col_name column_definition
	 *                        3.2) [CONSTRAINT [symbol]] PRIMARY KEY (index_col_name,...)
	 *                        3.3) [CONSTRAINT [symbol]] UNIQUE [INDEX|KEY] [index_name] (index_col_name,...)
	 *                        3.4) [CONSTRAINT [symbol]] FOREIGN KEY [index_name] (index_col_name,...) reference_definition
	 *                        3.4.1) A reference_definition contains: REFERENCES tbl_name (index_col_name,...) [MATCH FULL|MATCH PARTIAL|MATCH SIMPLE] [ON DELETE reference_option] [ON UPDATE reference_option]
	 *                        3.4.2) A reference_option is one of the following options: RESTRICT, CASCADE, SET NULL, NO ACTION
	 *                        3.5) {INDEX|KEY} [index_name] (index_col_name,...)
	 *                        4.) Write PRIMARY KEY and others like CONSTRAINT in a new line, don't add it to the definition of the column (for example "`column` INT, PRIMARY KEY(`column`)" instead of "`column` INT PRIMARY KEY")
	 *                        5.) Write a create query with every (column) definition in a new line (using \n)
	 * @throws IllegalArgumentException If the create query is not in the right format
	 * @throws SQLException If any handling with the database failed
	 */
	@Deprecated
	@SuppressWarnings("SqlNoDataSourceInspection")
	public static void updateDB(@NotNull Connection connection, @NotNull @Language("SQL") String tableDefinition) throws IllegalArgumentException, SQLException
	{
		new SQLTableValidatorMySQL().validate(connection, tableDefinition);
	}

	/**
	 * Creates an {@link PreparedStatement} form the {@link Connection}, fills it with the data given and executes it.
	 * This method is not async! And the connection is not closed after it is done!
	 *
	 * @param connection The connection used for the query.
	 * @param query The query to execute.
	 * @param args The arguments used for the query.
	 * @throws SQLException If there was a problem executing the SQL statement
	 */
	public static void runStatement(final @NotNull Connection connection, final @NotNull @Language("SQL") String query, final @Nullable Object... args) throws SQLException
	{
		try(PreparedStatement preparedStatement = connection.prepareStatement(query))
		{
			setParameters(preparedStatement, args);
			preparedStatement.execute();
		}
	}

	/**
	 * Creates an {@link PreparedStatement} form the {@link Connection}, fills it with the data given and executes it.
	 * This method is not async! And the connection is not closed after it is done!
	 *
	 * @param connection The connection used for the query.
	 * @param query The query to execute.
	 * @param args The arguments used for the query.
	 */
	public static void runStatementWithoutException(final @NotNull Connection connection, final @NotNull @Language("SQL") String query, final @Nullable Object... args)
	{
		try
		{
			runStatement(connection, query, args);
		}
		catch(SQLException e)
		{
			System.out.println("\nQuery: " + query + "\n" + "Data: " + Arrays.toString(args)); //TODO remove debug output
			e.printStackTrace();
		}
	}

	/**
	 * Method to batch set parameters of a prepared statement.
	 * Starts with 1.
	 *
	 * @param preparedStatement The prepared statement to set the parameters.
	 * @param args The values to be set.
	 * @throws SQLException If there was a problem.
	 */
	public static void setParameters(@NotNull PreparedStatement preparedStatement, @Nullable Object... args) throws SQLException
	{
		for(int i = 0; args != null && i < args.length; i++)
		{
			preparedStatement.setObject(i + 1, args[i]);
		}
	}
}