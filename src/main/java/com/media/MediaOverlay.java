package com.media;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Objects;
import lombok.Setter;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class MediaOverlay extends OverlayPanel
{
	private final MediaConfig config;
	private final MediaPlugin plugin;
	private final String pauseIcon = "⏸";
	private final String playIcon = "⏵";
	private String lastMediaTitle;
	@Setter
	private String lastMedia;
	private String toDisplay;

	@Inject
	public MediaOverlay(MediaPlugin mediaPlugin, MediaConfig mediaConfig)
	{
		super();
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		this.plugin = mediaPlugin;
		this.config = mediaConfig;
		addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Media overlay");
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		toDisplay = plugin.getMediaString();
		int longest = 0;
		if(toDisplay.contains("\n"))
		{
			for (String s : toDisplay.split("\n"))
			{
				panelComponent.getChildren().add(LineComponent.builder().left(s).leftColor(Color.WHITE).build());
				int strLen = graphics.getFontMetrics().stringWidth(s);
				if (strLen > longest)
				{
					longest = strLen;
				}
			}
			panelComponent.setPreferredSize(new Dimension(longest + 10, 0));
		}else{
			panelComponent.getChildren().add(TitleComponent.builder().text(toDisplay).color(Color.WHITE).build());
			panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(toDisplay) + 10, 0));
		}
		return super.render(graphics);
	}
/*
	private String getMediaTitle(String curMedia)
	{
		String mediaTitle = "";
		if (config.audioService() == MediaConfig.Services.SPOTIFY)
		{
			boolean isIdle = curMedia.contains(config.audioService().name);
			if (!Objects.equals(lastMedia, curMedia))
			{
				if (config.doStatusIcon())
				{


					if (isIdle)
					{
						if (!toDisplay.contains(pauseIcon))
						{
							mediaTitle = pauseIcon + lastMedia;
						}
					}
					else
					{

						mediaTitle = playIcon + curMedia;


					}
				}
				else
				{
					if (!isIdle)
					{
						mediaTitle = curMedia;
					}

				}
			}
			lastMedia = curMedia;
		}
		if (mediaTitle.isEmpty())
		{
			return lastMediaTitle;
		}
		if (config.artistSplit())
		{
			mediaTitle = mediaTitle.replaceFirst("-", "-\n");
		}
		lastMediaTitle = mediaTitle;
		return mediaTitle;
	}
*/
}
