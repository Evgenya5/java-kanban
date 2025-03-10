package api;

import com.google.gson.TypeAdapter;
import java.time.Duration;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        jsonWriter.value(duration.toMinutes());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
    }
}
