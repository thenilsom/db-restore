package br.com.autocom.restoreDb.util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import br.com.autocom.restoreDb.view.RedirectedFrame;

public final class Util {

	private Util() {}
	
	public static String selecionarPasta(String title) {
		visualWindows();
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle(title);

		int returnValue = jfc.showOpenDialog(null);
		// int returnValue = jfc.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			return selectedFile.getAbsolutePath();
		}
		
		return "";
	}
	
	/**
	 * Abre o frame para capturar um System.out e exibir em um frame
	 */
	public static void abrirCapturaLogConsole() {
		RedirectedFrame outputFrame =
   		     new RedirectedFrame(false, false, null, 700, 600, JFrame.DO_NOTHING_ON_CLOSE);
   		outputFrame.setVisible(true);
	}
	
	 private static void visualWindows() {
		 try {
			 String seta_look = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			 UIManager.setLookAndFeel(seta_look);
		} catch (Exception e) {
			e.printStackTrace();
		}

	      
	    }

}
