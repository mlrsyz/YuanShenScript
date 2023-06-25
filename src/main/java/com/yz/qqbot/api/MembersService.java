package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yz.qqbot.domain.Member;
import com.yz.qqbot.domain.RoleMembers;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 成员API <a href="https://bot.q.qq.com/wiki/develop/api/openapi/member/get_members.html">成员API</a>
 * 作者：ymx <br/>
 * 创建时间：2023/6/25 18:18 <br/>
 */
@Slf4j
@Service
public class MembersService extends BaseService {
    //获取频道成员列表
    private static final String guild_Members = "/guilds/{0}/members";

    //用于获取 guild_id 频道中指定role_id身份组下所有成员的详情列表，支持分页
    private static final String guild_role_members = "/guilds/{0}/roles/{1}/members";

    //
    /**
     * 用于删除 guild_id 指定的频道下的成员 user_id。&& 用于获取 guild_id 指定的频道中 user_id 对应成员的详细信息
     * <p>
     * 需要使用的 token 对应的用户具备踢人权限。如果是机器人，要求被添加为管理员。
     * 操作成功后，会触发频道成员删除事件。
     * 无法移除身份为管理员的成员
     * 注意
     * <p>
     * 公域机器人暂不支持申请，仅私域机器人可用，选择私域机器人后默认开通。
     * 注意: 开通后需要先将机器人从频道移除，然后重新添加，方可生效。
     */
    private static final String guilds_user = "/guilds/{0}/members/{1}";

    /**
     * 获取频道成员列表
     *
     * @param guild_id 指定的频道ID
     * @param after    上一次回包中最后一个member的user id， 如果是第一次请求填 0，默认为 0
     * @param limit    分页大小，1-400，默认是 1。成员较多的频道尽量使用较大的limit值，以减少请求数
     */
    public List<Member> queryGuildMembers(String guild_id, String after, Integer limit) {
        String requestUrl = botConfig.getPrefixApi(MessageFormat.format(guild_Members, guild_id));
        Map<String, String> params = new HashMap<String, String>() {{
            put("after", StringUtils.isEmpty(after) ? "0" : after);
            put("limit", Objects.isNull(limit) ? "10" : limit.toString());
        }};
        String result = ScriptUtils.sendGet(requestUrl, params, botConfig.getHeader());
        return JSONArray.parseArray(result, Member.class);
    }

    /**
     * 用于获取 guild_id 频道中指定role_id身份组下所有成员的详情列表，支持分页
     *
     * @param guild_id    指定的频道ID
     * @param start_index 将上一次回包中next填入， 如果是第一次请求填 0，默认为 0
     * @param limit       分页大小，1-400，默认是 1。成员较多的频道尽量使用较大的limit值，以减少请求数
     */
    public RoleMembers queryGuildRoleMembers(String guild_id,
                                             String role_id,
                                             String start_index,
                                             Integer limit) {
        String requestUrl = botConfig.getPrefixApi(MessageFormat.format(guild_role_members, guild_id, role_id));
        Map<String, String> params = new HashMap<String, String>() {{
            put("start_index", StringUtils.isEmpty(start_index) ? "0" : start_index);
            put("limit", Objects.isNull(limit) ? "10" : limit.toString());
        }};
        String result = ScriptUtils.sendGet(requestUrl, params, botConfig.getHeader());
        return JSON.parseObject(result, RoleMembers.class);
    }

    /**
     * 用于获取 guild_id 指定的频道中 user_id 对应成员的详细信息
     */
    public Member getGuildsUser(String guild_id, String user_id) {
        String requestUrl = botConfig.getPrefixApi(MessageFormat.format(guilds_user, guild_id, user_id));
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result, Member.class);
    }

    /**
     * 用于获取 guild_id 指定的频道中 user_id 对应成员的详细信息
     * 成功返回 HTTP 状态码 204
     *
     * @param add_blacklist           删除成员的同时，将该用户添加到频道黑名单中
     * @param delete_history_msg_days 删除成员的同时，撤回该成员的消息，可以指定撤回消息的时间范围(注：消息撤回时间范围仅支持固定的天数：3，7，15，30。 特殊的时间范围：-1: 撤回全部消息。默认值为0不撤回任何消息。)
     */
    public boolean delGuildsUser(String guild_id,
                                 String user_id,
                                 Boolean add_blacklist,
                                 Integer delete_history_msg_days) {
        String requestUrl = botConfig.getPrefixApi(MessageFormat.format(guilds_user, guild_id, user_id));
        Map<String, Object> botBody = new HashMap<String, Object>(2) {{
            if (Objects.nonNull(add_blacklist)) {
                put("add_blacklist", add_blacklist);
            }
            if (Objects.nonNull(delete_history_msg_days)) {
                put("delete_history_msg_days", delete_history_msg_days);
            }
        }};
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(botBody, botConfig.getHeader2()), String.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }

}
