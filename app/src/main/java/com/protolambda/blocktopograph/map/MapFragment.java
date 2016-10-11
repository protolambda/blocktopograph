package com.protolambda.blocktopograph.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import com.protolambda.blocktopograph.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.protolambda.blocktopograph.R;
import com.protolambda.blocktopograph.World;
import com.protolambda.blocktopograph.WorldActivity;
import com.protolambda.blocktopograph.WorldActivityInterface;
import com.protolambda.blocktopograph.WorldData;
import com.protolambda.blocktopograph.map.marker.AbstractMarker;
import com.protolambda.blocktopograph.map.marker.MarkerImageView;
import com.protolambda.blocktopograph.map.marker.MarkerType;
import com.protolambda.blocktopograph.map.marker.PlayerMarker;
import com.protolambda.blocktopograph.map.marker.WorldDataMarker;
import com.protolambda.blocktopograph.map.renderer.MapType;
import com.protolambda.blocktopograph.nbt.EditableNBT;
import com.protolambda.blocktopograph.nbt.convert.DataConverter;
import com.protolambda.blocktopograph.nbt.convert.NBTConstants;
import com.protolambda.blocktopograph.nbt.tags.CompoundTag;
import com.protolambda.blocktopograph.nbt.tags.FloatTag;
import com.protolambda.blocktopograph.nbt.tags.IntTag;
import com.protolambda.blocktopograph.nbt.tags.ListTag;
import com.protolambda.blocktopograph.nbt.tags.Tag;
import com.protolambda.blocktopograph.util.ViewID;
import com.protolambda.blocktopograph.util.math.DimensionVector3;
import com.protolambda.blocktopograph.util.math.Vector3;
import com.qozix.tileview.markers.MarkerLayout;
import com.qozix.tileview.tiles.TileCanvasViewGroup;
import com.qozix.tileview.widgets.ZoomPanLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MapFragment extends Fragment {

    private WorldActivityInterface worldProvider;

    private MCTileProvider minecraftTileProvider;

    private MapFragmentTileView tileView;

    //list of markers per chunk
    public Map<Long, ArrayList<AbstractMarker>> proceduralMarkers = new HashMap<>();
    public ArrayList<AbstractMarker> customMarkers = new ArrayList<>();

    public WorldDataMarker spawnMarker;
    public PlayerMarker localPlayerMarker;




    @Override
    public void onPause() {
        super.onPause();

        //pause drawing the map
        this.tileView.pause();
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle(this.worldProvider.getWorld().getWorldDisplayName());

        worldProvider.logFirebaseEvent(WorldActivity.CustomFirebaseEvent.MAPFRAGMENT_OPEN);
    }

    @Override
    public void onResume() {
        super.onResume();

        //resume drawing the map
        this.tileView.resume();

        worldProvider.logFirebaseEvent(WorldActivity.CustomFirebaseEvent.MAPFRAGMENT_RESUME);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public DimensionVector3<Float> getMultiPlayerPos(String dbKey) throws Exception {
        try {
            WorldData wData = worldProvider.getWorld().getWorldData();
            wData.openDB();
            byte[] data = wData.db.get(dbKey.getBytes(NBTConstants.CHARSET));
            if(data == null) throw new Exception("no data!");
            final CompoundTag player = (CompoundTag) DataConverter.read(data).get(0);

            ListTag posVec = (ListTag) player.getChildTagByKey("Pos");
            if (posVec == null || posVec.getValue() == null)
                throw new Exception("No \"Pos\" specified");
            if (posVec.getValue().size() != 3)
                throw new Exception("\"Pos\" value is invalid. value: " + posVec.getValue().toString());

            IntTag dimensionId = (IntTag) player.getChildTagByKey("DimensionId");
            if(dimensionId == null || dimensionId.getValue() == null)
                throw new Exception("No \"DimensionId\" specified");
            Dimension dimension = Dimension.getDimension(dimensionId.getValue());
            if(dimension == null) dimension = Dimension.OVERWORLD;

            return new DimensionVector3<>(
                    (float) posVec.getValue().get(0).getValue(),
                    (float) posVec.getValue().get(1).getValue(),
                    (float) posVec.getValue().get(2).getValue(),
                    dimension);

        } catch (Exception e) {
            Log.e(e.getMessage());

            Exception e2 = new Exception("Could not find "+dbKey);
            e2.setStackTrace(e.getStackTrace());
            throw e2;
        }
    }

    public DimensionVector3<Float> getPlayerPos() throws Exception {
        try {
            WorldData wData = worldProvider.getWorld().getWorldData();
            wData.openDB();
            byte[] data = wData.db.get(World.SpecialDBEntryType.LOCAL_PLAYER.keyBytes);

            final CompoundTag player = data != null
                    ? (CompoundTag) DataConverter.read(data).get(0)
                    : (CompoundTag) worldProvider.getWorld().level.getChildTagByKey("Player");

            ListTag posVec = (ListTag) player.getChildTagByKey("Pos");
            if (posVec == null || posVec.getValue() == null)
                throw new Exception("No \"Pos\" specified");
            if (posVec.getValue().size() != 3)
                throw new Exception("\"Pos\" value is invalid. value: " + posVec.getValue().toString());

            IntTag dimensionId = (IntTag) player.getChildTagByKey("DimensionId");
            if(dimensionId == null || dimensionId.getValue() == null)
                throw new Exception("No \"DimensionId\" specified");
            Dimension dimension = Dimension.getDimension(dimensionId.getValue());
            if(dimension == null) dimension = Dimension.OVERWORLD;

            return new DimensionVector3<>(
                    (float) posVec.getValue().get(0).getValue(),
                    (float) posVec.getValue().get(1).getValue(),
                    (float) posVec.getValue().get(2).getValue(),
                    dimension);

        } catch (Exception e) {
            Log.e(e.toString());

            Exception e2 = new Exception("Could not find player.");
            e2.setStackTrace(e.getStackTrace());
            throw e2;
        }
    }

    public DimensionVector3<Integer> getSpawnPos() throws Exception {
        try {
            CompoundTag level = this.worldProvider.getWorld().level;
            int spawnX = ((IntTag) level.getChildTagByKey("SpawnX")).getValue();
            int spawnY = ((IntTag) level.getChildTagByKey("SpawnY")).getValue();
            int spawnZ = ((IntTag) level.getChildTagByKey("SpawnZ")).getValue();
            return new DimensionVector3<>( spawnX, spawnY, spawnZ, Dimension.OVERWORLD);
        } catch (Exception e){
            throw new Exception("Could not find spawn");
        }
    }

    public static class MarkerListAdapter extends ArrayAdapter<AbstractMarker> {


        public MarkerListAdapter(Context context, ArrayList<AbstractMarker> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RelativeLayout v = (RelativeLayout) convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = (RelativeLayout) vi.inflate(R.layout.marker_list_entry, parent, false);
            }

            AbstractMarker m = getItem(position);

            if (m != null) {
                TextView name = (TextView) v.findViewById(R.id.marker_name);
                TextView xz = (TextView) v.findViewById(R.id.marker_xz);
                ImageView icon = (ImageView) v.findViewById(R.id.marker_icon);

                name.setText(m.displayName);
                String xzStr = String.format(Locale.ENGLISH, "x: %d, y: %d, z: %d", m.x, m.y, m.z);
                xz.setText(xzStr);

                m.loadIcon(icon, true);

            }

            return v;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //TODO handle savedInstance...


        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        RelativeLayout worldContainer = (RelativeLayout) rootView.findViewById(R.id.world_tileview_container);
        assert worldContainer != null;

        /*
        GPS button: moves camera to player position
         */
        FloatingActionButton fabGPSPlayer = (FloatingActionButton) rootView.findViewById(R.id.fab_menu_gps_player);
        assert fabGPSPlayer != null;
        fabGPSPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(tileView == null) throw new Exception("No map available.");

                    DimensionVector3<Float> playerPos = getPlayerPos();

                    Snackbar.make(tileView, String.format(getString(R.string.coordinates_xy_dimension), playerPos.x, playerPos.z, playerPos.dimension.name), Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                    if(playerPos.dimension != worldProvider.getDimension()){
                        worldProvider.changeMapType(playerPos.dimension.defaultMapType, playerPos.dimension);
                    }

                    worldProvider.logFirebaseEvent(WorldActivity.CustomFirebaseEvent.GPS_PLAYER);

                    frameTo((double) playerPos.x, (double) playerPos.z);

                } catch (Exception e){
                    Snackbar.make(view, R.string.failed_find_player, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        /*
        GPS button: moves camera to spawn
         */
        FloatingActionButton fabGPSSpawn = (FloatingActionButton) rootView.findViewById(R.id.fab_menu_gps_spawn);
        assert fabGPSSpawn != null;
        fabGPSSpawn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DimensionVector3<Integer> spawnPos = getSpawnPos();

                    Snackbar.make(tileView, "Spawn at: ("+spawnPos.x+"; "+spawnPos.z+") ["+spawnPos.dimension.name+"].", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                    if(spawnPos.dimension != worldProvider.getDimension()){
                        worldProvider.changeMapType(spawnPos.dimension.defaultMapType, spawnPos.dimension);
                    }

                    worldProvider.logFirebaseEvent(WorldActivity.CustomFirebaseEvent.GPS_SPAWN);

                    frameTo((double) spawnPos.x, (double) spawnPos.z);

                } catch (Exception e){
                    Snackbar.make(view, R.string.failed_find_spawn, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


        final Activity activity = getActivity();

        if(activity == null){
            new Exception("MapFragment: activity is null, cannot set worldProvider!").printStackTrace();
            return null;
        }
        try {
            //noinspection ConstantConditions
            worldProvider = (WorldActivityInterface) activity;
        } catch (ClassCastException e){
            new Exception("MapFragment: activity is not an worldprovider, cannot set worldProvider!", e).printStackTrace();
            return null;
        }


        //show the toolbar if the fab menu is opened
        FloatingActionMenu fabMenu = (FloatingActionMenu) rootView.findViewById(R.id.fab_menu);
        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if(opened) worldProvider.showActionBar();
                else worldProvider.hideActionBar();
            }
        });



        FloatingActionButton fabGPSMarker = (FloatingActionButton) rootView.findViewById(R.id.fab_menu_gps_marker);
        assert fabGPSMarker != null;
        fabGPSMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ArrayList<AbstractMarker> markers = worldProvider.getWorld().getMarkerManager().markers;

                    if(markers.isEmpty()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        TextView msg = new TextView(activity);
                        float dpi = activity.getResources().getDisplayMetrics().density;
                        msg.setPadding((int)(19*dpi), (int)(5*dpi), (int)(14*dpi), (int)(5*dpi));
                        msg.setMaxLines(20);
                        msg.setText(R.string.no_custom_markers);
                        builder.setView(msg)
                                .setTitle(R.string.no_custom_markers_title)
                                .setCancelable(true)
                                .setNeutralButton(android.R.string.ok, null)
                                .show();
                    } else {

                        AlertDialog.Builder markerDialogBuilder = new AlertDialog.Builder(activity);
                        markerDialogBuilder.setIcon(R.drawable.ic_place);
                        markerDialogBuilder.setTitle(R.string.go_to_a_marker_list);

                        final MarkerListAdapter arrayAdapter = new MarkerListAdapter(activity, markers);

                        markerDialogBuilder.setNegativeButton(android.R.string.cancel, null);

                        markerDialogBuilder.setAdapter(
                                arrayAdapter,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AbstractMarker m = arrayAdapter.getItem(which);

                                        Snackbar.make(tileView,
                                                String.format(Locale.ENGLISH, activity.getString(R.string.player_at_xy), m.displayName, m.x, m.z),
                                                Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();

                                        if(m.dimension != worldProvider.getDimension()){
                                            worldProvider.changeMapType(m.dimension.defaultMapType, m.dimension);
                                        }

                                        frameTo((double) m.x, (double) m.z);

                                        worldProvider.logFirebaseEvent(WorldActivity.CustomFirebaseEvent.GPS_MARKER);
                                    }
                                });
                        markerDialogBuilder.show();
                    }

                } catch (Exception e){
                    Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


        /*
        GPS button: moves camera to player position
         */
        FloatingActionButton fabGPSMultiplayer = (FloatingActionButton) rootView.findViewById(R.id.fab_menu_gps_multiplayer);
        assert fabGPSMultiplayer != null;
        fabGPSMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (tileView == null){
                    Snackbar.make(view, R.string.no_map_available, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                Snackbar.make(tileView,
                    R.string.searching_for_players,
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

                //this can take some time... ...do it in the background
                (new AsyncTask<Void, Void, String[]>(){

                        @Override
                        protected String[] doInBackground(Void... arg0) {
                            try {
                                return worldProvider.getWorld().getWorldData().getPlayers();
                            } catch (Exception e){
                                return null;
                            }
                        }

                        protected void onPostExecute(final String[] players) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(players == null){
                                        Snackbar.make(view, R.string.failed_to_retrieve_player_data, Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        return;
                                    }

                                    if(players.length == 0){
                                        Snackbar.make(view, R.string.no_multiplayer_data_found, Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        return;
                                    }



                                    //NBT tag type spinner
                                    final Spinner spinner = new Spinner(activity);
                                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(activity,
                                            android.R.layout.simple_spinner_item, players);

                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinner.setAdapter(spinnerArrayAdapter);


                                    //wrap layout in alert
                                    new AlertDialog.Builder(activity)
                                            .setTitle(R.string.go_to_player)
                                            .setView(spinner)
                                            .setPositiveButton(R.string.go_loud, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {

                                                    //new tag type
                                                    int spinnerIndex = spinner.getSelectedItemPosition();
                                                    String playerKey = players[spinnerIndex];

                                                    try {
                                                        DimensionVector3<Float> playerPos = getMultiPlayerPos(playerKey);

                                                        Snackbar.make(tileView,
                                                                String.format(Locale.ENGLISH, getString(R.string.player_at_xy), playerKey, playerPos.x, playerPos.z),
                                                                Snackbar.LENGTH_LONG)
                                                                .setAction("Action", null).show();

                                                        worldProvider.logFirebaseEvent(WorldActivity.CustomFirebaseEvent.GPS_MULTIPLAYER);

                                                        if(playerPos.dimension != worldProvider.getDimension()){
                                                            worldProvider.changeMapType(playerPos.dimension.defaultMapType, playerPos.dimension);
                                                        }

                                                        frameTo((double) playerPos.x, (double) playerPos.z);

                                                    } catch (Exception e){
                                                        Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
                                                                .setAction("Action", null).show();
                                                    }

                                                }
                                            })
                                            //or alert is cancelled
                                            .setNegativeButton(android.R.string.cancel, null)
                                            .show();
                                }
                            });
                        }

                    }).execute();

            }
        });


        /*
        GPS button: moves camera to player position
         */
        FloatingActionButton fabGPSCoord = (FloatingActionButton) rootView.findViewById(R.id.fab_menu_gps_coord);
        assert fabGPSCoord != null;
        fabGPSCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(tileView == null) throw new Exception("No map available.");

                    View xzForm = LayoutInflater.from(activity).inflate(R.layout.xz_coord_form, null);
                    final EditText xInput = (EditText) xzForm.findViewById(R.id.x_input);
                    xInput.setText("0");
                    final EditText zInput = (EditText) xzForm.findViewById(R.id.z_input);
                    zInput.setText("0");

                    //wrap layout in alert
                    new AlertDialog.Builder(activity)
                        .setTitle(R.string.go_to_coordinate)
                        .setView(xzForm)
                        .setPositiveButton(R.string.go_loud, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    int inX, inZ;
                                    try{
                                        inX = Integer.parseInt(xInput.getText().toString());
                                    } catch (NullPointerException | NumberFormatException e){
                                        Toast.makeText(activity, R.string.invalid_x_coordinate,Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    try{
                                        inZ = Integer.parseInt(zInput.getText().toString());
                                    } catch (NullPointerException | NumberFormatException e){
                                        Toast.makeText(activity, R.string.invalid_z_coordinate,Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    worldProvider.logFirebaseEvent(WorldActivity.CustomFirebaseEvent.GPS_COORD);

                                    frameTo((double) inX, (double) inZ);
                                }
                            })
                        .setCancelable(true)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                } catch (Exception e){
                    Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });



        try {
            Entity.loadEntityBitmaps(activity.getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Block.loadBitmaps(activity.getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            CustomIcon.loadCustomBitmaps(activity.getAssets());
        } catch (IOException e){
            e.printStackTrace();
        }




        /*
        Create tile view
         */
        this.tileView = new MapFragmentTileView(worldProvider, activity);
        this.tileView.setId(ViewID.generateViewId());

        //set the map-type
        tileView.getDetailLevelManager().setLevelType(worldProvider.getMapType());

        /*
        Add tile view to main layout
         */

        //add world view to its container in the main layout
        worldContainer.addView(tileView);


        /*
        Create tile(=bitmap) provider
         */
        this.minecraftTileProvider = new MCTileProvider(worldProvider);



        /*
        Set the bitmap-provider of the tile view
         */
        this.tileView.setBitmapProvider(this.minecraftTileProvider);
        this.tileView.setShouldRecycleBitmaps(false);


        /*
        Change tile view settings
         */

        this.tileView.setBackgroundColor(0xFF494E8E);

        // markers should align to the coordinate along the horizontal center and vertical bottom
        tileView.setMarkerAnchorPoints(-0.5f, -1.0f);

        this.tileView.defineBounds(
                -1.0,
                -1.0,
                1.0,
                1.0
        );


        //TODO should a custom-detail-level-manager be used to optimize the tileview?
        //see: https://github.com/moagrius/TileView/pull/297

        this.tileView.setSize(MCTileProvider.viewSizeW, MCTileProvider.viewSizeL);

        for(MapType mapType : MapType.values()){
            this.tileView.addDetailLevel(0.0625f,   "0.0625",   MCTileProvider.TILESIZE, MCTileProvider.TILESIZE, mapType);// 1/(1/16)=16 chunks per tile
            this.tileView.addDetailLevel(0.125f,    "0.125",    MCTileProvider.TILESIZE, MCTileProvider.TILESIZE, mapType);
            this.tileView.addDetailLevel(0.25f,     "0.25",     MCTileProvider.TILESIZE, MCTileProvider.TILESIZE, mapType);
            this.tileView.addDetailLevel(0.5f,      "0.5",      MCTileProvider.TILESIZE, MCTileProvider.TILESIZE, mapType);
            this.tileView.addDetailLevel(1f,        "1",        MCTileProvider.TILESIZE, MCTileProvider.TILESIZE, mapType);// 1/1=1 chunk per tile

        }


        this.tileView.setScale(0.5f);


        //TODO load player pos, center around that instead of origin
        //center frame around origin
        //TODO frameTo does not work!!! ???
        //this.frameTo(0, 0);

        //tileView.scrollTo(0.0, 0.0);


        boolean framedToPlayer = false;

        try {

            DimensionVector3<Float> playerPos = getPlayerPos();
            float x = playerPos.x, y = playerPos.y, z = playerPos.z;
            Log.d("Placed player marker at: "+x+";"+y+";"+z+" ["+playerPos.dimension.name+"]");
            localPlayerMarker = new PlayerMarker((int) x, (int) y, (int) z, playerPos.dimension, "~local_player");
            addMarker(localPlayerMarker);

            if(localPlayerMarker.dimension != worldProvider.getDimension()){
                worldProvider.changeMapType(localPlayerMarker.dimension.defaultMapType, localPlayerMarker.dimension);
            }

            frameTo((double) x, (double) z);
            framedToPlayer = true;

        } catch (Exception e){
            e.printStackTrace();
            Log.d("Failed to place player marker. "+e.toString());
        }


        try {
            DimensionVector3<Integer> spawnPos = getSpawnPos();

            spawnMarker = new WorldDataMarker(spawnPos.x, spawnPos.y, spawnPos.z, spawnPos.dimension, "Spawn", WorldDataMarker.MarkerResource.SPAWN);
            addMarker(spawnMarker);

            if(!framedToPlayer){

                if(spawnMarker.dimension != worldProvider.getDimension()){
                    worldProvider.changeMapType(spawnMarker.dimension.defaultMapType, spawnMarker.dimension);
                }

                frameTo((double) spawnPos.x, (double) spawnPos.z);
            }

        } catch (Exception e){
            //no spawn defined...
            if(!framedToPlayer) frameTo(0.0, 0.0);

        }






        tileView.getMarkerLayout().setMarkerTapListener(new MarkerLayout.MarkerTapListener() {
            @Override
            public void onMarkerTap(View view, int tapX, int tapY) {
                if(!(view instanceof MarkerImageView)){
                    Log.d("Markertaplistener found a marker that is not a MarkerImageView! "+view.toString());
                    return;
                }

                final AbstractMarker marker = ((MarkerImageView) view).getMarkerHook();
                if(marker == null){
                    Log.d("abstract marker is null! "+view.toString());
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder
                    .setMessage("Teleport to "+marker.x+";"+marker.y+";"+marker.z+" ["+marker.dimension.name+"] ("+marker.displayName+") ?")
                        .setPositiveButton("Teleport!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    final EditableNBT playerEditable = worldProvider.getEditablePlayer();
                                    if (playerEditable == null)
                                        throw new Exception("Player is null");

                                    Iterator playerIter = playerEditable.getTags().iterator();
                                    if (!playerIter.hasNext())
                                        throw new Exception("Player DB entry is empty!");

                                    //db entry consists of one compound tag
                                    final CompoundTag playerTag = (CompoundTag) playerIter.next();

                                    ListTag posVec = (ListTag) playerTag.getChildTagByKey("Pos");

                                    if (posVec == null) throw new Exception("No \"Pos\" specified");

                                    final List<Tag> playerPos = posVec.getValue();
                                    if (playerPos == null)
                                        throw new Exception("No \"Pos\" specified");
                                    if (playerPos.size() != 3)
                                        throw new Exception("\"Pos\" value is invalid. value: " + posVec.getValue().toString());

                                    IntTag dimensionId = (IntTag) playerTag.getChildTagByKey("DimensionId");
                                    if(dimensionId == null || dimensionId.getValue() == null)
                                        throw new Exception("No \"DimensionId\" specified");


                                    int newX = marker.x;
                                    int newY = marker.y;
                                    int newZ = marker.z;
                                    Dimension newDimension = marker.dimension;

                                    ((FloatTag) playerPos.get(0)).setValue(((float) newX) + 0.5f);
                                    ((FloatTag) playerPos.get(1)).setValue(((float) newY) + 0.5f);
                                    ((FloatTag) playerPos.get(2)).setValue(((float) newZ) + 0.5f);
                                    dimensionId.setValue(newDimension.id);


                                    if(playerEditable.save()){

                                        moveMarker(localPlayerMarker,newX, newY, newZ, newDimension);

                                        Snackbar.make(tileView,
                                                activity.getString(R.string.teleported_player_to_xyz_dimension)+newX+";"+newY+";"+newZ+" ["+newDimension.name+"] ("+marker.displayName+")",
                                                Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                    } else throw new Exception("Failed saving player");

                                } catch (Exception e){
                                    Log.w(e.toString());

                                    Snackbar.make(tileView, R.string.failed_teleporting_player,
                                            Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }

                            }
                        })
                    .setCancelable(true)
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();

            }
        });



        //do not loop the scale
        tileView.setShouldLoopScale(false);

        //prevents flickering
        this.tileView.setTransitionsEnabled(false);

        //more responsive rendering
        this.tileView.setShouldRenderWhilePanning(true);

        this.tileView.setSaveEnabled(true);



        /*
        Listen for map movements, remove invisible chunks
         */
        this.tileView.addZoomPanListener(new ZoomPanLayout.ZoomPanListener() {
            @Override
            public void onPanBegin(int x, int y, Origination origin) {

            }

            @Override
            public void onPanUpdate(int x, int y, Origination origin) {

            }

            @Override
            public void onPanEnd(int x, int y, Origination origin) {

                //disposes invisible chunks
                //updateViewport();
            }

            @Override
            public void onZoomBegin(float scale, Origination origin) {

            }

            @Override
            public void onZoomUpdate(float scale, Origination origin) {

            }

            @Override
            public void onZoomEnd(float scale, Origination origin) {

            }
        });


        /*

        TODO marker stuff:
        - add more marker-icons/types
        - player markers with name-tags
        - remove/edit custom marker option
        - view with list of filtered markers (filter on entity-type, tile-entity-type, custom-type, etc.)

         */



        reloadCustomMarkers();


        return rootView;
    }

    /*
    //TODO this may need to be multithreaded
    public void updateViewport(){


        float pixelsPerBlockScaled = MCTileProvider.pixelsPerBlock * tileView.getScale();
        int blockX = Math.round( (tileView.getScrollX()) / pixelsPerBlockScaled ) - MCTileProvider.HALF_WORLDSIZE;
        int blockZ = Math.round( (tileView.getScrollY()) / pixelsPerBlockScaled ) - MCTileProvider.HALF_WORLDSIZE;
        int blockW = Math.round( (tileView.getWidth()) / pixelsPerBlockScaled );
        int blockH = Math.round( (tileView.getHeight()) / pixelsPerBlockScaled );

        //minimum: (0; 0)
        int minChunkX = blockX / Chunk.WIDTH;
        int minChunkZ = blockZ / Chunk.LENGTH;
        int chunkW = blockW / Chunk.WIDTH;
        int chunkH = blockH / Chunk.LENGTH;

        //maximum: (width; height)
        int maxChunkX = minChunkX + chunkW;
        int maxChunkZ = minChunkZ + chunkH;

        //add some more space to the bounds
        minChunkX -= chunkW;
        minChunkZ -= chunkH;
        maxChunkX += chunkW;
        maxChunkZ += chunkH;

        ArrayList<Long> removeList = new ArrayList<>();
        for(Map.Entry<Long, ArrayList<AbstractMarker>> mList : proceduralMarkers.entrySet()){
            long key = mList.getKey();
            int cX = (int) (key >> 32);
            int cZ = (int) key;
            if(cX <= minChunkX || cX > maxChunkX || cZ <= minChunkZ || cZ > maxChunkZ){
                for(AbstractMarker m : mList.getValue()){
                    if(m.view != null) tileView.removeMarker(m.view);
                }
                removeList.add(key);
            }
        }
        for(long key : removeList){
            proceduralMarkers.remove(key);
        }



    }*/

    /*
    public Marker addPermanentMarker(Marker.MarkerResource markerRes, int x, int z){
        return (Marker) tileView.addMarker(new Marker(getActivity(), markerRes, x, z),
                (double) x / (double) MCTileProvider.HALF_WORLDSIZE,
                (double) z / (double) MCTileProvider.HALF_WORLDSIZE,
                -0.5f, -0.5f);
    }*/

    public void updateVisibilityForAllMarkers(){
        Dimension dimension = worldProvider.getDimension();
        Activity act = getActivity();
        if(act == null) return;

        if(spawnMarker != null) {
            if (spawnMarker.dimension == dimension)
                spawnMarker.getView(act).setVisibility(View.VISIBLE);
            else spawnMarker.getView(act).setVisibility(View.INVISIBLE);
        }

        if(localPlayerMarker != null) {
            if (localPlayerMarker.dimension == dimension)
                localPlayerMarker.getView(act).setVisibility(View.VISIBLE);
            else localPlayerMarker.getView(act).setVisibility(View.INVISIBLE);
        }

        for(AbstractMarker marker : customMarkers){
            if(marker.dimension == dimension) marker.getView(act).setVisibility(View.VISIBLE);
            else marker.getView(act).setVisibility(View.INVISIBLE);
        }

        for (ArrayList<AbstractMarker> markerChunk : proceduralMarkers.values()) {
            if(markerChunk == null) continue;
            for (AbstractMarker marker : markerChunk) {
                if(marker == null) continue;
                marker.getView(act).setVisibility(marker.dimension == dimension ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    public void moveMarker(AbstractMarker marker, int x, int y, int z, Dimension dimension){
        marker.move(x, y, z, dimension);
        if(marker.view != null){
            tileView.moveMarker(marker.view,
                    dimension.dimensionScale * (double) x / (double) MCTileProvider.HALF_WORLDSIZE,
                    dimension.dimensionScale * (double) z / (double) MCTileProvider.HALF_WORLDSIZE);
            if(dimension == worldProvider.getDimension()) marker.view.setVisibility(View.VISIBLE);
            else marker.view.setVisibility(View.INVISIBLE);
        }
    }

    public void addMarker(AbstractMarker marker){

        MarkerType markerType = marker.getMarkerType();
        if(markerType.procedural) {
            long key = (((long) marker.getChunkX()) << 32) | (((long) marker.getChunkZ()) & 0xFFFFFFFFL );
            ArrayList<AbstractMarker> mList = proceduralMarkers.get(key);
            if (mList == null) {
                mList = new ArrayList<>();
                proceduralMarkers.put(key, mList);
            }
            for (AbstractMarker b : mList) {
                //no markers on each other
                if (marker.x == b.x && marker.z == b.z && marker.dimension == b.dimension) return;
            }
            mList.add(marker);
        } else if(marker.getMarkerType() == MarkerType.CUSTOM){
            if(!customMarkers.contains(marker)) customMarkers.add(marker);
        }

        Activity act = getActivity();
        if (act == null) return;
        MarkerImageView markerView = marker.getView(act);

        markerView.setVisibility(
                marker.dimension == worldProvider.getDimension() ? View.VISIBLE : View.INVISIBLE);

        tileView.addMarker(markerView,
                marker.dimension.dimensionScale * (double) marker.x / (double) MCTileProvider.HALF_WORLDSIZE,
                marker.dimension.dimensionScale * (double) marker.z / (double) MCTileProvider.HALF_WORLDSIZE,
                -0.5f, -0.5f);
    }

    public void reloadCustomMarkers(){
        for(AbstractMarker marker : customMarkers){
            if(marker.view != null) tileView.removeMarker(marker.view);
        }
        customMarkers.clear();

        //Lets assume that this doesn't load extreme amounts of markers, it won't cause lag
        CustomMarkerManager markersManager = worldProvider.getWorld().getMarkerManager();
        markersManager.load();

        for(AbstractMarker m : markersManager.getMarkers()){
            this.addMarker(m);
        }


    }

    public void toggleMarkers(){
        int visibility = tileView.getMarkerLayout().getVisibility();
        tileView.getMarkerLayout().setVisibility( visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        tileView.destroy();
        tileView = null;
        minecraftTileProvider = null;

    }

    public void resetTileView(){

        if(this.tileView != null){
            worldProvider.logFirebaseEvent(WorldActivity.CustomFirebaseEvent.MAPFRAGMENT_RESET);

            updateVisibilityForAllMarkers();

            tileView.getDetailLevelManager().setLevelType(worldProvider.getMapType());
/*
            LEGACY WAY of resetting tileview, using a different method now;
             parallel detail-levels work very well.
                See the TileView fork by @protolambda

            TileCanvasViewGroup tileCanvasViewGroup = this.tileView.getTileCanvasViewGroup();
            this.tileView.getDetailLevelManager().getCurrentDetailLevel().invalidate();
            tileCanvasViewGroup.clear();
            //TODO tileCanvasViewGroup.clearAlreadyRendered();
            tileCanvasViewGroup.requestRender();
            tileView.resume();
            */
        }

    }



    /**
     * This is a convenience method to scrollToAndCenter after layout (which won't happen if called directly in onCreate
     * see https://github.com/moagrius/TileView/wiki/FAQ
     */
    public void frameTo( final double worldX, final double worldZ ) {
        this.tileView.post(new Runnable() {
            @Override
            public void run() {
                Dimension dimension = worldProvider.getDimension();
                if(tileView != null) tileView.scrollToAndCenter(
                        dimension.dimensionScale * worldX / (double) MCTileProvider.HALF_WORLDSIZE,
                        dimension.dimensionScale * worldZ / (double) MCTileProvider.HALF_WORLDSIZE);
            }
        });
    }


}
