package com.zaitunlabs.zlcore.modules.about;

import java.util.ArrayList;
import java.util.List;

public class SimpleExpandableDataModel {
	public String string;
	  public final List<String> children = new ArrayList<String>();

	  public SimpleExpandableDataModel(String string) {
	    this.string = string;
	  }

}
