package api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    public DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            return;
        }
        jsonWriter.value(localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        String text = jsonReader.nextString();
        if (text.equals("")) {
            return null;
        }
        return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
