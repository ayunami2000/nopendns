package me.ayunami2000.nopendns;

import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.xbill.DNS.Address;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.ProxyDohResolver;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Main {
    public static String[] dnsServers=new String[]{"1.1.1.1","94.140.14.14"};
    public static String[] dohAndProxy=new String[]{"https://dns.google/dns-query","127.0.0.1:9666"};
    public static ProxyDohResolver dohResolver=null;
    public static final String[] blockList = new String[]{
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

    //private static Pattern urlpat = Pattern.compile("^[a-zA-Z0-9_-]+:\\/\\/([^:/]+)");

    public static void main(String[] args){
        System.out.println("nopendns by ayunami2000");
        int p=8869;
        if(args.length>=1&&args[0].equalsIgnoreCase("doh")){
            if(args.length>=2)dohAndProxy[0]=args[1];
            if(args.length>=3)dohAndProxy[1]=args[2];
            String[] proxIpPort=dohAndProxy[1].split(":",2);
            dohResolver=new ProxyDohResolver(dohAndProxy[0],new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxIpPort[0], Integer.parseInt(proxIpPort[1]))));
            Lookup.setDefaultResolver(dohResolver);
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
                                try {
                                    norm = InetAddress.getByName(host).getHostAddress();
                                }catch(UnknownHostException e){}
                                if(norm==null||Arrays.asList(blockList).contains(norm)){
                                    return new InetSocketAddress(Address.getByName(host), port);
                                }else{
                                    return new InetSocketAddress(host, port);
                                }
                            }
                        })
                        /*
                        .withFiltersSource(new HttpFiltersSourceAdapter() {
                            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                                return new HttpFiltersAdapter(originalRequest) {
                                    @Override
                                    public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
                                        String[] parts=resolvingServerHostAndPort.split(":");
                                        InetSocketAddress addr;
                                        int port=80;
                                        try{
                                            port=Integer.parseInt(parts[1]);
                                        }catch(NumberFormatException e){

                                        }
                                        try {
                                            addr = new InetSocketAddress(Address.getByName(parts[0]),port);
                                        } catch (UnknownHostException e) {
                                            addr = new InetSocketAddress("0.0.0.0",port);
                                        }
                                        return addr;
                                    }

                                    @Override
                                    public HttpObject serverToProxyResponse(HttpObject httpObject) {
                                        // TODO: implement your filtering here
                                        return httpObject;
                                    }
                                };
                            }
                        })
                        */
                        .start();
        System.out.println("started on port :"+p);
        while(true){}
    }
}
