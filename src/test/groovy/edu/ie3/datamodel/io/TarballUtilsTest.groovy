/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io

import edu.ie3.datamodel.exceptions.FileException
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.io.FilenameUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

@Deprecated
class TarballUtilsTest extends Specification {
	@Shared
	Path tmpDirectory

	def setup() {
		tmpDirectory = Files.createTempDirectory("psdm_tarball utils")
	}

	def cleanup() {
		FileIOUtils.deleteRecursively(tmpDirectory)
	}

	def "The tarball utils throws an exception, if the target file does not end with '.tar.gz'"() {
		given:
		def filePath = Paths.get(getClass().getResource('/testGridFiles/grid/node_input.csv').toURI())
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "test.bli.blubb"))

		when:
		TarballUtils.compress(filePath, archiveFile)

		then:
		def ex = thrown(FileException)
		ex.message == "The target archive path has to end with '.tar.gz'. You provided: '" + tmpDirectory + "/test.bli.blubb'."
	}

	def "The tarball utils throws an exception, if the target file already exists"() {
		given:
		def filePath = Paths.get(getClass().getResource('/testGridFiles/grid/node_input.csv').toURI())
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "test.tar.gz"))
		Files.createFile(archiveFile)

		when:
		TarballUtils.compress(filePath, archiveFile)

		then:
		def ex = thrown(FileException)
		ex.message == "The target archive '" + tmpDirectory + "/test.tar.gz' already exists."
	}

	def "The tarball utils is able to zip one single file to .tar.gz"() {
		given:
		def filePath = Paths.get(getClass().getResource('/testGridFiles/grid/node_input.csv').toURI())
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "test.tar.gz"))

		when:
		TarballUtils.compress(filePath, archiveFile)

		then:
		noExceptionThrown()
		Files.exists(archiveFile)
		Files.size(archiveFile) >= 554 && Files.size(archiveFile) <= 588 // Should be around 571 bytes +/- 3 %
	}

	def "The tarball utils is able to zip the content of a folder to .tar.gz"() {
		given:
		def filePath = Paths.get(getClass().getResource('/testGridFiles/grid').toURI())
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "test.tar.gz"))

		when:
		TarballUtils.compress(filePath, archiveFile)

		then:
		noExceptionThrown()
		Files.exists(archiveFile)
		Files.size(archiveFile) >= 1330 && Files.size(archiveFile) <= 1412 // Should be around 1371 bytes +/- 3 %
	}

	def "The tarball utils is able to zip the content of a folder with nested structure to .tar.gz"() {
		given:
		def filePath = Paths.get(getClass().getResource('/testGridFiles/grid_default_hierarchy').toURI())
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "test.tar.gz"))

		when:
		TarballUtils.compress(filePath, archiveFile)

		then:
		noExceptionThrown()
		Files.exists(archiveFile)
		Files.size(archiveFile) >= 1370 && Files.size(archiveFile) <= 1454 // Should be around 1412 bytes +/- 3 %
	}

	def  "The tarball utils throws an exception, if the archive to extract, is not apparent"() {
		given:
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "noFile.tar.gz"))
		def targetDirectory = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "extract"))

		when:
		TarballUtils.extract(archiveFile, targetDirectory, false)

		then:
		def ex = thrown(FileException)
		ex.message == "There is no archive '" + tmpDirectory + "/noFile.tar.gz' apparent."
	}

	def  "The tarball utils throws an exception, if the archive to extract, is a directory"() {
		given:
		def archiveFile = tmpDirectory
		def targetDirectory = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "extract"))

		when:
		TarballUtils.extract(archiveFile, targetDirectory, false)

		then:
		def ex = thrown(FileException)
		ex.message == "Archive '" + tmpDirectory + "' is not a file."
	}

	def  "The tarball utils throws an exception, if the archive to extract, does not end on '.tar.gz'"() {
		given:
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "someFile.txt"))
		Files.createFile(archiveFile)
		def targetDirectory = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "extract"))

		when:
		TarballUtils.extract(archiveFile, targetDirectory, false)

		then:
		def ex = thrown(FileException)
		ex.message == "Archive '" + tmpDirectory + "/someFile.txt' does not end with '.tar.gz'."
	}

	def  "The tarball utils throws an exception, if the target folder already is available and overriding is deactivated"() {
		given:
		def archiveFile = Paths.get(getClass().getResource('/default_directory_hierarchy.tar.gz').toURI())
		def targetDirectory = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "extract"))
		Files.createDirectories(Paths.get(FilenameUtils.concat(FilenameUtils.concat(tmpDirectory.toString(), "extract"), "default_directory_hierarchy")))

		when:
		TarballUtils.extract(archiveFile, targetDirectory, false)

		then:
		def ex = thrown(FileException)
		ex.message == "The target path '" + tmpDirectory + "/extract/default_directory_hierarchy' already exists."
	}

	def  "The tarball utils throws an exception, if the target folder already is available, a file and overriding is activated"() {
		given:
		def archiveFile = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "default_directory_hierarchy.txt.tar.gz"))
		Files.createFile(archiveFile)
		def targetDirectory = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "extract"))
		def nestedTargetFolder = Paths.get(FilenameUtils.concat(FilenameUtils.concat(tmpDirectory.toString(), "extract"), "default_directory_hierarchy.txt"))
		Files.createDirectories(targetDirectory)
		Files.createFile(nestedTargetFolder)

		when:
		TarballUtils.extract(archiveFile, targetDirectory, true)

		then:
		def ex = thrown(FileException)
		ex.message == "You intend to extract content of '" + archiveFile + "' to '" + targetDirectory + "/default_directory_hierarchy.txt', which is a regular file."
	}

	def  "The tarball utils throws no exception, if the target folder already is available, filled and overriding is activated"() {
		given:
		def archiveFile = Paths.get(getClass().getResource('/default_directory_hierarchy.tar.gz').toURI())
		def targetDirectory = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "extract"))
		def nestedTargetFolder = Paths.get(FilenameUtils.concat(FilenameUtils.concat(tmpDirectory.toString(), "extract"), "default_directory_hierarchy"))
		Files.createDirectories(nestedTargetFolder)
		def oneFile = Paths.get(FilenameUtils.concat(nestedTargetFolder.toString(), "someFile.txt"))
		Files.createFile(oneFile)

		when:
		TarballUtils.extract(archiveFile, targetDirectory, true)

		then:
		noExceptionThrown()
	}

	def  "The tarball utils is able to extract a tarball archive correctly"() {
		given:
		def archiveFile = Paths.get(getClass().getResource('/default_directory_hierarchy.tar.gz').toURI())
		def targetDirectory = Paths.get(FilenameUtils.concat(tmpDirectory.toString(), "extract"))

		when:
		TarballUtils.extract(archiveFile, targetDirectory, false)

		then:
		noExceptionThrown()
		Files.exists(targetDirectory)
		Files.list(targetDirectory).map { it.toString() }.sorted().collect(Collectors.toList()) == [
			tmpDirectory.toString() + "/extract/default_directory_hierarchy"
		]

		def nestedTargetDirectoryPath = Paths.get(FilenameUtils.concat(targetDirectory.toString(), "default_directory_hierarchy"))
		Files.exists(nestedTargetDirectoryPath)
		Files.list(nestedTargetDirectoryPath).map { it.toString() }.sorted().collect(Collectors.toList()) == [
			tmpDirectory.toString() + "/extract/default_directory_hierarchy/grid",
			tmpDirectory.toString() + "/extract/default_directory_hierarchy/participants"
		]

		def gridPath = Paths.get(FilenameUtils.concat(nestedTargetDirectoryPath.toString(), "grid"))
		Files.exists(gridPath)
		Files.list(gridPath).map { it.toString() }.sorted().collect(Collectors.toList()) == [
			tmpDirectory.toString() + "/extract/default_directory_hierarchy/grid/line_input.csv",
			tmpDirectory.toString() + "/extract/default_directory_hierarchy/grid/measurement_unit_input.csv",
			tmpDirectory.toString() + "/extract/default_directory_hierarchy/grid/node_input.csv",
			tmpDirectory.toString() + "/extract/default_directory_hierarchy/grid/switch_input.csv",
			tmpDirectory.toString() + "/extract/default_directory_hierarchy/grid/transformer_2_w_input.csv",
			tmpDirectory.toString() + "/extract/default_directory_hierarchy/grid/transformer_3_w_input.csv"
		]

		def participantsPath = Paths.get(FilenameUtils.concat(FilenameUtils.concat(targetDirectory.toString(), "default_directory_hierarchy"), "participants"))
		Files.exists(participantsPath)
		Files.list(participantsPath).map { it.toString() }.sorted().collect(Collectors.toList()) == [
			tmpDirectory.toString() + "/extract/default_directory_hierarchy/participants/ev_input.csv"
		]
	}

	def "The zip slip protection detects malicious entries correctly"() {
		given:
		def entry = Mock(ArchiveEntry)
		entry.name >> "../../pirates/home"

		when:
		TarballUtils.zipSlipProtect(entry, tmpDirectory)

		then:
		def ex = thrown(IOException)
		ex.message == "Bad entry: ../../pirates/home"
	}
}
