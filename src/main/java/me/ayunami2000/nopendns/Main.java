package me.ayunami2000.nopendns;

import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.xbill.DNS.*;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.Arrays;

class ShutdownHook extends Thread {
    public void run()
    {
        try {
            Reg.restoreProxy();
        } catch (Exception e) {
            System.out.println("WARNING: Unable to restore system proxy!");
        }
    }
}

public class Main {
    private static String[] dnsServers=new String[]{"1.1.1.1","8.8.8.8"};
    private static String[] dohAndProxy=new String[]{"https://1.1.1.1/dns-query","127.0.0.1:9666"};
    private static Resolver dohResolver=null;
    private static Resolver defResolver=null;
    private static final String[] blockList = new String[]{
            "146.112.61.104",
            "::ffff:146.112.61.104",
            "146.112.61.105",
            "::ffff:146.112.61.105",
            "146.112.61.106",
            "::ffff:146.112.61.106",
            "146.112.61.107",
            "::ffff:146.112.61.107",
            "146.112.61.108",
            "::ffff:146.112.61.108",
            "146.112.61.109",
            "::ffff:146.112.61.109",
            "146.112.61.110",
            "::ffff:146.112.61.110"
    };
    private static int p=8869;
    public static byte[] oldSysProxy=null;
    public static byte[] oldSavedProxy=null;
    public static String oldSysProxyServer=null;
    public static int oldSysProxyEnabled=0;

    public static void main(String[] args){
        System.out.println("nopendns by ayunami2000");
        if(args.length==0){
            System.out.println("usage:\n  [...].jar [dns1] [dns2] [port]\n  [...].jar doh [dohurl] [port]\n  [...].jar pdoh [dohurl] [dohhttpproxy] [port]\n  [...].jar revert\n  note: adding \"justproxy\" after the .jar part will disable setting the system proxy on Windows");
        }
        if(args.length==1&&args[0].equalsIgnoreCase("revert")){
            try {
                Reg.readProxy();
                Reg.restoreProxy();
            } catch (Exception e) {
                System.out.println("WARNING: Unable to restore system proxy!");
            }
            return;
        }
        boolean doSetSysProxy=true;
        if(args.length>0&&args[0].equalsIgnoreCase("justproxy")){
            doSetSysProxy=false;
            args=Arrays.copyOfRange(args, 1, args.length);
        }
        if(args.length>0&&args[args.length-1].matches("\\d{1,5}")){
            p=Integer.parseInt(args[args.length-1]);
        }
        if(doSetSysProxy&&System.getProperty("os.name").startsWith("Windows")) {
            try {
                Reg.readProxy();
                Reg.setProxy("127.0.0.1:" + p);
                Runtime.getRuntime().addShutdownHook(new ShutdownHook());
            } catch (Exception e) {
                System.out.println("Unable to set system proxy!");
            }
        }
        if(args.length>=1&&(args[0].equalsIgnoreCase("doh")||args[0].equalsIgnoreCase("pdoh"))){
            defResolver=Lookup.getDefaultResolver();
            if(args.length>=2)dohAndProxy[0]=args[1];
            if(args[0].equalsIgnoreCase("doh")) {
                dohResolver = new DohResolver(dohAndProxy[0]);
                System.out.println("enabling DoH!");
            }else{
                if(args.length>=3)dohAndProxy[1]=args[2];
                String[] proxIpPort = dohAndProxy[1].split(":", 2);
                dohResolver = new ProxyDohResolver(dohAndProxy[0], new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxIpPort[0], Integer.parseInt(proxIpPort[1]))));
                System.out.println("enabling proxied DoH!");
            }
            //((ExtendedResolver)Lookup.getDefaultResolver()).addResolver(dohResolver);
        }else {
            if(args.length>=1)dnsServers[0]=args[0];
            if(args.length>=2)dnsServers[1]=args[1];
            System.setProperty("sun.net.spi.nameservice.nameservers", String.join(",", dnsServers));
            System.setProperty("dns.server", dnsServers[0]);
            System.setProperty("dns.fallback.server", dnsServers[1]);
        }


        HttpProxyServer server =
                DefaultHttpProxyServer.bootstrap()
                        .withPort(8869)
                        .withServerResolver((host, port) -> {
                            if(dohResolver==null){
                                return new InetSocketAddress(Address.getByName(host), port);
                            }else {
                                String norm = null;
                                Lookup.setDefaultResolver(defResolver);
                                try {
                                    norm = Address.getByName(host).getHostAddress();
                                }catch(UnknownHostException e){}
                                if(norm==null||Arrays.asList(blockList).contains(norm)){
                                    Lookup.setDefaultResolver(dohResolver);
                                    return new InetSocketAddress(Address.getByName(host), port);
                                }else{
                                    return new InetSocketAddress(host, port);
                                }
                            }
                        })
                        .start();
        System.out.println("started on port :"+p);
        while(true){}
    }
}
