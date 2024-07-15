package com.media.model.win32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface MMDevAPI extends StdCallLibrary
{

	class MMDevice extends Unknown
	{
		public MMDevice()
		{
			super();
		}

		public MMDevice(Pointer p)
		{
			super(p);
		}

		WinNT.HRESULT activate(PointerByReference ppInterface)
		{
			return (WinNT.HRESULT) _invokeNativeObject(3,
				new Object[]{this.getPointer(), AudioSes.AUDIO_SESSION_MANAGER_REF, WTypes.CLSCTX_INPROC_SERVER, null, ppInterface}, WinNT.HRESULT.class);
		}

		public AudioSes.AudioSessionManager2 createManager()
		{
			PointerByReference pSessionManager = new PointerByReference();

			WinNT.HRESULT hres = activate(pSessionManager);
			if (COMUtils.FAILED(hres))
			{
				System.out.println("ERROR IN NATIVE GETSESSIONMANAGER");
				return null;
			}

			return new AudioSes.AudioSessionManager2(pSessionManager.getValue());

		}

	}

	class MMDeviceEnumerator extends Unknown
	{
		public MMDeviceEnumerator()
		{
		}

		public MMDeviceEnumerator(Pointer p)
		{
			super(p);
		}


		public static MMDeviceEnumerator create()
		{
			final Guid.CLSID CLSID_MMDeviceEnumerator = new Guid.CLSID("bcde0395-e52f-467c-8e3d-c4579291692e");
			final Guid.GUID IID_IMMDeviceEnumerator = new Guid.GUID("a95664d2-9614-4f35-a746-de8db63617e6");

			PointerByReference pEnumerator = new PointerByReference();

			WinNT.HRESULT hResult = Ole32.INSTANCE.CoCreateInstance(
				CLSID_MMDeviceEnumerator, null, WTypes.CLSCTX_ALL, IID_IMMDeviceEnumerator, pEnumerator
			);
			if (COMUtils.FAILED(hResult))
			{
				System.out.println("ERROR IN NATIVE GETDEVENUM");
				return null;
			}

			return new MMDeviceEnumerator(pEnumerator.getValue());
		}

		WinNT.HRESULT GetDefaultAudioEndpoint(int EDataFlow, int ERole, PointerByReference ppEndpoint)
		{
			return (WinNT.HRESULT) _invokeNativeObject(4,
				new Object[]{this.getPointer(), EDataFlow, ERole, ppEndpoint}, WinNT.HRESULT.class);
		}

		public MMDevice getDefaultDevice()
		{
			PointerByReference test = new PointerByReference();
			WinNT.HRESULT hResult = GetDefaultAudioEndpoint(0, 1, test);
			if (COMUtils.FAILED(hResult))
			{
				System.out.println("ERROR IN NATIVE GETDEFAULTENDPOINT");
				return null;
			}
			return new MMDevice(test.getValue());
		}
	}
}
