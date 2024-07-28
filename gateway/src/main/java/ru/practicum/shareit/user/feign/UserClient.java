package ru.practicum.shareit.user.feign;

import ru.practicum.shareit.configuration.ShareItErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.generated.api.UsersApi;

@FeignClient(
        name = "UserClient",
        url = "${shareIt-server.url}",
        configuration = ShareItErrorDecoder.class
)
public interface UserClient extends UsersApi {

}
