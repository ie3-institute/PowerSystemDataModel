/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import edu.ie3.datamodel.exceptions.FileException;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to compress and decompress .tar.gz archives. I is mainly inspired by
 * https://mkyong.com/java/how-to-create-tar-gz-in-java/
 */
public class Zipper {
  private static final Logger logger = LoggerFactory.getLogger(Zipper.class);

  private Zipper() {
    throw new IllegalStateException("This is an Utility Class and not meant to be instantiated");
  }

  public static void compress(Path source, Path archiveFile) throws FileException {
    Path validatedArchiveFile = checkAndCreateArchive(archiveFile);

    /* Open a stream and add content to the archive */
    try (BufferedOutputStream bufferedOutputStream =
            new BufferedOutputStream(Files.newOutputStream(validatedArchiveFile));
        GzipCompressorOutputStream gzipOutputStream =
            new GzipCompressorOutputStream(bufferedOutputStream);
        TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(gzipOutputStream)) {
      Files.walkFileTree(
          source,
          new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              /* Skip symbolic link */
              if (attrs.isSymbolicLink()) return FileVisitResult.CONTINUE;

              /* Copy files to archive */
              Path relFile = source.relativize(file);
              TarArchiveEntry entry = new TarArchiveEntry(file.toFile(), relFile.toString());
              tarOutputStream.putArchiveEntry(entry);
              Files.copy(file, tarOutputStream);
              tarOutputStream.closeArchiveEntry();

              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
              logger.error(
                  "Unable to add '{}' to '{}'. Stopping compression.",
                  file,
                  validatedArchiveFile,
                  exc);
              return FileVisitResult.TERMINATE;
            }
          });

      /* Close everything properly */
      tarOutputStream.finish();
    } catch (IOException e) {
      throw new FileException("Unable to write to '" + validatedArchiveFile + "'.", e);
    }
  }

  /**
   * Checks, if the file path actually ends with '.tar.gz' and that it is not apparent, yet.
   * Finally, it creates the file.
   *
   * @param archivePath Foreseen file path of the file
   * @return Path to the actually created file
   * @throws FileException I the ending is wrong, or the archive already existed
   */
  private static Path checkAndCreateArchive(Path archivePath) throws FileException {
    if (!archivePath.toString().endsWith(".tar.gz"))
      throw new FileException(
          "The target archive path has to end with '.tar.gz'. You provided: '"
              + archivePath
              + "'.");
    if (Files.exists(archivePath))
      throw new FileException("The target archive '" + archivePath + "' already exists.");
    if (Files.notExists(archivePath)) {
      try {
        Files.createFile(archivePath);
      } catch (IOException e) {
        throw new FileException("Cannot create file '" + archivePath + "'.", e);
      }
    }
    return archivePath;
  }
}
