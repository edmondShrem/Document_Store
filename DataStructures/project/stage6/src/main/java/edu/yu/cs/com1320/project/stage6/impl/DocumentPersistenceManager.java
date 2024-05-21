package edu.yu.cs.com1320.project.stage6.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.PersistenceManager;
import jakarta.xml.bind.DatatypeConverter;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

//import com.google.gson.GsonBuilder;
//to be actually implemented later :P
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    //gotta make a constructor????????
    @Override
    public void serialize(URI uri, Document val) throws IOException {
        JsonSerializer<Document> fred = new JsonSerializer<Document>() {
            @Override
            public JsonElement serialize(Document src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject result = new JsonObject();
                if(src.getDocumentTxt() != null) {
                    result.add("Contents (txt)", new JsonPrimitive(src.getDocumentTxt()));
                } else {
                    String base64 = DatatypeConverter.printBase64Binary(src.getDocumentBinaryData());
                    result.add("Contents (bin)", new JsonPrimitive(base64));
                }
                Map<String, String> m = src.getMetadata();
                Set<String> h = m.keySet();
                int i = 1;
                for(String s : h){
                    result.add("meta data pair #" + i, new JsonPrimitive(s + "-" + m.get(s)));
                    i++;
                }
                result.add("URI", new JsonPrimitive(src.getKey().toString()));
                Map<String, Integer> wordMap = src.getWordMap();
                Set<String> words = wordMap.keySet();
                i=1;
                for(String s : words){
                    result.add("word pair #" + i, new JsonPrimitive(s + "-" + wordMap.get(s)));
                    i++;
                }

                return result;
            }
        };
        JsonElement j = fred.serialize(val, Document.class, null);
        Gson g = new Gson();
        String path = uri.toString();
        if(path.startsWith("http://")){
            path = path.substring(7);
        } else if (path.startsWith("https://")){
            path = path.substring(8);
        }
        File f = new File(path + ".json");
        f.getParentFile().mkdirs();
        if(f.createNewFile()) {
            FileWriter write = new FileWriter(f);
            g.toJson(j, write);
            write.close();
            FileWriter bob = new FileWriter(f);
            bob.write(j.toString());
            bob.close();
        }
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        JsonDeserializer<Document> nick = new JsonDeserializer<Document>() {
            @Override
            public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jObject = json.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entries = jObject.entrySet();
                String txt = null;
                byte[] b = null;
                HashMap<String, String> meta = new HashMap<>();
                URI uri = null;
                HashMap<String, Integer> wordMap = new HashMap<>();
                for(Map.Entry<String, JsonElement> e : entries){
                    if(e.getKey().equals("Contents (txt)")){
                        txt = e.getValue().getAsString();
                    } else if(e.getKey().equals("Contents (bin)")){
                        DatatypeConverter.parseBase64Binary(e.getValue().getAsString());
                        b = DatatypeConverter.parseBase64Binary(e.getValue().getAsString());
                    }else if (e.getKey().equals("URI")){
                        try {
                            uri = new URI(e.getValue().getAsString());
                        } catch (URISyntaxException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else if (e.getKey().startsWith("meta")){
                        String key = e.getValue().getAsString().substring(0, e.getValue().getAsString().indexOf("-"));
                        String val = e.getValue().getAsString().substring(e.getValue().getAsString().indexOf("-") + 1);
                        meta.put(key,val);
                    } else if (e.getKey().startsWith("word")){
                        String key = e.getValue().getAsString().substring(0, e.getValue().getAsString().indexOf("-"));
                        String val = e.getValue().getAsString().substring(e.getValue().getAsString().indexOf("-") + 1);
                        wordMap.put(key,Integer.parseInt(val));
                    }
                }
                Document d;
                //now construct based on the ol' txt != null
                if(txt != null){
                    d = new DocumentImpl(uri, txt, wordMap);
                } else {
                    d = new DocumentImpl(uri, b);
                }
                if(!meta.isEmpty()){
                    d.setMetadata(meta);
                }
                return d;
            }
        };
        String path = uri.toString();
        if(path.startsWith("http://")){
            path = path.substring(7);
        } else if (path.startsWith("https://")){
            path = path.substring(8);
        }
        File f = new File(path + ".json");
        FileReader reader = new FileReader(f);
        JsonElement babyJ = JsonParser.parseReader(reader);
        reader.close();
        return nick.deserialize(babyJ, Document.class, null);
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        String path = uri.toString();
        if(path.startsWith("http://")){
            path = path.substring(7);
        } else if (path.startsWith("https://")){
            path = path.substring(8);
        }
        File f = new File(path + ".json");
        boolean deleted = f.delete();
        if(deleted){
            while(f.getParent() != null) {
                f = new File(f.getParent());
                f.delete();
            }
        }
        return deleted;
    }
}
