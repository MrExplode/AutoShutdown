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

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

public class DateTimeProvider extends DrinkProvider<LocalDateTime> {
    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public LocalDateTime provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        try {
            var time = LocalTime.parse(arg.get());
            var candidate = LocalDateTime.of(LocalDate.now(), time);
            if (candidate.compareTo(LocalDateTime.now()) <= 0)
                candidate = candidate.plus(1, ChronoUnit.DAYS);
            return candidate;
        } catch (DateTimeParseException e) {
            throw new CommandExitMessage("Invalid time format");
        }
    }

    @Override
    public String argumentDescription() {
        return "time";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return Collections.singletonList(LocalTime.now().plus(5, ChronoUnit.MINUTES).toString());
    }
}
