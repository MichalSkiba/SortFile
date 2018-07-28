import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException {

        String[] jar = new String[]{"jar"};
        String[] xml = new String[]{"xml"};
        String[] all = new String[]{"xml", "jar"};

        File dev = new File("Dev");
        File test = new File("Test");
        File home = new File("Home");

        while (true) {

            Collection<File> allFile = FileUtils.listFiles(home, all, false);
            Collection<File> jarFile = FileUtils.listFiles(home, jar, false);
            Collection<File> xmlFile = FileUtils.listFiles(home, xml, false);
            LocalTime time = LocalTime.now();

            if (!(allFile.isEmpty())) {
                if (!jarFile.isEmpty()) {
                    for (File file : jarFile) {
                        if ((new Date(file.lastModified()).getHours()) % 2 == 0) {
                            CopyFile(dev, time, file);
                        } else {
                            try {
                                FileUtils.moveFileToDirectory(file, test, false);
                                copyTekst( "Dodano " + file.getName() +" do Test ", time);
                            } catch (FileExistsException e) {
                                copyTekst("Plik " + file.getName() + "  już istnieje w folderze Test ", time);
                                FileUtils.forceDelete(file);
                            }
                        }
                    }
                } else {
                    for (File file : xmlFile) {
                        CopyFile(dev, time, file);
                    }
                }
            }
        }
    }

    private static void CopyFile(File dev, LocalTime time, File file) throws IOException {
        try {
            FileUtils.moveFileToDirectory(file, dev, false);
            copyTekst("Dodano " + file.getName() + " do DEV ", time);
        } catch (FileExistsException e) {
            copyTekst("Plik " + file.getName() + " już istnieje w Folderze DEV ", time);
            FileUtils.forceDelete(file);
        }
    }

    private static void copyTekst(String data, LocalTime time) {
        try {
            File file = new File("Home/count.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file, true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(data + time);
            bufferWritter.newLine();
            bufferWritter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}