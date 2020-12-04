package com.ntu.fresheee;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.pluginscalebar.ScaleBarOptions;
import com.mapbox.pluginscalebar.ScaleBarPlugin;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MapBoxActivity extends AppCompatActivity implements
        OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {

    private static final LatLng BOUND_CORNER_NW = new LatLng(1.3560, 103.6770);
    private static final LatLng BOUND_CORNER_SE = new LatLng(1.3400, 103.6900);
    private static final LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
            .include(BOUND_CORNER_NW)
            .include(BOUND_CORNER_SE)
            .build();

    private final List<List<Point>> points = new ArrayList<>();
    private final List<Point> outerPoints = new ArrayList<>();

    private PermissionsManager permissionsManager;
    private ValueAnimator markerAnimator;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private boolean markerSelected = false;
    private LocationEngine locationEngine;
    private final long INTERVAL_IN_MILLISECONDS = 10000L;
    private long MAX_WAIT_TIME = INTERVAL_IN_MILLISECONDS * 5;
    private final MapBoxActivityLocationCallback callback = new MapBoxActivityLocationCallback(this);
    private static final String TAG = "MapBoxActivity";

    private FloatingActionButton taptoCenter;
    private Button btnDirection;
    private RelativeLayout mapboxPopup;

    private LatLng userCurrentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_map_box);

        getSupportActionBar().setTitle("NTU SafeEntry Map");

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mapboxPopup = findViewById(R.id.mapbox_popup);
        btnDirection = findViewById(R.id.popup_direction_button);
        taptoCenter = findViewById(R.id.tap_to_center);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        DatabaseReference entryPointsReference = FirebaseDatabase.getInstance().getReference("entry_points");

        LoadingDialog loadingDialog = new LoadingDialog(MapBoxActivity.this);
        loadingDialog.startLoadingDialog();

        entryPointsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<EntryLocation> entryLocationArrayList = new ArrayList<>();
                List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

                for(DataSnapshot locationSnapshot: snapshot.getChildren()) {
                    EntryLocation entryLocation = locationSnapshot.getValue(EntryLocation.class);
                    entryLocationArrayList.add(entryLocation);

                    Feature newFeature = Feature.fromGeometry(Point.fromLngLat(entryLocation.longitude, entryLocation.latitude));
                    newFeature.addStringProperty("name", entryLocation.name);
                    newFeature.addStringProperty("image", entryLocation.image);
                    symbolLayerIconFeatureList.add(newFeature);
                }

                if (!symbolLayerIconFeatureList.isEmpty()) {
                    MapBoxActivity.this.mapboxMap = mapboxMap;
                    mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            style.addImage("entry_points_icon_id", BitmapFactory.decodeResource(
                                    MapBoxActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));
                            style.addSource(new GeoJsonSource("entry_points_source_id",
                                    FeatureCollection.fromFeatures(symbolLayerIconFeatureList)));
                            // Add the selected marker source and layer
                            style.addSource(new GeoJsonSource("selected_entry_points_source_id"));
                            // Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
                            // middle of the icon being fixed to the coordinate point.
                            style.addLayer(new SymbolLayer("entry_points_layer_id", "entry_points_source_id")
                                    .withProperties(PropertyFactory.iconImage("entry_points_icon_id"),
                                            iconAllowOverlap(false),
                                            iconOffset(new Float[]{0f, -0.5f})));
                            // Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
                            // middle of the icon being fixed to the coordinate point.
                            style.addLayer(new SymbolLayer("selected_entry_points_layer_id", "selected_entry_points_source_id")
                                    .withProperties(PropertyFactory.iconImage("entry_points_icon_id"),
                                            iconAllowOverlap(true),
                                            iconOffset(new Float[]{0f, -0.5f})));

                            loadingDialog.dismissDialog();

                            taptoCenter.setVisibility(View.VISIBLE);

                            mapboxMap.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);

                            // Set Distance scale bar on map
                            ScaleBarPlugin scaleBarPlugin = new ScaleBarPlugin(mapView, mapboxMap);
                            ScaleBarOptions scaleBarOptions = new ScaleBarOptions(MapBoxActivity.this)
                                    .setMetricUnit(true);
                            scaleBarPlugin.create(scaleBarOptions);

                            mapboxMap.addOnMapClickListener(MapBoxActivity.this);
                            mapboxMap.setMinZoomPreference(14.2);
                            showBoundsArea(style);
                            enableLocationComponent(style);
                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(userCurrentLatLng));
                            Toast.makeText(MapBoxActivity.this, "You can zoom in and out to see more SafeEntry Location around the campus!", Toast.LENGTH_LONG).show();

                            taptoCenter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(userCurrentLatLng), 1000);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getMessage());
            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Style style = mapboxMap.getStyle();

        if (style != null) {
            final SymbolLayer selectedMarkerSymbolLayer = (SymbolLayer) style.getLayer("selected_entry_points_layer_id");
            final PointF clickedPixel = mapboxMap.getProjection().toScreenLocation(point);

            List<Feature> clickedFeatures = mapboxMap.queryRenderedFeatures(clickedPixel, "entry_points_layer_id");


            if (clickedFeatures.isEmpty()) {
                if (markerSelected) {
                    deselectMarker(selectedMarkerSymbolLayer);
                }
                return false;
            }

            GeoJsonSource selectedEntryPointsSource = style.getSourceAs("selected_entry_points_source_id");
            if (selectedEntryPointsSource != null) {
                selectedEntryPointsSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(clickedFeatures.get(0).geometry())}));
            }

            if (markerSelected) {
                deselectMarker(selectedMarkerSymbolLayer);
            }
            if (clickedFeatures.size() > 0) {
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(point), 500);
                selectMarker(selectedMarkerSymbolLayer, clickedFeatures.get(0));
            }
        }
        return true;
    }

    private void selectMarker(final SymbolLayer selectedMarkerSymbolLayer, Feature clickFeature) {
        markerAnimator = new ValueAnimator();
        markerAnimator.setObjectValues(1f, 1.5f);
        markerAnimator.setDuration(100);
        markerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                selectedMarkerSymbolLayer.setProperties(
                        PropertyFactory.iconSize((float) animator.getAnimatedValue())
                );
            }
        });
        markerAnimator.start();
        markerSelected = true;

        Point clickPoint = ((Point) clickFeature.geometry());

        if(mapboxPopup.getVisibility() == View.GONE) {
            final TextView popupName = (TextView) findViewById(R.id.popup_name);
            popupName.setText(clickFeature.properties().get("name").toString().replace("\"", ""));
            final ImageView popupImage = (ImageView) findViewById(R.id.popup_image);
            Glide.with(this).load(clickFeature.properties().get("image").toString().replace("\"", "")).into(popupImage);
            final LatLng originLatLng = new LatLng(userCurrentLatLng.getLatitude(), userCurrentLatLng.getLongitude());
            final LatLng destinationLatLng = new LatLng(clickPoint.latitude(), clickPoint.longitude());

            mapboxPopup.setVisibility(View.VISIBLE);
            btnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getRoute(originLatLng, destinationLatLng);
                    String uri = "http://maps.google.com/maps?saddr=" + originLatLng.getLatitude() + "," + originLatLng.getLongitude()
                            + "&daddr=" + destinationLatLng.getLatitude() + "," + destinationLatLng.getLongitude();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(i);
                }
            });
        }
    }

    private void deselectMarker(final SymbolLayer iconLayer) {
        markerAnimator.setObjectValues(1.5f, 1f);
        markerAnimator.setDuration(100);
        markerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                iconLayer.setProperties(
                        PropertyFactory.iconSize((float) animator.getAnimatedValue())
                );
            }
        });
        markerAnimator.start();
        markerSelected = false;

        if(mapboxPopup.getVisibility() == View.VISIBLE) {
            mapboxPopup.setVisibility(View.GONE);
        }
    }

    private void showBoundsArea(@NonNull Style loadedMapStyle) {
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getNorthWest().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getNorthWest().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getNorthEast().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getNorthEast().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getSouthEast().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getSouthEast().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getSouthWest().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getSouthWest().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getNorthWest().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getNorthWest().getLatitude()));
        points.add(outerPoints);

        loadedMapStyle.addSource(new GeoJsonSource("bounding_box_source_id",
                Polygon.fromLngLats(points)));

        loadedMapStyle.addLayer(new FillLayer("bounding_box_layer_id", "bounding_box_source_id").withProperties(
                fillOpacity(.00f)
        ));
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.NONE);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();
        }
        else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class MapBoxActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {
        private final WeakReference<MapBoxActivity> activityWeakReference;

        MapBoxActivityLocationCallback(MapBoxActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            MapBoxActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                userCurrentLatLng = new LatLng(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
        }
    }

    private void getRoute(LatLng originLatLng, LatLng destinationLatLng) {
        Point origin = Point.fromLngLat(originLatLng.getLongitude(), originLatLng.getLatitude());
        Point destination = Point.fromLngLat(destinationLatLng.getLongitude(), destinationLatLng.getLatitude());
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        if (markerAnimator != null) {
            markerAnimator.cancel();
        }
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}