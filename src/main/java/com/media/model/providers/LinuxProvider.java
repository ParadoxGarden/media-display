package com.media.model.providers;

import com.media.MediaConfig;
import net.runelite.api.Client;

public abstract class LinuxProvider extends AudioProvider
{
	LinuxProvider(MediaConfig config, Client client)
	{
		super(config, client);
	}
}
