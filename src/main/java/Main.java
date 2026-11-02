import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

/** Moves XML into Dev and JAR files into Dev/Test based on last-modified local hour parity. */
public final class Main {
    private Main() {}

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean once = args.length == 1 && "--once".equals(args[0]);
        if (args.length > 0 && !once) {
            System.err.println("Usage: java Main [--once]");
            System.exit(2);
        }
        Path root = Path.of(".").toAbsolutePath().normalize();
        Path home = root.resolve("Home");
        Path dev = root.resolve("Dev");
        Path test = root.resolve("Test");
        if (!Files.isDirectory(home)) {
            throw new IOException("Source directory does not exist: " + home);
        }
        Files.createDirectories(dev);
        Files.createDirectories(test);
        do {
            process(home, dev, test);
            if (!once) {
                Thread.sleep(1000);
            }
        } while (!once);
    }

    private static void process(Path home, Path dev, Path test) throws IOException {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(home)) {
            for (Path file : files) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
                Path destination;
                if (name.endsWith(".xml")) {
                    destination = dev;
                } else if (name.endsWith(".jar")) {
                    int hour = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(Files.getLastModifiedTime(file).toMillis()),
                            ZoneId.systemDefault()).getHour();
                    destination = (hour % 2 == 0) ? dev : test;
                } else {
                    continue;
                }
                Path target = destination.resolve(file.getFileName());
                if (Files.exists(target)) {
                    // Never delete a source file on a name collision.
                    continue;
                }
                try {
                    Files.move(file, target);
                    log(home.resolve("count.txt"), "Moved " + file.getFileName() + " to "
                            + destination.getFileName());
                } catch (FileAlreadyExistsException ignored) {
                    // A concurrent process created the destination; leave source intact.
                } catch (IOException ex) {
                    log(home.resolve("count.txt"), "Failed to move " + file.getFileName()
                            + ": " + ex.getMessage());
                }
            }
        }
    }

    private static void log(Path output, String message) throws IOException {
        Files.writeString(output, LocalDateTime.now() + " " + message + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
