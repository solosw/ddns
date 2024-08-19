package com.example.ddns.utils;

import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

public class WanGetIpUtils {




    public static String getPublicIPV4(){
        try {
            // 使用HttpURLConnection发送GET请求到公网IP查询API
            URL url = new URL("https://api.ipify.org");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 读取响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            // 输出公网IPv4地址
            String publicIPv4 = content.toString().trim();
           return publicIPv4;
        } catch (IOException e) {
            System.err.println("Error getting public IPv4 address: " + e.getMessage());
        }

        return "";
    }
    /**
     * 传统方式，非常简单直接通过InetAddress获取，但不准确获取的为虚拟ip
     * @throws UnknownHostException
     */
    public static String getTraditionIp() throws UnknownHostException {

        InetAddress localHost = InetAddress.getLocalHost();
        String hostAddress = localHost.getHostAddress();
        String hostName = localHost.getHostName();
        return hostAddress;
    }
    public static List<String> getLocalIPv6Address() {

        List<String> ips=new ArrayList<>();

        try {
            // 获取所有网络接口
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // 遍历每个网络接口的InetAddress
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // 检查是否为IPv6地址
                    if (inetAddress instanceof java.net.Inet6Address && isPublicIPv6Address(inetAddress)) {
                        ips.add(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("SocketException occurred: " + e.getMessage());
        }

        return ips;
    }

    private static boolean isPublicIPv6Address(InetAddress address) {
        String addrStr = address.getHostAddress();
        // 检查是否为链路本地地址
        if (addrStr.startsWith("fe80:")) {
            return false;
        }
        // 检查是否为站点本地地址 (已废弃)
        if (addrStr.startsWith("fec0:")) {
            return false;
        }
        // 检查是否为回环地址
        if (addrStr.equals("::1")) {
            return false;
        }
        // 检查是否为未指定地址
        if (addrStr.equals("::")) {
            return false;
        }
        // 检查是否为多播地址
        if (addrStr.startsWith("ff")) {
            return false;
        }
        if (addrStr.startsWith("0:")) {
            return false;
        }
        // 如果没有匹配以上任何一种情况，则认为是公共IPv6地址
        return true;
    }
    /*
     * 获取本机所有网卡信息   得到所有IP信息
     * @return Inet4Address>
     */
    public static List<Inet4Address> getLocalIp4AddressFromNetworkInterface() throws SocketException {
        List<Inet4Address> addresses = new ArrayList<>(1);

        // 所有网络接口信息
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        if (ObjectUtils.isEmpty(networkInterfaces)) {
            return addresses;
        }
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            //滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
            if (!isValidInterface(networkInterface)) {
                continue;
            }

            // 所有网络接口的IP地址信息
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                // 判断是否是IPv4，并且内网地址并过滤回环地址.
                if (isValidAddress(inetAddress)) {
                    addresses.add((Inet4Address) inetAddress);
                }
            }
        }
        return addresses;
    }

    /**
     * 过滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
     *
     * @param ni 网卡
     * @return 如果满足要求则true，否则false
     */
    private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
        return !ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens"));
    }

    /**
     * 判断是否是IPv4，并且内网地址并过滤回环地址.
     */
    private static boolean isValidAddress(InetAddress address) {
        return address instanceof Inet4Address && !address.isSiteLocalAddress() && !address.isLoopbackAddress();
    }

    /*
     * 通过Socket 唯一确定一个IP
     * 当有多个网卡的时候，使用这种方式一般都可以得到想要的IP。甚至不要求外网地址8.8.8.8是可连通的
     * @return Inet4Address>
     */
    private static Optional<Inet4Address> getIpBySocket() throws SocketException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            if (socket.getLocalAddress() instanceof Inet4Address) {
                return Optional.of((Inet4Address) socket.getLocalAddress());
            }
        } catch (UnknownHostException networkInterfaces) {
            throw new RuntimeException(networkInterfaces);
        }
        return Optional.empty();
    }

    /*
     * 获取本地IPv4地址
     * @return Inet4Address>
     */
    public static Optional<Inet4Address> getLocalIp4Address() throws SocketException {
        final List<Inet4Address> inet4Addresses = getLocalIp4AddressFromNetworkInterface();
        if (inet4Addresses.size() != 1) {
            final Optional<Inet4Address> ipBySocketOpt = getIpBySocket();
            if (ipBySocketOpt.isPresent()) {
                return ipBySocketOpt;
            } else {
                return inet4Addresses.isEmpty() ? Optional.empty() : Optional.of(inet4Addresses.get(0));
            }
        }
        return Optional.of(inet4Addresses.get(0));
    }





}
