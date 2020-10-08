package com.ntu.fresheee;

import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.pluginscalebar.ScaleBarOptions;
import com.mapbox.pluginscalebar.ScaleBarPlugin;


import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.style.layers.FillLayer;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_map_box);

        getSupportActionBar().getTitle();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        DatabaseReference entryPointsReference = FirebaseDatabase.getInstance().getReference("entry_points");

        entryPointsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<EntryLocation> entryLocationArrayList = new ArrayList<>();
                List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

                for(DataSnapshot locationSnapshot: snapshot.getChildren()) {
                    EntryLocation entryLocation = locationSnapshot.getValue(EntryLocation.class);
                    entryLocationArrayList.add(entryLocation);
                    symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(entryLocation.longitude, entryLocation.latitude)));
                    System.out.println(entryLocation.name);
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
                                            iconAllowOverlap(true),
                                            iconOffset(new Float[]{0f, -0.5f})));
                            // Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
                            // middle of the icon being fixed to the coordinate point.
                            style.addLayer(new SymbolLayer("selected_entry_points_layer_id", "selected_entry_points_source_id")
                                    .withProperties(PropertyFactory.iconImage("entry_points_icon_id"),
                                            iconAllowOverlap(true),
                                            iconOffset(new Float[]{0f, -0.5f})));

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
            final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);

            List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "entry_points_layer_id");
            List<Feature> selectedFeature = mapboxMap.queryRenderedFeatures(pixel, "selected_entry_points_layer_id");

            if (selectedFeature.size() > 0 && markerSelected) {
                return false;
            }

            if (features.isEmpty()) {
                if (markerSelected) {
                    deselectMarker(selectedMarkerSymbolLayer);
                }
                return false;
            }

            GeoJsonSource source = style.getSourceAs("selected_entry_points_source_id");
            if (source != null) {
                source.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(features.get(0).geometry())}));
            }

            if (markerSelected) {
                deselectMarker(selectedMarkerSymbolLayer);
            }
            if (features.size() > 0) {
                selectMarker(selectedMarkerSymbolLayer);
            }
        }
        return true;
    }

    private void selectMarker(final SymbolLayer iconLayer) {
        markerAnimator = new ValueAnimator();
        markerAnimator.setObjectValues(1f, 1.5f);
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
        markerSelected = true;
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

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Enable the most basic pulsing styling by ONLY using
            // the `.pulseEnabled()` method
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .pulseEnabled(false)
                    .build();

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.NONE);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
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
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}