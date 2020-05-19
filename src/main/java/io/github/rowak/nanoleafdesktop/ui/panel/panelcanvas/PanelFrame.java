package io.github.rowak.nanoleafdesktop.ui.panel.panelcanvas;

import io.github.rowak.nanoleafapi.Frame;
import io.github.rowak.nanoleafapi.Panel;

public class PanelFrame
{
	private Panel panel;
	private Frame frame;
	
	public PanelFrame(Panel panel, Frame frame)
	{
		this.panel = panel;
		this.frame = frame;
	}
	
	public Panel getPanel()
	{
		return panel;
	}
	
	public Frame getFrame()
	{
		return frame;
	}
}
