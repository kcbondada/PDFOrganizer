import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

    public class PDFOrganizer {
        public static void main(String[] args) {
            String sourceFolderPath = "Write Your Path"; // Update this path

            File sourceFolder = new File(sourceFolderPath);
            if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
                System.out.println("Source folder does not exist or is not a directory.");
                return;
            }


            Pattern pattern = Pattern.compile("[_-]s(\\d+)[_.-]?");

            File[] pdfFiles = sourceFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

            if (pdfFiles == null || pdfFiles.length == 0) {
                System.out.println("No PDF files found in the folder.");
                return;
            }

            // Process files in parallel for efficiency
            Arrays.stream(pdfFiles).parallel().forEach(pdf -> {
                String fileName = pdf.getName();
                Matcher matcher = pattern.matcher(fileName);

                String targetFolderName;
                if (matcher.find()) {
                    targetFolderName = "s" + matcher.group(1);
                } else {
                    targetFolderName = "Unsorted"; // Default folder for unmatched files
                }

                File targetFolder = new File(sourceFolder, targetFolderName);
                try {
                    Files.createDirectories(targetFolder.toPath());
                    Path targetPath = new File(targetFolder, fileName).toPath();
                    Files.move(pdf.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Moved: " + fileName + " -> " + targetFolder.getAbsolutePath());
                } catch (IOException e) {
                    System.err.println("Error moving file " + fileName + ": " + e.getMessage());
                }
            });

            System.out.println("PDF files organized successfully.");
        }
    }
