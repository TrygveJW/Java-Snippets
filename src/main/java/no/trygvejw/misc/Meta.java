package no.trygvejw.misc;

import java.io.*;
import java.util.*;

/**
 * metacoding
 *
 */
public class Meta {

    /**
     * Recursively searches through the specified directory and its sub directory
     * @param startNode the root directory to searches form
     * @return returns a array with ClassPath objects
     */
    public static ClassPath[] probeForClassPath(File startNode){
        // thread safing the list, because we are only adding this isnt strictly neccecery
        List<ClassPath> retunlist = (Collections.synchronizedList(new ArrayList<>()));
        if (startNode.isDirectory()){
            Meta.recursiveProbe(startNode, retunlist);
        }

        return retunlist.toArray(new ClassPath[0]);
    }


    private static void recursiveProbe(File startNode, List<ClassPath> returnList) {
        ArrayList<Thread> scanThreads = new ArrayList<>();
        ArrayList<File> toScan = new ArrayList<>();


        for (String path: startNode.list()) {
            File tmpFile = new File(startNode.getPath() + "/" + path);

            if (path.contains(".java")){
                toScan.add(tmpFile);
            } else if(tmpFile.isDirectory()){
                scanThreads.add(new Thread(() -> Meta.recursiveProbe(tmpFile, returnList)));
            }
        }

        scanThreads.forEach(tr -> tr.start());
        toScan.forEach((scanfile) -> {
            if (Meta.scanFile(scanfile)){
                returnList.add(new ClassPath(scanfile));
            }
        });

        scanThreads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        });

    }

    private static boolean scanFile(File file) {
        boolean ret = false;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ret = reader.lines().anyMatch(line -> line.contains("public static void main("));
            if (ret){System.out.println("Found Main at " + file.toString());}

        } catch (IOException e){
            e.printStackTrace();
        }
        return ret;
    }


}
