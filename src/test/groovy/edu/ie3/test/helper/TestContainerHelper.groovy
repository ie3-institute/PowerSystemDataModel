package edu.ie3.test.helper

import org.testcontainers.utility.MountableFile

import java.nio.file.Path
import java.nio.file.Paths

trait TestContainerHelper {

    /**
     * Retrieve resource with the class' resource loader.
     * In contrast to {@link org.testcontainers.utility.MountableFile#forClasspathResource(java.lang.String, java.lang.Integer)},
     * this also works with paths relative to the current class (i.e. without leading '/').
     * @param resource the resource directory or file path
     * @return a MountableFile to use with test containers
     */
    MountableFile getMountableFile(String resource) {
        URL url = getClass().getResource(resource)
        if (url == null) {
            throw new ResourceException("Resource '" + resource + "' was not found from " + getClass().toString())
        }
        Path path = Paths.get(url.toURI())

        return MountableFile.forHostPath(path)
    }
}
