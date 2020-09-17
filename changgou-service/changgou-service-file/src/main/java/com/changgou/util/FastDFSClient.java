package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.*;


public class FastDFSClient {

    static {
        //从classpath下获取文件对象获取路径
        String path = new ClassPathResource("fdfs_client.conf").getPath();
        System.out.println(path);
        try {
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     *
     * @param file 上传的文件信息封装
     * @return
     */
    public static String[] upload(FastDFSFile file) {
        try {
            //创建一个Tracker访问的客户端对象TrackerClient
            TrackerClient trackerClient = new TrackerClient();
            //通过TrackerClient访问TrackerServer服务,获取连接信息
            TrackerServer trackerServer = trackerClient.getConnection();
            //通过TrackerServer的链接信息可以获取Storage的链接信息,创建StorageClient对象存储Storage的链接信息
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //参数1 字节数组
            //参数2 扩展名(不带点)
            //参数3 元数据( 文件的大小,文件的作者,文件的创建时间戳)
            NameValuePair[] meta_list = new NameValuePair[]{new NameValuePair("作者", file.getAuthor()), new NameValuePair("文件名", file.getName())};

            /**
             * 通过StorageClient访问Storage,实现文件上传,并且获取文件上传后的存储信息
             * 1.上传文件的字节数组
             * 2.文件的扩展名 jpg
             * 3.附加参数 比如:拍摄地址 :北京
             * strings[0]==文件上传所存储的storge的组名字  group1
             * strings[1]==文件存储到storge上的文件名字 M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg
             *
             */
            String[] strings = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);

            return strings;//
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //图片下载
    public static InputStream downFile(String groupName, String remoteFileName) {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //5.创建stroageserver 对象
            //6.创建storageclient 对象
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //7.根据组名 和 文件名 下载图片

            //参数1:指定组名
            //参数2 :指定远程的文件名
            byte[] bytes = storageClient.download_file(groupName, remoteFileName);
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            return byteArrayInputStream;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //图片下载到本地
    public static void downFileToLocal() throws Exception {
        InputStream is = downFile("group1", "M00/00/00/wKjThF9gwreAbzXdAAAAGowMoMM084.txt");
        FileOutputStream os = new FileOutputStream("D:/1.txt");
        //定义缓冲区
        byte[] buffer = new byte[1024];
        while (true) {
            int length = is.read(buffer);
            if (-1 == length)
                break;
            os.write(buffer, 0, length);
        }

        os.flush();
        os.close();
        is.close();
    }
    //图片删除

    public static void deleteFile(String groupName, String remoteFileName) {
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //5.创建stroageserver 对象
            //6.创建storageclient 对象
            StorageClient storageClient = new StorageClient(trackerServer, null);
            int i = storageClient.delete_file(groupName, remoteFileName);
            if (i == 0) {
                System.out.println("删除成功");
            } else {
                System.out.println("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取Storages信息
     *
     * @param groupName
     * @return
     */
    public static StorageServer getStorages(String groupName) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            //参数1 指定traqckerserver 对象
            //参数2 指定组名
            StorageServer group1 = trackerClient.getStoreStorage(trackerServer, groupName);
            return group1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static StorageServer getStorages() {
        try {
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            //参数1 指定traqckerserver 对象
            //参数2 指定组名
            StorageServer group1 = trackerClient.getStoreStorage(trackerServer);
            return group1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件信息
     *
     * @param groupName      文件组名
     * @param remoteFileName 文件的存储路径名字
     * @return
     */
    public static FileInfo getFile(String groupName, String remoteFileName) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            StorageClient storageClient = new StorageClient(trackerServer, null);

            //参数1 指定组名
            //参数2 指定文件的路径
            FileInfo fileInfo = storageClient.get_file_info(groupName, remoteFileName);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取Storages的ip和端口信息
     *
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName) {
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            ServerInfo[] group1s = trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
            return group1s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    //获取tracker 的ip和端口的信息
    //http://192.168.211.132:8080
    public static String getTrackerUrl() {
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //tracker 的ip的信息
            String hostString = trackerServer.getInetSocketAddress().getHostString();

            //http://192.168.211.132:8080/group1/M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg img
            int g_tracker_http_port = ClientGlobal.getG_tracker_http_port();
            return "http://" + hostString + ":" + g_tracker_http_port;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取TrackerServer
     *
     * @return
     */
    public static TrackerServer getTrackerServer() {
        TrackerClient trackerClient = new TrackerClient();
        try {
            return trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取StorageClient
     *
     * @param trackerServer
     * @return
     */
    public static StorageClient getStorageClient(TrackerServer trackerServer) {
        return new StorageClient(trackerServer, null);
    }


    public static void main(String[] args) throws Exception {
        //downFileToLocal();
        //deleteFile("group1", "M00/00/00/wKjThF9gwreAbzXdAAAAGowMoMM084.txt");
        System.out.println(getStorages().getStorePathIndex());
    }
}
