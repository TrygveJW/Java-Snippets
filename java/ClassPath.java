package no.ntnu.util;

import java.io.File;


/**
 * represents a class path
 */
public class ClassPath {
    private File filePath;

    // TODO: this is a potential weak point
    private final File srcRoot = new File("/home/trygve/dev/projects/PredatorPreySimulation/src/main/java").getAbsoluteFile();


    public ClassPath(File filePath) {
        this.filePath = filePath;
    }

    public String asDotFormat(){
        String strPath = this.filePath.getPath();
        strPath = strPath.replace(srcRoot.getPath(), "");
        strPath = strPath.substring(1, strPath.length() - (".java".length()));

        strPath = strPath.replace("/", ".");
        strPath = strPath.replace("\\", "."); // sadly i have to support windows
        return strPath;

    }

}
