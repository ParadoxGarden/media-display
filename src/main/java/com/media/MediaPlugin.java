package com.media;

import com.google.inject.Provides;
import com.media.model.AudioSes;
import com.media.model.AudioSes.AudioSessionControl2;
import com.media.model.AudioSes.AudioSessionEnumerator;
import com.media.model.AudioSes.AudioSessionManager2;
import com.media.model.MMDevAPI.MMDevice;
import com.media.model.MMDevAPI.MMDeviceEnumerator;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.ptr.IntByReference;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
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

	public AudioSessionControl2 activeAudioSession;
	public DesktopWindow activeAudioWindow;
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
	private MMDevice defaultDevice;

	@Provides
	MediaConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MediaConfig.class);
	}

	private MMDevice getDefaultDevice()
	{
		MMDeviceEnumerator audioDevices = MMDeviceEnumerator.create();
		assert audioDevices != null;
		return audioDevices.getDefaultDevice();
	}

	public void findActiveAudioSes()
	{
		AudioSessionManager2 sessionManager = defaultDevice.createManager();
		AudioSessionEnumerator sessionEnumerator = sessionManager.getSessionEnumerator();
		int count = sessionEnumerator.getCount();
		for (int i = 0; i < count; i++)
		{
			AudioSessionControl2 ses = sessionEnumerator.getSession(i);
			if (ses.getState() == AudioSes.AudioSessionStateActive)
			{
				if (Objects.equals(ses.getName(), config.audioService().name))
				{
					activeAudioSession = ses;
					activeAudioWindow = getWindowByPID(ses.getProcessID());
					return;
				}

			}
		}

	}

	public DesktopWindow getWindowByPID(int pid)
	{
		for (DesktopWindow window : WindowUtils.getAllWindows(false))
		{
			IntByReference winPID = new IntByReference();
			User32.INSTANCE.GetWindowThreadProcessId(window.getHWND(), winPID);
			if (winPID.getValue() == pid)
			{
				return window;
			}

		}
		return null;
	}


	private void updateMedia()
	{

		findActiveAudioSes();
		if (activeAudioWindow != null)
		{
			mediaString = activeAudioWindow.getTitle();
			overlayManager.add(overlay);
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

		defaultDevice = getDefaultDevice();
		clientThread.invoke(() -> {

			if (client.getGameState() == GameState.LOGGED_IN)
			{
				updateMedia();
			}
		});
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged config)
	{
		overlay.setLastMedia("");

	}

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
