package br.com.autocom.restoreDb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import br.com.autocom.restoreDb.util.Util;
import br.com.autocom.restoreDb.util.VisualUtil;

/**
 * Hello world!
 *
 */
public class App 
{
	 // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "org.postgresql.Driver";  
	   static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/postgres";
	   static final String DB_URL_AC_POSTO = "jdbc:postgresql://127.0.0.1:5432/ac-posto";

	   //  Database credentials
	   static final String USER_NAME = "autocom";
	   static String PASS = "Autocom";
	   static final String DB_NAME = "ac-posto";

	public static void main(String[] args){
		try {
			VisualUtil.look();
			
			requererAcesso();
			
			testarConection();
			
			//abre o frame para exibir os log do system.out
			Util.abrirCapturaLogConsole();
			
			//cria o usuario autocom se já não existir;
			createUser();
			
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
	 * Requere o acesso
	 */
	private static void requererAcesso() {
		// Cria campo onde o usuario entra com a senha
					JPasswordField password = new JPasswordField(10);
					password.setEchoChar('*'); 
					VisualUtil.enter_tab(password);
					// Cria um rótulo para o campo
					JLabel rotulo = new JLabel("Entre com a senha:");
					// Coloca o rótulo e a caixa de entrada numa JPanel:
					JPanel entUsuario = new JPanel();
					entUsuario.add(rotulo);
					entUsuario.add(password);
					
					boolean acertouSenha = false;
					
					do {
						if(JOptionPane.showOptionDialog(null, entUsuario, "Acesso restrito", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Logar", "Cancelar"},  password)!= JOptionPane.YES_OPTION){
							System.exit(0);
					       }else{
					    	   if(String.valueOf(password.getPassword()).equalsIgnoreCase("auto")) {
					    		   acertouSenha = true;
					    	   }else {
					    		   JOptionPane.showMessageDialog(null, "Senha inválida");
					    		   password.setText("");
					    	   }
					       }
					} while (!acertouSenha);
	}
	
	/**
	 * Faz um teste de conecção
	 */
	private static void testarConection() {
		try {
			getConnection();
		} catch (Exception e) {
			JPasswordField password = new JPasswordField(10);
			password.setEchoChar('*');
			VisualUtil.enter_tab(password);
			JLabel rotulo = new JLabel("Informe a senha do banco de dados:");
			JPanel entUsuario = new JPanel();
			entUsuario.add(rotulo);
			entUsuario.add(password);
			if (JOptionPane.showOptionDialog(null, entUsuario, "Senha Banco", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, new String[] { "OK", "Cancelar" },
					password) != JOptionPane.YES_OPTION) {
				System.exit(0);
			} else {
				PASS = String.valueOf(password.getPassword());
			}
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
	 * Cria o usuario do banco de dados
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static void createUser(){
		try {
			Connection connection = getConnection();
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE USER autocom with password 'Autocom'");
			connection.close();
			
			System.out.println("Usuario autocom criado com sucesso !");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	/*
	 * Retorna a connection com o banco ac-posto
	 */
	private static Connection getConnectionAcPosto() throws ClassNotFoundException, SQLException {
		Class.forName(JDBC_DRIVER);
	    return DriverManager.getConnection(DB_URL_AC_POSTO, "autocom", "Autocom");
	}
	
	/**
	 * Restaura o banco de dados
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	private static void restoreDataBase() throws IOException, InterruptedException, ClassNotFoundException, SQLException {
		String caminhoBackup = Util.selecionarPasta("Selecione o arquivo de backup");
	           ProcessBuilder pb = new ProcessBuilder( getCaminhoRestore(),
	       		    "--host=localhost",
	       		    "--port=5432",
	       		    "--username=" + USER_NAME,
	       		    "--dbname=" + DB_NAME,
	       		    "--verbose",
	       		    caminhoBackup);
	           
	           pb.environment().put("PGPASSWORD", "Autocom");
	           
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
	               
	              alterarAmbienteNFE();
	}
	
	/**
	 * Altera o ambente da NF-e para 2-Homologação
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private static void alterarAmbienteNFE() throws SQLException, ClassNotFoundException {
	  	Connection connection = getConnectionAcPosto();
	  	PreparedStatement stmt = connection.prepareStatement("UPDATE " + Util.concatenarAspasDuplas("configuracao_nfe") + " set ambiente = 2");
	  	stmt.executeUpdate();
	  	connection.close();
	   
	   JOptionPane.showMessageDialog(null, "Banco restaurado com sucesso ! (AMBIENTE ALTERADO PARA 2-HOMOLOGAÇÃO)");
       System.exit(0);
}
	
	private static String getCaminhoRestore() {
		return new File("").getAbsolutePath() + "/Backup/bin/pg_restore";
	}
}
