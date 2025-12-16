package generation;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

public class FileGenerator {

    public void generateFile(String fileName, String content, String outputDir) {
        try {
            File directory = new File(outputDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName + ".java");

            try (PrintWriter writer = new PrintWriter(file)) {
                writer.print(content);
            }

            System.out.println("Fichier généré : " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier " + fileName);
            e.printStackTrace();
        }
    }
}