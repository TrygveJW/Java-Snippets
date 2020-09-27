package no.trygvejw.sockets.todo.protocoll;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class Segment {

    private HashMap<String,String> metaValues = new HashMap<>();
    private BiConsumer<BufferedInputStream, HashMap<String, String>> segmentConsumer;

    public Segment(BiConsumer<BufferedInputStream, HashMap<String, String>> segmentConsumer){
        this.segmentConsumer = segmentConsumer;
        metaValues.put(DEFAULT_KEYS.MIME.name(), null); // mimetype
        metaValues.put(DEFAULT_KEYS.SIZE.name(), null); // bytesize
    }


    public Segment addMetaValue(String valueName){
        metaValues.put(valueName,null);
        return this;
    }


    public boolean parseMeta(JSONObject jsonObject){
        boolean suc = true;

        for (String key: metaValues.keySet()){
            if (jsonObject.has(key)){
                metaValues.put(key, jsonObject.getString(key));
            } else {
                suc = false;
                break;
            }
        }
        return suc;
    }

    public boolean readSegment(BufferedInputStream inputStream){
        if (segmentConsumer != null){
            segmentConsumer.accept(inputStream, metaValues);
        }

        return true; // tror æ hadd en plan med den her men ikke helt sikker på hva
    }

    public static enum DEFAULT_KEYS{
        MIME,
        SIZE
    }

}
