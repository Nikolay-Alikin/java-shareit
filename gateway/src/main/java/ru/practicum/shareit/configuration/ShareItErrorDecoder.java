package ru.practicum.shareit.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.exception.Error;
import ru.practicum.shareit.exception.ServerException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShareItErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String s, Response response) {
        if (response.body() == null) {
            return new ServerException("Сервер вернул пустой ответ", response.status());
        }
        try {
            Error badRequestException = objectMapper.readValue(response.body().asInputStream(),
                    Error.class);
            return new ServerException(badRequestException.error(), response.status());
        } catch (Exception e) {
            throw new ServerException("Ошибка", response.status());
        }
    }
}
