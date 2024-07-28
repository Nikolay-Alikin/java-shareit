package ru.practicum.shareit.item.feign;

import ru.practicum.shareit.configuration.ShareItErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.generated.api.ItemsApi;

@FeignClient(
        name = "ItemClient",
        url = "${shareIt-server.url}",
        configuration = ShareItErrorDecoder.class
)
public interface ItemClient extends ItemsApi {

}
