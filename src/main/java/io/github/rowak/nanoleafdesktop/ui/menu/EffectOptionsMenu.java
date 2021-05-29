package io.github.rowak.nanoleafdesktop.ui.menu;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import io.github.rowak.nanoleafapi.Aurora;
import io.github.rowak.nanoleafapi.NanoleafException;
import io.github.rowak.nanoleafapi.NanoleafGroup;
import io.github.rowak.nanoleafdesktop.tools.BasicEffects;
import io.github.rowak.nanoleafdesktop.ui.dialog.OptionDialog;
import io.github.rowak.nanoleafdesktop.ui.dialog.SingleEntryDialog;
import io.github.rowak.nanoleafdesktop.ui.panel.EffectsPanel;

public class EffectOptionsMenu extends ModernPopupMenu {
	
	private boolean basicEffectsMode;
	private String effect;
	private EffectsPanel effectsPanel;
	private NanoleafGroup group;
	private Component parent;
	
	public EffectOptionsMenu(EffectsPanel effectsPanel, String label,
			NanoleafGroup group, Component parent) {
		this.effectsPanel = effectsPanel;
		this.group = group;
		this.parent = parent;
		effect = getEffect();
		initUI();
		
		if (label.equals("Basic Effects")) {
			basicEffectsMode = true;
		}
	}
	
	private void initUI() {
		//ModernMenuItem itemEdit = new ModernMenuItem("Edit");
		ModernMenuItem itemRename = new ModernMenuItem("Rename");
		itemRename.addActionListener((e) -> {
			showRenameDialog();
		});
		ModernMenuItem itemDelete = new ModernMenuItem("Delete");
		itemDelete.addActionListener((e) -> {
			showDeleteDialog();
		});
		
		add(itemRename);
		add(itemDelete);
		
		Point menuLoc = getMenuLocation();
		show(parent, menuLoc.x, menuLoc.y);
	}
	
	private String getEffect() {
		effectsPanel.getList().setSelectedIndex(getEffectIndex());
		return effectsPanel.getList().getSelectedValue();
	}
	
	private int getEffectIndex() {
		return effectsPanel.getList().locationToIndex(getEffectLocation());
	}
	
	private Point getMenuLocation() {
		Point menuPoint = MouseInfo.getPointerInfo().getLocation();
		JScrollPane scrollPane = (JScrollPane)effectsPanel.getList().getParent().getParent();
		SwingUtilities.convertPointFromScreen(menuPoint, scrollPane);
		int listLocation = effectsPanel.getY();
		return new Point(menuPoint.x, menuPoint.y + listLocation);
	}
	
	private Point getEffectLocation() {
		Point menuPoint = getMenuLocation();
		JScrollPane scrollPane = (JScrollPane)effectsPanel.getList().getParent().getParent();
		int cellHeight = effectsPanel.getList().getCellBounds(0, 0).height;
		int verticalScroll = scrollPane.getVerticalScrollBar().getValue();
		int listLocation = effectsPanel.getY();
		Point listPoint = new Point(menuPoint.x, menuPoint.y-cellHeight+
				verticalScroll-listLocation);
		return listPoint;
	}
	
	private void renameEffectOnDevice(String newName) {
		try {
			group.renameEffect(effect, newName);
		}
		catch (NanoleafException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void showRenameDialog() {
		SingleEntryDialog renameDialog = new SingleEntryDialog(
				parent, effect, "Ok",
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						SingleEntryDialog dialog =
								(SingleEntryDialog)((JButton)e.getSource())
								.getTopLevelAncestor();
						String newName = dialog.getEntryField().getText();
						if (basicEffectsMode) {
							BasicEffects.renameBasicEffect(effect, newName);
						}
						else {
							renameEffectOnDevice(newName);
						}
						int i = effectsPanel.getModel().indexOf(effect);
						effectsPanel.getModel().setElementAt(newName, i);
						dialog.dispose();
					}
				});
		renameDialog.setVisible(true);
	}
	
	private void deleteEffectFromDevice() {
		try {
			group.deleteEffect(effect);
		}
		catch (NanoleafException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void showDeleteDialog() {
		String target = " from your device?";
		if (basicEffectsMode) {
			target = " from this computer?";
		}
		OptionDialog deleteDialog = new OptionDialog(parent,
				"Are you sure you want to delete " + effect + target,
				"Yes", "No", new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (basicEffectsMode) {
							BasicEffects.removeBasicEffect(effect);
						}
						else {
							deleteEffectFromDevice();
						}
						effectsPanel.removeEffect(effect);
						OptionDialog dialog = (OptionDialog)((JButton)e.getSource())
								.getTopLevelAncestor();
						dialog.dispose();
					}
				},
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						OptionDialog dialog = (OptionDialog)((JButton)e.getSource())
								.getTopLevelAncestor();
						dialog.dispose();
					}
				});
		deleteDialog.setVisible(true);
	}
}
