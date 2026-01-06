package com.media.model.providers;

import com.media.MediaConfig;
import net.runelite.api.Client;

public class MPRIS2 extends MediaProvider
{
	public MPRIS2(MediaConfig config, Client client)
	{
		super(config, client);
	}


	@Override
	public String getMediaFromProvider()
	{
		ProcessBuilder proc = new ProcessBuilder();
		String procfmt = "{{ artist }} - {{ title }}";
		if (config.artistSplit())
		{
			procfmt = "{{ artist }} -\n{{ title }}";
		}
		if (config.doStatusIcon())
		{
			if(isPlaying())
			{
				procfmt = playIcon + procfmt;
			}
			else
			{
				procfmt = pauseIcon + procfmt;
			}
		}
		proc.command("playerctl", "metadata", "--format", procfmt);
		String result = "";
		try
		{
			Process process = proc.start();
			result = new String(process.getInputStream().readAllBytes());
			process.waitFor();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
		
	}

	@Override
	public void findAudio()
	{
	// stub function, not needed for playerctl
	}

	@Override
	public boolean isPlaying()
	{
		ProcessBuilder proc = new ProcessBuilder();
		proc.command("playerctl", "status");
		String result = "";
		try
		{
			Process process = proc.start();
			result = new String(process.getInputStream().readAllBytes());
			process.waitFor();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result.contains("Playing");
	}

	@Override
	public boolean isStopped()
	{
		return !isPlaying();
	}

}

