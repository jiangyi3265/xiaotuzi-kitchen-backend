package com.ruoyi.framework.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

class ApplicationConfigTest
{
    @Test
    void serializesBusinessTimesInChinaStandardTime() throws Exception
    {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        new ApplicationConfig().jacksonObjectMapperCustomization().customize(builder);
        ObjectMapper mapper = builder.build();

        TimestampHolder value = new TimestampHolder(Date.from(Instant.parse("2026-07-17T07:32:20Z")));
        JsonNode json = mapper.readTree(mapper.writeValueAsString(value));

        assertEquals("2026-07-17 15:32:20", json.get("createTime").asText());
    }

    private static class TimestampHolder
    {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private final Date createTime;

        TimestampHolder(Date createTime)
        {
            this.createTime = createTime;
        }

        public Date getCreateTime()
        {
            return createTime;
        }
    }
}
