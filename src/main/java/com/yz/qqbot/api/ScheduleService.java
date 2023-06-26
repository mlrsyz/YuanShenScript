package com.yz.qqbot.api;

import com.alibaba.fastjson.JSONArray;
import com.yz.qqbot.domain.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 日程API
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 22:17 <br/>
 */
@Slf4j
@Service
public class ScheduleService extends BaseService {
    //GET /channels/{channel_id}/schedules 获取频道日程列表
    String schedulesUrl(String channel_id) {
        return MessageFormat.format("/channels/{0}/schedules", channel_id);
    }

    //GET /channels/{channel_id}/schedules/{schedule_id}  获取日程详情
    String schedulesDetail(String channel_id, String schedule_id) {
        return MessageFormat.format("/channels/{0}/schedules/{1}", channel_id, schedule_id);
    }

    /**
     * 用于获取channel_id指定的子频道中当天的日程列表。
     * 若带了参数 since，则返回在 since 对应当天的日程列表；若未带参数 since，则默认返回今天的日程列表
     *
     * @param channel_id 子频道Id
     * @param since      起始时间戳(ms) 不传则默认当前时间
     */
    public List<Schedule> schedulesList(String channel_id, Long since) {
        String requestUrl = botConfig.getPrefixApi(schedulesUrl(channel_id));
        String response = restTemplate.getForObject(requestUrl, String.class, new HttpEntity<>(new HashMap<String, Long>(1) {{
            put("since", Objects.nonNull(since) ? since : System.currentTimeMillis());
        }}, botConfig.getHeader2()));
        return JSONArray.parseArray(response, Schedule.class);
    }

    /**
     * 获取日程子频道 channel_id 下 schedule_id 指定的的日程的详情
     */
    public Schedule getSchedulesDetail(String channel_id, String schedule_id) {
        String requestUrl = botConfig.getPrefixApi(schedulesDetail(channel_id, schedule_id));
        return restTemplate.getForObject(requestUrl, Schedule.class, new HttpEntity<>(null, botConfig.getHeader2()));
    }


    /**
     * 用于在 channel_id 指定的日程子频道下创建一个日程。
     * 要求操作人具有管理频道的权限，如果是机器人，则需要将机器人设置为管理员。
     * 创建成功后，返回创建成功的日程对象。
     * 创建操作频次限制
     * 单个管理员每天限10次。
     * 单个频道每天100次
     */
    public Schedule createSchedules(String channel_id, Schedule createSchedule) {
        String requestUrl = botConfig.getPrefixApi(schedulesUrl(channel_id));
        return restTemplate.postForObject(requestUrl, new HttpEntity<>(createSchedule, botConfig.getHeader2()), Schedule.class);
    }

    /**
     * 用于修改日程子频道 channel_id 下 schedule_id 指定的日程的详情。
     * 要求操作人具有管理频道的权限，如果是机器人，则需要将机器人设置为管理员
     */
    public Schedule updateSchedules(String channel_id, String schedule_id, Schedule updateSchedule) {
        String requestUrl = botConfig.getPrefixApi(schedulesDetail(channel_id, schedule_id));
        ResponseEntity<Schedule> response = restTemplate.exchange(requestUrl, HttpMethod.PATCH, new HttpEntity<>(updateSchedule, botConfig.getHeader2()), Schedule.class);
        return response.getBody();
    }

    /**
     * 用于删除日程子频道 channel_id 下 schedule_id 指定的日程。
     * 要求操作人具有管理频道的权限，如果是机器人，则需要将机器人设置为管理员
     */
    public boolean delSchedules(String channel_id, String schedule_id) {
        String requestUrl = botConfig.getPrefixApi(schedulesDetail(channel_id, schedule_id));
        ResponseEntity<Void> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(null, botConfig.getHeader2()), Void.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }
}
