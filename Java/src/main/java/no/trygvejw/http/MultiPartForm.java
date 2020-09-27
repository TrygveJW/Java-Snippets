package no.trygvejw.http;


import java.io.*;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

public class MultiPartForm {

    // ---- fields ---- //
    private final Vector<FormPart> formParts = new Vector<>();

    private final UUID boundary = UUID.randomUUID();

    // ---- accessors ---- //
    // ---- constructors ---- //
    // ---- public ---- //

    public String getContentTypeHeader(){
        return "multipart/form-data; boundary=" + boundary;
    }

    public HttpRequest.BodyPublisher getPublisher(){
        if (formParts.size() == 0){
            throw new IllegalStateException("Must have at least one part in the multi part form");
        }
        formParts.add(new lastBoundery());
        return HttpRequest.BodyPublishers.ofByteArrays(bytesIterator::new);

    }

    public MultiPartForm addPart(String name, String value){
        formParts.add(new StringPart(value, name));
        return this;
    }

    public MultiPartForm addPart(String name, File value){
        formParts.add(new FilePart(value, name));
        return this;
    }

    public MultiPartForm addPart(String name, String filename, String mimetype, InputStream stream){
        formParts.add(new StreamPart(name,filename, mimetype,stream));
        return this;
    }



    // ---- private ---- //






    private abstract class FormPart{
        String name;
        String mimeType;
        String valueStr = "";


        protected String dispositionParams;
        protected String typeParams;

        public FormPart(String name, String mimeType) {
            this.name = name;
            this.mimeType = mimeType;
        }

        protected String getFormString(){
            String formStr =  "--" + boundary + "\r\n" +
                    String.format("Content-Disposition: form-data; name=\"%s\"; %s\r\n", name, dispositionParams) +
                    String.format("Content-Type: %s; %s \r\n\r\n", mimeType, typeParams);

            if (!this.valueStr.isEmpty()){
                formStr += valueStr + "\r\n";
            }

            return formStr;
        }



    }

    private interface StreamablePart{
        InputStream getInputStreamStream();
    }

    private class lastBoundery extends FormPart{
        public lastBoundery() {
            super("", "");
        }

        @Override
        protected String getFormString() {
            return "--" + boundary + "--";
        }
    }

    private class StringPart extends FormPart{

        public StringPart(String value, String name) {
            super(name, "text/plain");

            this.dispositionParams = "";
            this.typeParams = " charset=UTF-8";
            valueStr = value;
        }
    }


    private class FilePart extends FormPart implements StreamablePart{
        private File value;

        public FilePart(File value, String name) {
            super(name, "application/octet-stream");
            this.value = value;
            this.dispositionParams = "filename=\"" + value.getName() + "\"";
            this.typeParams = " charset=UTF-8";
        }



        @Override
        public InputStream getInputStreamStream() {
            try{
                return new FileInputStream(value);
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    private class StreamPart extends FormPart implements StreamablePart{
        InputStream value;

        public StreamPart(String name, String fileName, String mimetype, InputStream stream) {
            super(name, mimetype);
            this.value = stream;
            this.dispositionParams = "filename=\"" + fileName + "\"";
            this.typeParams = " charset=UTF-8";
        }

        @Override
        public InputStream getInputStreamStream() {
            return value;
        }
    }

    private class bytesIterator implements Iterator<byte[]> {
        private Iterator<FormPart> iterator = formParts.iterator();

        private InputStream activeStream = null;


        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return iterator.hasNext() || activeStream != null;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public byte[] next() {
            if (!hasNext()) throw new NoSuchElementException("next called after last element given");

            if (activeStream == null){
                FormPart part = iterator.next();
                String stringPart = part.getFormString();

                if(part instanceof StreamablePart){
                    this.activeStream = ((StreamablePart) part).getInputStreamStream();
                }
                return stringPart.getBytes(StandardCharsets.UTF_8);
            } else {
                byte[] buffer = new byte[4096];
                try {
                    int bytesRead = activeStream.read(buffer);
                    if (bytesRead > 0){
                        return Arrays.copyOf(buffer, bytesRead);
                    } else {
                      activeStream.close();
                      activeStream = null;
                      return "\n\r".getBytes(StandardCharsets.UTF_8);
                    }
                } catch (IOException e){
                    throw new UncheckedIOException(e);
                }
            }
        }
    }
}
