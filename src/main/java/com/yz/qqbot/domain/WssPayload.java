package com.yz.qqbot.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * wss通信对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 14:57 <br/>
 */
@Data
public class WssPayload {
    /**
     * 指的是 opcode，全部  <a href="https://bot.q.qq.com/wiki/develop/api/gateway/opcode.html">列表参考 opcode</a>
     * CODE	名称	                    客户端操作	        描述
     * 0	Dispatch	            Receive	            服务端进行消息推送
     * 1	Heartbeat	            Send/Receive	    客户端或服务端发送心跳
     * 2	Identify	            Send            	客户端发送鉴权
     * 6	Resume	                Send	            客户端恢复连接
     * 7	Reconnect	            Receive             服务端通知客户端重新连接
     * 9	Invalid Session         Receive             当identify或resume的时候，如果参数有错，服务端会返回该消息
     * 10	Hello	                Receive	            当客户端与网关建立ws连接之后，网关下发的第一条消息
     * 11	Heartbeat ACK           Receive/Reply	    当发送心跳成功之后，就会收到该消息
     * 12	HTTP Callback ACK	    Reply	            仅用于 http 回调模式的回包，代表机器人收到了平台推送的数据
     * 客户端操作含义如下：
     * Receive  客户端接收到服务端 push 的消息
     * Send     客户端发送消息
     * Reply    客户端接收到服务端发送的消息之后的回包（HTTP 回调模式）
     */
    private Integer op;
    /**
     * 行消息都会有一个序列号，标识消息的唯一性，客户端需要再发送心跳的时候，携带客户端收到的最新的s
     */
    private Integer s;
    /**
     * t和d 主要是用在op为 0 Dispatch 的时候，t 代表事件类型，d 代表事件内容，不同事件类型的事件内容格式都不同，请注意识别
     */
    private String t;
    private Object d;
    /**
     * AT_MESSAGE_CREATE:7659210e-ea10-4002-853a-0c06d7b096cc   应该是请求链路ID
     */
    private Object id;


    /**
     * 建立 websocket 连接之后，就需要进行鉴权了，需要发送一个 OpCode 2 Identify 消息
     */
    @Data
    public static class Identify {
        /**
         * 是创建机器人的时候分配的，格式为Bot {appid}.{app_token}
         */
        private String token;
        /**
         * 是此次连接所需要接收的事件，具体可参考 <a href="https://bot.q.qq.com/wiki/develop/api/gateway/intents.html">Intents</a>
         * intents = [
         *       "GUILDS",
         *       "GUILD_MEMBERS",
         *       "GUILD_MESSAGE_REACTIONS",
         *       "DIRECT_MESSAGE",
         *       "OPEN_FORUMS_EVENT",
         *       "INTERACTION",
         *       "MESSAGE_AUDIT",
         *       "PUBLIC_GUILD_MESSAGES",
         *     ]
         */
        private Integer intents;
        /**
         * 该参数是用来进行水平分片的。该参数是个拥有两个元素的数组。例如：[0,4]，代表分为四个片，当前链接是第 0 个片，业务稍后应该继续建立 shard 为[1,4],[2,4],[3,4]的链接，才能完整接收事件。
         * 更多详细的内容可以参考<a href="https://bot.q.qq.com/wiki/develop/api/gateway/shard.html">Shard</a>
         */
        private List<Integer> shard;
        /**
         * 目前无实际作用，可以按照自己的实际情况填写，也可以留空
         */
        private Properties properties;
    }

    @Data
    public static class Properties {
        /**
         * t和d 主要是用在op为 0 Dispatch 的时候，t 代表事件类型，d 代表事件内容，不同事件类型的事件内容格式都不同，请注意识别
         */
        @JSONField(name = "$os")
        private String os;
        /**
         * t和d 主要是用在op为 0 Dispatch 的时候，t 代表事件类型，d 代表事件内容，不同事件类型的事件内容格式都不同，请注意识别
         */
        @JSONField(name = "$browser")
        private String browser;
        /**
         * t和d 主要是用在op为 0 Dispatch 的时候，t 代表事件类型，d 代表事件内容，不同事件类型的事件内容格式都不同，请注意识别
         */
        @JSONField(name = "$device")
        private String device;
    }

    /**
     * 鉴权成功之后，后台会下发一个 Ready Event
     */
    @Data
    public static class ReadyEvent {
        /**
         * 版本号
         */
        private String version;
        /**
         * session_id
         */
        private String session_id;
        /**
         * 机器人信息
         */
        private User user;
        /**
         * 目前无实际作用，可以按照自己的实际情况填写，也可以留空
         */
        private List<Integer> shard;
    }


    /**
     * 有很多原因都会导致连接断开，断开之后短时间内重连会补发中间遗漏的事件，以保障业务逻辑的正确性。断开重连不需要发送Identify请求。在连接到 Gateway 之后，需要发送 Opcode 6 Resume消息
     */
    @Data
    public static class Resume {
        /**
         * 是创建机器人的时候分配的，格式为Bot {appid}.{app_token}
         */
        private String token;
        /**
         * session_id
         */
        private String session_id;
        /**
         * seq 指的是在接收事件时候的 s 字段，我们推荐开发者在处理过事件之后记录下 s 这样可以在 resume 的时候传递给 websocket，websocket 会自动补发这个 seq 之后的事件
         */
        private Integer seq;
    }
}
