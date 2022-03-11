/*
 *     Copyright (C) 2022  SunStorm
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.sunstorm.autoshutdown;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ShutdownTask {
    private final LocalDateTime time;
    private final boolean shutdownPC;
    private final List<ScheduledFuture<?>> futureList = new ArrayList<>();

    public ShutdownTask(LocalDateTime time, boolean shutdownPC) {
        this.time = time;
        this.shutdownPC = shutdownPC;
    }

    public void start() {
        var now = LocalDateTime.now();
        if (ChronoUnit.MINUTES.between(now, time) > 1) {
            Duration duration = Duration.between(now, time.minus(1, ChronoUnit.MINUTES));
            delayed(() -> {
                AutoShutdown.INSTANCE.getSLF4JLogger().info("Scheduled shutdown in 1 minute{}", shutdownPC ? ", PC included" : "");
                Bukkit.broadcast(Component.text("Scheduled server shutdown in 1 minute").color(TextColor.color(255, 43, 16)));
            }, duration);
        }

        delayed(() -> {
            if (shutdownPC) {
                try {
                    Runtime.getRuntime().exec("shutdown /s /t 60 /f");
                } catch (IOException e) {
                    AutoShutdown.INSTANCE.getSLF4JLogger().error("Failed to execute windows shutdown", e);
                }
            }
            Bukkit.getOnlinePlayers().forEach(p -> p.kick(Component.text("Scheduled shutdown")));
            Bukkit.shutdown();
        }, Duration.between(now, time));
    }

    public void cancel() {
        futureList.forEach(f -> f.cancel(true));
        try {
            Runtime.getRuntime().exec("shutdown /a");
        } catch (IOException e) {
            AutoShutdown.INSTANCE.getSLF4JLogger().error("Failed to execute windows shutdown abort", e);
        }
    }

    private void delayed(Runnable task, Duration delay) {
        futureList.add(AutoShutdown.INSTANCE.getScheduler().schedule(task, TimeUnit.SECONDS.toNanos(delay.getSeconds()) + delay.getNano(), TimeUnit.NANOSECONDS));
    }
}
