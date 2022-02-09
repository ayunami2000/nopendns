package me.ayunami2000.nopendns;

import at.jta.Key;
import at.jta.RegistryErrorException;
import at.jta.Regor;

public class RegTwo {
    private static Regor regor = null;

    private static void ensureRegor() throws RegistryErrorException {
        if(regor==null){
            regor = new Regor();
        }
    }

    private static String readString(Key baseKey, String subKey, String name) throws RegistryErrorException {
        Key k=regor.openKey(baseKey, subKey);
        String res=regor.readValueAsString(k, name);
        regor.closeKey(k);
        return res;
    }

    private static String readDword(Key baseKey, String subKey, String name) throws RegistryErrorException {
        Key k=regor.openKey(baseKey, subKey);
        String res=regor.readDword(k, name);
        regor.closeKey(k);
        return res;
    }

    private static void writeString(Key baseKey, String subKey, String name, String value) throws RegistryErrorException {
        Key k=regor.openKey(baseKey, subKey);
        regor.setValue(k, name, value);
        regor.closeKey(k);
    }

    private static void writeDword(Key baseKey, String subKey, String name, String value) throws RegistryErrorException {
        Key k=regor.openKey(baseKey, subKey);
        regor.saveDword(k, name, value);
        regor.closeKey(k);
    }

    public static void readProxy() throws RegistryErrorException {
        ensureRegor();
        Main.oldSysProxy = readString(Regor.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyOverride");
        Main.oldSysProxyEnabled = readDword(Regor.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable");
    }
    public static void setProxy(String prox) throws RegistryErrorException {
        ensureRegor();
        writeString(Regor.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyOverride", prox);
        writeDword(Regor.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable", "1");
    }
    public static void restoreProxy() throws RegistryErrorException {
        ensureRegor();
        writeString(Regor.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyOverride", Main.oldSysProxy);
        writeDword(Regor.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable", Main.oldSysProxyEnabled);
    }
}