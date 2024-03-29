package ch.TCPTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Start {
    //public static ResourceBundle properties; //存储.properties配置文件数据
    public static String GPSAddr;
    public static String GPSPort;
    public static String LocalAddr;
    public static String LocalPort;

    public static void main(String[] args) {
        //读配置文件configfile.properties
        //properties = ResourceBundle.getBundle("configTest");
        //properties = ResourceBundle.getBundle("config");

        Properties props = new Properties();
        File file = new File("config.properties");
        try {
            FileInputStream fis = new FileInputStream(file);
            props.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GPSAddr = props.getProperty("GPSAddr");
        GPSPort = props.getProperty("GPSPort");
        LocalAddr = props.getProperty("LocalAddr");
        LocalPort = props.getProperty("LocalPort");
        //GPSAddr = properties.getString("GPSAddr");
        //GPSPort = properties.getString("GPSPort");
        //LocalAddr = properties.getString("LocalAddr");
        //LocalPort = properties.getString("LocalPort");

        //启动服务端（LocalAddr:LocalPort），监听客户端连接
        Server server = new Server(LocalAddr, Integer.valueOf(LocalPort));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.startServer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //启动客户端，连接到GPS主机，接收差分码
        Client client = new Client();
        client.run();
    }
}

