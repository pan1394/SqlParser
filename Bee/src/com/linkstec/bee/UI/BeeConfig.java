package com.linkstec.bee.UI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JOptionPane;

import com.linkstec.bee.UI.config.BeeProjectRootDialog;
import com.linkstec.bee.UI.config.Configuration;

public class BeeConfig {

	private Configuration config;

	public BeeConfig() {
		this.readConfig();

	}

	public Configuration getConfig() {
		return this.config;
	}

	public void readConfig() {
		try {
			String root = System.getProperty("user.home");
			String path = root + File.separator + ".bee";
			File f = new File(path);
			if (!f.exists()) {
				BeeProjectRootDialog dialog = new BeeProjectRootDialog();
				Configuration config = dialog.getConfig();

				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));

				oos.writeObject(config);
				oos.close();
				this.config = config;
			} else {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
				Object obj = ois.readObject();
				ois.close();
				this.config = (Configuration) obj;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public boolean save() {
		try {
			String userDir = System.getProperty("user.home");
			String path = userDir + File.separator + ".bee";

			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.config);
			oos.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showConfirmDialog(null, "Exption ocurred when try to save config", "Config Save", JOptionPane.OK_OPTION);
			return false;
		}
	}

}
