import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Load_CSV_To_The_Filter {

    public static CuckooFilter loadFilterFromCsv(String filePath, int bucketSize, int bucketCount, int maxKicks) throws IOException {
        CuckooFilter cuckooFilter = new CuckooFilter(bucketSize, bucketCount, maxKicks);

        List<String> lines = Files.readAllLines(Paths.get(filePath),  StandardCharsets.UTF_8); // Changed charset to UTF-8
        // Skip the first line (header) by starting from index 1
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",");
            // Assuming the first column in the CSV is the ISBN and it's properly formatted as a String
            String isbn = parts[0].replace("\"", "").trim(); // Removing potential quotes and trimming whitespace
            cuckooFilter.add(isbn);
        }

        return cuckooFilter;
    }
}