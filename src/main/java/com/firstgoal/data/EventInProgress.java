package com.firstgoal.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@RedisHash(value = "EventInProgress", timeToLive = 15800)
@Builder
public class EventInProgress {
    @Id private String eventUrl;
    private ArrayList<String> competitions;
}
