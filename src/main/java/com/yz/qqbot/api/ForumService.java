package com.yz.qqbot.api;

import com.yz.qqbot.domain.Forum;
import com.yz.qqbot.request.AddForum;
import com.yz.qqbot.response.AddForumRes;
import com.yz.qqbot.response.GetForums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 1:49 <br/>
 */
@Slf4j
@Service
public class ForumService extends BaseService {
    //GET /channels/{channel_id}/threads 该接口用于获取子频道下的帖子列表
    String forumUrl(String channel_id) {
        return MessageFormat.format("/channels/{0}/threads", channel_id);
    }

    //GET /channels/{channel_id}/threads/{thread_id} 该接口用于获取子频道下的帖子详情
    String getForumUrl(String channel_id, String thread_id) {
        return forumUrl(channel_id) + "/" + thread_id;
    }

    /**
     * 该接口用于获取子频道下的帖子列表。
     * 注意:
     * 公域机器人暂不支持申请，仅私域机器人可用，选择私域机器人后默认开通。
     * 注意: 开通后需要先将机器人从频道移除，然后重新添加，方可生效
     */
    public GetForums getForums(String channel_id) {
        String requestUrl = botConfig.getPrefixApi(forumUrl(channel_id));
        return restTemplate.getForObject(requestUrl, GetForums.class, new HttpEntity<>(null, botConfig.getHeader2()));
    }

    /**
     * 该接口用于获取子频道下的帖子详情。
     * 注意:
     * 公域机器人暂不支持申请，仅私域机器人可用，选择私域机器人后默认开通。
     * 注意: 开通后需要先将机器人从频道移除，然后重新添加，方可生效
     */
    public Forum.Thread getForum(String channel_id, String thread_id) {
        String requestUrl = botConfig.getPrefixApi(getForumUrl(channel_id, thread_id));
        return restTemplate.getForObject(requestUrl, Forum.Thread.class, new HttpEntity<>(null, botConfig.getHeader2()));
    }

    /**
     * 创建成功后，返回创建成功的任务ID
     */
    public AddForumRes addForum(String channel_id, AddForum addForum) {
        String requestUrl = botConfig.getPrefixApi(forumUrl(channel_id));
        return restTemplate.exchange(requestUrl, HttpMethod.PUT, new HttpEntity<>(addForum, botConfig.getHeader2()), AddForumRes.class).getBody();
    }

    /**
     * 该接口用于删除指定子频道下的某个帖子
     */
    public boolean delForum(String channel_id, String thread_id) {
        String requestUrl = botConfig.getPrefixApi(getForumUrl(channel_id, thread_id));
        ResponseEntity<Void> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(null, botConfig.getHeader2()), Void.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }
}
