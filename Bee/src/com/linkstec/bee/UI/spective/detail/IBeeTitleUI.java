package com.linkstec.bee.UI.spective.detail;

import java.io.Serializable;
import java.util.List;

public interface IBeeTitleUI extends Serializable {

	public String getTitleLabel();

	public void setTitleLabel(String title);

	public void setTitleWithOutListenerAction(String title);

	public void addTitleChangeListener(TitleChangeListener listener);

	public List<TitleChangeListener> getTitleChangeListeners();

	public interface TitleChangeListener extends Serializable {

		public void change(IBeeTitleUI comp);

		public void setError(boolean error);

		public void setAlert(boolean alert);
	}
}
