package com.coloryr.allmusic.client;

import com.coloryr.allmusic.codec.MusicPack;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AllMusicDebug {
    private static boolean enabled = true;
    private static BufferedWriter writer;
    private static Path logFile;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static int timePacketCount = 0;

    public static void init(Path configDir) {
        Path logDir = configDir.resolve("allmusic").resolve("logs");
        try {
            Files.createDirectories(logDir);
            logFile = logDir.resolve("debug.log");
            closeWriter();
            writer = Files.newBufferedWriter(logFile,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            write("=== AllMusic Client Debug started === (enabled=" + enabled + ")");
        } catch (IOException e) {
            System.err.println("[AllMusicDebug] Failed to open log file: " + e.getMessage());
        }
    }

    private static void closeWriter() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("[AllMusicDebug] Close failed: " + e.getMessage());
            }
            writer = null;
        }
    }

    public static void markConnect() {
        write("=== Connected to server ===");
        timePacketCount = 0;
    }

    public static void stop() {
        write("=== Disconnected ===");
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void toggle() {
        enabled = !enabled;
        log("Debug " + (enabled ? "ON" : "OFF"));
    }

    public static void log(String msg) {
        if (!enabled) return;
        write("[LOG] " + msg);
    }

    public static void logPacket(MusicPack pack) {
        if (!enabled) return;
        if (pack.type == com.coloryr.allmusic.codec.CommandType.TIME) {
            timePacketCount++;
            if (timePacketCount % 100 == 1) {
                write("[PACKET:TIME] #" + timePacketCount);
            }
            return;
        }
        write("[PACKET] " + pack.type);
    }

    public static void logHudState(String msg) {
        if (!enabled) return;
        write("[HUD] " + msg);
    }

    public static void logError(String msg) {
        write("[ERROR] " + msg);
    }

    private static synchronized void write(String msg) {
        if (writer == null) return;
        try {
            var line = "[" + LocalDateTime.now().format(TIME) + "] " + msg;
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("[AllMusicDebug] Write failed: " + e.getMessage());
        }
    }
}
