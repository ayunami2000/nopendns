package me.ayunami2000.nopendns;

import at.jta.Key;
import at.jta.RegistryErrorException;
import at.jta.Regor;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class Reg {

    public static void readProxy() throws RegistryErrorException {
        Main.oldSysProxy = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyOverride");
        Main.oldSysProxyEnabled = Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable");
        //Main.oldSysProxy = WindowsRegistry.readValue("HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyOverride");
        //Main.oldSysProxyEnabled = WindowsRegistry.readValue("HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable");
    }
    public static void setProxy(String prox) throws RegistryErrorException {
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyOverride", prox);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable", 1);
        //WindowsRegistry.writeValue("HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyOverride", prox);
        //WindowsRegistry.writeValue("HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable", "1");
    }
    public static void restoreProxy() throws RegistryErrorException {
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyOverride", Main.oldSysProxy);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable", Main.oldSysProxyEnabled);
        //WindowsRegistry.writeValue("HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyOverride", Main.oldSysProxy);
        //WindowsRegistry.writeValue("HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable", Main.oldSysProxyEnabled);
    }
}