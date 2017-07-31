package slaynash.sgengine.gui.textField;

import java.util.EventListener;

public interface GUITextFieldListener extends EventListener{
	void textChanged(GUITextFieldEvent e);
	void focusAcquired(GUITextFieldEvent e);
	void focusLost(GUITextFieldEvent e);
}
