package de.mbaaba.tool.pw.gui;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import de.mbaaba.util.ConfigManager;

public class TrayItemView {
	private Image iconGreen;
	private Image iconYellow;
	private Image iconRed;
	private TrayItem trayItem;
	private Display display;
	protected Menu popupMenu;

	private void loadImages() {
		InputStream is = getClass().getClassLoader().getResourceAsStream("images/green.ico");
		iconGreen = new Image(display, is);
		is = getClass().getClassLoader().getResourceAsStream("images/red.ico");
		iconRed = new Image(display, is);
		is = getClass().getClassLoader().getResourceAsStream("images/yellow.ico");
		iconYellow = new Image(display, is);
	}

	public TrayItemView(Display aDisplay) {

		display = aDisplay;
		loadImages();
		final Tray tray = display.getSystemTray();
		if (tray == null) {
			System.out.println("The system tray is not available");
		} else {
			trayItem = new TrayItem(tray, SWT.NONE);
			trayItem.addListener(SWT.Show, new Listener() {
				public void handleEvent(Event event) {
				}
			});
			trayItem.addListener(SWT.Hide, new Listener() {
				public void handleEvent(Event event) {
				}
			});
			trayItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					boolean visibility = ConfigManager.getInstance().getProperty(ConfigManager.CFG_SHOW_DISPLAY, false);
					visibility = !visibility;
					ConfigManager.getInstance().setProperty(ConfigManager.CFG_SHOW_DISPLAY, visibility);
					
				}
			});
			trayItem.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(Event event) {
				}
			});

			trayItem.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					if (popupMenu != null) {
						popupMenu.setVisible(true);
					}
				}
			});
			setTrayImage(iconRed);
		}
	}

	public void setColorScheme(final int aColor) {
		if (display != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					if (aColor == SWT.COLOR_RED) {
						setTrayImage(iconRed);
					} else if (aColor == SWT.COLOR_YELLOW) {
						setTrayImage(iconYellow);
					} else {
						setTrayImage(iconGreen);
					}

				}
			});
		}
	}

	protected void setTrayImage(Image aIcon) {
		if (trayItem.getImage() != aIcon) {
			trayItem.setImage(aIcon);
		}
	}

	public void setPopupMenu(Menu aPopupMenu) {
		popupMenu = aPopupMenu;
	}

	public void setToolTipText(String aMsg) {
		trayItem.setToolTipText(aMsg);

	}

}
