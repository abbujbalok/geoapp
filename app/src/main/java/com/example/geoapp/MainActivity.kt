package com.example.geoapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ScaleBarOverlay
import java.io.File

class MainActivity : AppCompatActivity(), CacheManager.CacheManagerCallback {

    private lateinit var map: MapView
    private lateinit var status: TextView
    private lateinit var offlineButton: Button

    private val bangladesh = BoundingBox(26.634, 92.673, 20.741, 88.084)
    private val minZoom = 5
    private val maxZoom = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cfg = Configuration.getInstance()
        cfg.load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        cfg.userAgentValue = packageName

        val base = File(getExternalFilesDir(null) ?: filesDir, "osmdroid").apply { mkdirs() }
        cfg.osmdroidBasePath = base
        cfg.osmdroidTileCache = File(base, "tiles").apply { mkdirs() }
        cfg.setTileFileSystemCacheMaxBytes(3L * 1024 * 1024 * 1024)
        cfg.setTileFileSystemCacheTrimBytes(2800L * 1024 * 1024)

        setContentView(R.layout.activity_main)

        map = findViewById(R.id.map)
        status = findViewById(R.id.status)
        offlineButton = findViewById(R.id.offlineBtn)

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(true)
        map.controller.setZoom(7.0)
        map.controller.setCenter(GeoPoint(23.685, 90.356))

        val scaleBar = ScaleBarOverlay(map)
        scaleBar.setAlignRight(true)
        map.overlays.add(scaleBar)

        findViewById<Button>(R.id.downloadBtn).setOnClickListener { startDownload() }

        offlineButton.setOnClickListener {
            val goingOffline = map.isUsingDataConnection
            map.setUseDataConnection(!goingOffline)
            offlineButton.text =
                if (goingOffline) getString(R.string.offline_label_off)
                else getString(R.string.offline_label_on)
            val message = if (goingOffline) "Offline mode ON" else "Offline mode OFF"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startDownload() {
        status.text = getString(R.string.status_downloading)
        Toast.makeText(
            this,
            "Downloading Bangladesh (zoom $minZoom-$maxZoom). Keep internet on.",
            Toast.LENGTH_LONG
        ).show()
        CacheManager(map).downloadAreaAsync(this, bangladesh, minZoom, maxZoom, this)
    }

    override fun onTaskComplete() {
        runOnUiThread { status.text = getString(R.string.status_done) }
    }

    override fun onTaskFailed(errors: Int) {
        runOnUiThread { status.text = getString(R.string.status_failed, errors) }
    }

    override fun updateProgress(progress: Int, currentZoomLevel: Int, zoomMin: Int, zoomMax: Int) {
        runOnUiThread {
            status.text = "Downloading $progress% (zoom $currentZoomLevel of $zoomMax)"
        }
    }

    override fun downloadStarted() {}

    override fun setPossibleTilesInArea(total: Int) {
        runOnUiThread { status.text = getString(R.string.status_queued, total) }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
