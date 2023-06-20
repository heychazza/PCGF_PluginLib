/*
 *   Copyright (C) 2023 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Message.Placeholder.Processors;

import at.pcgamingfreaks.Message.Placeholder.Processors.IPlaceholderProcessor;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerNamePlaceholderProcessor implements IPlaceholderProcessor
{
	public static final PlayerNamePlaceholderProcessor INSTANCE = new PlayerNamePlaceholderProcessor();

	@Override
	public @NotNull String process(@Nullable Object parameter)
	{
		if (parameter instanceof Player) return ((Player) parameter).getName();
		else if (parameter instanceof OfflinePlayer)
		{
			String name = ((OfflinePlayer) parameter).getName();
			return name == null ? "null" : name;
		}
		else if (parameter instanceof CommandSender)
		{
			String name = ((CommandSender) parameter).getName();
			return name == null ? "null" : name;
		}
		return "Unknown";
	}
}
