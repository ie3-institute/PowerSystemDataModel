/*package edu.ie3.datamodel.io

import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.datamodel.io.source.csv.CsvGraphicSource
import edu.ie3.datamodel.io.source.csv.CsvGraphicSourceTest
import edu.ie3.datamodel.io.source.csv.CsvJointGridContainerSource
import edu.ie3.datamodel.io.source.csv.CsvRawGridSource
import edu.ie3.datamodel.io.source.csv.CsvTestDataMeta
import edu.ie3.datamodel.io.source.csv.CsvTypeSource
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import spock.lang.Specification

import java.nio.file.Path
import java.security.MessageDigest
import org.apache.commons.io.FileUtils

class GridIoIT extends Specification implements CsvTestDataMeta {

    def setup(){
        println "setup in progress..."
    }

    def cleanup(){
        println "cleanup in progress..."

    }

    def "Imported files into a JointGridContainer get exported in the correct way."(){

        given:
        // create joint grid container
        def gridname = new String("vn_simona")
        def seperator = new String(",")
        def folderpath = new String(jointGridFolderPath)
        def firstgridContainer = CsvJointGridContainerSource.read(gridname, seperator, folderpath)
        def secondgridContainer = CsvJointGridContainerSource.read(gridname, seperator, folderpath)

        println("joint grid container created")

        // input
        def inDirectory = new File(folderpath)
        def inHashCodes = [:]
        println("input")

        // output
        def outFolderpath = new String("./exampleGridOut")
        def sink = new CsvFileSink(outFolderpath)
        def outDirectory = new File(outFolderpath)
        def outHashCodes = [:]
        println("output")


        // list and number of filenames
        def filenames = []
        def filesCount = 0
        def checkUpCount = 0
        println("lists")

        when:
        String file = "src/test/resources/edu/ie3/datamodel/io/source/csv/_joint_grid/line_input.csv"
        List<String> hashcodes = new ArrayList<String>()
        BufferedReader br = new BufferedReader(new FileReader(file))
        String line
        String test = "Hallo"
        println("br setup")

        while ((line = br.readLine()) != null) {
            String values = line.split(",")
            println test.hashCode()
            println "NEW LINE!!!"
        }
        println("br loop")

        // Read original grid from input directory and generate hashcodes
        inDirectory.eachFile {
            inHashCodes.putAt(it.name, generateMD5(it))
        }
        println("inHashCodes generated")

        // write files in output directory
        sink.persistJointGrid(firstgridContainer)

        // read files from output folder and generate hashcodes
        outDirectory.eachFile {
            outHashCodes.putAt(it.name, generateMD5(it))
            filenames.add(it.name)
            filesCount++ // geht auch über filenames
        }
        println("outHashCodes generated")

        // delete files in output directory
        outDirectory.eachFile {
            it.deleteOnExit()
        }

        then:

        firstgridContainer.getSystemParticipants().equals(second)
        //SPA, RawGrid eleemnts, ...

        /*while(checkUpCount < filesCount) {
            print(inHashCodes.get(filenames.get(checkUpCount)))
            print(" = ")
            println(outHashCodes.get(filenames.get(checkUpCount)))
            inHashCodes.get(filenames.get(checkUpCount)) == outHashCodes.get(filenames.get(checkUpCount))
            checkUpCount++
        }

        inHashCodes.keySet().each{assert inHashCodes.get(it) == outHashCodes.get(it)}

    }



    def csvreader(final file) {
        List<String> hashcodes = new ArrayList<String>()
        BufferedReader br = new BufferedReader(new FileReader(file))
        String line
        String delimiter = ","

        while ((line = br.readLine()) != null) {
            String values = line.split(delimiter)
            println "line"
        }
    }


    List<List<String>> records = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader("book.csv"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(COMMA_DELIMITER);
            records.add(Arrays.asList(values));
        }
    }



    def generateMD5(final file) {
        //StringBuilder strb = new StringBuilder()
        //strb.append()
        MessageDigest digest = MessageDigest.getInstance("MD5")
        file.withInputStream() { is ->
            byte[] buffer = new byte[8192]
            int read = 0
            while( (read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] md5sum = digest.digest()
        BigInteger bigInt = new BigInteger(1, md5sum)

        return bigInt.toString(16).padLeft(32, '0')
    }
}
*/