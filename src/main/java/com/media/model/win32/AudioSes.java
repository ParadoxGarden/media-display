package com.media.model.win32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface AudioSes extends StdCallLibrary
{

	Guid.REFIID AUDIO_SESSION_MANAGER_REF = new Guid.REFIID(new Guid.IID("77AA99A0-1BD6-484F-8BC7-2C654C9A9B6F"));
	Guid.REFIID AUDIO_SESSION_CONTROL2_REF = new Guid.REFIID(new Guid.IID("bfb7ff88-7239-4fc9-8fa2-07c950be9c6d"));
	int AudioSessionStateInactive = 0;
	int AudioSessionStateActive = 1;
	int AudioSessionStateExpired = 2;

	class AudioSessionControl extends Unknown
	{
		// https://learn.microsoft.com/en-us/windows/win32/api/audiopolicy/nn-audiopolicy-iaudiosessioncontrol

		public AudioSessionControl(Pointer p)
		{
			super(p);
		}

		WinNT.HRESULT GetState(IntByReference sessionState)
		{
			return (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{this.getPointer(), sessionState}, WinNT.HRESULT.class);
		}

		WinNT.HRESULT GetDisplayName(PointerByReference pRetValue)
		{
			return (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{this.getPointer(), pRetValue}, WinNT.HRESULT.class);
		}

		public String getName()
		{
			PointerByReference pName = new PointerByReference();
			WinNT.HRESULT hResult = GetDisplayName(pName);
			if (COMUtils.FAILED(hResult))
			{
				System.out.println("ERROR IN NATIVE GETNAME");
				return null;
			}
			return pName.getValue().getWideString(0);
		}

		public int getState()
		{
			IntByReference pState = new IntByReference();
			WinNT.HRESULT hResult = GetState(pState);
			if (COMUtils.FAILED(hResult))
			{
				System.out.println("ERROR IN NATIVE GETSTATE");
				return 0;
			}
			return pState.getValue();
		}
	}

	class AudioSessionControl2 extends AudioSessionControl
	{
		public AudioSessionControl2(Pointer p)
		{
			super(p);
		}

		WinNT.HRESULT GetSessionIdentifier(PointerByReference pSessionStr)
		{
			return (WinNT.HRESULT) _invokeNativeObject(12, new Object[]{this.getPointer(), pSessionStr}, WinNT.HRESULT.class);
		}

		WinNT.HRESULT GetProcessID(IntByReference pProcID)
		{
			return (WinNT.HRESULT) _invokeNativeObject(14, new Object[]{this.getPointer(), pProcID}, WinNT.HRESULT.class);
		}

		WinNT.HRESULT IsSystemSoundsSession()
		{
			return (WinNT.HRESULT) _invokeNativeObject(15, new Object[]{this.getPointer()}, WinNT.HRESULT.class);
		}

		public Boolean isSysSession()
		{
			WinNT.HRESULT hResult = IsSystemSoundsSession();
			return COMUtils.FAILED(hResult);

		}

		public String getSessionIdentifier()
		{
			PointerByReference pName = new PointerByReference();
			WinNT.HRESULT hResult = GetSessionIdentifier(pName);
			if (COMUtils.FAILED(hResult))
			{
				System.out.println("ERROR IN NATIVE GETSESSIONIDENTIFIER");
				return null;
			}
			return pName.getValue().getWideString(0);
		}

		public int getProcessID()
		{
			IntByReference pState = new IntByReference();
			WinNT.HRESULT hResult = GetProcessID(pState);
			if (COMUtils.FAILED(hResult))
			{
				System.out.println("ERROR IN NATIVE GETPROCESSID");
				return 0;
			}
			return pState.getValue();

		}


	}

	class AudioSessionEnumerator extends Unknown
	{
		public AudioSessionEnumerator(Pointer p)
		{
			super(p);
		}

		private WinNT.HRESULT GetCount(IntByReference count)
		{
			return (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{this.getPointer(), count}, WinNT.HRESULT.class);
		}

		private WinNT.HRESULT GetSession(int sessionNum, PointerByReference pAudioSessionControl)
		{
			return (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{this.getPointer(), sessionNum, pAudioSessionControl}, WinNT.HRESULT.class);

		}

		public int getCount()
		{
			IntByReference test = new IntByReference();
			WinNT.HRESULT hResult = GetCount(test);
			if (COMUtils.FAILED(hResult))
			{
				System.out.println("ERROR IN NATIVE GETCOUNT");
				return 0;
			}
			return test.getValue();
		}

		public AudioSessionControl2 getSession(int sessionNum)
		{
			PointerByReference audioSes = new PointerByReference();
			WinNT.HRESULT hResult = GetSession(sessionNum, audioSes);
			if (COMUtils.FAILED(hResult))
			{
				System.out.println("ERROR IN NATIVE GETSESSION");
				return null;
			}
			//get audiosessioncontrol2
			PointerByReference audioSes2 = new PointerByReference();
			WinNT.HRESULT hResult2 = new AudioSessionControl(audioSes.getValue()).QueryInterface(AUDIO_SESSION_CONTROL2_REF, audioSes2);
			if (COMUtils.FAILED(hResult2))
			{
				System.out.println("ERROR IN NATIVE GETSESSION2");
				return null;
			}
			return new AudioSessionControl2(audioSes2.getValue());

		}

	}

	class AudioSessionEvents extends Unknown
	{
		public AudioSessionEvents(Pointer p)
		{
			super(p);
		}
		//public WinNT.HRESULT OnDisplayNameChanged(

		//)

	}

	class AudioSessionManager2 extends Unknown
	{
		public AudioSessionManager2(Pointer p)
		{
			super(p);
		}

		private WinNT.HRESULT GetSessionEnumerator(PointerByReference pSessionEnum)
		{
			return (WinNT.HRESULT) _invokeNativeObject(5, new Object[]{this.getPointer(), pSessionEnum}, WinNT.HRESULT.class);
		}

		public AudioSessionEnumerator getSessionEnumerator()
		{
			PointerByReference test = new PointerByReference();
			WinNT.HRESULT hResult = GetSessionEnumerator(test);
			if (COMUtils.FAILED(hResult))
			{
				System.out.println("ERROR IN NATIVE GETSESSIONENUM");
				return null;
			}
			return new AudioSessionEnumerator(test.getValue());
		}
	}
}
