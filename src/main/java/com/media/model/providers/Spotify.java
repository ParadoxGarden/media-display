package com.media.model.providers;

import com.media.MediaConfig;
import com.media.Service;
import java.util.Objects;
import net.runelite.api.Client;

public class Spotify extends Win32Provider
{
	private String lastMedia = "";
	private String lastDisplay = "";
	private String curMedia = "";

	public Spotify(MediaConfig config, Client client)
	{
		super(config, client);
		service = Service.SPOTIFY;
		serviceName = "Spotify";

	}


	@Override
	public String getMediaFromProvider()
	{
		String toDisplay = "";
		boolean isIdle = !isPlaying();
		boolean noIcon = !(lastDisplay.contains(pauseIcon) || lastDisplay.contains(playIcon));
		if (config.doStatusIcon())
		{

			if (!Objects.equals(lastMedia, curMedia) || noIcon)
			{
				if (isIdle)
				{
					if (!lastDisplay.contains(pauseIcon))
					{
						toDisplay = pauseIcon + lastMedia;
					}
				}
				else
				{

					toDisplay = playIcon + curMedia;


				}
			}
		}
		else
		{
			if (!isIdle)
			{
				toDisplay = curMedia;
			}


		}
		lastMedia = curMedia;

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
		if (activeAudioSession != null)
		{
			curMedia = activeAudioWindow.getTitle();

			return !curMedia.contains(serviceName);
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isStopped()
	{
		return activeAudioWindow == null;
	}
}
