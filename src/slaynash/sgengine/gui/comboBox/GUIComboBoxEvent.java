package slaynash.sgengine.gui.comboBox;

public class GUIComboBoxEvent<T> {
	
	private T selectedObject;
	
	public GUIComboBoxEvent(T selectedObject) {
		this.selectedObject = selectedObject;
	}
	
	public T getSelected(){
		return selectedObject;
	}

}
