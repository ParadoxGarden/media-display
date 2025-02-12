package com.media.model.providers;

import com.media.MediaConfig;
import net.runelite.api.Client;

public abstract class Linux extends MediaProvider
{
	Linux(MediaConfig config, Client client)
	{
		super(config, client);
	}
}
