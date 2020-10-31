package xlk.demo.test.connection.server;


import xlk.demo.test.connection.DataProtocol;

/**
 * Created by meishan on 16/12/1.
 */
public interface ResponseCallback {

    void targetIsOffline(DataProtocol reciveMsg);

    void targetIsOnline(String clientIp);
}
