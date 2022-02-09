package me.ayunami2000.nopendns;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class Reg {
    public static void readProxy() {
        //Main.oldSysProxy = Advapi32Util.registryGetBinaryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections", "DefaultConnectionSettings");
        //Main.oldSavedProxy = Advapi32Util.registryGetBinaryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections", "SavedLegacySettings");;

        if(!Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "CachedProxyServer")){
            if(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyServer")){
                Main.oldSysProxyServer = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyServer");
                Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "CachedProxyServer", Main.oldSysProxyServer);
            }
        }else{
            Main.oldSysProxyServer = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "CachedProxyServer");
        }
        if(!Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "CachedProxyEnable")){
            Main.oldSysProxyEnabled = Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable");
            Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "CachedProxyEnable", Main.oldSysProxyEnabled);
        }else{
            Main.oldSysProxyEnabled = Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "CachedProxyEnable");
        }
    }
    public static void setProxy(String proxstr) {
        //byte[] prox = convertProxy(proxstr);
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyServer", proxstr);
        //Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections", "DefaultConnectionSettings", prox);
        //Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections", "SavedLegacySettings", prox);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable", 1);
    }
    public static void restoreProxy() {
        if(Main.oldSysProxyServer==null){
            Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyServer");
        }else{
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyServer", Main.oldSysProxyServer);
        }
        //Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections", "DefaultConnectionSettings", Main.oldSysProxy);
        //Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections", "SavedLegacySettings", Main.oldSavedProxy);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable", Main.oldSysProxyEnabled);

        if(Main.oldSysProxyServer!=null)Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "CachedProxyServer");
        Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "CachedProxyEnable");
    }
    public static byte[] convertProxy(String prox){
        byte[] proxexc=";<local>".getBytes();
        byte[] proxbyte=prox.getBytes();
        byte[] out = new byte[56+proxbyte.length+proxexc.length];
        System.arraycopy(Main.oldSysProxy, 0, out, 0, Math.min(out.length,Main.oldSysProxy.length));
        out[4]++;
        out[8]=3;
        out[12]=(byte)proxbyte.length;
        System.arraycopy(proxbyte, 0, out, 16, proxbyte.length);
        out[16+proxbyte.length]=(byte)proxexc.length;
        System.arraycopy(proxexc, 0, out, 20+proxbyte.length, proxexc.length);
        return out;
        /*
            0.  keep this value
            1.  "00" placeholder
            2.  "00" placeholder
            3.  "00" placeholder
            4.  "xx" increments if changed
            5.  "xx" increments if 4. is "FF"
            6.  "00" placeholder
            7.  "00" placeholder
            8.  "03"=enable proxy, enable auto detect settings, auto script etc
            9.  "00" placeholder
            10. "00" placeholder
            11. "00" placeholder
            12. "xx" length of "proxyserver:port"
            13. "00" placeholder
            14. "00" placeholder
            15. "00" placeholder
            "proxyserver:port"
                "xx" length of proxy exception list
                "00" placeholder
                "00" placeholder
                "00" placeholder
            Proxy Exception list delimited by semi-colons (use "<local>" to exclude local addresses)
            36 times "00"
        */
    }
}