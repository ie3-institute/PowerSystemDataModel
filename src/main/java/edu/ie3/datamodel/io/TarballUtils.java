/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import edu.ie3.datamodel.exceptions.FileException;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to compress and decompress .tar.gz archives.
 *
 * @deprecated replaced by linked class FileIOUtils.
 * @see <a
 *     href="https://github.com/ie3-institute/PowerSystemUtils/blob/master/src/main/java/edu/ie3/util/io/FileIOUtils.java">FileIOUtils</a>
 */
@Deprecated
public class TarballUtils {
  private static final Logger logger = LoggerFactory.getLogger(TarballUtils.class);

  private static final int MAX_AMOUNT_OF_ENTRIES = 5000;
  private static final long MAX_SIZE_UNCOMPRESSED = 0x280000000L; // 10 GB
  private static final double MAX_COMPRESSION_RATIO = 0.75;

  private TarballUtils() {
    throw new IllegalStateException("This is an Utility Class and not meant to be instantiated");
  }

  /**
   * Compresses files to .tar.gz format
   *
   * @param source source path to compress
   * @param archiveFile path of the target archive file
   * @throws FileException If unable to create the file
   * @deprecated replaced by #compress(String, String, boolean) in linked class FileIOUtils. The
   *     file paths are now passed as Strings and there is a need to mention whether a directory has
   *     to be compressed or a single file.
   * @see <a
   *     href="https://github.com/ie3-institute/PowerSystemUtils/blob/master/src/main/java/edu/ie3/util/io/FileIOUtils.java">FileIOUtils</a>
   */
  @Deprecated
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

  /**
   * Extracts the given archive to a sub-directory with the same name that the archive has beneath
   * the target directory. You may toggle, if already existing files should be overridden, otherwise
   * a {@link FileException} is thrown.
   *
   * @param archive Compressed tarball archive to extract
   * @param target Path to the target folder
   * @param override true, if already existing files may be overridden.
   * @return Path to the actual folder, where the content is extracted to
   * @throws FileException If the archive is not in a well shape, the target folder doesn't meet the
   *     requirements or the archive tries to impose harm by exploiting zip slip vulnerability
   * @deprecated replaced by #extract(Path, Path) in linked class FileIOUtils. The override
   *     parameter is invalid now.
   * @see <a
   *     href="https://github.com/ie3-institute/PowerSystemUtils/blob/master/src/main/java/edu/ie3/util/io/FileIOUtils.java">FileIOUtils</a>
   */
  @Deprecated
  public static Path extract(Path archive, Path target, boolean override) throws FileException {
    /* Pre-flight checks and assembly of the target path */
    Path targetDirectory = determineTargetDirectory(archive, target, override);

    /* Get the archive file size */
    long archiveSize = archive.toFile().length();

    /* Create the target folder */
    try {
      Files.createDirectories(targetDirectory);
    } catch (IOException e) {
      throw new FileException("Cannot create target directory '" + targetDirectory + "'.", e);
    }

    /* Monitor amount of entries and their size for safety reasons */
    int entries = 0;
    long size = 0;
    try (InputStream fileInputStream = Files.newInputStream(archive);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        GzipCompressorInputStream gzipInputStream =
            new GzipCompressorInputStream(bufferedInputStream);
        TarArchiveInputStream tarInputStream = new TarArchiveInputStream(gzipInputStream)) {
      ArchiveEntry archiveEntry;
      while ((archiveEntry = tarInputStream.getNextEntry()) != null) {
        /* Control the total amount of entries */
        entries++;
        if (entries > MAX_AMOUNT_OF_ENTRIES)
          throw new IOException(
              "The archive contains too many entries and is therefore possibly malicious.");

        /* Control the size of extracted archive files */
        long uncompressedSize = archiveEntry.getSize();
        if (uncompressedSize == ArchiveEntry.SIZE_UNKNOWN)
          throw new IOException(
              "Unknown uncompressed file size of '" + archiveEntry.getName() + "'");
        size += uncompressedSize;
        if (size > MAX_SIZE_UNCOMPRESSED)
          throw new IOException(
              "Uncompressed size of archive exceeds permissible "
                  + (MAX_SIZE_UNCOMPRESSED / 1024 / 1024)
                  + " MB. Possibly malicious archive");

        /* Control the compression ratio */
        if (1 - (double) archiveSize / size > MAX_COMPRESSION_RATIO)
          throw new IOException(
              "Compression ratio exceeds its maximum permissible value "
                  + (MAX_COMPRESSION_RATIO * 100)
                  + " %. Possibly malicious archive");

        handleZipEntrySafely(archiveEntry, targetDirectory, tarInputStream);
      }
    } catch (IOException ex) {
      throw new FileException("Unable to extract from '" + archive + "'.", ex);
    }

    return targetDirectory;
  }

  /**
   * Runs some pre-flight checks and assembles the target directory
   *
   * @param archive Compressed tarball archive to extract
   * @param target Path to the target folder
   * @param override true, if already existing files may be overridden.
   * @return Path to the folder, where the content is meant to be extracted to
   * @throws FileException If the pre-flight checks fail
   */
  private static Path determineTargetDirectory(Path archive, Path target, boolean override)
      throws FileException {
    /* Pre-flight checks */
    if (Files.notExists(archive))
      throw new FileException("There is no archive '" + archive + "' apparent.");
    if (!Files.isRegularFile(archive))
      throw new FileException("Archive '" + archive + "' is not a file.");
    if (!archive.toString().endsWith(".tar.gz"))
      throw new FileException("Archive '" + archive + "' does not end with '.tar.gz'.");

    /* Determine the file name */
    String fileName = archive.getFileName().toString().replaceAll("\\.tar\\.gz$", "");
    Path targetDirectory = Paths.get(FilenameUtils.concat(target.toString(), fileName));

    /* Some more pre-flight checks */
    if (Files.exists(targetDirectory)) {
      if (override && Files.isRegularFile(targetDirectory))
        throw new FileException(
            "You intend to extract content of '"
                + archive
                + "' to '"
                + targetDirectory
                + "', which is a regular file.");
      else if (override) logger.debug("Deleting apparent content of '{}}'.", targetDirectory);
      else throw new FileException("The target path '" + targetDirectory + "' already exists.");
    }

    return targetDirectory;
  }

  /**
   * Handles the "copying" of the zip entry's content to actual files on the hard drive.
   *
   * @param archiveEntry Entry to be treated
   * @param targetDirectory Path to the target folder
   * @param tarInputStream Input stream
   * @throws IOException Whenever something toes not work
   */
  private static void handleZipEntrySafely(
      ArchiveEntry archiveEntry, Path targetDirectory, TarArchiveInputStream tarInputStream)
      throws IOException {
    /* Check against zip slip vulnerability and return normalized path w.r.t. the target path */
    Path targetEntryPath = zipSlipProtect(archiveEntry, targetDirectory);

    if (archiveEntry.isDirectory()) {
      Files.createDirectories(targetEntryPath);
    } else {
      /* Check, if parent folder is apparent, otherwise create it */
      Path parentDirectoryPath = targetEntryPath.getParent();
      if (parentDirectoryPath != null && Files.notExists(parentDirectoryPath)) {
        Files.createDirectories(parentDirectoryPath);
      }

      /* Copy content to new path */
      Files.copy(tarInputStream, targetEntryPath, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /**
   * Offers protection against zip slip vulnerability, by making sure, that the target entry still
   * contains the target directory. If everything is fine, the normalized path (w.r.t. the target
   * path) is handed back.
   *
   * @see <a href="https://snyk.io/research/zip-slip-vulnerability">Snyk.io vulnerability
   *     description</a>
   * @see <a href="https://mkyong.com/java/how-to-create-tar-gz-in-java/">MyKong Tutorial</a>
   * @param entry Entry to be extracted
   * @param targetDir Path to the target directory
   * @return Normalized path w.r.t. the target path
   * @throws IOException If the entry may impose zip slip danger
   */
  private static Path zipSlipProtect(ArchiveEntry entry, Path targetDir) throws IOException {
    Path targetDirResolved = targetDir.resolve(entry.getName());
    Path normalizePath = targetDirResolved.normalize();

    if (!normalizePath.startsWith(targetDir)) {
      throw new IOException("Bad entry: " + entry.getName());
    }

    return normalizePath;
  }
}
