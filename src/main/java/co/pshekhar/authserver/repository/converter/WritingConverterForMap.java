package co.pshekhar.authserver.repository.converter;

import co.pshekhar.authserver.util.Utilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

@WritingConverter
public class WritingConverterForMap implements Converter<Map<String, Object>, String> {
    private static final Logger log = LoggerFactory.getLogger(WritingConverterForMap.class);

    @Override
    public String convert(@NotNull Map<String, Object> source) {
        try {
            return Utilities.objectMapper().writeValueAsString(source);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while serializing Map to String: {}", source, e);
        }
        return null;
    }
}
