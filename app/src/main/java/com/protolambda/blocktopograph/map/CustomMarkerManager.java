package com.protolambda.blocktopograph.map;

import com.protolambda.blocktopograph.Log;
import com.protolambda.blocktopograph.map.marker.AbstractMarker;
import com.protolambda.blocktopograph.map.marker.CustomBlockMarker;
import com.protolambda.blocktopograph.map.marker.CustomEntityMarker;
import com.protolambda.blocktopograph.map.marker.CustomIconMarker;
import com.protolambda.blocktopograph.map.marker.CustomTileEntityMarker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO docs
 *
 * [CustomMarker]-manager, not the other way around
 */
public class CustomMarkerManager {


    public final File markerFile;

    // g0: Whole match
    // g1: Format version (int)
    // g2: Marker display name
    // g3: Marker icon name
    // g4: X coordinate
    // g5: Y coordinate
    // g6: Z coordinate
    // g7: Dimension name
    //
    // example:
    // 1 "Test marker" "default _ma 1 rker 1" 1234 64 4321 overworld ;
    public static final Pattern formatRegex = Pattern.compile("(\\d+)\\s+\"(.+?)\"\\s+\"(.+?)\"\\s+(.+?)\\s+(.+?)\\s+(.+?)\\s+(.+?)\\s*;");

    public CustomMarkerManager(File markerFile){
        this.markerFile = markerFile;
    }

    public ArrayList< AbstractMarker> markers = new ArrayList<>();

    public void load(){
        markers = new ArrayList<>();
        if(!this.markerFile.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(this.markerFile));
            String line;

            while ((line = br.readLine()) != null) {
                if(line.length() < 6) continue;

                try {
                    int format = Integer.parseInt(line.split(" ")[0]);
                    switch (format) {
                        //add versions here if it changes
                        default: {
                            Matcher matcher = formatRegex.matcher(line);
                            if(matcher.find()) {
                                String markerDisplayName = matcher.group(2).replace("\"", "");
                                String iconName = matcher.group(3).replace("\"", "");
                                int x = Integer.parseInt(matcher.group(4));
                                int y = Integer.parseInt(matcher.group(5));
                                int z = Integer.parseInt(matcher.group(6));
                                String dimensionName = matcher.group(7).replace("\"", "");
                                Dimension dimension = Dimension.getDimension(dimensionName);
                                if(dimension == null) dimension = Dimension.OVERWORLD;
                                markers.add(markerFromData(markerDisplayName, iconName, x, y, z, dimension));
                                
                            }
                        }
                    }
                } catch (Exception e){
                    //ok, probably a comment or something, just ignore
                    Log.d("Invalid line in marker file: "+line);
                }
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public AbstractMarker markerFromData(String displayName, String iconName, int x, int y, int z, Dimension dimension){
        Block b = Block.getByDataName(iconName);
        if(b != null && b.bitmap != null) return new CustomBlockMarker(x, y, z, dimension, displayName, b);

        Entity e = Entity.getEntity(iconName);
        if(e != null && e.bitmap != null) return new CustomEntityMarker(x, y, z, dimension, displayName, e);

        TileEntity te = TileEntity.getTileEntity(iconName);
        if(te != null && te.getBitmap() != null) return new CustomTileEntityMarker(x, y, z, dimension, displayName, te);

        CustomIcon icon = CustomIcon.getCustomIcon(iconName);
        if(icon == null) icon = CustomIcon.DEFAULT_MARKER;
        return new CustomIconMarker(x, y, z, dimension, displayName, icon);

    }

    public void addMarker(AbstractMarker marker){
        if(markers == null) load();

        try {

            if(markerFile.createNewFile()) Log.d("Created "+this.markerFile.getAbsolutePath());

            //append to file
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.markerFile, true)));
            out.println();
            //format with some nice padding
            out.println("1 \""+marker.displayName+"\" \""+marker.iconName+"\" "+marker.x+" "+marker.y+" "+marker.z + " " + marker.dimension.dataName + " ;");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Iterable<AbstractMarker> getMarkers(){
        return markers;
    }


}
