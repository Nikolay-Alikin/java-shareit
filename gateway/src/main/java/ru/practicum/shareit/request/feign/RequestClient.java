package ru.practicum.shareit.request.feign;

import ru.practicum.shareit.configuration.ShareItErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.generated.api.RequestsApi;

@FeignClient(
        name = "RequestClient",
        url = "${shareIt-server.url}",
        configuration = ShareItErrorDecoder.class
)
public interface RequestClient extends RequestsApi {

}
