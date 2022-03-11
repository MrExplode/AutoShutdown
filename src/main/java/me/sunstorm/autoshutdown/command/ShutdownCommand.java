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

package me.sunstorm.autoshutdown.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.OptArg;
import com.jonahseguin.drink.annotation.Sender;
import me.sunstorm.autoshutdown.AutoShutdown;
import me.sunstorm.autoshutdown.ShutdownTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class ShutdownCommand {

    @Command(name = "", desc = "Manage shutdown options", aliases = {"ash", "as"})
    public void onDefault(@Sender CommandSender sender) {
        if (validateSender(sender)) return;
        sender.sendMessage("create cancel");
    }

    @Command(name = "create", desc = "Create a shutdown", usage = "<time> <shutdownPC>")
    public void onCreate(@Sender CommandSender sender, LocalDateTime time, @OptArg("false") boolean shutdownPC) {
        if (validateSender(sender)) return;
        if (AutoShutdown.INSTANCE.getShutdownTask() != null) {
            sender.sendMessage("Shutdown already scheduled! Cancel with /ah cancel");
            return;
        }
        var task = new ShutdownTask(time, shutdownPC);
        task.start();
        AutoShutdown.INSTANCE.setShutdownTask(task);
        sender.sendMessage("Shutdown scheduled at " + time.toString() + " PC shutdown: " + shutdownPC);
    }

    @Command(name = "cancel", desc = "Cancel scheduled shutdown")
    public void onCancel(@Sender CommandSender sender) {
        if (validateSender(sender)) return;
        if (AutoShutdown.INSTANCE.getShutdownTask() == null) {
            sender.sendMessage("No shutdown scheduled currently");
            return;
        }
        AutoShutdown.INSTANCE.getShutdownTask().cancel();
        AutoShutdown.INSTANCE.setShutdownTask(null);
        sender.sendMessage("Shutdown cancelled successfully");
    }

    public static boolean validateSender(CommandSender sender) {
        boolean player = sender instanceof Player;
        if (player) sender.sendMessage("§cEzt csak konzolból használhatod!");
        return player;
    }
}
