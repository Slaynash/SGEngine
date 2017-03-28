package slaynash.opengl.render2d.button;

import java.util.EventListener;

public interface GUIButtonListener extends EventListener{
	void mouseEntered(GUIButtonEvent e);
	void mouseExited(GUIButtonEvent e);
	void mousePressed(GUIButtonEvent e);
	void mouseReleased(GUIButtonEvent e);
}
