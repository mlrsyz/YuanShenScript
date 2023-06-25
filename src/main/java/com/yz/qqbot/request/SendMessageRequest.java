package com.yz.qqbot.request;

import com.yz.qqbot.domain.MessageArk;
import com.yz.qqbot.domain.MessageEmbed;
import com.yz.qqbot.domain.MessageMarkdown;
import com.yz.qqbot.domain.MessageReference;
import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:59 <br/>
 */
@Data
public class SendMessageRequest {
    //选填，消息内容，文本内容，支持内嵌格式
    private String content;
    //选填，embed 消息，一种特殊的 ark，详情参考Embed消息
    private MessageEmbed embed;
    //ark消息对象	选填，ark 消息d
    private MessageArk ark;
    //引用消息对象	选填，引用消息
    private MessageReference message_reference;
    //选填，图片url地址，平台会转存该图片，用于下发图片消息
    private String image;
    //选填，图片文件。form-data 支持直接通过文件上传的方式发送图片。
    private byte[] file_image;
    //文件名称file_image存在时赋予
    private String imageName = "";
    //选填，要回复的消息id(Message.id), 在 AT_CREATE_MESSAGE 事件中获取。
    private String msg_id;
    //选填，要回复的事件id, 在各事件对象中获取。
    private String event_id;
    //markdown 消息对象	选填，markdown 消息
    private MessageMarkdown markdown;

    public void setFileImage(String imageName, byte[] file_image) {
        this.imageName = imageName;
        this.file_image = file_image;
    }
}
