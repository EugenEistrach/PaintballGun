package de.fllip.paintballgun;

import de.fllip.paintballgun.events.EventHandlers;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.07.2019
 * Time: 19:31
 */
public class PaintballGun extends JavaPlugin {

    public final static int TICKS_PER_SECOND = 20;

    public final static int TICKS_TO_UPDATE = 20;

    @Getter
    private static PaintballGun instance;

    private EventHandlers eventHandlers;

    @Override
    public void onEnable() {
        instance = this;

        init();
    }


    @Override
    public void onDisable() {

    }

    private void init() {
        eventHandlers = new EventHandlers();
        Bukkit.getPluginManager().registerEvents(eventHandlers, this);
        eventHandlers.runTaskTimer(this, 0, TICKS_TO_UPDATE);

    }

    private void registerListener(String... packages) {
        Arrays.stream(packages).forEach(p -> new Reflections(p, this.getClassLoader(), new SubTypesScanner(false)).getSubTypesOf(Listener.class).forEach(listener -> {
            try {
                Bukkit.getPluginManager().registerEvents(listener.newInstance(), this);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }));

    }

}
