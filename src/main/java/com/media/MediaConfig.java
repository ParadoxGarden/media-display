package com.media;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("mediaDisplay")
public interface MediaConfig extends Config
{
	@ConfigItem(
		keyName = "audioService",
		name = "Audio Service",
		description = "Audio service you want displayed"
	)
	default Service audioService()
	{
		return Service.SPOTIFY;

	}

	@ConfigItem(
		keyName = "doStatusIcon",
		name = "Pause / Play icon",
		description = "Should we show if the music is paused or playing"
	)
	default boolean doStatusIcon()
	{
		return true;

	}

	@ConfigItem(
		keyName = "artistSplit",
		name = "Split artist name",
		description = "Should we put the artist on a different line"
	)
	default boolean artistSplit()
	{
		return true;

	}




}

