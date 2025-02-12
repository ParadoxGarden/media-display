package com.media;

import com.google.inject.Provides;
import com.media.model.providers.MediaProvider;
import com.media.model.providers.Game;
import com.media.model.providers.Mpv;
import com.media.model.providers.Spotify;
import java.util.concurrent.Semaphore;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;


@Slf4j
@PluginDescriptor(name = "Media Display")
public class MediaPlugin extends Plugin
{

	@Getter
	private static MediaPlugin instance;
	private final Semaphore semaphore = new Semaphore(1);
	@Setter
	public MediaProvider audio;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private MediaConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private MediaOverlay overlay;
	@Getter
	private String mediaString = "";

	@Provides
	MediaConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MediaConfig.class);
	}

	private void updateMedia()
	{
		audio.findAudio();
		if (!audio.isStopped())
		{
			mediaString = audio.getMediaFromProvider();
			if (overlayManager.getWidgetItems().contains(overlay)){
				return;
			}else {
				overlayManager.add(overlay);
			}
		}
		else
		{
			overlayManager.remove(overlay);
		}
		semaphore.release();
	}

	@Override
	protected void startUp() throws Exception
	{
		this.overlay = new MediaOverlay(this, config);
		audio = getProvider(config.audioService());
	}

	/**
	 *
	 * @param service one of the supported services
	 * @return class that will do the work of finding media strings
	 */
	public MediaProvider getProvider(Service service) {
		switch (service){
			case SPOTIFY:
				return new Spotify(config, client);
			case MPV:
				return new Mpv(config, client);
			case GAME:
			default:
				return new Game(config, client);
		}
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	/**
	 * updates the instance stored config
	 * ensures that the media info provider is kept up to date
	 * @param configChanged config sent from the game client
	 */
	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		Service curService = this.config.audioService();
		if (audio.getService() != curService){
			audio = getProvider(curService);
		}


	}

	/**
	 * once a game tick see if we can check media for updates
	 */
	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (!semaphore.tryAcquire())
		{
			Thread t = new Thread(this::updateMedia);
			t.start();
		}
	}


}
