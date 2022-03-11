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

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import lombok.Getter;
import lombok.Setter;
import me.sunstorm.autoshutdown.command.DateTimeProvider;
import me.sunstorm.autoshutdown.command.ShutdownCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public final class AutoShutdown extends JavaPlugin {
    public static AutoShutdown INSTANCE;
    private ScheduledExecutorService scheduler;
    @Nullable
    @Setter private ShutdownTask shutdownTask;

    @Override
    public void onEnable() {
        INSTANCE = this;
        scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        CommandService drink = Drink.get(this);
        drink.bind(LocalDateTime.class).toProvider(new DateTimeProvider());
        drink.register(new ShutdownCommand(), "autoshutdown", "as", "ash");
        drink.registerCommands();
    }
}
