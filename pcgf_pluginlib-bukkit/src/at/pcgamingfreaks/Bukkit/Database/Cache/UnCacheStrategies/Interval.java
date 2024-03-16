/*
 *   Copyright (C) 2020 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Database.Cache.UnCacheStrategies;

import at.pcgamingfreaks.Bukkit.PaperLib.ScheduleHandler;
import at.pcgamingfreaks.Database.Cache.ICacheablePlayer;
import at.pcgamingfreaks.Database.Cache.IPlayerCache;
import at.pcgamingfreaks.Database.Cache.BaseUnCacheStrategy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.scheduling.ScheduledTask;

public class Interval extends BaseUnCacheStrategy implements Runnable
{
	final ScheduledTask taskId;

	public Interval(final @NotNull Plugin plugin, final @NotNull IPlayerCache cache, final long delay, final long interval)
	{
		super(cache);
		taskId = ScheduleHandler.getScheduler().runAtFixedRate(this, delay, interval);
	}

	@Override
	public void run()
	{
		for(ICacheablePlayer player : cache.getCachedPlayers())
		{
			if(!player.isOnline() && player.canBeUncached())
			{
				this.cache.unCache(player);
			}
		}
	}

	@Override
	public void close()
	{
		taskId.cancel();
	}
}