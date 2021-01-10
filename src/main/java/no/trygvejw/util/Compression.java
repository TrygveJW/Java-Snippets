package no.trygvejw.util;

import no.trygvejw.debugLogger.DebugLogger;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Predicate;
import java.util.zip.*;


/**
 * Utillety class for compressing and decompressing files and dirs
 */
public class Compression {

    private static final DebugLogger dbl = new DebugLogger(false);

    /*
        TODO: clean up

        TODO: probably shold have some tests here




     */






    /**
     * Zips the contents of a dir at the provided path to the provided out file at the provided compression level.
     *
     * @param dirPath          The path to the dir to compress
     * @param outPath          the path to the compressed out file
     * @param compressionLevel the level of compression
     * @throws FileNotFoundException If the input file does not exists, or is a file
     * @throws ZipException          If there is an error zipping the zip file
     * @throws IOException           if an io error has occurred
     */
    public static void zipContents(File dirPath, File outPath, int compressionLevel) throws IOException {
        if (dirPath.isDirectory()) {
            throw new FileNotFoundException("Zip contents target not a dir");
        }

        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outPath));
        zipOutputStream.setLevel(compressionLevel);
        zipDirectory(dirPath, outPath.getName(), zipOutputStream, true);
        zipOutputStream.close();
    }

    /**
     * GunZips the dir at the provided path to a .zip.gz file. zip with no compression is used for packaging because tar is not natively supported
     *
     * @param dirPath The path to the dir to compress
     * @throws FileNotFoundException If the input file does not exists or is an directory.
     * @throws ZipException          If there is an error zipping the zip file
     * @throws IOException           if an io error has occurred
     */
    public static void gZipContents(File dirPath, File outPath) throws IOException {
        if (dirPath.isDirectory()) {
            throw new FileNotFoundException("Zip contents target not a dir");
        }


        File tmpZipFile = new File(FileUtils.systemTmpDir, dirPath.getName());
        Compression.zipContents(dirPath, tmpZipFile, Deflater.NO_COMPRESSION);
        Compression.gZip(tmpZipFile, outPath);
        tmpZipFile.delete();
    }


    /**
     * Zips the file/dir at the provided path to the provided out file at the provided compression level.
     *
     * @param filePath The path to the file or dir to compress
     * @return the compressed file
     * @throws FileNotFoundException If the input file does not exists
     * @throws ZipException          If there is an error zipping the zip file
     * @throws IOException           if an io error has occurred
     */
    public static File zip(File filePath) throws FileNotFoundException, ZipException, IOException {
        return zip(filePath, null, -1);
    }

    /**
     * Zips the file/dir at the provided path to the provided out file at the provided compression level.
     *
     * @param filePath         The path to the file or dir to compress
     * @param outPath          the path to the compressed out file
     * @return the compressed file
     * @throws FileNotFoundException If the input file does not exists
     * @throws ZipException          If there is an error zipping the zip file
     * @throws IOException           if an io error has occurred
     */
    public static File zip(File filePath, File outPath) throws FileNotFoundException, ZipException, IOException {
        return zip(filePath, outPath, Deflater.DEFAULT_COMPRESSION);
    }

    /**
     * Zips the file/dir at the provided path to the provided out file at the provided compression level.
     *
     * @param filePath         The path to the file or dir to compress
     * @param outPath          the path to the compressed out file
     * @param compressionLevel the level of compression
     * @return the compressed file
     * @throws FileNotFoundException If the input file does not exists
     * @throws ZipException          If there is an error zipping the zip file
     * @throws IOException           if an io error has occurred
     */
    private static File zip(File filePath, File outPath, int compressionLevel) throws FileNotFoundException, ZipException, IOException {
        outPath = (outPath == null) ? new File(filePath.getCanonicalPath() + ".zip") : outPath;


        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outPath));
        zipOutputStream.setLevel(compressionLevel);
        if (filePath.isDirectory()) {
            Compression.zipDirectory(filePath, filePath.getName(), zipOutputStream, false);

        } else if (filePath.isFile()) {
            Compression.zipFile(filePath, filePath.getName(), zipOutputStream);
        }

        zipOutputStream.close();
        return outPath;
    }


    /**
     * GunZips the file/dir at the provided path to a .zip.gz file. zip with no compression is used for packaging because tar is not natively supported
     *
     * @param filePath the path to the dir/file to compress
     * @return the path to the zipped file
     * @throws FileNotFoundException If the input file does not exists or is an directory.
     * @throws ZipException          If there is an error zipping the zip file
     * @throws IOException           if an io error has occurred
     */
    public static File gZip(File filePath) throws FileNotFoundException, ZipException, IOException {
        return gZip(filePath, null);
    }

    /**
     * GunZips the file/dir at the provided path to a .zip.gz file. zip with no compression is used for packaging because tar is not natively supported
     *
     * @param filePath the path to the dir/file to compress
     * @param outPath  the path of the out file. if null one will be generated
     * @return the path to the zipped file
     * @throws FileNotFoundException If the input file does not exists or is an directory.
     * @throws ZipException          If there is an error zipping the zip file
     * @throws IOException           if an io error has occurred
     */
    public static File gZip(File filePath, File outPath) throws FileNotFoundException, ZipException, IOException {
        if (!filePath.exists()) {
            throw new FileNotFoundException("Zip target not found");
        }
        boolean isFile = filePath.isFile();

        if (outPath == null) {
            outPath = (isFile) ? new File(filePath.getCanonicalPath() + ".gz") : new File(filePath.getCanonicalPath() + ".zip.gz");
        }


        File tmpZipFile = new File(FileUtils.systemTmpDir, filePath.getName());
        Compression.zip(filePath, tmpZipFile, Deflater.NO_COMPRESSION); // using the zip as a tar

        Compression.gzipFileCompress(tmpZipFile, outPath);
        tmpZipFile.delete();

        return outPath;
    }


    /**
     * Unzips the provided .zip, .gz or .zip.gz file to the selected directory. if the out dir does not exist one wil be created
     *
     * @param filePath the path to the file to decompress
     * @throws FileNotFoundException If the input file does not exists. if the output dir is a file
     * @throws ZipException          If there is an error zipping the zip file
     * @throws IOException           if an io error has occurred
     */
    public static void unzip(File filePath) throws FileNotFoundException, ZipException, IOException {
        unzip(filePath, null);
    }

    /**
     * Unzips the provided .zip, .gz or .zip.gz file to the selected directory. if the out dir does not exist one wil be created
     *
     * @param filePath the path to the file to decompress
     * @param outDir   the dir to decompress inn to
     * @throws FileNotFoundException If the input file does not exists. if the output dir is a file
     * @throws ZipException          If there is an error zipping the zip file
     * @throws IOException           if an io error has occurred
     */
    public static void unzip(File filePath, File outDir) throws FileNotFoundException, ZipException, IOException {
        boolean isGzip = filePath.getCanonicalPath().endsWith(".gz");
        boolean isZip = FileUtils.getFileWithOneLessEnding(filePath).getCanonicalPath().endsWith(".zip");
        if (!(isGzip || isZip)) {
            throw new IOException("Unsupported decompression file ending");
        }

        if (outDir == null) {
            outDir = (isGzip && isZip) ?
                    FileUtils.getFileWithOneLessEnding(FileUtils.getFileWithOneLessEnding(filePath)) :
                    FileUtils.getFileWithOneLessEnding(filePath);
            var suc = outDir.mkdir();
            if (!suc) {
                throw new IOException("cannot write unzipped root dir");
            }
        }


        if (isGzip && !isZip) {
            dbl.log("only gz");
            //plan .gz decompress
            File tmpFile = new File(FileUtils.systemTmpDir, FileUtils.getFileWithOneLessEnding(filePath).getName());
            gzipFileDecompress(filePath, tmpFile);
            Files.move(tmpFile.toPath(), new File(outDir, tmpFile.getName()).toPath());
        } else if (isGzip) {
            dbl.log("only zip.gz");
            // zip.gz decompress
            File tmpFile = new File(FileUtils.systemTmpDir, FileUtils.getFileWithOneLessEnding(filePath).getName());
            gzipFileDecompress(filePath, tmpFile);

            unzipZipFile(tmpFile, outDir);
            tmpFile.delete();
        } else {
            // plain .zip decompress
            unzipZipFile(filePath, outDir);
        }
    }

    // -- private


    /**
     * Recursively goes through the provided dir and zips all the files and sub directories to the provided ZipOutputStream
     *
     * @param dirPath       system path to the dir to zip
     * @param zippedDirPath the zip path to the dir to zip
     * @param stream        the ZipOutputStream to write to
     * @param onlyContents  true if the parent dir should be included false if only the contents
     * @throws ZipException If there is an error zipping the zip file
     * @throws IOException  if an io error has occurred
     */
    private static void zipDirectory(File dirPath, String zippedDirPath, ZipOutputStream stream, boolean onlyContents) throws ZipException, IOException {
        zippedDirPath = zippedDirPath.endsWith(File.separator) ? zippedDirPath : zippedDirPath + File.separator;

        if (!onlyContents) {
            stream.putNextEntry(new ZipEntry(zippedDirPath));
            stream.closeEntry();
        }


        File[] dirContents = dirPath.listFiles();

        if (dirContents != null) {
            for (File childFile : dirContents) {
                File childFp = new File(dirPath.getCanonicalPath() + File.separator + childFile.getName());
                if (childFile.isDirectory()) {
                    zipDirectory(childFp, zippedDirPath + childFile.getName() + File.separator, stream, false);
                } else {
                    zipFile(childFp, zippedDirPath + childFile.getName(), stream);
                }
            }
        }

    }





    /**
     * Creates an entry on the provided ZipOutputStream and writes the file from the provide file path to the provided zip path
     *
     * @param filePath   the path to the file on the system
     * @param zippedPath the path to the file in the zip file
     * @param stream     the ZipOutputStream to write the file to
     * @throws FileNotFoundException If the input file does not exists or is an directory.
     * @throws ZipException          If there is an error zipping the zip file
     * @throws IOException           if an io error has occurred
     */
    private static void zipFile(File filePath, String zippedPath, ZipOutputStream stream) throws FileNotFoundException, ZipException, IOException {
        FileInputStream inputStream = new FileInputStream(filePath);
        stream.putNextEntry(new ZipEntry(zippedPath));

        byte[] bytes = new byte[1024];
        int bytes_read;
        while ((bytes_read = inputStream.read(bytes)) >= 0) {
            stream.write(bytes, 0, bytes_read);
        }
        inputStream.close();
        stream.closeEntry();
    }

    /**
     * Unzips the provided zip file to the provided dir
     *
     * @param filePath the file object of the file to unzip
     * @param outDir   the directory to unzip into
     * @throws FileNotFoundException If the input file does not exists or is an directory. If the output file is not a directory
     * @throws ZipException          If there is an error unzipping the zip file
     * @throws IOException           if an io error has occurred
     */
    private static void unzipZipFile(File filePath, File outDir) throws FileNotFoundException, ZipException, IOException {
        if (!outDir.isDirectory()) {
            throw new FileNotFoundException("Unzip out dir is not a dir");
        }

        byte[] buffer = new byte[1024];

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filePath));
        ZipEntry zipEntry = zipInputStream.getNextEntry();


        while (zipEntry != null) {

            dbl.log(zipEntry);

            File entryFile = new File(outDir, zipEntry.getName());

            testForZipSlip(entryFile, outDir);

            /*
            File parentDir = entryFile.getParentFile();

            if (!parentDir.exists()){
                if (!parentDir.mkdirs()) throw new IOException("Parent dir does not exist and cant be written to");
            }
            */

            if (zipEntry.isDirectory()) {
                if (!entryFile.mkdir()) throw new IOException("cannot write unzipped dir");
            } else {
                FileOutputStream outputStream = new FileOutputStream(entryFile);

                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.close();
            }

            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
    }

    /**
     * Tests if the unzipped entry is located outside the decompress dir.
     * If this is the case someone is utilizing the Zip slip vulnerability, raise an IOException
     *
     * @param entryPath  the path of the unzipped entry
     * @param outDirPath the path of the out dir
     * @throws IOException if the entry is outside the out dir
     */
    private static void testForZipSlip(File entryPath, File outDirPath) throws IOException {
        if (!FileUtils.isFileChildOfDir(entryPath, outDirPath)) {
            throw new IOException("Entry:" +entryPath.getCanonicalPath() + " is outside of the target dir: " + outDirPath.getCanonicalPath());
        }
    }

    /**
     * Gzip compresses the provided file to the provided out file
     * If the outFile already exists it wil be overwritten TODO: test this
     *
     * @param toCompress the file to compress
     * @param outFile    the output file for the compression
     * @throws FileNotFoundException If the input file does not exists or is an directory. If the output file is a directory
     * @throws IOException           if an io error has occurred
     */
    private static void gzipFileCompress(File toCompress, File outFile) throws FileNotFoundException, IOException {
        FileInputStream inputStream = new FileInputStream(toCompress);
        FileOutputStream outputStream = new FileOutputStream(outFile);
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);

        byte[] buffer = new byte[4096];
        int bytes_read;
        while ((bytes_read = inputStream.read(buffer)) != -1) {
            gzipOutputStream.write(buffer, 0, bytes_read);
        }

        inputStream.close();
        gzipOutputStream.close();
        outputStream.close();
    }

    /**
     * Decompresses the provided gzip file to the provided out file
     * If the outFile already exists it wil be overwritten TODO: test this
     *
     * @param compressed the file to decompress
     * @param outFile    the file to decompress to
     * @throws FileNotFoundException If the input file does not exists or is an directory. If the output file is a directory
     * @throws ZipException          If there is an error unzipping the gzip file
     * @throws IOException           if an io error has occurred
     */
    private static void gzipFileDecompress(File compressed, File outFile) throws FileNotFoundException, ZipException, IOException {
        FileInputStream inputStream = new FileInputStream(compressed);
        FileOutputStream outputStream = new FileOutputStream(outFile);
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);

        byte[] buffer = new byte[4096];
        int bytes_read;
        while ((bytes_read = gzipInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytes_read);
        }

        inputStream.close();
        gzipInputStream.close();
        outputStream.close();
    }



    //////  this is sort of temporary until the whole class is rewritten it wil happen soon^tm     //////


    private static void zipDirectory(File dirPath, String zippedDirPath, ZipOutputStream stream, Predicate<File> doNotInclude) throws ZipException, IOException {
        zippedDirPath = zippedDirPath.endsWith(File.separator) ? zippedDirPath : zippedDirPath + File.separator;


        stream.putNextEntry(new ZipEntry(zippedDirPath));
        stream.closeEntry();



        File[] dirContents = dirPath.listFiles();

        if (dirContents != null) {
            for (File childFile : dirContents) {
                File childFp = new File(dirPath.getCanonicalPath() + File.separator + childFile.getName());
                if (childFile.isDirectory()) {
                    zipDirectory(childFp, zippedDirPath + childFile.getName() + File.separator, stream, doNotInclude);
                } else {
                    if (!doNotInclude.test(childFile)){
                        zipFile(childFp, zippedDirPath + childFile.getName(), stream);
                    }
                }
            }
        }

    }
    public static File gZip(File filePath, File outPath, Predicate<File> doNotInclude) throws FileNotFoundException, ZipException, IOException {
        if (!filePath.exists()) {
            throw new FileNotFoundException("Zip target not found");
        }
        boolean isFile = filePath.isFile();

        if (outPath == null) {
            outPath = (isFile) ? new File(filePath.getCanonicalPath() + ".gz") : new File(filePath.getCanonicalPath() + ".zip.gz");
        }


        File tmpZipFile = new File(FileUtils.systemTmpDir, filePath.getName());
        Compression.zip(filePath, tmpZipFile, Deflater.NO_COMPRESSION, doNotInclude); // using the zip as a tar

        Compression.gzipFileCompress(tmpZipFile, outPath);
        tmpZipFile.delete();

        return outPath;
    }

    private static File zip(File filePath, File outPath, int compressionLevel, Predicate<File> doNotInclude) throws FileNotFoundException, ZipException, IOException {
        outPath = (outPath == null) ? new File(filePath.getCanonicalPath() + ".zip") : outPath;


        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outPath));
        zipOutputStream.setLevel(compressionLevel);
        if (filePath.isDirectory()) {
            Compression.zipDirectory(filePath, filePath.getName(), zipOutputStream, doNotInclude);

        } else if (filePath.isFile()) {
            Compression.zipFile(filePath, filePath.getName(), zipOutputStream);
        }

        zipOutputStream.close();
        return outPath;
    }





}
