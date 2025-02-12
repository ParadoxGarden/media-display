package com.media.model.providers;

import com.media.MediaConfig;
import com.media.Service;
import java.util.Objects;
import net.runelite.api.Client;

public class Mpv extends Win32Provider
{
	private String lastMedia = "";
	private String lastDisplay = "";
	private String curMedia = "";

	public Mpv(MediaConfig config, Client client)
	{
		super(config, client);
		service = Service.MPV;
		serviceName = "mpv";
	}

	@Override
	public String getMediaFromProvider()
	{
		String toDisplay = "";
		try{

			curMedia = activeAudioWindow.getTitle();
		}catch(Exception e){
			return toDisplay;
		}
		curMedia = curMedia.substring(0,curMedia.length() - 6);
		if (!Objects.equals(lastMedia, curMedia))
		{
			toDisplay = curMedia;

		}

		if (toDisplay.isEmpty())
		{
			toDisplay = lastDisplay;
		}
		if (config.artistSplit() && !toDisplay.contains("\n"))
		{
			toDisplay = toDisplay.replaceFirst(" - ", " -\n");
		}
		lastDisplay = toDisplay;
		return toDisplay;
	}


	@Override
	public void findAudio()
	{
		findActiveAudioSession();
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
