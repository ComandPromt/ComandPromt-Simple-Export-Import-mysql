package mySQLBackup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Scanner;

import com.smattme.MysqlExportService;
import com.smattme.MysqlImportService;

public class Main {

	static File myObj;

	static Scanner myReader;

	static LinkedList<String> lista;

	static String ssl;

	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {

		try {

			leerConfig();

			if (args.length == 4) {

				String modo = args[1].trim();

				switch (modo) {

				case "1":

					exportDB(args);

					break;

				case "2":

					importDB(args);

					break;

				}

			}

			else {

				help();

			}
		} catch (Exception e) {

		}
	}

	public static void leerConfig() throws FileNotFoundException {

		myObj = new File("Config.txt");

		myReader = new Scanner(myObj);

		lista = new LinkedList<String>();

		String dato;

		while (myReader.hasNextLine()) {

			dato = myReader.nextLine().trim();

			if (!dato.isEmpty()) {

				dato = dato.substring(dato.indexOf("--> ") + 4, dato.length());

				if (!dato.contains("-->") && !dato.isEmpty()) {

					lista.add(dato);

				}

			}

		}

		myReader.close();

		ssl = "false";

		if (lista.get(5).equals("true") || Integer.parseInt(lista.get(5)) == 1) {

			ssl = "true";

		}

	}

	public static void help() {

		System.out.println("\n--------------------------------------------------");

		System.out.println("\n Use -m or --mode");

		System.out.println("\n  Mode 1 --> export  ex: --mode 1");

		System.out.println("\n  Mode 2 --> import  ex: --mode 2");

		System.out.println("\n Use -o or --output");

		System.out.println("\n  ex: -output /home/user/test.sql");

		System.out.println("\n--------------------------------------------------");

	}

	private static void importDB(String[] args) {
		try {

			String sql = new String(Files.readAllBytes(Paths.get(args[3])));

			MysqlImportService.builder()

					.setDatabase("test").setSqlString(sql)
					.setJdbcConnString("jdbc:mysql://" + lista.get(0) + ":" + lista.get(4) + "/" + lista.get(1)
							+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL="
							+ ssl)
					.setUsername(lista.get(2)).setPassword(lista.get(3)).setDeleteExisting(true).setDropExisting(true)
					.importDatabase();

		} catch (Exception e) {

		}
	}

	public static void exportDB(String[] args) {

		try {

			Properties properties = new Properties();

			properties.setProperty(MysqlExportService.DB_NAME, lista.get(1));

			properties.setProperty(MysqlExportService.DB_USERNAME, lista.get(2));

			properties.setProperty(MysqlExportService.DB_PASSWORD, lista.get(3));

			properties.setProperty(MysqlExportService.TEMP_DIR, new File(args[3]).getPath());

			boolean zip = false;

			if (args[3].endsWith(".sql")) {

				properties.setProperty(MysqlExportService.PRESERVE_GENERATED_SQL_FILE, "true");

			}

			else {

				zip = true;

				properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");

			}

			properties.setProperty(MysqlExportService.ADD_IF_NOT_EXISTS, "true");

			properties.setProperty(MysqlExportService.JDBC_DRIVER_NAME, "com.mysql.cj.jdbc.Driver");

			properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, "jdbc:mysql://" + lista.get(0) + ":"
					+ lista.get(4) + "/" + lista.get(1)
					+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL="
					+ ssl);

			if (lista.get(6).equals("true") || Integer.parseInt(lista.get(5)) == 1) {

				properties.setProperty(MysqlExportService.EMAIL_HOST, lista.get(7));

				properties.setProperty(MysqlExportService.EMAIL_PORT, lista.get(8));

				properties.setProperty(MysqlExportService.EMAIL_USERNAME, lista.get(9));

				properties.setProperty(MysqlExportService.EMAIL_PASSWORD, lista.get(10));

				properties.setProperty(MysqlExportService.EMAIL_FROM, lista.get(11));

				properties.setProperty(MysqlExportService.EMAIL_TO, lista.get(12));

			}

			MysqlExportService mysqlExportService = new MysqlExportService(properties);

			if (zip) {

				mysqlExportService.getGeneratedZipFile();

			}

			else {

				mysqlExportService.getGeneratedSql();

			}

			mysqlExportService.export();

		}

		catch (Exception e) {

		}

	}

}
