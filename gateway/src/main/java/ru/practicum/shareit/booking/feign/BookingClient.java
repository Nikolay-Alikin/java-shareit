package ru.practicum.shareit.booking.feign;

import ru.practicum.shareit.configuration.ShareItErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.generated.api.BookingsApi;

@FeignClient(
        name = "BookingClient",
        url = "${shareIt-server.url}",
        configuration = ShareItErrorDecoder.class
)
public interface BookingClient extends BookingsApi {

}
