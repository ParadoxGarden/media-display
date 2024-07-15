package com.media.model.providers;

import com.media.MediaConfig;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;

public class Game extends AudioProvider
{
	private static final int CURRENTLY_PLAYING_WIDGET_ID = 9;
	private Widget curTrackWidget;
	public Game(MediaConfig config, Client client)
	{
		super(config, client);
	}

	@Override
	public String getMediaFromProvider()
	{
		return curTrackWidget.getText();
	}

	@Override
	public void findAudio()
	{
		curTrackWidget = client.getWidget(WidgetID.MUSIC_GROUP_ID, CURRENTLY_PLAYING_WIDGET_ID);
	}

	@Override
	public boolean isPlaying()
	{
		return true;
	}

	@Override
	public boolean isStopped()
	{
		return client.getMusicVolume() == 0;
	}
}
