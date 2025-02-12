package com.media.model.providers;

import com.media.MediaConfig;
import com.media.model.win32.AudioSes;
import com.media.model.win32.MMDevAPI;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.ptr.IntByReference;
import net.runelite.api.Client;

public abstract class Win32Provider extends MediaProvider
{
	private final MMDevAPI.MMDevice defaultDevice;
	AudioSes.AudioSessionControl2 activeAudioSession;
	DesktopWindow activeAudioWindow;
	String serviceName;
	Win32Provider(MediaConfig config, Client client)
	{
		super(config, client);
		defaultDevice = getDefaultDevice();
	}

	MMDevAPI.MMDevice getDefaultDevice()
	{
		MMDevAPI.MMDeviceEnumerator audioDevices = MMDevAPI.MMDeviceEnumerator.create();
		assert audioDevices != null;
		return audioDevices.getDefaultDevice();
	}



	DesktopWindow getWindowByPID(int pid)
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

	void findActiveAudioSession()
	{
		AudioSes.AudioSessionManager2 sessionManager = getDefaultDevice().createManager();
		AudioSes.AudioSessionEnumerator sessionEnumerator = sessionManager.getSessionEnumerator();
		int count = sessionEnumerator.getCount();
		for (int i = 0; i < count; i++)
		{

			AudioSes.AudioSessionControl2 ses = sessionEnumerator.getSession(i);



			if (ses.getState() == AudioSes.AudioSessionStateActive)
			{


				if (ses.getName().contains(serviceName))
				{
					try{
						DesktopWindow window = getWindowByPID(ses.getProcessID());

					}catch (Exception e){
						return;
					}
					activeAudioSession = ses;
					activeAudioWindow = getWindowByPID(ses.getProcessID());

					return;
				}

			}
		}
		activeAudioSession = null;
		activeAudioWindow = null;

	}
}
