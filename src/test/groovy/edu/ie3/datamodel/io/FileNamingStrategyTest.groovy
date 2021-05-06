package edu.ie3.datamodel.io

import edu.ie3.datamodel.io.csv.DefaultDirectoryHierarchy
import edu.ie3.datamodel.io.csv.FlatDirectoryHierarchy
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.datamodel.models.result.system.LoadResult
import spock.lang.Specification

import java.nio.file.Paths

class FileNamingStrategyTest extends Specification {

    def "The FileNamingStrategy ..."() {
        given:
        def ens = new EntityPersistenceNamingStrategy()
        def dns = new DefaultDirectoryHierarchy("/foo/test/", "testGrid")
        def fns = new FileNamingStrategy(ens, dns, ".csv")
        def path = Paths.get("/bla/foo/")

        when:
        def filePath = fns.getFilePath(LoadResult)

        then:
        filePath == "/bla/foo/xyz.csv"
    }

    def "The FileNamingStrategy ... flat"() {
        given:
        def ens = new EntityPersistenceNamingStrategy()
        def fns = new FileNamingStrategy(ens)

        when:
        def filePath = fns.getFilePath(LoadResult)

        then:
        filePath == "/bla/foo/xyz.csv"
    }


}