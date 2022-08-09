package edu.ie3.datamodel.io

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

    def "Input Files equal Output Files." (){

        given:
        // create joint grid container
        def gridname = new String("vn_simona")
        def seperator = new String(",")
        def folderpath = new String(jointGridFolderPath)
        def firstgridContainer = CsvJointGridContainerSource.read(gridname, seperator, folderpath)
        def secondgridContainer = CsvJointGridContainerSource.read(gridname, seperator, folderpath)

        // input
        def inDirectory = new File(folderpath)
        def inHashCodes = [:]

        // output
        def outFolderpath = new String("./exampleGridOut")
        def sink = new CsvFileSink(outFolderpath)
        def outDirectory = new File(outFolderpath)
        def outHashCodes = [:]

        // list and number of filenames
        def filenames = []
        def filesCount = 0
        def checkUpCount = 0

        when:
        // Read original grid from input directory and generate hashcodes
        inDirectory.eachFile {
            inHashCodes.putAt(it.name, csvreader(it))
        }
        //println(inHashCodes)

        // write files in output directory
        sink.persistJointGrid(firstgridContainer)

        // read files from output folder and generate hashcodes
        outDirectory.eachFile {
            outHashCodes.putAt(it.name, csvreader(it))
            filenames.add(it.name)
            filesCount++ // geht auch über filenames
        }

        // delete files in output directory
        outDirectory.eachFile {
            it.deleteOnExit()
        }

        then:
        inHashCodes.keySet().each{assert inHashCodes.get(it) == outHashCodes.get(it)}

        //firstgridContainer.getSystemParticipants().equals(second)
        println firstgridContainer.hashCode()
        println secondgridContainer.hashCode()
        //SPA, RawGrid eleemnts, ...
    }

    def "Input JointGridContainer equals Output JointGridContainer."(){

        given:
        // create joint grid container
        def gridname = new String("vn_simona")
        def seperator = new String(",")
        def folderpath = new String(jointGridFolderPath)
        def firstGridContainer = CsvJointGridContainerSource.read(gridname, seperator, folderpath)

        // output
        def outFolderpath = new String("./exampleGridOut")
        def sink = new CsvFileSink(outFolderpath)
        def outDirectory = new File(outFolderpath)

        when:
        // write files in output directory
        sink.persistJointGrid(firstGridContainer)
        // an welcher Stelle wird cp type gekürzt?
        // sonst doubles mit toleranz vergleichen

        // create second grid container
        def secondGridContainer = CsvJointGridContainerSource.read(gridname, seperator, outFolderpath)

        // delete files in output directory
        /*outDirectory.eachFile {
            it.deleteOnExit()
        }*/

        then:
        /*
        println firstGridContainer.getSystemParticipants().hashCode()
        println secondGridContainer.getSystemParticipants().hashCode()
        println firstGridContainer.getRawGrid().hashCode()
        println secondGridContainer.getRawGrid().hashCode()
        */
        println("Grid Name: " + firstGridContainer.getGridName().equals(secondGridContainer.getGridName()))
        println("RawGrid: " + firstGridContainer.getRawGrid().equals(secondGridContainer.getRawGrid()))
        println("System Participants: " + firstGridContainer.getSystemParticipants().equals(secondGridContainer.getSystemParticipants()))
        println("Graphics: " + firstGridContainer.getGraphics().equals(secondGridContainer.getGraphics()))

        println("System Participants - Fixed Feed Ins: " + firstGridContainer.getSystemParticipants().getFixedFeedIns().equals(secondGridContainer.getSystemParticipants().getFixedFeedIns()))
        println("System Participants - BM Plants: " + firstGridContainer.getSystemParticipants().getBmPlants().equals(secondGridContainer.getSystemParticipants().getBmPlants()))
        println("System Participants - PV Plants: " + firstGridContainer.getSystemParticipants().getPvPlants().equals(secondGridContainer.getSystemParticipants().getPvPlants()))
        println("System Participants - Loads: " + firstGridContainer.getSystemParticipants().getLoads().equals(secondGridContainer.getSystemParticipants().getLoads()))
        println("System Participants - EvCS: " + firstGridContainer.getSystemParticipants().getEvCS().equals(secondGridContainer.getSystemParticipants().getEvCS()))
        println("System Participants - Storages: " + firstGridContainer.getSystemParticipants().getStorages().equals(secondGridContainer.getSystemParticipants().getStorages()))

        println("System Participants - wec Plants: " + firstGridContainer.getSystemParticipants().getWecPlants().equals(secondGridContainer.getSystemParticipants().getWecPlants()))
        //println("System Participants - Properties: " + firstGridContainer.getSystemParticipants().getProperties().equals(secondGridContainer.getSystemParticipants().getProperties()))
        //println("System Participants - Meta Property Values: " + firstGridContainer.getSystemParticipants().getMetaPropertyValues().equals(secondGridContainer.getSystemParticipants().getMetaPropertyValues()))
    }


    def csvreader(final file) {
        List<String> hashcodes = new ArrayList<String>()
        BufferedReader br = new BufferedReader(new FileReader(file))
        String line
        println("br setup")

        while ((line = br.readLine()) != null) {
            String values = line.split(",")
            hashcodes.add(values.hashCode())
        }
        println("br loop done")

        return hashcodes;
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
