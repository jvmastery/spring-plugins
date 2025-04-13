package cn.jvmaster.core.util;

import cn.jvmaster.core.exception.SystemException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import oshi.PlatformEnum;
import oshi.SystemInfo;

/**
 * 操作系统工具类
 * @author AI
 * @date 2024/12/23 15:54
 * @version 1.0
**/
public class OsUtils {

    private static final OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN = ManagementFactory.getOperatingSystemMXBean();
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();

    /**
     * 获取操作系统
     */
    public static PlatformEnum getOs() {
        return SystemInfo.getCurrentPlatform();
    }

    /**
     * 获取ip地址
     * @return  ip地址集合
     */
    public static List<String> getIpAddressList() {
        return getLocalAllInetAddress()
            .stream()
            .map(item -> item.getHostAddress().toLowerCase())
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 获取mac地址
     * @return  mac地址
     */
    public static List<String> getMacAddress() {
        List<String> macList = getLocalAllInetAddress()
            .stream()
            .map(OsUtils::getMacAddress)
            .distinct()
            .collect(Collectors.toList());
        macList.removeIf(Objects::isNull);

        return macList;
    }

    /**
     * 将网络地址转换为mac地址
     * @param inetAddress   网络地址
     * @return  mac地址
     */
    public static String getMacAddress(InetAddress inetAddress) {
        try {
            byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
            if(mac == null || mac.length == 0) {
                return "";
            }

            StringBuilder stringBuffer = new StringBuilder();
            for(int i = 0; i < mac.length; i++){
                if(i != 0) {
                    stringBuffer.append("-");
                }
                //将十六进制byte转化为字符串
                String temp = Integer.toHexString(mac[i] & 0xff);
                if(temp.length() == 1){
                    stringBuffer.append("0").append(temp);
                }else{
                    stringBuffer.append(temp);
                }
            }

            return stringBuffer.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取网络地址
     * @return 网络地址
     */
    public static List<InetAddress> getLocalAllInetAddress() {
        List<InetAddress> inetAddressList = new ArrayList<>();
        // 遍历所有网口，查询对应的IP地址
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();

                // 获取所有的IP地址
                List<InetAddress> addressList = networkInterface
                    .getInterfaceAddresses()
                    .stream()
                    .map(InterfaceAddress::getAddress)
                    .toList();

                inetAddressList.addAll(addressList);
            }

            return inetAddressList;
        } catch (Exception e) {
            throw new SystemException("获取服务器IP地址失败：" + e.getMessage());
        }
    }

    /**
     * 获取操作系统名称
     * @return  操作系统名称
     */
    public static String getOsName() {
        return OPERATING_SYSTEM_MX_BEAN.getName();
    }

    /**
     * 获取系统负载的平均值，通常是过去 1 分钟的负载。如果该值为负数，则表明操作系统不支持负载平均值的收集
     * @return  负载
     */
    public static double getSystemLoadAverage() {
        return OPERATING_SYSTEM_MX_BEAN.getSystemLoadAverage();
    }

    /**
     * 获取 CPU 核心数
     * @return CPU 核心数
     */
    public static int getCpuCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取系统信息
     * @return  系统信息
     */
    public static SystemInfo getSystemInfo() {
        return SYSTEM_INFO;
    }
}
