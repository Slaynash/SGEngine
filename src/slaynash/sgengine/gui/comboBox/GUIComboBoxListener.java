package slaynash.sgengine.gui.comboBox;

import java.util.EventListener;

public interface GUIComboBoxListener<T> extends EventListener{
	void selectedObjectChanged(GUIComboBoxEvent<T> e);
}
