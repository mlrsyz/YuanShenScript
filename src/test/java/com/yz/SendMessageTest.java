package com.yz;

import com.alibaba.fastjson.JSON;
import com.yz.qqbot.api.MembersService;
import com.yz.qqbot.api.SendMessage;
import com.yz.qqbot.api.UserService;
import com.yz.qqbot.domain.GuildsMe;
import com.yz.qqbot.domain.Member;
import com.yz.qqbot.request.SendMessageRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 2:08 <br/>
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SendMessageTest {
    @Autowired
    private SendMessage sendMessage;
    @Autowired
    private MembersService membersService;
    @Autowired
    private UserService userService;


    @SneakyThrows
    @Test
    public void t01() {
        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setContent("发送蟾蜍图片测试");
        sendMessageRequest.setFileImage("蟾蜍", fileToByte(new File("C:\\Users\\ymx\\Desktop\\files\\IMG_20201107_185028.jpg")));
//        sendMessage.sendDefaultChannelMsg(sendMessageRequest);
        sendMessage.sendChannelMsg(sendMessageRequest,"6637455034193802989",true);
    }


    @SneakyThrows
    @Test
    public void t02() {
        List<Member> members = membersService.queryGuildMembers("6947530166796095191", "0", 3);
        List<GuildsMe> GuildsMes = userService.queryGuildMe(null, null, null);
        log.info("members ====> {}", JSON.toJSONString(members));
        log.info("GuildsMes ====> {}", JSON.toJSONString(GuildsMes));
    }

    /**
     * 将文件转换成byte数组
     *
     * @param file 目标文件
     * @return byte 目标字节
     */
    public static byte[] fileToByte(File file) {
        byte[] bytes = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            log.error("byteArrayToFile error " + e.getMessage(), e);
        }
        return bytes;
    }

    /**
     * byte数组转File
     *
     * @param byteArray  字节数组
     * @param targetPath 目标路径
     * @return File 目标文件
     */
    public static File byteArrayToFile(byte[] byteArray, String targetPath) {
        InputStream in = new ByteArrayInputStream(byteArray);
        File file = new File(targetPath);
        String path = targetPath.substring(0, targetPath.lastIndexOf("/"));
        if (!file.exists()) {
            new File(path).mkdir();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return file;
        } catch (Exception e) {
            log.error("byteArrayToFile error " + e.getMessage(), e);
            return null;
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
