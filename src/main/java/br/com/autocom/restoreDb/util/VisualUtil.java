package br.com.autocom.restoreDb.util;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe Responsavel Por Modificar o Visual Do Programa.
 *
 * @author Samuel Oliveira
 */
public class VisualUtil {

    public static void look() {
    	try {
    		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    
	 // troca, tab por enter
	    public static void enter_tab(Component comp) {

	        Set<AWTKeyStroke> keystrokes = comp.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
	        Set<AWTKeyStroke> newKeystrokes = new HashSet<AWTKeyStroke>(keystrokes);
	        newKeystrokes.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
	        comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newKeystrokes);
	    }

}
