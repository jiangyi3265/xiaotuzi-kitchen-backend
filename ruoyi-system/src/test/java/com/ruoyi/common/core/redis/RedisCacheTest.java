package com.ruoyi.common.core.redis;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

class RedisCacheTest
{
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    void luaSerializerKeepsStoredValuesCompatibleButWritesNumericTtlAsAscii()
    {
        RedisTemplate template = mock(RedisTemplate.class);
        RedisSerializer<Object> valueSerializer = mock(RedisSerializer.class);
        when(template.getValueSerializer()).thenReturn(valueSerializer);
        when(valueSerializer.serialize("PROCESSING:owner"))
                .thenReturn("\"PROCESSING:owner\"".getBytes(StandardCharsets.UTF_8));

        RedisCache cache = new RedisCache();
        cache.redisTemplate = template;
        RedisSerializer<Object> serializer = cache.scriptArgumentSerializer();

        assertArrayEquals("\"PROCESSING:owner\"".getBytes(StandardCharsets.UTF_8),
                serializer.serialize("PROCESSING:owner"));
        assertArrayEquals("15".getBytes(StandardCharsets.UTF_8), serializer.serialize(15L));
    }
}
