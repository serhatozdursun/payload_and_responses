import java.io.IOException;
import java.util.Objects;

public class FileHelper {


    public String getFile(String fileName) {
        var is = Objects.requireNonNull(
                getClass()
                        .getClassLoader()
                        .getResourceAsStream("payloads/" + fileName));
        try {
            return new String(is.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(String.format("An error occurred message:%s", e.getMessage()));
        }
    }
}
