package no.trygvejw.sockets.todo.protocoll.request;


import no.trygvejw.sockets.todo.protocoll.Segment;
import no.trygvejw.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SendThumbnailRequest extends AbstractRequest {

    private String fileName = "";

    public SendThumbnailRequest(FractalRequestMeta requestMeta) {
        super(requestMeta);
    }

    @Override
    public void doAction() {
        System.out.println(fileName); // vil da ver verdien fra som trilla inn fra get segments

    }

    @Override
    protected ArrayList<Segment> getSegments() {
        ArrayList<Segment> segments = new ArrayList<>();

        segments.add(new Segment((inputStream, segment_meta) -> {
            String fileName = segment_meta.get(metaKeys.IM_NAME.name());
            int fileSize = Integer.parseInt(segment_meta.get(Segment.DEFAULT_KEYS.SIZE.name()));

            this.fileName = fileName;
            File writefile = new File(fileName);
            try {
                StreamUtils.readImageToFile(inputStream, fileSize, writefile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).addMetaValue(metaKeys.IM_NAME.name()));

        segments.add(new Segment((inputStream, stringStringHashMap) -> {
            // json parsing elns //
        }).addMetaValue("meta param 1")
        .addMetaValue("metaparam 2 osv.."));

        return segments;
    }

    private static enum metaKeys{
        IM_NAME,
        META_PARAM
    }
}
