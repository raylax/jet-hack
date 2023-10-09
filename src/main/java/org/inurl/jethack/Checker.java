package org.inurl.jethack;

import java.io.File;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Checker {

    public static final String NAME = Checker.class.getName().replaceAll("\\.", "/");
    private static final String JAVA_AGENT_PREFIX = "-javaagent:";

    private static final String[] DNS_DENY_LIST = {
            "jetbrains.com",
            "plugin.obroom.com",
    };

    private static final String[] URL_DENY_LIST = {
            "https://account.jetbrains.com/lservice/rpc/validateKey.action"
    };

    public static void checkURL(URL url) throws Exception {
        String s = url.toString();
        for (String d : URL_DENY_LIST) {
            if (s.startsWith(d)) {
                Logger.info("Block url=%s", url);
                throw new SocketTimeoutException("blocked");
            }
        }
    }

    public static void checkDnsQuery(String host) throws Exception {
        if (checkDnsName(host)) {
            Logger.info("Block dns-query=%s", host);
            throw new UnknownHostException("blocked");
        }
    }

    public static boolean checkDnsReachable(InetAddress address) throws Exception {
        String host = address.getHostName();
        Logger.info("Checking dns-reachable=%s", host);
        return !checkDnsName(host);
    }

    private static boolean checkDnsName(String name) {
        for (String denyName : DNS_DENY_LIST) {
            if (denyName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static BigInteger[] checkArgs(BigInteger a, BigInteger b) {
        BigInteger[] args = BigIntegerCache.ARG_CACHE.get(a + "," + b);
        return args == null ? new BigInteger[]{a, b} : args;
    }

    public static BigInteger checkResult(BigInteger t, BigInteger a, BigInteger b) {
        return BigIntegerCache.RESULT_CACHE.get(t + "," + a + "," + b);
    }

    public static List<String> checkVmArgs(List<String> vmArgs) {
        List<String> list = new ArrayList<>();
        for (String arg : vmArgs) {
            if (arg.startsWith(JAVA_AGENT_PREFIX)) {
                File agent = new File(Jethack.SELF_PATH);
                if (agent.equals(new File(arg.substring(JAVA_AGENT_PREFIX.length())))) {
                    continue;
                }
            }
            list.add(arg);
        }
        return Collections.unmodifiableList(list);
    }



}
