package com.media.model.providers;

import com.media.MediaConfig;
import com.media.Service;
import lombok.Getter;
import net.runelite.api.Client;

public abstract class MediaProvider
{
	MediaConfig config;
	@Getter
	Service service;
	Client client;
	MediaProvider(MediaConfig config, Client client){
		this.config = config;
		this.client = client;
	}
	String pauseIcon = "⏸";
	String playIcon = "⏵";

	public abstract String getMediaFromProvider();

	public abstract void findAudio();

	public abstract boolean isPlaying();
	public abstract boolean isStopped();
}
