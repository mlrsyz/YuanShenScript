package com.yz.qqbot.api.enumtype;

import com.yz.qqbot.domain.WssPayload;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * 事件
 * 作者：ymx <br/>
 * 创建时间：2023/6/28 9:53 <br/>
 */
@AllArgsConstructor
public enum Intents {
    NON("不存在的事件"),
    //GUILDS (1 << 0)
    GUILD_CREATE("当机器人加入新guild时"),
    GUILD_UPDATE("当guild资料发生变更时"),
    GUILD_DELETE("当机器人退出guild时"),
    CHANNEL_CREATE("当channel被创建时"),
    CHANNEL_UPDATE("当channel被更新时"),
    CHANNEL_DELETE("当channel被删除时"),

    //GUILD_MEMBERS (1 << 1)
    GUILD_MEMBER_ADD("当成员加入时"),
    GUILD_MEMBER_UPDATE("当成员资料变更时"),
    GUILD_MEMBER_REMOVE("  当成员被移除时"),

    //GUILD_MESSAGES (1 << 9)    消息事件，仅 *私域* 机器人能够设置此 intents。
    MESSAGE_CREATE("发送消息事件，代表频道内的全部消息，而不只是 at 机器人的消息。内容与 AT_MESSAGE_CREATE 相同"),
    MESSAGE_DELETE("删除（撤回）消息事件"),

    //GUILD_MESSAGE_REACTIONS (1 << 10)
    MESSAGE_REACTION_ADD("为消息添加表情表态"),
    MESSAGE_REACTION_REMOVE("为消息删除表情表态"),

    //DIRECT_MESSAGE (1 << 12)
    DIRECT_MESSAGE_CREATE("当收到用户发给机器人的私信消息时"),
    DIRECT_MESSAGE_DELETE("删除（撤回）消息事件"),

    //OPEN_FORUMS_EVENT (1 << 18)       论坛事件, 此为公域的论坛事件
    OPEN_FORUM_THREAD_CREATE("当用户创建主题时"),
    OPEN_FORUM_THREAD_UPDATE("当用户更新主题时"),
    OPEN_FORUM_THREAD_DELETE("当用户删除主题时"),
    OPEN_FORUM_POST_CREATE("当用户创建帖子时"),
    OPEN_FORUM_POST_DELETE("当用户删除帖子时"),
    OPEN_FORUM_REPLY_CREATE("当用户回复评论时"),
    OPEN_FORUM_REPLY_DELETE("当用户删除评论时"),

    //AUDIO_OR_LIVE_CHANNEL_MEMBER (1 << 19)   音视频/直播子频道成员进出事件
    AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER("当用户进入音视频/直播子频道"),
    AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT("当用户离开音视频/直播子频道"),

    //INTERACTION (1 << 26)
    INTERACTION_CREATE("互动事件创建时"),

    //MESSAGE_AUDIT (1 << 27)
    MESSAGE_AUDIT_PASS("消息审核通过"),
    MESSAGE_AUDIT_REJECT("消息审核不通过"),

    //FORUMS_EVENT (1 << 28)  论坛事件，仅 *私域* 机器人能够设置此 intents。
    FORUM_THREAD_CREATE("当用户创建主题时"),
    FORUM_THREAD_UPDATE("当用户更新主题时"),
    FORUM_THREAD_DELETE("当用户删除主题时"),
    FORUM_POST_CREATE("当用户创建帖子时"),
    FORUM_POST_DELETE("当用户删除帖子时"),
    FORUM_REPLY_CREATE("当用户回复评论时"),
    FORUM_REPLY_DELETE("当用户删除评论时"),
    FORUM_PUBLISH_AUDIT_RESULT(" 当用户发表审核通过时"),

    //AUDIO_ACTION (1 << 29)
    AUDIO_START("音频开始播放时"),
    AUDIO_FINISH("音频播放结束时"),
    AUDIO_ON_MIC("上麦时"),
    AUDIO_OFF_MIC("下麦时"),

    //PUBLIC_GUILD_MESSAGES (1 << 30)  消息事件，此为公域的消息事件
    AT_MESSAGE_CREATE("当收到@机器人的消息时"),
    PUBLIC_MESSAGE_DELETE("当频道的消息被删除时");
    private final String desc;

    //是否恢复件补发完成
    public static boolean isResumed(WssPayload payload) {
        return Objects.equals(payload.getOp(), 0) && Objects.equals(payload.getT(), "RESUMED");
    }

    //是否恢复件补发完成
    public static boolean isReady(WssPayload payload) {
        return Objects.equals(payload.getOp(), 0) && Objects.equals(payload.getT(), "READY");
    }

    //是否恢复件补发完成
    public static boolean isHello(WssPayload payload) {
        return Objects.equals(payload.getOp(), 10) && Objects.nonNull(payload.getD());
    }

    public static boolean isIntents(Intents intent) {
        return !NON.equals(intent);
    }

    public static Intents getIntents(String intent) {
        return Arrays.stream(values())
                .filter(event -> Objects.equals(intent, event.name()))
                .findFirst().orElse(NON);
    }
}
