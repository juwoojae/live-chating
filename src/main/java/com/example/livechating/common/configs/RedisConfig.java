package com.example.livechating.common.configs;


import com.example.livechating.chat.service.RedisPubSubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    // 연결 기본 객체
    @Bean
    @Qualifier("chatPubSub")
    public RedisConnectionFactory chatPubSubFactory() {

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        //    redis pub/sub 에서는 특정 데이터 베이스에 의존적이지 않음.
        //    configuration.setDatabase(0); //10 개의 데이터 베이스중 선택

        return new LettuceConnectionFactory(host, Integer.valueOf(port)); //레디스 연결객체 만들기
    }
    // publish 객체
    // String 형태의 publish
    @Bean
    @Qualifier("chatPubSub")
    public StringRedisTemplate stringRedisTemplate(
            @Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory){//위에서 만든 connectionFactory 를 사용하겠다.
        //일반적으로는  RedisTemplate<key 데이터 타입, value 데이터 타입> 을 사용 하지만 우리는 메세지를 주고받는게 목적이므로 StringRedisTemplate 사용
        return new StringRedisTemplate(redisConnectionFactory);
    }

    // subscribe 객체
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory,
            MessageListenerAdapter messageListenerAdapter
            ){
                RedisMessageListenerContainer container = new RedisMessageListenerContainer();
                container.setConnectionFactory(redisConnectionFactory);
                container.addMessageListener(messageListenerAdapter, new PatternTopic("chat")); //chat 이란 패턴으로 메세지가 들어오면 메세지를 아래의 메서드로 처리하겠다.
                return container;
            }

    //  redis 에서 수신된 메세지를 처리하는 객체 생성
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService){
        //      redisPubSubService 의 특정 메서드가 수신된 메시지를 처리할수 있도록 지정
        return new MessageListenerAdapter(redisPubSubService, "onMessage");
    }
}

