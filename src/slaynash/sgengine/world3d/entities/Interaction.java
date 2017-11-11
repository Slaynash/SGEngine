package slaynash.sgengine.world3d.entities;

import slaynash.sgengine.interactions.InteractionsManager;

public class Interaction {
	
	private String name;
	private String targetName;
	private String targetAction;
	private String[] args;

	public Interaction(String name, String targetName, String targetAction, String... args) {
		this.name = name;
		this.targetName = targetName;
		this.targetAction = targetAction;
		this.args = args;
	}
	
	public void trigger() {
		InteractionsManager.trigger(this);
	}

	public String getName() {
		return name;
	}

	public String getTargetName() {
		return targetName;
	}

	public String getTargetAction() {
		return targetAction;
	}

	public String[] getArgs() {
		return args;
	}
	
	@Override
	public String toString() {
		if(args != null) {
			String r = name+": "+targetName+" > "+targetAction+"(";
			for(int i=0;i<args.length;i++) {
				r+=args[i];
				if(i+1 < args.length) r+=",";
			}
			return r;
		}
		else {
			String r = name+": "+targetName+" > "+targetAction;
			return r;
		}
	}
	
}
