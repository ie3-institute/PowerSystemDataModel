package edu.ie3.datamodel.io

import edu.ie3.datamodel.io.csv.DefaultDirectoryHierarchy
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.datamodel.models.result.system.LoadResult
import spock.lang.Specification

class FileNamingStrategyTest extends Specification {

    def "The FileNamingStrategy ..."() {
        given:
        def ens = new EntityPersistenceNamingStrategy("prefix", "suffix")
        def dns = new DefaultDirectoryHierarchy("/foo/test/", "test_grid")
        def fns = new FileNamingStrategy(ens, dns, ".TXT")

        when:
        def filePath = fns.getFilePath(modelClass)

        then:
        filePath.present
        filePath.get() == expectedString

        where:
        modelClass               || expectedString
        LoadResult               || "test_grid" + File.separator + "results" + File.separator + "participants" + File.separator + "prefix_load_res_suffix" + ".TXT"
    }

    def "The FileNamingStrategy ... flat"() {
        given:
        def ens = new EntityPersistenceNamingStrategy("prefix", "suffix")
        def fns = new FileNamingStrategy(ens)

        when:
        def filePath = fns.getFilePath(LoadResult)

        then:
        filePath.present
        filePath.get() == expectedString

        where:
        modelClass               || expectedString
        LoadResult               || "prefix_load_res_suffix" + ".csv"
    }


}