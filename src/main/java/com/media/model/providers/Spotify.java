package com.media.model.providers;

import com.media.MediaConfig;
import com.media.Service;
import com.media.model.win32.AudioSes;
import com.media.model.win32.MMDevAPI;
import com.sun.jna.platform.DesktopWindow;
import java.util.Objects;
import net.runelite.api.Client;

public class Spotify extends Win32Provider
{
	private final MMDevAPI.MMDevice defaultDevice;
	private final String spotify = "Spotify";
	private AudioSes.AudioSessionControl2 activeAudioSession;
	private DesktopWindow activeAudioWindow;
	private String lastMedia = "";
	private String lastDisplay = "";
	private String curMedia = "";

	public Spotify(MediaConfig config, Client client)
	{
		super(config, client);
		service = Service.SPOTIFY;
		defaultDevice = getDefaultDevice();
	}

	private void findActiveAudioSession()
	{
		AudioSes.AudioSessionManager2 sessionManager = defaultDevice.createManager();
		AudioSes.AudioSessionEnumerator sessionEnumerator = sessionManager.getSessionEnumerator();
		int count = sessionEnumerator.getCount();
		for (int i = 0; i < count; i++)
		{
			AudioSes.AudioSessionControl2 ses = sessionEnumerator.getSession(i);
			if (ses.getState() == AudioSes.AudioSessionStateActive)
			{
				if (ses.getName().contains(spotify))
				{
					activeAudioSession = ses;
					activeAudioWindow = getWindowByPID(ses.getProcessID());
					return;
				}

			}
		}
		activeAudioSession = null;
		activeAudioWindow = null;

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
		if (config.artistSplit())
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
			return !curMedia.contains(spotify);
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
