package com.yz.qqbot.api;

import com.yz.qqbot.domain.AudioControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 23:28 <br/>
 */
@Slf4j
@Service
public class AudioService extends BaseService {
    //POST /channels/{channel_id}/audio
    String audioControl(String channel_id) {
        return MessageFormat.format("/channels/{0}/audio", channel_id);
    }

    //DELETE /channels/{channel_id}/mic 机器人上/下麦
    String micControl(String channel_id) {
        return MessageFormat.format("/channels/{0}/mic", channel_id);
    }

    /**
     * 用于控制子频道 channel_id 下的音频。
     * 音频接口：仅限音频类机器人才能使用，后续会根据机器人类型自动开通接口权限，现如需调用，需联系平台申请权限
     */
    public boolean audioControl(String channel_id, AudioControl audioControl) {
        String requestUrl = botConfig.getPrefixApi(audioControl(channel_id));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(audioControl, botConfig.getHeader2()), Object.class);
        return HttpStatus.OK.equals(response.getStatusCode());
    }

    /**
     * 机器人在 channel_id 对应的语音子频道上麦。
     * 音频接口：仅限音频类机器人才能使用，后续会根据机器人类型自动开通接口权限，现如需调用，需联系平台申请权限
     */
    public boolean micUp(String channel_id) {
        String requestUrl = botConfig.getPrefixApi(micControl(channel_id));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.PUT, new HttpEntity<>(null, botConfig.getHeader2()), Object.class);
        return HttpStatus.OK.equals(response.getStatusCode());
    }

    /**
     * 机器人在 channel_id 对应的语音子频道下麦。
     * 音频接口：仅限音频类机器人才能使用，后续会根据机器人类型自动开通接口权限，现如需调用，需联系平台申请权限
     */
    public boolean micDown(String channel_id) {
        String requestUrl = botConfig.getPrefixApi(micControl(channel_id));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(null, botConfig.getHeader2()), Object.class);
        return HttpStatus.OK.equals(response.getStatusCode());
    }
}
