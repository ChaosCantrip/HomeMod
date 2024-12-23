package com.chaoscantrip.homemod;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModEventHandler {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        HomeCommand.register(event.getDispatcher());
        SetHomeCommand.register(event.getDispatcher());
        DelHomeCommand.register(event.getDispatcher());
        BackCommand.register(event.getDispatcher());
    }

}
