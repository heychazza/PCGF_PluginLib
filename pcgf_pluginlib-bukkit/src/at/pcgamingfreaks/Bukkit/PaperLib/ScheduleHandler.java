package at.pcgamingfreaks.Bukkit.PaperLib;

import lombok.Getter;
import org.bukkit.Bukkit;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.scheduling.AsynchronousScheduler;
import space.arim.morepaperlib.scheduling.RegionalScheduler;

import java.util.Objects;

public class ScheduleHandler {
    @Getter
    private static final MorePaperLib morePaperLib = new MorePaperLib(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MarriageMaster")));

    public static AsynchronousScheduler getAsyncScheduler() {
       return getMorePaperLib().scheduling().asyncScheduler();
    }

    public static RegionalScheduler getScheduler() {
        return getMorePaperLib().scheduling().globalRegionalScheduler();
    }
}
