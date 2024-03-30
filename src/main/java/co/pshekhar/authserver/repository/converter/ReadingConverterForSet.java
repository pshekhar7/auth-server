package co.pshekhar.authserver.repository.converter;

import co.pshekhar.authserver.util.Utilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Set;

@ReadingConverter
public class ReadingConverterForSet implements Converter<String, Set<String>> {
    private static final Logger log = LoggerFactory.getLogger(ReadingConverterForSet.class);

    @Override
    public Set<String> convert(String source) {
        try {
            return Utilities.objectMapper().readValue(source, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Error occurred while deserializing String to Map: {}", source, e);
        }
        return null;
    }
}
