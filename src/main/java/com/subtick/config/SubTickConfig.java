package com.subtick.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.subtick.SubTickInputEngine;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * SubTick Engine Yapılandırma Yöneticisi (Configuration Manager).
 *
 * <p>Mod ayarlarını JSON formatında saklar ve Fabric konfigürasyon dizininden
 * ({@code config/subtick-input-engine.json}) yükler.
 * GSON kütüphanesi kullanılarak serileştirme/deserileştirme işlemleri yapılır.
 *
 * <p>Handles loading, saving, and querying configuration properties for the
 * SubTick Input Engine mod.
 */
public class SubTickConfig {

    private static final String CONFIG_FILE_NAME = "subtick-input-engine.json";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static SubTickConfig instance;

    // ─── Yapılandırma Alanları (Configuration Fields) ─────────────────

    /** Mod aktif/pasif anahtarı (Master toggle for the mod) */
    private boolean enabled = true;

    /** HUD overlay gösterimi (Display HUD overlay on screen) */
    private boolean hudEnabled = true;

    /** Detaylı hata ayıklama loglaması (Verbose debug logging) */
    private boolean debugMode = false;

    /** Olay kuyruğu maksimum kapasitesi (Max capacity of the event buffer queue) */
    private int maxBufferCapacity = 2000;

    /** Tick başına işlenebilecek maksimum olay sayısı (Max events to process per tick flush) */
    private int maxEventsPerFlush = 1000;

    /** Fare hareketlerini yakalama (Capture mouse movement events at frame rate) */
    private boolean captureMouseMove = true;

    /** Klavye girişlerini yakalama (Capture keyboard events at frame rate) */
    private boolean captureKeyboard = true;

    /** HUD gösterge ölçeği (HUD overlay scale factor) */
    private float hudScale = 1.0f;

    /** HUD X pozisyonu piksel ofseti (HUD X position on screen) */
    private int hudX = 5;

    /** HUD Y pozisyonu piksel ofseti (HUD Y position on screen) */
    private int hudY = 5;

    /**
     * Varsayılan yapıcı (Default constructor).
     * GSON serileştirmesi ve varsayılan başlatma için gereklidir.
     */
    public SubTickConfig() {
    }

    /**
     * Singleton yapılandırma örneğini döndürür.
     * Henüz yüklenmemişse {@link #load()} metodunu çağırır.
     *
     * @return SubTickConfig singleton örneği
     */
    public static synchronized SubTickConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    /**
     * Konfigürasyon dosyasını diskten yükler.
     * Dosya mevcut değilse veya okunamazsa varsayılan değerlerle yeni bir dosya oluşturur ve kaydeder.
     *
     * @return Yüklenen veya varsayılan ayarlarla oluşturulan {@link SubTickConfig} nesnesi
     */
    public static synchronized SubTickConfig load() {
        Path configPath = getConfigFilePath();

        if (Files.exists(configPath)) {
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                SubTickConfig loadedConfig = GSON.fromJson(reader, SubTickConfig.class);
                if (loadedConfig != null) {
                    loadedConfig.validate();
                    instance = loadedConfig;
                    SubTickInputEngine.LOGGER.info("[SubTickConfig] Yapılandırma dosyası başarıyla yüklendi: {}", configPath.toAbsolutePath());
                    return instance;
                }
            } catch (Exception e) {
                SubTickInputEngine.LOGGER.error("[SubTickConfig] Yapılandırma dosyası okunurken hata oluştu! Varsayılan ayarlar kullanılacak. Yol: {}", configPath.toAbsolutePath(), e);
            }
        }

        // Dosya yoksa veya okunamadıysa varsayılan konfigürasyonu oluştur ve kaydet
        instance = new SubTickConfig();
        instance.save();
        return instance;
    }

    /**
     * Mevcut yapılandırma durumunu JSON formatında diskteki konfigürasyon dosyasına kaydeder.
     */
    public synchronized void save() {
        Path configPath = getConfigFilePath();
        try {
            Path parentDir = configPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(this, writer);
                SubTickInputEngine.LOGGER.info("[SubTickConfig] Yapılandırma dosyası kaydedildi: {}", configPath.toAbsolutePath());
            }
        } catch (IOException e) {
            SubTickInputEngine.LOGGER.error("[SubTickConfig] Yapılandırma dosyası kaydedilirken hata oluştu: {}", configPath.toAbsolutePath(), e);
        }
    }

    /**
     * Konfigürasyon dosyasının mutlak yolunu döndürür.
     *
     * @return {@code .minecraft/config/subtick-input-engine.json} dosya yolu
     */
    public static Path getConfigFilePath() {
        return FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
    }

    /**
     * Geçersiz veya sınır dışı değerleri sınırlar / doğrular.
     */
    private void validate() {
        if (maxBufferCapacity < 100) {
            maxBufferCapacity = 100;
        }
        if (maxEventsPerFlush < 10) {
            maxEventsPerFlush = 10;
        }
        if (hudScale <= 0.0f) {
            hudScale = 1.0f;
        }
        if (hudX < 0) {
            hudX = 0;
        }
        if (hudY < 0) {
            hudY = 0;
        }
    }

    // ─── Getters & Setters ───────────────────────────────────────────

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isHudEnabled() {
        return hudEnabled;
    }

    public void setHudEnabled(boolean hudEnabled) {
        this.hudEnabled = hudEnabled;
    }

    /**
     * HUD gösterimini açar/kapatır (Toggles HUD display state).
     *
     * @return Yeni HUD durumu (New HUD enabled state)
     */
    public boolean toggleHud() {
        this.hudEnabled = !this.hudEnabled;
        return this.hudEnabled;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public int getMaxBufferCapacity() {
        return maxBufferCapacity;
    }

    public void setMaxBufferCapacity(int maxBufferCapacity) {
        this.maxBufferCapacity = maxBufferCapacity;
    }

    public int getMaxEventsPerFlush() {
        return maxEventsPerFlush;
    }

    public void setMaxEventsPerFlush(int maxEventsPerFlush) {
        this.maxEventsPerFlush = maxEventsPerFlush;
    }

    public boolean isCaptureMouseMove() {
        return captureMouseMove;
    }

    public void setCaptureMouseMove(boolean captureMouseMove) {
        this.captureMouseMove = captureMouseMove;
    }

    public boolean isCaptureKeyboard() {
        return captureKeyboard;
    }

    public void setCaptureKeyboard(boolean captureKeyboard) {
        this.captureKeyboard = captureKeyboard;
    }

    public float getHudScale() {
        return hudScale;
    }

    public void setHudScale(float hudScale) {
        this.hudScale = hudScale;
    }

    public int getHudX() {
        return hudX;
    }

    public void setHudX(int hudX) {
        this.hudX = hudX;
    }

    public int getHudY() {
        return hudY;
    }

    public void setHudY(int hudY) {
        this.hudY = hudY;
    }
}
