package com.example.instagram.feed_read.infrastructor;

import com.example.instagram.common.util.DataSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;


@Repository
@RequiredArgsConstructor
public class FeedQueryModelRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "feed-read::feedId::%s";

    public void create(FeedQueryModel feedQueryModel, Duration ttl){
        redisTemplate.opsForValue()
                .set(generateKey(feedQueryModel), DataSerializer.serialize(feedQueryModel), ttl);
    }

    public void update(FeedQueryModel feedQueryModel){
        redisTemplate.opsForValue().setIfPresent(generateKey(feedQueryModel), DataSerializer.serialize(feedQueryModel));
    }

    public void delete(Long feedId) {
        redisTemplate.delete(generateKey(feedId));
    }

    public Optional<FeedQueryModel> read(Long feedId) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(generateKey(feedId))
        ).map(json -> DataSerializer.deserialize(json, FeedQueryModel.class));
    }

    private String generateKey(FeedQueryModel feedQueryModel) {
        return generateKey(feedQueryModel.getFeedId());
    }
    private String generateKey(Long feedId) {
        return KEY_FORMAT.formatted(feedId);
    }

    public Map<Long, FeedQueryModel> readAll(List<Long> feedIds) {
        List<String> keyList = feedIds.stream()
                .map(this::generateKey)
                .toList();

        List<String> values = redisTemplate.opsForValue().multiGet(keyList);

        Map<Long, FeedQueryModel> result = new HashMap<>();
        for (int i = 0; i < feedIds.size(); i++) {
            String json = values.get(i);
            FeedQueryModel model = (json == null) ? null : DataSerializer.deserialize(json, FeedQueryModel.class);
            result.put(feedIds.get(i), model);
        }
        return result;
    }
}
