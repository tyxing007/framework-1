package com.github.sunflowerlb.framework.core.http;

/**
 * @author lb
 * @date 2012-10-25 下午8:47:09 
 * @Description 
 */
public class ClientConfiguration {

    public static final String DEFAULT_ENCODING = "UTF-8";
    
    public static final int DEFAULT_SOCKET_TIMEOUT = 60 * 1000;
    
    public static final int DEFAULT_CONNECTION_TIMEOUT = 60 * 1000;
    
    public static final int DEFAULT_MAX_CONNECTIONS = 50;
    
    public static final int DEFAULT_MAX_PRE_ROUTE = DEFAULT_MAX_CONNECTIONS;
    
    public static final int DEFAULT_MAX_RETRIES = 3;
    
    public static final boolean DEFAULT_TCP_NO_DELAY = true;
    
    public static final boolean DEFAULT_STALE_CHECK = true;
    
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    
    private int maxConnections = DEFAULT_MAX_CONNECTIONS;
    
    private int maxPreRoute = DEFAULT_MAX_PRE_ROUTE;
    
    private int maxRetries = DEFAULT_MAX_RETRIES;
    
    private boolean tcpNoDelay = DEFAULT_TCP_NO_DELAY;
    
    private boolean staleCheck = DEFAULT_STALE_CHECK;
    
    private String proxyHost = null;
    
    private int proxyPort = -1;
    
    private String proxyUserName = null;
    
    private String proxyPassword = null;
    
    private String proxyDomain = null;
    
    private String proxyWorkstation = null;
    
    private int socketSendBufferSize = 0;
    
    private int socketReceiveBufferSize = 0;

    private boolean needTimeout = true;
    
    public ClientConfiguration(){
        
    }
    
    public ClientConfiguration(ClientConfiguration other){
        this.socketTimeout = other.socketTimeout;
        this.connectionTimeout = other.connectionTimeout;
        this.maxConnections = other.maxConnections;
        this.maxPreRoute = other.maxPreRoute;
        this.maxRetries = other.maxRetries;
        this.tcpNoDelay = other.tcpNoDelay;
        this.staleCheck = other.staleCheck;
        this.proxyHost = other.proxyHost;
        this.proxyPort = other.proxyPort;
        this.proxyUserName = other.proxyUserName;
        this.proxyPassword = other.proxyPassword;
        this.proxyDomain = other.proxyDomain;
        this.proxyWorkstation = other.proxyWorkstation;
        this.socketSendBufferSize = other.socketSendBufferSize;
        this.socketReceiveBufferSize = other.socketReceiveBufferSize;
        this.needTimeout = other.needTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxPreRoute() {
        return maxPreRoute;
    }

    public void setMaxPreRoute(int maxPreRoute) {
        this.maxPreRoute = maxPreRoute;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public boolean isStaleCheck() {
        return staleCheck;
    }

    public void setStaleCheck(boolean staleCheck) {
        this.staleCheck = staleCheck;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public String getProxyDomain() {
        return proxyDomain;
    }

    public void setProxyDomain(String proxyDomain) {
        this.proxyDomain = proxyDomain;
    }

    public String getProxyWorkstation() {
        return proxyWorkstation;
    }

    public void setProxyWorkstation(String proxyWorkstation) {
        this.proxyWorkstation = proxyWorkstation;
    }

    public int getSocketSendBufferSize() {
        return socketSendBufferSize;
    }

    public void setSocketSendBufferSize(int socketSendBufferSize) {
        this.socketSendBufferSize = socketSendBufferSize;
    }

    public int getSocketReceiveBufferSize() {
        return socketReceiveBufferSize;
    }

    public void setSocketReceiveBufferSize(int socketReceiveBufferSize) {
        this.socketReceiveBufferSize = socketReceiveBufferSize;
    }

    public boolean isNeedTimeout() {
        return needTimeout;
    }

    public void setNeedTimeout(boolean needTimeout) {
        this.needTimeout = needTimeout;
    }
    
}
