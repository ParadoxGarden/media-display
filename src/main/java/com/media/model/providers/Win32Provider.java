package com.media.model.providers;

import com.media.MediaConfig;
import com.media.model.win32.MMDevAPI;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.ptr.IntByReference;
import net.runelite.api.Client;

public abstract class Win32Provider extends AudioProvider
{
	Win32Provider(MediaConfig config, Client client)
	{
		super(config, client);
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
}
