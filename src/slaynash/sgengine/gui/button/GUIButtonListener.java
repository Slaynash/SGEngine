package slaynash.sgengine.gui.button;

import java.util.EventListener;

public interface GUIButtonListener extends EventListener{
	void mouseEntered(GUIButtonEvent e);
	void mouseExited(GUIButtonEvent e);
	void mousePressed(GUIButtonEvent e);
	void mouseReleased(GUIButtonEvent e);
}
