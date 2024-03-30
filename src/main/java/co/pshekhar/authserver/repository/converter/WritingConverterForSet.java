package co.pshekhar.authserver.repository.converter;

import co.pshekhar.authserver.util.Utilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Set;

@WritingConverter
public class WritingConverterForSet implements Converter<Set<String>, String> {
    private static final Logger log = LoggerFactory.getLogger(WritingConverterForSet.class);

    @Override
    public String convert(@NotNull Set<String> source) {
        try {
            return Utilities.objectMapper().writeValueAsString(source);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while serializing Map to String: {}", source, e);
        }
        return null;
    }
}
