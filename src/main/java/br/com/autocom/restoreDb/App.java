package br.com.autocom.restoreDb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import br.com.autocom.restoreDb.util.Util;

/**
 * Hello world!
 *
 */
public class App 
{
	 // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "org.postgresql.Driver";  
	   static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/postgres";

	   //  Database credentials
	   static final String USER_NAME = "autocom";
	   static final String PASS = "Autocom";
	   static final String DB_NAME = "ac-posto";

	public static void main(String[] args){
		try {
			String senha = JOptionPane.showInputDialog(null, "Informe a senha");//senha é 'auto'
			
			if(!senha.equalsIgnoreCase("auto")) {
				JOptionPane.showMessageDialog(null, "Senha inválida.");
				System.exit(0);
			}
			
			//abre o frame para exibir os log do system.out
			Util.abrirCapturaLogConsole();
			
			//exclui o banco de dados se existir
			if(existeDatabase())
				dropDatabase();
			
			//cria o banco novamente
			createDatabase();
			
			//restaura o backup
			restoreDataBase();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(0);
		}
		
	}
	
	/**
	 * Retorna true se existe o banco de dados
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private static boolean existeDatabase() throws SQLException, ClassNotFoundException {
		  	Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    ResultSet rs = statement.executeQuery("select * from pg_database where datname = " + Util.concatenarAspasSimples(DB_NAME));
		    connection.close();
		    
		    return rs.next();
	}
	
	/**
	 * Cria o banco de dados
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static void createDatabase() throws ClassNotFoundException, SQLException {
			Connection connection = getConnection();
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE DATABASE " + Util.concatenarAspasDuplas(DB_NAME) + " owner autocom");
			connection.close();
			
			 System.out.println("Banco: " + DB_NAME + " criado com sucesso !");
    }
	
	/**
	 * Exclui o banco de dados
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static void dropDatabase() throws ClassNotFoundException, SQLException {
	    Connection connection = getConnection();
	    Statement statement = connection.createStatement();
	    statement.executeUpdate("DROP DATABASE " + Util.concatenarAspasDuplas(DB_NAME));
	    connection.close();
	    
	    System.out.println("Banco: " + DB_NAME + " excluido com sucesso !");
	}
	
	/**
	 * Retorna o statement da connection
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		 	Class.forName(JDBC_DRIVER);
		    return DriverManager.getConnection(DB_URL, "postgres", PASS);
	}
	
	/**
	 * Restaura o banco de dados
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void restoreDataBase() throws IOException, InterruptedException {
		String caminhoBackup = Util.selecionarPasta("Selecione o arquivo de backup");
	           ProcessBuilder pb = new ProcessBuilder( getCaminhoRestore(),
	       		    "--host=localhost",
	       		    "--port=5432",
	       		    "--username=" + USER_NAME,
	       		    "--dbname=" + DB_NAME,
	       		    "--verbose",
	       		    caminhoBackup);
	           
	           pb.environment().put("PGPASSWORD", PASS);
	           
	           final Process processo = pb.start();
	           final BufferedReader r = new BufferedReader(      
	                   new InputStreamReader(processo.getErrorStream()));      
	               String line = r.readLine();      
	              
	               while (line != null) {      
	            	System.out.println(line);
	               line = r.readLine();      
	               }      
	               r.close();      
	               processo.waitFor();    
	               processo.destroy(); 
	               
	               JOptionPane.showMessageDialog(null, "Banco restaurado com sucesso !");
	               
	               System.exit(0);
	}
	
	private static String getCaminhoRestore() {
		return new File("").getAbsolutePath() + "/Backup/bin/pg_restore";
	}
}
