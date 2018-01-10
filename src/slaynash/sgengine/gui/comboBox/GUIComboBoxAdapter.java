package slaynash.sgengine.gui.comboBox;

public abstract class GUIComboBoxAdapter<T> implements GUIComboBoxListener<T>{
	@Override
	public void selectedObjectChanged(GUIComboBoxEvent<T> e) {}
}
