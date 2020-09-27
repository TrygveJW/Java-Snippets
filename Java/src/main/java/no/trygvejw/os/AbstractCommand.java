package no.trygvejw.os;


import no.fractal.debugLogger.DebugLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Wrapper of the process builder with som parameters
 */
public abstract class AbstractCommand {

    private final DebugLogger dbl = new DebugLogger(true);

    private boolean dumpIO = false;

    private File outputFile = null;
    private File errorFile = null;

    private boolean keepOutput = false;
    private boolean keepError = false;

    private BiConsumer<Process, Throwable> onComplete;

    private boolean isBlocking = false;

    private int timeoutSeconds = -1;
    private Consumer<Process> onTimeout = null;

    /**
     * Sets the timeout in seconds for the process. defult is -1 any second value < 0 means no timeout
     *
     * @param timeoutSeconds the timeout in seconds for the process
     */
    public void setTimeout(int timeoutSeconds){

        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Sets the method to run if the processes run time exceeds the defined limit
     *
     * @param onTimeout the consumer to run the prosess object is supplied
     */
    public void setOnTimeout(Consumer<Process> onTimeout){
        this.onTimeout = onTimeout;
    }

    /**
     * Sets the output file for the standard out for the command.
     *
     * @param outputFile The file to log to.
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Sets the output file for the standard error for the command.
     *
     * @param errorFile The file to log the error to.
     */
    public void setErrorFile(File errorFile) {
        this.errorFile = errorFile;
    }

    /**
     * Sets whether or not the standard out will be kept.
     * By default the standard out wil be redirected to /dev/null if no logfile is set
     *
     * @param keepOutput whether or not the standard out will be kept.
     */
    public void setKeepOutput(boolean keepOutput) {
        this.keepOutput = keepOutput;
    }

    /**
     * Sets whether or not the standard error will be kept.
     * By default the standard error wil be redirected to /dev/null if no logfile is set
     *
     * @param keepError whether or not the standard error will be kept.
     */
    public void setKeepError(boolean keepError) {
        this.keepError = keepError;
    }

    /**
     * Sets whether or not inherit the commands io.
     *
     * @param dumpIO whether or not inherit the commands io.
     */
    public void setDumpIO(boolean dumpIO) {
        this.dumpIO = dumpIO;
    }


    /**
     * Sets the Bi consumer to be run when the command is complete.
     *
     * @param onComplete The Bi consumer to be run when the command is complete.
     */
    public void setOnComplete(BiConsumer<Process, Throwable> onComplete) {
        this.onComplete = onComplete;
    }

    /**
     * Sets if the command should block the current thread until it is complete.
     *
     * @param blocking If the command should block the current thread until it is complete.
     */
    public void setBlocking(boolean blocking) {
        isBlocking = blocking;
    }


    /**
     * Starts the built command and returns the command's process.
     *
     * @return the process of the command.
     */
    public Process run() {
        ArrayList<String> commandParts = new ArrayList<>();

        commandParts.add("/bin/bash");
        commandParts.add("-c");

        commandParts.add(String.join(" ", this.buildCommand()));

        ProcessBuilder builder = new ProcessBuilder(commandParts);

        dbl.log("running command: ", String.join(" ", commandParts));

        // it seems that if the io is not drained the process can block or even dedlock
        if (this.outputFile != null) {
            builder.redirectOutput(ProcessBuilder.Redirect.appendTo(outputFile));
        } else if (!keepOutput) {
            builder.redirectOutput(new File("/dev/null"));
        }


        if (this.errorFile != null) {
            builder.redirectError(ProcessBuilder.Redirect.appendTo(errorFile));
        } else if (!keepError) {
            builder.redirectError(new File("/dev/null"));
        }

        if (dumpIO) {
            builder.inheritIO();
        }



        Process process = null;
        try {
            process = builder.start();


            if (this.timeoutSeconds > 0){
                Process processCopy = process;
                Thread t = new Thread(() -> {
                    try {
                        if (!processCopy.waitFor(this.timeoutSeconds, TimeUnit.SECONDS)){
                            // if the timeout is reached before the process is complete
                            processCopy.destroy();
                            if(this.onTimeout != null){
                                onTimeout.accept(processCopy);
                            }

                            // give the process 5 sec to shut down if not complete kill it forcefully
                            Thread.sleep(5000);
                            if(processCopy.isAlive()){
                                processCopy.destroyForcibly();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                t.start();
            }

            if (this.onComplete != null) {
                process.onExit().whenComplete(onComplete);
            }

            if (this.isBlocking) {
                process.waitFor();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){} // this means the process was interrupted likely due to a timeout

        return process;
    }


    /**
     * Builds the docker command and returns a list of the command parts.
     *
     * @return A list of all the command parts for the command to run.
     */
    protected abstract ArrayList<String> buildCommand();
}
