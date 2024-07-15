package com.media.model.providers;

import com.media.MediaConfig;
import net.runelite.api.Client;

public class Firefox extends Win32Provider
{
	Firefox(MediaConfig config, Client client)
	{
		super(config, client);
	}

	@Override
	public String getMediaFromProvider()
	{
		return "";
	}

	@Override
	public void findAudio()
	{

	}

	@Override
	public boolean isPlaying()
	{
		return false;
	}

	@Override
	public boolean isStopped()
	{
		return false;
	}
}
