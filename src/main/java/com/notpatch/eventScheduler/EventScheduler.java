package com.notpatch.eventScheduler;

import com.notpatch.eventScheduler.command.CommandEvent;
import com.notpatch.eventScheduler.command.CommandMain;
import com.notpatch.eventScheduler.hook.HookPlaceholderAPI;
import com.notpatch.eventScheduler.manager.ConfigurationManager;
import com.notpatch.eventScheduler.manager.TaskManager;
import com.notpatch.eventScheduler.manager.WebhookManager;
import com.notpatch.eventScheduler.task.ScheduleTask;
import fr.mrmicky.fastinv.FastInvManager;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class EventScheduler extends JavaPlugin {

    private static EventScheduler instance;
    private TaskManager taskManager;

    private ConfigurationManager configurationManager;
    private WebhookManager webhookManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveConfig();

        Metrics metrics = new Metrics(this, 24035);

        if (usePlaceholderAPI()) {
            new HookPlaceholderAPI().register();
        }

        configurationManager = new ConfigurationManager();
        configurationManager.loadConfigurations();

        webhookManager = new WebhookManager(this);
        webhookManager.loadWebhooks();

        taskManager = new TaskManager(this);
        taskManager.loadTasks();

        ScheduleTask task = new ScheduleTask(this);
        task.startRepeatingTask();

        FastInvManager.register(this);

        getCommand("eventscheduler").setExecutor(new CommandMain(this));
        getCommand("events").setExecutor(new CommandEvent());

    }

    @Override
    public void onDisable() {
        configurationManager.saveConfigurations();
    }


    private boolean usePlaceholderAPI() {
        if (getConfig().getBoolean("UsePlaceholderAPI")) {
            Plugin plugin = getServer().getPluginManager().getPlugin("PlaceholderAPI");
            if (plugin != null || (plugin instanceof PlaceholderAPIPlugin)) {
                return true;
            }
        }
        return false;
    }

    public static EventScheduler getInstance() {
        return instance;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public WebhookManager getWebhookManager() {
        return webhookManager;
    }
}
