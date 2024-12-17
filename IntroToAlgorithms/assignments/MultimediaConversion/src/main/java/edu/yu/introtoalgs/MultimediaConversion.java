package edu.yu.introtoalgs;

import java.util.*;

public class MultimediaConversion extends MultimediaConversionBase{
    private final String SOURCE_FORMAT;
    private HashMap<String, Set<Edge>> graph;
    private Set<String> formats;
    /**
     * Constructor: client passes the multimedia source format: the one that
     * needs to be converted to other formats
     *
     * @param sourceFormat the multimedia source format, can't be empty
     * @throws IllegalArgumentException as appropriate.
     */
    public MultimediaConversion(String sourceFormat) {
        super(sourceFormat);
        if(sourceFormat == null || sourceFormat.isEmpty()){
            throw new IllegalArgumentException("Y'know you actually have to uh give me a format -_-");
        }
        this.SOURCE_FORMAT = sourceFormat;
        this.graph = new HashMap<>();
        this.formats = new HashSet<>();
    }

    /** Add a unit of multimedia conversion information: format1 can be converted
     * to format2 (and vice versa) with the process taking the specified
     * duration.  An exception must be thrown if the client attempts to add an
     * conversion specification that has previously been added (even if the
     * duration differs from the previous specification).
     *
     * @param format1 Name of the format1 multimedia format, can't be empty
     * @param format2 Name of the format2 multimedia format, can't be empty
     * @param duration the time (in ms) required to do the format conversion,
     * can't be negative.
     * @throws IllegalArgumentException as appropriate.
     */
    @Override
    public void add(String format1, String format2, double duration) {
        if(duration < 0 || format1 == null || format1.isEmpty() || format2 == null || format2.isEmpty() || format2.equals(format1)){
            throw new IllegalArgumentException("Ya passed illegal arguments you silly silly boi");
        }
        graph.putIfAbsent(format1, new HashSet<>());
        graph.putIfAbsent(format2, new HashSet<>());
        Set<Edge> l1 = graph.get(format1);
        Set<Edge> l2 = graph.get(format2);
        Edge e1 = new Edge(format1, format2, duration);
        if(((l1.contains(e1)))){ //might be able to cut off one of these conversion but im not certain
            throw new IllegalArgumentException("Already have that conversion");
        }
        l1.add(e1);
        l2.add(e1);
        formats.add(e1.dst);
        formats.add(e1.src);
        //this some disgusting code.
    }
    /** Convert the source format into as many as the specified output formats as
     * possible.  The rules for the conversion are specified in the requirements
     * document.
     *
     * @param outputFormats one or more output formats, each of which must have
     * been been specified as a format in a previously invoked add() invocation.
     * The source format cannot be one of the specified output formats, nor can
     * the outputFormats contain duplicate formats.
     * @return a mapping of each of the specified output formats to the minimal
     * * duration required to convert the source format to the output format.  If
     * * the source format cannot be converted to an output format, associate the
     * * output format with Double.NaN.
     * NOTE: the conversion process can be done through one or more intemediary
     * conversion formats.
     * @throws IllegalArgumentException if the preconditions are violated.
     */
    @Override
    public Map<String, Double> convert(String... outputFormats) {
        Set<String> checkset = new HashSet<>();
        for(String s:outputFormats){
            if(!formats.contains(s)){
                throw new IllegalArgumentException();
            } else {
                checkset.add(s);
            }
        }
        if(checkset.size() < outputFormats.length || checkset.contains(SOURCE_FORMAT)){
            throw new IllegalArgumentException("dupes or source");
        }
        //dont need to find everything, right? but is it faster to start over again and again?
        HashMap<String, Integer> depthLevels = new HashMap<>();
        HashMap<String, Double> timeToReach = new HashMap<>();
        //bascially. do bfs, marking the depth of each node. note the cost of the path to each node.
        // then perform dikestra or whatver, stopping if we either dont go forward and the path is therefore longer.
        this.bfs(timeToReach, depthLevels);
        //ok so now i have to do dikestra or whatver and possibly change the paths. it can be short circuted tho,
        // as once the depth of the node we are at is below the depth of the destination note we can just stop lol
        HashMap<String, Double> toReturn = new HashMap<>();
        for(String s:outputFormats){
            if(timeToReach.get(s) == null){
                toReturn.put(s,Double.NaN);
            } else {
                toReturn.put(s,timeToReach.get(s));
            }
        }
        return toReturn;
    }

    private void bfs(Map<String, Double> time, Map<String, Integer> depth){
        HashSet<String> markedBois = new HashSet<>();
        Queue<String> kwayway = new ArrayDeque<>();
        kwayway.add(SOURCE_FORMAT);
        markedBois.add(SOURCE_FORMAT);
        String curVert;
        String newVert;
        time.put(SOURCE_FORMAT, 0.0);
        depth.put(SOURCE_FORMAT, 0);
        //dont add source to DT cuz uh not necessary
        while(!kwayway.isEmpty()){
            curVert = kwayway.poll();
            for(Edge e : graph.get(curVert)){
                newVert = e.one().equals(curVert) ? e.other() : e.one();
                if(!markedBois.contains(newVert)){
                    kwayway.add(newVert);
                    markedBois.add(newVert);
                    time.put(newVert, time.get(curVert) + e.getWeight());
                    depth.put(newVert, depth.get(curVert) + 1);
                } else {
                    if(/*same depth*/(depth.get(curVert) + 1 == depth.get(newVert)) && (time.get(newVert) > time.get(curVert) + e.getWeight())){
                        time.put(newVert, time.get(curVert) + e.getWeight());
                    }
                }
            }

        }
        time.remove(SOURCE_FORMAT);
        depth.remove(SOURCE_FORMAT);
    }


    private class Edge{
       private  String src;
        private String dst;
        private double weight;

        private Edge(String src,String dst, double w){
            this.src = src;
            this.dst = dst;
            this.weight = w;
        }
        private double getWeight(){
            return this.weight;
        }
        private String one(){
            return this.src;
        }
        private String other(){
            return dst;
        }


        @Override
        public boolean equals(Object o){
            return (this.one().equals(((Edge) o).one()) || this.one().equals(((Edge) o).other())) && (this.other().equals(((Edge) o).one()) || this.other().equals(((Edge) o).other()));
        }
        @Override
        public int hashCode(){
            return Objects.hash(src,dst);
        }
    }

}
